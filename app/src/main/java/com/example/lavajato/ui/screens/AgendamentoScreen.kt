package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Tela 4 - Agendamento (etapa "Serviço" selecionada nesta versão).
 */
@Composable
fun AgendamentoScreen(
    onVoltarClick: () -> Unit,
    onProximoClick: () -> Unit
) {
    var selectedService by remember { mutableStateOf<Service?>(ServiceRepository.services.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltarClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = NavyDark)
            }
            Text(
                text = "Agendar Serviço",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        }

        // Indicador de etapas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { step ->
                val isActive = step.number == 1
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (isActive) YellowAccent else White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.number.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isActive) NavyDark else TextGraySecondary
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

        Text(
            text = "Escolha o serviço",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ServiceRepository.services) { service ->
                ServiceSelectableCard(
                    service = service,
                    selected = selectedService?.id == service.id,
                    onSelect = { selectedService = service }
                )
            }
        }

        Box(modifier = Modifier.padding(20.dp)) {
            AppButton(text = "Próximo", onClick = onProximoClick)
        }
    }
}
