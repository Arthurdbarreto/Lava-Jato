package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White
import com.example.lavajato.ui.theme.YellowAccent

private data class Review(val author: String, val rating: Int, val comment: String)

private val mockReviews = listOf(
    Review("Carlos Silva", 5, "Serviço excelente! O carro ficou como novo."),
    Review("Mariana Souza", 5, "Atendimento impecável e muito rápido."),
    Review("Roberto Alves", 4, "Muito bom, recomendo a higienização interna.")
)

@Composable
fun AvaliacoesScreen(onVoltarClick: () -> Unit) {
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
            IconButton(onClick = onVoltarClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = NavyDark)
            }
            Text(text = "Avaliações", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockReviews.size) { index ->
                val review = mockReviews[index]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = review.author, fontWeight = FontWeight.Bold, color = NavyDark)
                        Row {
                            repeat(review.rating) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = YellowAccent
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = review.comment, fontSize = 13.sp, color = TextGraySecondary)
                }
            }
        }
    }
}
