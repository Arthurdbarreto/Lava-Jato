package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.ServiceRepository
import com.example.lavajato.ui.components.BottomNavigationBar
import com.example.lavajato.ui.components.BottomTab
import com.example.lavajato.ui.components.HomeShortcutCard
import com.example.lavajato.ui.components.LogoCompact
import com.example.lavajato.ui.components.ServiceSummaryCard
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White
import com.example.lavajato.ui.theme.YellowAccent

/**
 * Tela 3 - Home
 */
@Composable
fun HomeScreen(
    onAgendarServicoClick: () -> Unit,
    onMeusAgendamentosClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.INICIO) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = selectedTab,
                onSelect = { tab ->
                    selectedTab = tab
                    if (tab == BottomTab.AGENDAMENTOS) onMeusAgendamentosClick()
                }
            )
        },
        containerColor = LightGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                // Topo azul-marinho
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyDark)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LogoCompact(tint = White)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(White.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil", tint = White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Olá, Arthur!", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Seu carro sempre em boas mãos.",
                        color = White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }
            }

            item {
                // Banner "Carro limpo é outra história"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp)
                        .height(150.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NavyDark, NavyDark.copy(alpha = 0.85f))
                            ),
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "CARRO LIMPO", color = White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "É OUTRA", color = White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "HISTÓRIA!", color = YellowAccent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Qualidade, praticidade\ne o melhor atendimento.",
                            color = White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(110.dp)
                            .padding(end = 8.dp)
                    )
                }
            }

            item {
                // Atalhos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeShortcutCard(
                        icon = Icons.Default.DirectionsCar,
                        label = "Agendar Serviço",
                        onClick = onAgendarServicoClick,
                        modifier = Modifier.weight(1f)
                    )
                    HomeShortcutCard(
                        icon = Icons.Default.CalendarMonth,
                        label = "Meus Agendamentos",
                        onClick = onMeusAgendamentosClick,
                        modifier = Modifier.weight(1f)
                    )
                    HomeShortcutCard(
                        icon = Icons.Default.Star,
                        label = "Avaliações",
                        onClick = { /* Avaliações - não implementado nesta etapa */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Nossos Serviços",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ServiceRepository.services.forEach { service ->
                        ServiceSummaryCard(service = service, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
