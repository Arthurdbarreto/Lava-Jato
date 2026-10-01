package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.ui.components.AppButton
import com.example.lavajato.ui.components.AppTextField
import com.example.lavajato.ui.components.Logo
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.YellowAccent

/**
 * Tela 2 - Cadastro
 */
@Composable
fun CadastroScreen(
    onVoltarClick: () -> Unit,
    onEntrarClick: () -> Unit,
    onCadastrarClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = onVoltarClick, modifier = Modifier.size(40.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = NavyDark)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Logo(tint = NavyDark, iconSize = 52.dp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Crie sua conta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        Text(
            text = "Preencha os dados abaixo para começar.",
            fontSize = 13.sp,
            color = TextGraySecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppTextField(
            value = nome,
            onValueChange = { nome = it },
            placeholder = "Nome completo",
            leadingIcon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "E-mail",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = telefone,
            onValueChange = { telefone = it },
            placeholder = "Telefone",
            leadingIcon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = senha,
            onValueChange = { senha = it },
            placeholder = "Senha",
            leadingIcon = Icons.Default.Lock,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = confirmarSenha,
            onValueChange = { confirmarSenha = it },
            placeholder = "Confirmar senha",
            leadingIcon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(text = "Cadastrar", onClick = onCadastrarClick)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Já tem uma conta? ", color = TextGraySecondary, fontSize = 13.sp)
            Text(
                text = "Entrar",
                color = YellowAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onEntrarClick)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
