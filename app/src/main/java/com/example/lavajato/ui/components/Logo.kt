package com.example.lavajato.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.ui.theme.YellowAccent

/**
 * Logo reutilizável "LAVA JATO" com destaque amarelo em "JATO"
 * e ícone de carro/água representando a marca.
 */
@Composable
fun Logo(
    tint: Color = Color.White,
    iconSize: androidx.compose.ui.unit.Dp = 56.dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = YellowAccent,
                modifier = Modifier.size(iconSize * 0.35f)
            )
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Logo Lava Jato",
                tint = YellowAccent,
                modifier = Modifier.size(iconSize)
            )
        }
        Row {
            Text(
                text = "LAVA ",
                color = tint,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "JATO",
                color = YellowAccent,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "SEU CARRO SEMPRE NOVO",
            color = tint,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

/** Versão compacta usada em topbars (Home, Cadastro pequeno, etc). */
@Composable
fun LogoCompact(tint: Color = Color.White) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = "Logo Lava Jato",
            tint = YellowAccent,
            modifier = Modifier.size(26.dp)
        )
        Row {
            Text(text = "LAVA ", color = tint, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "JATO", color = YellowAccent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
