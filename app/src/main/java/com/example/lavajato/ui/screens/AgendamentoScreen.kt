package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.AppointmentRepository
import com.example.lavajato.data.model.Service
import com.example.lavajato.data.model.ServiceRepository
import com.example.lavajato.ui.components.AppButton
import com.example.lavajato.ui.components.ServiceSelectableCard
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White
import com.example.lavajato.ui.theme.YellowAccent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class BookingStep(val number: Int, val label: String)
private val bookingSteps = listOf(
    BookingStep(1, "Serviço"),
    BookingStep(2, "Veículo"),
    BookingStep(3, "Data"),
    BookingStep(4, "Horário"),
    BookingStep(5, "Confirmar")
)

private data class BookingVehicle(
    val id: String,
    val marca: String,
    val modelo: String,
    val placa: String,
    val cor: String
)

private val dateDisplayFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale("pt", "BR"))
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val bookingTimes = listOf("08:00", "09:30", "11:00", "14:00", "15:30", "17:00")

@Composable
fun AgendamentoScreen(
    onVoltarClick: () -> Unit,
    onConcluidoClick: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var vehicles by remember { mutableStateOf<List<BookingVehicle>>(emptyList()) }
    var loadingVehicles by remember { mutableStateOf(true) }
    var vehicleError by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = remember { FirebaseFirestore.getInstance() }

    // Recalcula o relógio ao retornar ao fluxo e imediatamente antes de avançar.
    var clockNow by remember { mutableStateOf(LocalDateTime.now()) }
    val today = clockNow.toLocalDate()
    val availableDates = (0L..29L).map { today.plusDays(it) }
    val availableTimes = bookingTimes.filter { time ->
        val date = selectedDate
        date != null && !date.isBefore(today) &&
                (date.isAfter(today) || LocalTime.parse(time, timeFormatter).isAfter(clockNow.toLocalTime()))
    }
    val selectedVehicle = vehicles.firstOrNull { it.id == selectedVehicleId }

    DisposableEffect(uid) {
        var registration: ListenerRegistration? = null
        if (uid == null) {
            loadingVehicles = false
            vehicleError = "Entre na sua conta para escolher um veículo."
        } else {
            registration = firestore.collection("usuarios").document(uid)
                .collection("veiculos")
                .addSnapshotListener { snapshot, error ->
                    loadingVehicles = false
                    if (error != null) {
                        vehicleError = "Não foi possível consultar seus veículos: ${error.localizedMessage}"
                    } else {
                        vehicleError = null
                        vehicles = snapshot?.documents?.map { doc ->
                            BookingVehicle(
                                id = doc.id,
                                marca = doc.getString("marca").orEmpty(),
                                modelo = doc.getString("modelo").orEmpty(),
                                placa = doc.getString("placa").orEmpty(),
                                cor = doc.getString("cor").orEmpty()
                            )
                        }.orEmpty().sortedWith(compareBy({ it.marca }, { it.modelo }))
                    }
                }
        }
        onDispose { registration?.remove() }
    }

    Column(modifier = Modifier.fillMaxSize().background(LightGray)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                message = null
                if (currentStep > 1) currentStep-- else onVoltarClick()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = NavyDark)
            }
            Text("Agendar Serviço", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bookingSteps.forEach { step ->
                val active = step.number == currentStep
                val done = step.number < currentStep
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(29.dp)
                            .background(if (active || done) YellowAccent else White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(step.number.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        step.label, fontSize = 10.sp,
                        color = if (active) NavyDark else TextGraySecondary,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                1 -> StepService(selectedService) { selectedService = it }
                2 -> StepVehicle(vehicles, selectedVehicleId, loadingVehicles, vehicleError) {
                    selectedVehicleId = it
                }
                3 -> StepDate(availableDates, selectedDate) {
                    selectedDate = it
                    selectedTime = null
                }
                4 -> StepTime(availableTimes, selectedTime) { selectedTime = it }
                5 -> StepConfirmation(selectedService, selectedVehicle, selectedDate, selectedTime)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            if (message != null) {
                Text(message.orEmpty(), color = androidx.compose.ui.graphics.Color(0xFFC62828), fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }
            AppButton(
                text = if (saving) "Salvando..." else if (currentStep == 5) "Confirmar Agendamento" else "Próximo",
                onClick = {
                    if (saving) return@AppButton
                    clockNow = LocalDateTime.now()
                    val now = LocalDateTime.now()
                    val chosenDate = selectedDate
                    val chosenTime = selectedTime
                    val dateTimeValid = chosenDate != null && chosenTime != null &&
                            !LocalDateTime.of(chosenDate, LocalTime.parse(chosenTime, timeFormatter)).isBefore(now) &&
                            LocalDateTime.of(chosenDate, LocalTime.parse(chosenTime, timeFormatter)).isAfter(now)

                    message = when (currentStep) {
                        1 -> if (selectedService == null) "Selecione um serviço." else null
                        2 -> if (selectedVehicle == null) "Selecione um veículo cadastrado." else null
                        3 -> if (chosenDate == null || chosenDate.isBefore(now.toLocalDate())) "Selecione uma data válida." else null
                        4 -> if (!dateTimeValid) "Selecione um horário futuro." else null
                        else -> when {
                            selectedService == null -> "Selecione um serviço."
                            selectedVehicle == null -> "Selecione um veículo cadastrado."
                            !dateTimeValid -> "A data ou o horário passou. Escolha outro horário."
                            else -> null
                        }
                    }
                    if (message == null) {
                        if (currentStep < 5) {
                            currentStep++
                        } else {
                            val service = selectedService!!
                            val vehicle = selectedVehicle!!
                            saving = true
                            AppointmentRepository.addAppointment(
                                serviceName = service.name,
                                date = chosenDate!!.toString(),
                                time = chosenTime!!,
                                vehicleId = vehicle.id,
                                vehicleDescription = "${vehicle.marca} ${vehicle.modelo}",
                                vehiclePlate = vehicle.placa
                            ) { error ->
                                saving = false
                                if (error == null) onConcluidoClick()
                                else message = "Não foi possível salvar: $error"
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun StepService(selected: Service?, onSelect: (Service) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ServiceRepository.services) { service ->
            ServiceSelectableCard(
                service = service,
                selected = selected?.id == service.id,
                onSelect = { onSelect(service) }
            )
        }
    }
}

@Composable
private fun StepVehicle(
    vehicles: List<BookingVehicle>,
    selectedId: String?,
    loading: Boolean,
    error: String?,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Selecione o veículo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(Modifier.height(12.dp))
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text(error, color = androidx.compose.ui.graphics.Color(0xFFC62828))
            vehicles.isEmpty() -> Text("Nenhum veículo cadastrado. Acesse Perfil → Meus veículos para adicionar um.", color = TextGraySecondary)
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vehicles, key = { it.id }) { vehicle ->
                    SelectableBookingBox(
                        title = "${vehicle.marca} ${vehicle.modelo}",
                        subtitle = "${vehicle.placa} • ${vehicle.cor}",
                        selected = vehicle.id == selectedId,
                        onClick = { onSelect(vehicle.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepDate(dates: List<LocalDate>, selected: LocalDate?, onSelect: (LocalDate) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Selecione a data", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dates, key = { it.toString() }) { date ->
                SelectableBookingBox(
                    title = date.format(dateDisplayFormatter).replaceFirstChar { it.titlecase(Locale("pt", "BR")) },
                    subtitle = if (date == LocalDate.now()) "Hoje" else "",
                    selected = date == selected,
                    onClick = { onSelect(date) }
                )
            }
        }
    }
}

@Composable
private fun StepTime(times: List<String>, selected: String?, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Selecione o horário", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(Modifier.height(12.dp))
        if (times.isEmpty()) {
            Text("Não há horários futuros para esta data. Volte e escolha outro dia.", color = TextGraySecondary)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(times) { time ->
                    SelectableBookingBox(
                        title = time,
                        subtitle = "",
                        selected = time == selected,
                        onClick = { onSelect(time) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepConfirmation(
    service: Service?,
    vehicle: BookingVehicle?,
    date: LocalDate?,
    time: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp)
            .background(White, RoundedCornerShape(16.dp)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = YellowAccent, modifier = Modifier.size(60.dp))
        Spacer(Modifier.height(12.dp))
        Text("Resumo do Agendamento", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(Modifier.height(16.dp))
        Text("Serviço: ${service?.name.orEmpty()}", fontSize = 14.sp)
        Text("Valor: R$ ${service?.price ?: 0.0}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text("Veículo: ${vehicle?.marca.orEmpty()} ${vehicle?.modelo.orEmpty()}", fontSize = 14.sp)
        Text("Placa: ${vehicle?.placa.orEmpty()}", fontSize = 14.sp)
        Text("Data: ${date?.format(dateDisplayFormatter).orEmpty()}", fontSize = 14.sp)
        Text("Horário: ${time.orEmpty()}", fontSize = 14.sp)
    }
}

@Composable
private fun SelectableBookingBox(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(if (selected) NavyDark else White, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, color = if (selected) White else NavyDark, fontWeight = FontWeight.SemiBold)
        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = if (selected) White else TextGraySecondary, fontSize = 12.sp)
        }
    }
}
