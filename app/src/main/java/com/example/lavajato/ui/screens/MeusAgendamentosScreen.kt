
package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.Appointment
import com.example.lavajato.data.model.AppointmentRepository
import com.example.lavajato.data.model.AppointmentStatus
import com.example.lavajato.ui.components.BottomNavigationBar
import com.example.lavajato.ui.components.BottomTab

private val Navy = Color(0xFF061C36)
private val Blue = Color(0xFF1677FF)
private val Background = Color(0xFFF5F7FB)
private val SecondaryText = Color(0xFF77869B)

private enum class AgendamentosTab {
    PROXIMOS,
    CONCLUIDOS
}

@Composable
fun MeusAgendamentosScreen(
    onVoltarClick: () -> Unit,
    onInicioClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    var selectedTab by remember {
        mutableStateOf(AgendamentosTab.PROXIMOS)
    }

    var loadError by remember { mutableStateOf<String?>(null) }
    DisposableEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        AppointmentRepository.observe { loadError = it }
        onDispose { AppointmentRepository.stopObserving() }
    }

    val filteredList = AppointmentRepository.list.filter { appointment ->
        when (selectedTab) {
            AgendamentosTab.PROXIMOS ->
                appointment.status == AppointmentStatus.CONFIRMADO
            AgendamentosTab.CONCLUIDOS ->
                appointment.status != AppointmentStatus.CONFIRMADO
        }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BottomNavigationBar(
                selected = BottomTab.AGENDAMENTOS,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab.INICIO -> onInicioClick()
                        BottomTab.AGENDAMENTOS -> Unit
                        BottomTab.PERFIL -> onPerfilClick()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy)
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 24.dp,
                        bottom = 28.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Blue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Text(
                        text = "Lava+",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Meus agendamentos",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Acompanhe seus serviços e agendamentos.",
                    color = Color(0xFFB6C7DB),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF132F4F),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AgendamentoTabButton(
                        text = "Próximos",
                        selected = selectedTab == AgendamentosTab.PROXIMOS,
                        onClick = {
                            selectedTab = AgendamentosTab.PROXIMOS
                        },
                        modifier = Modifier.weight(1f)
                    )

                    AgendamentoTabButton(
                        text = "Concluídos",
                        selected = selectedTab == AgendamentosTab.CONCLUIDOS,
                        onClick = {
                            selectedTab = AgendamentosTab.CONCLUIDOS
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (loadError != null) {
                Text("Erro ao carregar agendamentos: ${loadError}", color = Color.Red, modifier = Modifier.padding(20.dp))
            } else if (filteredList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .background(
                                Color(0xFFE7F0FF),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Blue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (
                            selectedTab == AgendamentosTab.PROXIMOS
                        ) {
                            "Nenhum agendamento próximo"
                        } else {
                            "Nenhum agendamento concluído"
                        },
                        color = Navy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (
                            selectedTab == AgendamentosTab.PROXIMOS
                        ) {
                            "Seus próximos serviços aparecerão aqui."
                        } else {
                            "Seu histórico de serviços aparecerá aqui."
                        },
                        color = SecondaryText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 22.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList) { appointment ->
                        AgendamentoCard(
                            appointment = appointment,
                            onStatusChange = { newStatus ->
                                AppointmentRepository.updateStatus(
                                    appointment.id,
                                    newStatus
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendamentoTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) Color.White
                else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Navy
            else Color(0xFFCFD9E7),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun AgendamentoCard(
    appointment: Appointment,
    onStatusChange: (AppointmentStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(18.dp)
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = appointment.serviceName,
                color = Navy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        if (
                            appointment.status ==
                            AppointmentStatus.CONFIRMADO
                        ) {
                            Color(0xFFE7F0FF)
                        } else {
                            Color(0xFFE5F7EC)
                        },
                        RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
            ) {
                Text(
                    text = appointment.status.label,
                    color = if (
                        appointment.status ==
                        AppointmentStatus.CONFIRMADO
                    ) {
                        Blue
                    } else {
                        Color(0xFF198754)
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(9.dp))

            Text(
                text = appointment.date,
                color = SecondaryText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = appointment.time,
                color = SecondaryText,
                fontSize = 13.sp
            )
        }

        if (appointment.vehicleDescription.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Veículo: ${appointment.vehicleDescription} • ${appointment.vehiclePlate}", color = SecondaryText, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(13.dp))

        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(9.dp))

            Text(
                text = appointment.address,
                color = SecondaryText,
                fontSize = 12.sp
            )
        }

        if (appointment.status == AppointmentStatus.CONFIRMADO) {
            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(
                color = Color(0xFFEDF0F5)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Cancelar agendamento",
                color = Blue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.clickable {
                    onStatusChange(AppointmentStatus.CANCELADO)
                }
            )
        }
    }
}

