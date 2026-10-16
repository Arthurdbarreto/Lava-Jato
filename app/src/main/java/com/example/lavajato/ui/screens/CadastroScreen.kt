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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.UserRepository
import com.example.lavajato.ui.components.AppButton
import com.example.lavajato.ui.theme.LightGray
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.White

@Composable
fun CadastroScreen(
    onCadastrarClick: () -> Unit,
    onJaTenhoContaClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var modeloCarro by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Criar Conta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = modeloCarro,
                    onValueChange = { modeloCarro = it },
                    label = { Text("Modelo do Veículo (ex: Civic Sedan)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppButton(
                    text = "Cadastrar",
                    onClick = {
                        if (nome.isNotBlank()) {
                            UserRepository.registerUser(
                                name = nome.trim(),
                                email = email.trim(),
                                carModel = modeloCarro.ifBlank { "Não informado" }
                            )
                        }
                        onCadastrarClick()
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row {
                    Text(
                        text = "Já possui uma conta? ",
                        color = TextGraySecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Entrar",
                        color = NavyDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onJaTenhoContaClick() }
                    )
                }
            }
        }
    }
}