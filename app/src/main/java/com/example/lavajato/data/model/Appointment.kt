package com.example.lavajato.data.model

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

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
        val data = hashMapOf(
            "usuarioId" to userId,
            "veiculoId" to vehicleId,
            "serviceName" to serviceName,
            "date" to date,
            "time" to time,
            "vehicleDescription" to vehicleDescription,
            "vehiclePlate" to vehiclePlate,
            "address" to "Av. Recife, 123 - Boa Viagem - Recife/PE",
            "status" to AppointmentStatus.CONFIRMADO.name
        )
        db.collection("agendamentos").add(data)
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(it.localizedMessage ?: "Não foi possível salvar o agendamento.") }
    }

    fun updateStatus(id: String, newStatus: AppointmentStatus, onResult: (String?) -> Unit = {}) {
        val _userId = uid ?: run {
            onResult("Faça login novamente.")
            return
        }
        val appointment = list.firstOrNull { it.id == id }
        if (appointment == null || appointment.status != AppointmentStatus.CONFIRMADO ||
            newStatus != AppointmentStatus.CANCELADO) {
            onResult("Somente agendamentos confirmados podem ser cancelados.")
            return
        }
        db.collection("agendamentos").document(id).update("status", newStatus.name)
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(it.localizedMessage ?: "Falha ao cancelar agendamento.") }
    }
}
