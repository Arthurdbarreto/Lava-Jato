package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.UserRepository
import com.example.lavajato.ui.components.BottomNavigationBar
import com.example.lavajato.ui.components.BottomTab

private val HomeNavy = Color(0xFF061C36)
private val HomeBlue = Color(0xFF1677FF)
private val HomeMuted = Color(0xFF8393A9)
private val HomeBorder = Color(0xFFE0E8F2)
private val HomeBackground = Color(0xFFF8FAFD)

private data class LavaJatoDemo(
    val nome: String,
    val endereco: String,
    val avaliacao: String,
    val distancia: String
)

private val lavaJatosDemo = listOf(
    LavaJatoDemo(
        nome = "Lava Jato Prime",
        endereco = "Endereço demonstrativo",
        avaliacao = "4,8",
        distancia = "Distância indisponível"
    ),
    LavaJatoDemo(
        nome = "Auto Clean",
        endereco = "Endereço demonstrativo",
        avaliacao = "4,7",
        distancia = "Distância indisponível"
    )
)

@Composable
fun HomeScreen(
    onAgendarServicoClick: () -> Unit,
    onMeusAgendamentosClick: () -> Unit,
    onAvaliacoesClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    var busca by remember { mutableStateOf("") }
    var mostrarAvisoAutoClean by remember { mutableStateOf(false) }

    val primeiroNome = UserRepository.registeredName
        .trim()
        .substringBefore(" ")
        .ifBlank { "Cliente" }

    val iniciais = UserRepository.registeredName
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "US" }

    val estabelecimentos = lavaJatosDemo.filter {
        it.nome.contains(busca, ignoreCase = true)
    }

    Scaffold(
        containerColor = HomeBackground,
        bottomBar = {
            BottomNavigationBar(
                selected = BottomTab.INICIO,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab.INICIO -> Unit
                        BottomTab.AGENDAMENTOS -> onMeusAgendamentosClick()
                        BottomTab.PERFIL -> onPerfilClick()
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 26.dp,
                                bottomEnd = 26.dp
                            )
                        )
                        .background(HomeNavy)
                        .padding(
                            start = 22.dp,
                            end = 22.dp,
                            top = 24.dp,
                            bottom = 32.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(HomeBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(21.dp)
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Lava",
                                color = Color.White,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+",
                                color = HomeBlue,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(0xFFE0EEFF),
                                    CircleShape
                                )
                                .clickable { onPerfilClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = iniciais,
                                color = HomeNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(35.dp))

                    Text(
                        text = "Olá, $primeiroNome!",
                        fontSize = 14.sp,
                        color = Color(0xFFB9D9FF)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Encontre o melhor cuidado para o seu carro.",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    Spacer(Modifier.height(22.dp))

                    OutlinedTextField(
                        value = busca,
                        onValueChange = { busca = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                "Buscar lava-jato ou serviço",
                                color = HomeMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = HomeBlue
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = HomeBlue,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = HomeNavy,
                            unfocusedTextColor = HomeNavy
                        )
                    )
                }
            }

            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lava-jatos próximos",
                            fontSize = 18.sp,
                            color = HomeNavy,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Ver todos",
                            fontSize = 12.sp,
                            color = HomeBlue,
                            modifier = Modifier.clickable {
                                onAgendarServicoClick()
                            }
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    if (estabelecimentos.isEmpty()) {
                        Text(
                            text = "Nenhum lava-jato demonstrativo encontrado.",
                            color = HomeMuted,
                            modifier = Modifier.padding(horizontal = 22.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 22.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            estabelecimentos.forEach { lavaJato ->
                                LavaJatoCard(
                                    lavaJato = lavaJato,
                                    onClick = {
                                        if (lavaJato.nome == "Lava Jato Prime") {
                                            onAgendarServicoClick()
                                        } else {
                                            mostrarAvisoAutoClean = true
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Estabelecimentos ilustrativos — localização não ativada",
                        fontSize = 10.sp,
                        color = HomeMuted,
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp)
                ) {
                    Text(
                        text = "Serviços populares",
                        fontSize = 18.sp,
                        color = HomeNavy,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ServicoCard(
                            titulo = "Lavagem Simples",
                            preco = "A partir de R$ 30,00",
                            modifier = Modifier.weight(1f),
                            onClick = onAgendarServicoClick
                        )

                        ServicoCard(
                            titulo = "Lavagem Completa",
                            preco = "A partir de R$ 60,00",
                            modifier = Modifier.weight(1f),
                            onClick = onAgendarServicoClick
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ServicoCard(
                            titulo = "Higienização Interna",
                            preco = "Preço demonstrativo",
                            modifier = Modifier.weight(1f),
                            onClick = onAgendarServicoClick
                        )

                        ServicoCard(
                            titulo = "Polimento",
                            preco = "Preço demonstrativo",
                            modifier = Modifier.weight(1f),
                            onClick = onAgendarServicoClick
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Valores ilustrativos, sujeitos à definição pelos estabelecimentos.",
                        color = HomeMuted,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    TextButton(
                        onClick = onAvaliacoesClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = HomeBlue
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Ver avaliações",
                            color = HomeBlue
                        )
                    }
                }
            }
        }
    }

    if (mostrarAvisoAutoClean) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoAutoClean = false },
            title = { Text("Auto Clean") },
            text = {
                Text(
                    "Os agendamentos do Auto Clean ainda não estão disponíveis. " +
                            "Por enquanto, você pode realizar agendamentos no Lava Jato Prime."
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarAvisoAutoClean = false }) {
                    Text("Entendi")
                }
            }
        )
    }
}

@Composable
private fun LavaJatoCard(
    lavaJato: LavaJatoDemo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(244.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            HomeBorder
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .background(Color(0xFFE7F0FC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalCarWash,
                    contentDescription = "Imagem ilustrativa de lava-jato",
                    tint = HomeBlue,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lavaJato.nome,
                        color = HomeNavy,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF5AE23),
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = lavaJato.avaliacao,
                        color = HomeNavy,
                        fontSize = 11.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = HomeMuted,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = lavaJato.endereco,
                        fontSize = 11.sp,
                        color = HomeMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFE5F7F0),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Text(
                            text = "● Aberto (demo)",
                            fontSize = 10.sp,
                            color = Color(0xFF008A60)
                        )
                    }

                    Text(
                        text = lavaJato.distancia,
                        fontSize = 10.sp,
                        color = HomeMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ServicoCard(
    titulo: String,
    preco: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(122.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            HomeBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(
                        Color(0xFFEAF3FF),
                        RoundedCornerShape(11.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = HomeBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = preco,
                    fontSize = 10.sp,
                    color = HomeMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
