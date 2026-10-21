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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.UserRepository
import com.example.lavajato.ui.components.AppButton
import com.example.lavajato.ui.components.AppTextField
import com.example.lavajato.ui.components.Logo
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.NavyDarker
import com.example.lavajato.ui.theme.YellowAccent

@Composable
fun LoginScreen(
    onEntrarClick: () -> Unit,
    onCadastreSeClick: () -> Unit
) {
    // Carrega a sugestão do último e-mail cadastrado/usado
    var emailOrPhone by remember { mutableStateOf(UserRepository.lastSavedEmail) }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyDarker, NavyDark, NavyDarker)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
        ) {
            Spacer(modifier = Modifier.height(90.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Logo(tint = Color.White, iconSize = 64.dp)
            }

            Spacer(modifier = Modifier.height(48.dp))

            AppTextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                placeholder = "E-mail ou telefone",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                fieldBackground = Color.White.copy(alpha = 0.06f),
                borderColor = Color.White.copy(alpha = 0.25f),
                textColor = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Senha",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                fieldBackground = Color.White.copy(alpha = 0.06f),
                borderColor = Color.White.copy(alpha = 0.25f),
                textColor = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                text = "Entrar",
                onClick = {
                    if (emailOrPhone.isNotBlank()) {
                        UserRepository.lastSavedEmail = emailOrPhone
                    }
                    onEntrarClick()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esqueceu sua senha?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Não tem uma conta? ",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Cadastre-se",
                    color = YellowAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable(onClick = onCadastreSeClick)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}