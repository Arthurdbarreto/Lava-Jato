
package com.example.lavajato.ui.screens

import com.google.firebase.firestore.FirebaseFirestore
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(
    onEntrarClick: () -> Unit,
    onCadastreSeClick: () -> Unit
) {
    var emailOrPhone by remember {
        mutableStateOf(UserRepository.lastSavedEmail)
    }
    var password by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

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
                .padding(horizontal = 28.dp)
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
                onValueChange = {
                    emailOrPhone = it
                    mensagemErro = ""
                },
                placeholder = "E-mail",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                fieldBackground = Color.White.copy(alpha = 0.06f),
                borderColor = Color.White.copy(alpha = 0.25f),
                textColor = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensagemErro = ""
                },
                placeholder = "Senha",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                fieldBackground = Color.White.copy(alpha = 0.06f),
                borderColor = Color.White.copy(alpha = 0.25f),
                textColor = Color.White
            )

            if (mensagemErro.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = mensagemErro,
                    color = Color(0xFFFF7777),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                text = if (carregando) "Entrando..." else "Entrar",
                onClick = {
                    if (!carregando) {
                        val email = emailOrPhone.trim()

                        when {
                            email.isBlank() || password.isBlank() -> {
                                mensagemErro = "Preencha o e-mail e a senha."
                            }

                            else -> {
                                carregando = true
                                mensagemErro = ""

                                FirebaseAuth.getInstance()
                                    .signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        carregando = false


                                        if (task.isSuccessful) {
                                            val usuario = FirebaseAuth.getInstance().currentUser

                                            if (usuario == null) {
                                                mensagemErro = "Não foi possível identificar o usuário."
                                            } else {
                                                FirebaseFirestore.getInstance()
                                                    .collection("usuarios")
                                                    .document(usuario.uid)
                                                    .get()
                                                    .addOnSuccessListener { documento ->
                                                        if (documento.exists()) {
                                                            val nome = documento.getString("nome").orEmpty()
                                                            val email = documento.getString("email")
                                                                ?: usuario.email.orEmpty()
                                                            val modeloCarro =
                                                                documento.getString("modeloCarro").orEmpty()

                                                            UserRepository.registerUser(
                                                                name = nome,
                                                                email = email,
                                                                carModel = modeloCarro
                                                            )

                                                            onEntrarClick()
                                                        } else {
                                                            UserRepository.clearUser()
                                                            mensagemErro =
                                                                "Sua conta existe, mas o perfil ainda não foi cadastrado."
                                                        }
                                                        carregando = false
                                                    }
                                                    .addOnFailureListener {
                                                        carregando = false
                                                        mensagemErro =
                                                            "Não foi possível carregar seu perfil. Tente novamente."
                                                    }
                                            }
                                        } else {

                                            mensagemErro = when (task.exception) {
                                                is FirebaseAuthInvalidUserException,
                                                is FirebaseAuthInvalidCredentialsException ->
                                                    "E-mail ou senha inválidos."

                                                else ->
                                                    "Não foi possível entrar. Verifique sua conexão e tente novamente."
                                            }
                                        }
                                    }
                            }
                        }
                    }
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
                    modifier = Modifier.clickable(
                        onClick = onCadastreSeClick
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
