package com.example.lavajato.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.Appointment
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White
import com.example.lavajato.ui.theme.YellowAccent

@Composable
fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appointment.serviceName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NavyDark
            )
            Box(
                modifier = Modifier
                    .background(YellowAccent, RoundedCornerShape(8.dp))
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

        Text(
            text = "${appointment.date} às ${appointment.time}",
            fontSize = 13.sp,
            color = TextGraySecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = appointment.address,
            fontSize = 12.sp,
            color = TextGraySecondary
        )
    }
}