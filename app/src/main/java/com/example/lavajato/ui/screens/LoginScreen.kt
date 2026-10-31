
package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

private val LoginNavy = Color(0xFF061C36)
private val LoginBlue = Color(0xFF1677FF)
private val LoginSecondary = Color(0xFF77869B)
private val LoginBorder = Color(0xFFDCE5F1)

@Composable
fun LoginScreen(
    onEntrarClick: () -> Unit,
    onCadastreSeClick: () -> Unit
) {
    var email by remember {
        mutableStateOf(UserRepository.lastSavedEmail)
    }
    var senha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }
    var mensagemSucesso by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }
    var recuperandoSenha by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabeçalho azul-marinho
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp)
                .background(LoginNavy)
        ) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .offset(x = 240.dp, y = (-75).dp)
                    .background(
                        Color(0xFF0B3267),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 28.dp, top = 38.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(LoginBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Lava+",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Seu carro, do seu jeito.",
                    color = Color(0xFFD0DCEB),
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = Color(0xFF45617D),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 34.dp, bottom = 35.dp)
                    .size(75.dp)
            )
        }

        // Formulário branco sobre o cabeçalho
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 25.dp
                    )
                )
                .background(Color.White)
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 28.dp,
                    bottom = 28.dp
                )
        ) {
            Text(
                text = "BEM-VINDO DE VOLTA",
                color = LoginBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Entre na sua conta",
                color = LoginNavy,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Cuidar do seu carro ficou mais simples.",
                color = LoginSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "E-mail",
                color = LoginNavy,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    mensagemErro = ""
                    mensagemSucesso = ""
                },
                placeholder = {
                    Text(
                        text = "seuemail@exemplo.com",
                        color = Color(0xFF98ABC4),
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LoginBlue,
                    unfocusedBorderColor = LoginBorder,
                    focusedContainerColor = Color(0xFFFAFBFD),
                    unfocusedContainerColor = Color(0xFFFAFBFD)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Senha",
                color = LoginNavy,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = {
                    senha = it
                    mensagemErro = ""
                },
                placeholder = {
                    Text(
                        text = "Sua senha",
                        color = Color(0xFF98ABC4),
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                visualTransformation =
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LoginBlue,
                    unfocusedBorderColor = LoginBorder,
                    focusedContainerColor = Color(0xFFFAFBFD),
                    unfocusedContainerColor = Color(0xFFFAFBFD)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (recuperandoSenha) {
                    "Enviando..."
                } else {
                    "Esqueci minha senha"
                },
                color = LoginBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(
                        enabled = !recuperandoSenha && !carregando
                    ) {
                        val emailRecuperacao = email.trim()

                        if (emailRecuperacao.isBlank()) {
                            mensagemErro =
                                "Informe seu e-mail para recuperar a senha."
                        } else {
                            recuperandoSenha = true
                            mensagemErro = ""
                            mensagemSucesso = ""

                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(emailRecuperacao)
                                .addOnCompleteListener { task ->
                                    recuperandoSenha = false

                                    if (task.isSuccessful) {
                                        mensagemSucesso =
                                            "Se o e-mail estiver cadastrado, verifique sua caixa de entrada."
                                    } else {
                                        mensagemErro =
                                            "Não foi possível enviar o e-mail. Verifique o endereço informado."
                                    }
                                }
                        }
                    }
            )

            if (mensagemErro.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = mensagemErro,
                    color = Color(0xFFD63C45),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (mensagemSucesso.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = mensagemSucesso,
                    color = Color(0xFF198754),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    if (!carregando) {
                        val emailLogin = email.trim()

                        when {
                            emailLogin.isBlank() ||
                                    senha.isBlank() -> {
                                mensagemErro =
                                    "Preencha o e-mail e a senha."
                            }

                            else -> {
                                carregando = true
                                mensagemErro = ""
                                mensagemSucesso = ""

                                FirebaseAuth.getInstance()
                                    .signInWithEmailAndPassword(
                                        emailLogin,
                                        senha
                                    )
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val usuario =
                                                FirebaseAuth.getInstance()
                                                    .currentUser

                                            if (usuario == null) {
                                                carregando = false
                                                mensagemErro =
                                                    "Não foi possível identificar o usuário."
                                            } else {
                                                FirebaseFirestore
                                                    .getInstance()
                                                    .collection("usuarios")
                                                    .document(usuario.uid)
                                                    .get()
                                                    .addOnSuccessListener {
                                                            documento ->

                                                        if (documento.exists()) {
                                                            val nome =
                                                                documento.getString(
                                                                    "nome"
                                                                ).orEmpty()

                                                            val emailSalvo =
                                                                documento.getString(
                                                                    "email"
                                                                ) ?: usuario.email.orEmpty()

                                                            // Compatibilidade com
                                                            // contas antigas.
                                                            val modeloCarro =
                                                                documento.getString(
                                                                    "modeloCarro"
                                                                ).orEmpty()

                                                            UserRepository
                                                                .registerUser(
                                                                    name = nome,
                                                                    email = emailSalvo,
                                                                    carModel = modeloCarro
                                                                )

                                                            carregando = false
                                                            onEntrarClick()
                                                        } else {
                                                            carregando = false
                                                            UserRepository.clearUser()
                                                            mensagemErro =
                                                                "Sua conta existe, mas o perfil ainda não foi cadastrado."
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        carregando = false
                                                        mensagemErro =
                                                            "Não foi possível carregar seu perfil. Tente novamente."
                                                    }
                                            }
                                        } else {
                                            carregando = false

                                            mensagemErro =
                                                when (task.exception) {
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
                },
                enabled = !carregando && !recuperandoSenha,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginBlue
                )
            ) {
                if (carregando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Entrar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ainda não tem uma conta? ",
                    color = LoginSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "Cadastre-se",
                    color = LoginBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        onCadastreSeClick()
                    }
                )
            }
        }
    }
}
