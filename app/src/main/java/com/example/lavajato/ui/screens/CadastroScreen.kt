
package com.example.lavajato.ui.screens

import android.util.Patterns
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
import androidx.compose.ui.graphics.Color
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

    var mensagemErro by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    White,
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Criar Conta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Nome completo
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        mensagemErro = ""
                    },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // E-mail
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        mensagemErro = ""
                    },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Modelo do veículo
                OutlinedTextField(
                    value = modeloCarro,
                    onValueChange = {
                        modeloCarro = it
                        mensagemErro = ""
                    },
                    label = {
                        Text("Modelo do Veículo (ex: Civic Sedan)")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Senha
                OutlinedTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        mensagemErro = ""
                    },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mensagem de validação
                if (mensagemErro.isNotEmpty()) {
                    Text(
                        text = mensagemErro,
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                }

                // Botão cadastrar
                AppButton(
                    text = "Cadastrar",
                    onClick = {
                        val nomeLimpo = nome.trim()
                        val emailLimpo = email.trim()
                        val carroLimpo = modeloCarro.trim()

                        mensagemErro = when {
                            nomeLimpo.isBlank() ||
                                    emailLimpo.isBlank() ||
                                    carroLimpo.isBlank() ||
                                    senha.isBlank() -> {
                                "Preencha todos os campos obrigatórios."
                            }

                            !Patterns.EMAIL_ADDRESS
                                .matcher(emailLimpo)
                                .matches() -> {
                                "Digite um e-mail válido. Exemplo: usuario@gmail.com"
                            }

                            senha.length < 6 -> {
                                "A senha deve ter pelo menos 6 caracteres."
                            }

                            else -> ""
                        }

                        if (mensagemErro.isEmpty()) {
                            UserRepository.registerUser(
                                name = nomeLimpo,
                                email = emailLimpo,
                                carModel = carroLimpo
                            )

                            onCadastrarClick()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Link para login
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
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
                        modifier = Modifier.clickable {
                            onJaTenhoContaClick()
                        }
                    )
                }
            }
        }
    }
}
