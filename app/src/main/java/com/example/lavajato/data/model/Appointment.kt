package com.example.lavajato.data.model

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

enum class AppointmentStatus(val label: String) {
    CONFIRMADO("Confirmado"), FINALIZADO("Finalizado"), CANCELADO("Cancelado")
}

data class Appointment(
    val id: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val address: String = "Av. Recife, 123 - Boa Viagem - Recife/PE",
    val status: AppointmentStatus = AppointmentStatus.CONFIRMADO,
    val vehicleId: String = "",
    val vehicleDescription: String = "",
    val vehiclePlate: String = ""
)

object AppointmentRepository {

    // Identificador do estabelecimento Lava Jato Prime
    const val ESTABELECIMENTO_ID = "lava_jato_prime"
    val zone: ZoneId = ZoneId.of("America/Recife")
    val bookingHours = listOf("08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "14:30", "15:00", "16:00", "17:00")
    private val controlRef get() = db.collection("controle_horarios").document(ESTABELECIMENTO_ID)

    // Sem reconciliação administrativa não é possível inferir ausência de legados.
    fun observeAvailability(date: String, onChange: (Set<String>?, String?) -> Unit): () -> Unit {
        var ready = false
        var occupied: Set<String>? = null
        var disposed = false
        fun emit() {
            if (!disposed) onChange(if (ready) occupied else null,
                if (ready && occupied != null) null else "Disponibilidade não confirmada. Aguarde a conexão e a conferência dos horários.")
        }
        onChange(null, null)
        val control = controlRef.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
            ready = error == null && snapshot != null && !snapshot.metadata.isFromCache &&
                !snapshot.metadata.hasPendingWrites() && snapshot.get("reconciliado") == true
            emit()
        }
        val slots = db.collection("horarios_reservados")
            .whereEqualTo("estabelecimentoId", ESTABELECIMENTO_ID).whereEqualTo("date", date)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                occupied = if (error == null && snapshot != null && !snapshot.metadata.isFromCache &&
                    !snapshot.metadata.hasPendingWrites() && snapshot.documents.all {
                        val time = it.get("time") as? String
                        time in bookingHours && it.id == gerarIdHorario(ESTABELECIMENTO_ID, date, time.orEmpty())
                    }) snapshot.documents.map { it.get("time") as String }.toSet() else null
                emit()
            }
        return { disposed = true; control.remove(); slots.remove() }
    }

    private fun errorMessage(error: Exception, fallback: String): String =
        generateSequence<Throwable>(error) { it.cause }
            .filterIsInstance<IllegalStateException>().firstOrNull()?.message ?: fallback

    private fun gerarIdHorario(
        estabelecimentoId: String,
        date: String,
        time: String
    ): String {
        val horarioFormatado = time.replace(":", "-")
        return "${estabelecimentoId}_${date}_${horarioFormatado}"
    }
    val list = mutableStateListOf<Appointment>()

    private val db get() = FirebaseFirestore.getInstance()

    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid

    private var listener: ListenerRegistration? = null

    private var activeUid: String? = null

    fun observe(onError: (String) -> Unit = {}) {
        val userId = uid
        if (userId == null) {
            stopObserving()
            onError("Entre na sua conta para consultar os agendamentos.")
            return
        }
        if (activeUid == userId && listener != null) return
        stopObserving()
        activeUid = userId
        listener = db.collection("agendamentos")
            .whereEqualTo("usuarioId", userId)
            .addSnapshotListener { snapshot, error ->
                if (activeUid != userId) return@addSnapshotListener
                if (error != null) {
                    onError(error.localizedMessage ?: "Falha ao consultar agendamentos.")
                    return@addSnapshotListener
                }
                val appointments = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    val status = runCatching {
                        AppointmentStatus.valueOf(doc.getString("status") ?: "CONFIRMADO")
                    }.getOrDefault(AppointmentStatus.CONFIRMADO)
                    Appointment(
                        id = doc.id,
                        serviceName = doc.getString("serviceName").orEmpty(),
                        date = doc.getString("date").orEmpty(),
                        time = doc.getString("time").orEmpty(),
                        address = doc.getString("address") ?: "Av. Recife, 123 - Boa Viagem - Recife/PE",
                        status = status,
                        vehicleId = doc.getString("veiculoId").orEmpty(),
                        vehicleDescription = doc.getString("vehicleDescription").orEmpty(),
                        vehiclePlate = doc.getString("vehiclePlate").orEmpty()
                    )
                }.sortedWith(compareBy<Appointment> { it.date }.thenBy { it.time })
                list.clear()
                list.addAll(appointments)
            }
    }

    fun stopObserving() {
        listener?.remove()
        listener = null
        activeUid = null
        list.clear()
    }

    fun addAppointment(
        serviceName: String,
        date: String,
        time: String,
        vehicleId: String,
        vehicleDescription: String,
        vehiclePlate: String,
        onResult: (String?) -> Unit
    ) {
        val userId = uid ?: run {
            onResult("Faça login para confirmar o agendamento.")
            return
        }

        if (vehicleId.isBlank()) {
            onResult("Selecione um veículo.")
            return
        }
        val start = runCatching { LocalDateTime.parse("${date}T$time") }.getOrNull()
        if (start == null || start.toLocalDate().toString() != date || time !in bookingHours ||
            !start.isAfter(LocalDateTime.now(zone))) {
            onResult("Escolha uma data e um horário futuros válidos.")
            return
        }

        val horarioId = gerarIdHorario(
            ESTABELECIMENTO_ID,
            date,
            time
        )

        val reservaRef = db.collection("horarios_reservados")
            .document(horarioId)

        val agendamentoRef = db.collection("agendamentos")
            .document()

        val data = hashMapOf(
            "usuarioId" to userId,
            "estabelecimentoId" to ESTABELECIMENTO_ID,
            "horarioId" to horarioId,
            "controleVersao" to 2,
            "inicio" to Timestamp(Date.from(start.atZone(zone).toInstant())),
            "veiculoId" to vehicleId,
            "serviceName" to serviceName,
            "date" to date,
            "time" to time,
            "vehicleDescription" to vehicleDescription,
            "vehiclePlate" to vehiclePlate,
            "address" to "Av. Recife, 123 - Boa Viagem - Recife/PE",
            "status" to AppointmentStatus.CONFIRMADO.name
        )

        val reserva = hashMapOf(
            "estabelecimentoId" to ESTABELECIMENTO_ID,
            "date" to date,
            "time" to time,
            "agendamentoId" to agendamentoRef.id,
            "bloqueioLegado" to false
        )

        db.runTransaction { transaction ->
            check(transaction.get(controlRef).getBoolean("reconciliado") == true) {
                "Os horários aguardam conferência do estabelecimento. Tente novamente mais tarde."
            }
            check(start.isAfter(LocalDateTime.now(zone))) { "Este horário já passou. Escolha outro." }
            check(transaction.get(db.collection("usuarios").document(userId)
                .collection("veiculos").document(vehicleId)).exists()) { "Selecione um veículo cadastrado na sua conta." }
            val horarioExistente = transaction.get(reservaRef)

            if (horarioExistente.exists()) {
                throw IllegalStateException(
                    "Este horário já está reservado. Escolha outro horário."
                )
            }

            transaction.set(agendamentoRef, data)
            transaction.set(reservaRef, reserva)

            null

        }.addOnSuccessListener {
            onResult(null)
        }.addOnFailureListener { error ->
            onResult(
                errorMessage(error, "Não foi possível confirmar. Verifique sua conexão e tente novamente.")
            )
        }
    }

    fun updateStatus(id: String, newStatus: AppointmentStatus, onResult: (String?) -> Unit = {}) {
        val userId = uid
        if (userId == null) {
            onResult("Faça login novamente.")
            return
        }
        if (newStatus != AppointmentStatus.CANCELADO) {
            onResult("Somente agendamentos confirmados podem ser cancelados.")
            return
        }
        val ref = db.collection("agendamentos").document(id)
        db.runTransaction { transaction ->
            val appointment = transaction.get(ref)
            check(appointment.getString("usuarioId") == userId) { "Você só pode cancelar seus próprios agendamentos." }
            check(appointment.getString("status") == AppointmentStatus.CONFIRMADO.name) { "Somente agendamentos confirmados podem ser cancelados." }
            val slotId = appointment.getString("horarioId")
            val slotRef = slotId?.let { db.collection("horarios_reservados").document(it) }
            val slot = slotRef?.let { transaction.get(it) }
            val reconciled = transaction.get(controlRef).getBoolean("reconciliado") == true
            val release = SlotReleasePolicy.canRelease(id, appointment.data.orEmpty(), slot?.data.orEmpty(), reconciled)
            transaction.update(ref, "status", newStatus.name)
            if (release && slotRef != null) transaction.delete(slotRef)
            null
        }
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(errorMessage(it, "Não foi possível cancelar. Verifique sua conexão e tente novamente.")) }
    }
}
