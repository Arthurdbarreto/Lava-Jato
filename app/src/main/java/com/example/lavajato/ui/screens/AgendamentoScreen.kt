package com.example.lavajato.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.AppointmentRepository
import com.example.lavajato.data.model.Service
import com.example.lavajato.data.model.ServiceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Blue = Color(0xFF1976FF)
private val Dark = Color(0xFF091C35)
private val Muted = Color(0xFF7D8DA4)
private val Border = Color(0xFFDCE5F1)
private val Pale = Color(0xFFF4F7FB)
private val LightBlue = Color(0xFFEAF2FF)
private val BrazilianLocale = Locale("pt", "BR")
private val DateLabel = DateTimeFormatter.ofPattern(
    "dd 'de' MMMM, EEEE", BrazilianLocale
)
private val MonthLabel = DateTimeFormatter.ofPattern(
    "MMMM yyyy", BrazilianLocale
)
private val HourFormat = DateTimeFormatter.ofPattern("HH:mm")

private val BookingHours = listOf(
    "08:00", "09:00", "10:00",
    "11:00", "13:00", "14:00",
    "14:30", "15:00", "16:00", "17:00"
)

private data class BookingVehicle(
    val id: String,
    val marca: String,
    val modelo: String,
    val placa: String,
    val cor: String,
    val principal: Boolean = false
)

