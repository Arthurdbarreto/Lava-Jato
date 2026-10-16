package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.UserRepository
import com.example.lavajato.ui.components.BottomNavigationBar
import com.example.lavajato.ui.components.BottomTab
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White
import com.example.lavajato.ui.theme.YellowAccent

@Composable
fun HomeScreen(
    onAgendarServicoClick: () -> Unit,
    onMeusAgendamentosClick: () -> Unit,
    onAvaliacoesClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.INICIO) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = selectedTab,
                onSelect = { tab ->
                    selectedTab = tab
                    when (tab) {
                        BottomTab.INICIO -> {}
                        BottomTab.AGENDAMENTOS -> onMeusAgendamentosClick()
                        BottomTab.PERFIL -> onPerfilClick()
                    }
                }
            )
        },
        containerColor = LightGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header / Cabeçalho com cor de fundo
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(NavyDark)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Olá, ${UserRepository.registeredName} 👋",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "O que seu carro precisa hoje?",
                                    fontSize = 14.sp,
                                    color = White.copy(alpha = 0.8f)
                                )
                            }

                            // Avatar com clique no perfil
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(White.copy(alpha = 0.15f), CircleShape)
                                    .clickable { onPerfilClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Card com informações do carro cadastrado
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Carro",
                                tint = YellowAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Veículo Cadastrado: ",
                                fontSize = 13.sp,
                                color = White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = UserRepository.registeredCarModel.ifBlank { "Não informado" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = YellowAccent
                            )
                        }
                    }
                }
            }

            // Opções do Menu principal
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Serviços & Atalhos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    MenuOptionCard(
                        title = "Agendar Serviço",
                        description = "Escolha lavagem, higienização ou polimento",
                        icon = Icons.Default.LocalCarWash,
                        badgeText = "Rápido",
                        onClick = onAgendarServicoClick
                    )

                    MenuOptionCard(
                        title = "Meus Agendamentos",
                        description = "Acompanhe seus horários marcados",
                        icon = Icons.Default.CalendarMonth,
                        onClick = onMeusAgendamentosClick
                    )

                    MenuOptionCard(
                        title = "Avaliações",
                        description = "Confira a opinião e notas dos clientes",
                        icon = Icons.Default.Star,
                        onClick = onAvaliacoesClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(YellowAccent, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NavyDark,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )

                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(NavyDark, RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextGraySecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = TextGraySecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}