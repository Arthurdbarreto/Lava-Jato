package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private data class Step(val number: Int, val label: String)

private val steps = listOf(
    Step(1, "Serviço"),
    Step(2, "Data"),
    Step(3, "Horário"),
    Step(4, "Confirmação")
)

@Composable
fun AgendamentoScreen(
    onVoltarClick: () -> Unit,
    onConcluidoClick: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedService by remember { mutableStateOf<Service?>(ServiceRepository.services.firstOrNull()) }
    var selectedDate by remember { mutableStateOf("15 Out") }
    var selectedTime by remember { mutableStateOf("14:00") }

    val availableDates = listOf("15 Out", "16 Out", "17 Out", "18 Out", "19 Out")
    val availableTimes = listOf("08:00", "09:30", "11:00", "14:00", "15:30", "17:00")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentStep > 1) currentStep-- else onVoltarClick()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = NavyDark)
            }
            Text(
                text = "Agendar Serviço",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { step ->
                val isActive = step.number == currentStep
                val isDone = step.number < currentStep
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (isActive || isDone) YellowAccent else White,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.number.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyDark
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = step.label,
                        fontSize = 10.sp,
                        color = if (isActive) NavyDark else TextGraySecondary,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                1 -> StepService(selectedService) { selectedService = it }
                2 -> StepDate(availableDates, selectedDate) { selectedDate = it }
                3 -> StepTime(availableTimes, selectedTime) { selectedTime = it }
                4 -> StepConfirmation(selectedService, selectedDate, selectedTime)
            }
        }

        Box(modifier = Modifier.padding(20.dp)) {
            AppButton(
                text = if (currentStep == 4) "Confirmar Agendamento" else "Próximo",
                onClick = {
                    if (currentStep < 4) {
                        currentStep++
                    } else {
                        selectedService?.let {
                            AppointmentRepository.addAppointment(
                                serviceName = it.name,
                                date = selectedDate,
                                time = selectedTime
                            )
                        }
                        onConcluidoClick()
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
private fun StepDate(dates: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Selecione a Data", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dates) { date ->
                val isSelected = date == selected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) NavyDark else White, RoundedCornerShape(12.dp))
                        .clickable { onSelect(date) }
                        .padding(16.dp)
                ) {
                    Text(
                        text = date,
                        color = if (isSelected) White else NavyDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepTime(times: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Selecione o Horário", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(times) { time ->
                val isSelected = time == selected
                Box(
                    modifier = Modifier
                        .background(if (isSelected) NavyDark else White, RoundedCornerShape(12.dp))
                        .clickable { onSelect(time) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = time,
                        color = if (isSelected) White else NavyDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepConfirmation(service: Service?, date: String, time: String) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .background(White, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = YellowAccent,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Resumo do Agendamento", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Serviço: ${service?.name ?: ""}", fontSize = 14.sp)
        Text("Valor: R$ ${service?.price ?: 0.0}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text("Data: $date", fontSize = 14.sp)
        Text("Horário: $time", fontSize = 14.sp)
    }
}