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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.Appointment
import com.example.lavajato.data.model.AppointmentRepository
import com.example.lavajato.data.model.AppointmentStatus
import com.example.lavajato.ui.components.BottomNavigationBar
import com.example.lavajato.ui.components.BottomTab
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White
import com.example.lavajato.ui.theme.YellowAccent

private enum class AgendamentosTab { PROXIMOS, HISTORICO }

@Composable
fun MeusAgendamentosScreen(
    onVoltarClick: () -> Unit,
    onInicioClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AgendamentosTab.PROXIMOS) }
    var selectedBottomTab by remember { mutableStateOf(BottomTab.AGENDAMENTOS) }

    val filteredList = AppointmentRepository.list.filter {
        if (selectedTab == AgendamentosTab.PROXIMOS) {
            it.status == AppointmentStatus.CONFIRMADO
        } else {
            it.status != AppointmentStatus.CONFIRMADO
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = selectedBottomTab,
                onSelect = { tab ->
                    selectedBottomTab = tab
                    if (tab == BottomTab.INICIO) onInicioClick()
                }
            )
        },
        containerColor = LightGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVoltarClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = NavyDark)
                }
                Text(
                    text = "Meus Agendamentos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .background(White, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabItem(
                    text = "Próximos",
                    selected = selectedTab == AgendamentosTab.PROXIMOS,
                    onClick = { selectedTab = AgendamentosTab.PROXIMOS },
                    modifier = Modifier.weight(1f)
                )
                TabItem(
                    text = "Histórico",
                    selected = selectedTab == AgendamentosTab.HISTORICO,
                    onClick = { selectedTab = AgendamentosTab.HISTORICO },
                    modifier = Modifier.weight(1f)
                )
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum agendamento encontrado.",
                        color = TextGraySecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { appointment ->
                        AppointmentCardCustom(
                            appointment = appointment,
                            onStatusChange = { newStatus ->
                                AppointmentRepository.updateStatus(appointment.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentCardCustom(
    appointment: Appointment,
    onStatusChange: (AppointmentStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = appointment.serviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
            Box(
                modifier = Modifier
                    .background(
                        if (appointment.status == AppointmentStatus.CONFIRMADO) YellowAccent else LightGray,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = appointment.status.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "${appointment.date} às ${appointment.time}", fontSize = 13.sp, color = TextGraySecondary)
        Text(text = appointment.address, fontSize = 12.sp, color = TextGraySecondary)

        if (appointment.status == AppointmentStatus.CONFIRMADO) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[ Marcar como Finalizado ]",
                    fontSize = 12.sp,
                    color = NavyDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onStatusChange(AppointmentStatus.FINALIZADO) }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(if (selected) NavyDark else White, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) White else TextGraySecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}