@Composable
fun AgendamentoScreen(
    onVoltarClick: () -> Unit,
    onConcluidoClick: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var service by remember { mutableStateOf<Service?>(null) }
    var vehicleId by remember { mutableStateOf<String?>(null) }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var hour by remember { mutableStateOf<String?>(null) }

    var vehicles by remember {
        mutableStateOf<List<BookingVehicle>>(emptyList())
    }
    var loadingVehicles by remember { mutableStateOf(true) }
    var vehicleError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = remember { FirebaseFirestore.getInstance() }
    val selectedVehicle = vehicles.firstOrNull { it.id == vehicleId }

    // Seleciona o veículo principal ao entrar na etapa 2.
    // Não substitui uma escolha manual já realizada.
    LaunchedEffect(step, loadingVehicles, vehicles) {
        if (step == 2 && !loadingVehicles && vehicleId == null) {
            vehicleId = vehicles.firstOrNull { it.principal }?.id
        }
    }

    // Carrega os veículos cadastrados no Firestore.
    DisposableEffect(uid) {
        var registration: ListenerRegistration? = null

        if (uid == null) {
            loadingVehicles = false
            vehicleError = "Entre na sua conta para selecionar um veículo."
        } else {
            registration = firestore.collection("usuarios")
                .document(uid)
                .collection("veiculos")
                .addSnapshotListener { snapshot, error ->
                    loadingVehicles = false

                    if (error != null) {
                        vehicleError = error.localizedMessage
                            ?: "Não foi possível carregar os veículos."
                    } else {
                        vehicleError = null

                        vehicles = snapshot?.documents?.map { document ->
                            BookingVehicle(
                                id = document.id,
                                marca = document.getString("marca").orEmpty(),
                                modelo = document.getString("modelo").orEmpty(),
                                placa = document.getString("placa").orEmpty(),
                                cor = document.getString("cor").orEmpty(),
                                principal = document.getBoolean("principal") == true
                            )
                        }.orEmpty().sortedWith(
                            compareByDescending<BookingVehicle> {
                                it.principal
                            }.thenBy {
                                it.marca.lowercase(BrazilianLocale)
                            }.thenBy {
                                it.modelo.lowercase(BrazilianLocale)
                            }
                        )

                        // Evita manter selecionado um veículo excluído.
                        if (
                            vehicleId != null &&
                            vehicles.none { it.id == vehicleId }
                        ) {
                            vehicleId = null
                        }
                    }
                }
        }

        onDispose {
            registration?.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BookingHeader(
            step = step,
            onBack = {
                message = null
                if (step > 1) {
                    step--
                } else {
                    onVoltarClick()
                }
            }
        )

        HorizontalDivider(color = Border)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            BookingProgress(step)

            when (step) {
                1 -> ServiceStep(
                    selected = service,
                    onSelect = { service = it }
                )

                2 -> VehicleStep(
                    vehicles = vehicles,
                    selectedId = vehicleId,
                    loading = loadingVehicles,
                    error = vehicleError,
                    onSelect = { vehicleId = it }
                )

                3 -> DateStep(
                    selected = date,
                    today = now.toLocalDate(),
                    onSelect = {
                        date = it
                        hour = null
                    }
                )

                4 -> TimeStep(
                    service = service,
                    vehicle = selectedVehicle,
                    date = date,
                    selected = hour,
                    now = now,
                    onSelect = { hour = it }
                )
            }
        }

        if (message != null) {
            Text(
                text = message.orEmpty(),
                color = Color(0xFFB3261E),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        HorizontalDivider(color = Border)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step == 1 && service != null) {
                Column(modifier = Modifier.padding(end = 14.dp)) {
                    Text(
                        text = "Total",
                        fontSize = 11.sp,
                        color = Muted
                    )
                    Text(
                        text = "R$ ${service!!.price}",
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )
                }
            }

            Button(
                onClick = {
                    if (saving) return@Button

                    now = LocalDateTime.now()
                    message = null

                    when (step) {
                        1 -> {
                            if (service == null) {
                                message = "Selecione um serviço."
                            } else {
                                step = 2
                            }
                        }

                        2 -> {
                            if (selectedVehicle == null) {
                                message = "Selecione um veículo cadastrado."
                            } else {
                                step = 3
                            }
                        }

                        3 -> {
                            if (
                                date == null ||
                                date!!.isBefore(now.toLocalDate())
                            ) {
                                message = "Selecione uma data válida."
                            } else {
                                step = 4
                            }
                        }

                        4 -> {
                            val chosenDate = date
                            val chosenHour = hour
                            val chosenVehicle = selectedVehicle
                            val chosenService = service

                            val validDateTime =
                                chosenDate != null &&
                                        chosenHour != null &&
                                        LocalDateTime.of(
                                            chosenDate,
                                            LocalTime.parse(chosenHour, HourFormat)
                                        ).isAfter(now)

                            if (!validDateTime) {
                                message = "Escolha um horário futuro."
                            } else if (
                                chosenVehicle == null ||
                                chosenService == null
                            ) {
                                message = "Confira os dados do agendamento."
                            } else {
                                saving = true

                                AppointmentRepository.addAppointment(
                                    serviceName = chosenService.name,
                                    date = chosenDate!!.toString(),
                                    time = chosenHour!!,
                                    vehicleId = chosenVehicle.id,
                                    vehicleDescription =
                                        "${chosenVehicle.marca} ${chosenVehicle.modelo}",
                                    vehiclePlate = chosenVehicle.placa
                                ) { error ->
                                    saving = false

                                    if (error == null) {
                                        onConcluidoClick()
                                    } else {
                                        message = "Não foi possível salvar: $error"
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !saving,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = when {
                        saving -> "Salvando..."
                        step == 4 -> "Confirmar horário"
                        else -> "Continuar"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BookingHeader(
    step: Int,
    onBack: () -> Unit
) {
    val title = when (step) {
        1 -> "Escolha o serviço"
        2 -> "Selecionar veículo"
        3 -> "Escolha a data"
        else -> "Escolha o horário"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
                .background(Pale, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Dark
            )
        }

        Text(
            text = title,
            fontSize = 17.sp,
            color = Dark,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun BookingProgress(step: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$step de 4",
            fontSize = 11.sp,
            color = Muted
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .width(29.dp)
                        .height(3.dp)
                        .background(
                            color = if (index < step) Blue else Border,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ServiceStep(
    selected: Service?,
    onSelect: (Service) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            EstablishmentCard()
            Spacer(Modifier.height(22.dp))

            Text(
                text = "Qual cuidado seu carro precisa?",
                fontSize = 17.sp,
                color = Dark
            )

            Spacer(Modifier.height(12.dp))
        }

        items(ServiceRepository.services) { item ->
            val active = selected?.id == item.id

            Surface(
                onClick = { onSelect(item) },
                shape = RoundedCornerShape(14.dp),
                color = if (active) Pale else Color.White,
                border = BorderStroke(
                    width = if (active) 1.5.dp else 1.dp,
                    color = if (active) Blue else Border
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = active,
                        onClick = { onSelect(item) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Blue
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            color = Dark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = "Selecione para agendar",
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "R$ ${item.price}",
                        color = Blue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EstablishmentCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Border),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(
                        LightBlue,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🚘", fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lava Jato Prime",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Text(
                    text = "Serviços automotivos",
                    fontSize = 11.sp,
                    color = Muted
                )
            }

            Text(
                text = "● Aberto",
                color = Color(0xFF21866B),
                fontSize = 11.sp
            )
        }
    }
}

// =============================================
// ETAPA 2 — SELEÇÃO DO VEÍCULO
// =============================================

@Composable
private fun VehicleStep(
    vehicles: List<BookingVehicle>,
    selectedId: String?,
    loading: Boolean,
    error: String?,
    onSelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Qual veículo receberá o serviço?",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Escolha um dos seus veículos cadastrados.",
                fontSize = 13.sp,
                color = Muted
            )

            Spacer(Modifier.height(12.dp))
        }

        when {
            loading -> {
                item {
                    CircularProgressIndicator(color = Blue)
                }
            }

            error != null -> {
                item {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 13.sp
                    )
                }
            }

            vehicles.isEmpty() -> {
                item {
                    Text(
                        text = "Nenhum veículo cadastrado. Acesse Perfil → Meus veículos.",
                        color = Muted,
                        fontSize = 13.sp
                    )
                }
            }

            else -> {
                items(
                    items = vehicles,
                    key = { it.id }
                ) { vehicle ->
                    val active = vehicle.id == selectedId
                    val isPrincipal = vehicle.principal

                    val backgroundColor = when {
                        active -> LightBlue
                        isPrincipal -> Color(0xFFF3F8FF)
                        else -> Color.White
                    }

                    val borderColor = when {
                        active -> Blue
                        isPrincipal -> Color(0xFF91BCFF)
                        else -> Border
                    }

                    Surface(
                        onClick = { onSelect(vehicle.id) },
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        border = BorderStroke(
                            width = if (active) 2.dp else 1.dp,
                            color = borderColor
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = active,
                                onClick = { onSelect(vehicle.id) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Blue
                                )
                            )

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        color = if (isPrincipal) {
                                            Color(0xFFDCEBFF)
                                        } else {
                                            LightBlue
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🚙",
                                    fontSize = 22.sp
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "${vehicle.marca} ${vehicle.modelo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Dark
                                )

                                Text(
                                    text = "${vehicle.placa} · ${vehicle.cor}",
                                    fontSize = 11.sp,
                                    color = Muted
                                )

                                if (isPrincipal) {
                                    Spacer(Modifier.height(3.dp))

                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFFDCEBFF)
                                    ) {
                                        Text(
                                            text = "★ Principal",
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            ),
                                            color = Blue,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (active) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Veículo selecionado",
                                    tint = Blue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Border)
            ) {
                Text(
                    text = "Para cadastrar outro veículo, acesse Perfil → Meus veículos.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Blue,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =============================================
// ETAPA 3 — CALENDÁRIO
// =============================================

@Composable
private fun DateStep(
    selected: LocalDate?,
    today: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    var visibleMonth by remember {
        mutableStateOf(YearMonth.from(today))
    }

    val lastDate = today.plusDays(29)
    val firstMonth = YearMonth.from(today)
    val lastMonth = YearMonth.from(lastDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(15.dp),
            color = Pale
        ) {
            Column(Modifier.padding(14.dp)) {
                SummaryLine(
                    "Estabelecimento",
                    "Lava Jato Prime"
                )
                Spacer(Modifier.height(8.dp))
                SummaryLine(
                    "Disponibilidade",
                    "Próximos 30 dias"
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Border),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        enabled = visibleMonth > firstMonth,
                        onClick = {
                            visibleMonth = visibleMonth.minusMonths(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Mês anterior",
                            tint = Dark
                        )
                    }

                    Text(
                        text = visibleMonth.format(MonthLabel)
                            .replaceFirstChar {
                                it.titlecase(BrazilianLocale)
                            },
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    IconButton(
                        enabled = visibleMonth < lastMonth,
                        onClick = {
                            visibleMonth = visibleMonth.plusMonths(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Próximo mês",
                            tint = Dark
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                val weekdays = listOf(
                    "D", "S", "T", "Q", "Q", "S", "S"
                )

                Row(Modifier.fillMaxWidth()) {
                    weekdays.forEach { day ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                color = Muted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                val firstDay = visibleMonth.atDay(1)
                val offset = firstDay.dayOfWeek.value % 7
                val totalDays = visibleMonth.lengthOfMonth()
                val totalCells =
                    ((offset + totalDays + 6) / 7) * 7

                repeat(totalCells / 7) { week ->
                    Row(Modifier.fillMaxWidth()) {
                        repeat(7) { weekday ->
                            val number =
                                week * 7 + weekday - offset + 1

                            val day = if (number in 1..totalDays) {
                                visibleMonth.atDay(number)
                            } else {
                                null
                            }

                            val enabled =
                                day != null &&
                                        !day.isBefore(today) &&
                                        !day.isAfter(lastDate)

                            val active =
                                day != null && day == selected

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(47.dp)
                                    .padding(3.dp)
                                    .background(
                                        color = if (active) {
                                            Blue
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .then(
                                        if (enabled) {
                                            Modifier.clickable {
                                                onSelect(day!!)
                                            }
                                        } else {
                                            Modifier
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day?.dayOfMonth
                                        ?.toString()
                                        .orEmpty(),
                                    fontSize = 12.sp,
                                    color = when {
                                        active -> Color.White
                                        enabled -> Dark
                                        else -> Border
                                    },
                                    fontWeight = if (active) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Selecione uma data disponível no calendário.",
            fontSize = 12.sp,
            color = Muted
        )
    }
}

// =============================================
// ETAPA 4 — HORÁRIO E CONFIRMAÇÃO
// =============================================

@Composable
private fun TimeStep(
    service: Service?,
    vehicle: BookingVehicle?,
    date: LocalDate?,
    selected: String?,
    now: LocalDateTime,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        if (date != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = LightBlue,
                border = BorderStroke(1.dp, Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅",
                        fontSize = 24.sp
                    )

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "DATA SELECIONADA",
                            color = Blue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = date.format(DateLabel),
                            color = Dark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Pale
        ) {
            Column(Modifier.padding(14.dp)) {
                SummaryLine(
                    "Estabelecimento",
                    "Lava Jato Prime"
                )

                Spacer(Modifier.height(8.dp))

                SummaryLine(
                    "Serviço",
                    service?.name.orEmpty()
                )

                Spacer(Modifier.height(8.dp))

                SummaryLine(
                    "Veículo",
                    if (vehicle == null) {
                        ""
                    } else {
                        "${vehicle.marca} ${vehicle.modelo} · ${vehicle.placa}"
                    }
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Horários disponíveis",
            color = Dark,
            fontSize = 17.sp
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Selecione o melhor horário para você.",
            color = Muted,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(20.dp))

        BookingHours.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { time ->
                    val future =
                        date != null &&
                                LocalDateTime.of(
                                    date,
                                    LocalTime.parse(time, HourFormat)
                                ).isAfter(now)

                    val active = time == selected

                    Surface(
                        onClick = { onSelect(time) },
                        enabled = future,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(11.dp),
                        color = when {
                            active -> Blue
                            !future -> Color(0xFFF1F3F6)
                            else -> Color.White
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (active) Blue else Border
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = time,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = when {
                                    active -> Color.White
                                    !future -> Color.LightGray
                                    else -> Dark
                                }
                            )
                        }
                    }
                }

                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(9.dp))
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Horários desabilitados já passaram. A disponibilidade real do estabelecimento será integrada futuramente.",
            color = Muted,
            fontSize = 11.sp
        )
    }
}

// =============================================
// COMPONENTE AUXILIAR
// =============================================

@Composable
private fun SummaryLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Muted,
            fontSize = 11.sp
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = value,
            color = Dark,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}