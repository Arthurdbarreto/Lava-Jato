package com.example.lavajato.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa um serviço oferecido pelo Lava Jato.
 * Dados fictícios, sem integração com backend.
 */
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val icon: ImageVector
)

object ServiceRepository {
    val services = listOf(
        Service(
            id = "lavagem_simples",
            name = "Lavagem Simples",
            description = "Lavagem externa e secagem.",
            price = "R$ 40,00",
            icon = Icons.Default.DirectionsCar
        ),
        Service(
            id = "higienizacao_interna",
            name = "Higienização Interna",
            description = "Limpeza completa do interior.",
            price = "R$ 80,00",
            icon = Icons.Default.LocalCarWash
        ),
        Service(
            id = "limpeza_geral",
            name = "Limpeza Geral",
            description = "Lavagem + higienização interna e pretinho nos pneus.",
            price = "R$ 120,00",
            icon = Icons.Default.AutoAwesome
        )
    )
}
