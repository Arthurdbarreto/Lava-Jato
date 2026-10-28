
package com.example.lavajato.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.CadastroValidator
import com.example.lavajato.data.model.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

private val CadastroNavy = Color(0xFF061C36)
private val CadastroBlue = Color(0xFF1677FF)
private val CadastroSecondary = Color(0xFF77869B)
private val CadastroBorder = Color(0xFFDCE5F1)
private val CadastroBackground = Color(0xFFFAFBFD)

@Composable
fun CadastroScreen(
    onCadastrarClick: () -> Unit,
    onJaTenhoContaClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var aceitouTermos by remember { mutableStateOf(false) }
    var cadastrando by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onJaTenhoContaClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Color(0xFFF1F4F8),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = CadastroNavy
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Crie sua conta",
                    color = CadastroNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(38.dp))
        }

        HorizontalDivider(
            color = CadastroBorder,
            thickness = 1.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 23.dp,
                    vertical = 26.dp
                )
        ) {
            Text(
                text = "Preencha seus dados e comece a cuidar melhor do seu carro.",
                color = CadastroSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))


            CadastroCampo(
                titulo = "Nome completo",
                valor = nome,
                onValueChange = {
                    nome = it
                    mensagemErro = ""
                },
                placeholder = "Como podemos te chamar?",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(16.dp))


            CadastroCampo(
                titulo = "E-mail",
                valor = email,
                onValueChange = {
                    email = it
                    mensagemErro = ""
                },
                placeholder = "seuemail@exemplo.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            CadastroCampo(
                titulo = "Telefone",
                valor = telefone,
                onValueChange = {
                    telefone = it
                    mensagemErro = ""
                },
                placeholder = "(11) 99999-9999",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            CadastroCampo(
                titulo = "Senha",
                valor = senha,
                onValueChange = {
                    senha = it
                    mensagemErro = ""
                },
                placeholder = "Mínimo de 8 caracteres",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            CadastroCampo(
                titulo = "Confirmar senha",
                valor = confirmarSenha,
                onValueChange = {
                    confirmarSenha = it
                    mensagemErro = ""
                },
                placeholder = "Repita sua senha",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = aceitouTermos,
                    onCheckedChange = {
                        aceitouTermos = it
                        mensagemErro = ""
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = CadastroBlue
                    ),
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Li e aceito os Termos de Uso e a Política de Privacidade",
                    color = CadastroSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }

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

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    if (!cadastrando) {
                        val nomeLimpo = nome.trim()
                        val emailLimpo = email.trim()
                        val telefoneLimpo = telefone.trim()
                        val numerosTelefone =
                            telefoneLimpo.filter { it.isDigit() }

                        mensagemErro = when {
                            nomeLimpo.isBlank() ||
                                    emailLimpo.isBlank() ||
                                    telefoneLimpo.isBlank() ||
                                    senha.isBlank() ||
                                    confirmarSenha.isBlank() ->
                                "Preencha todos os campos obrigatórios."

                            !Patterns.EMAIL_ADDRESS
                                .matcher(emailLimpo)
                                .matches() ->
                                "Digite um e-mail válido."

                            !CadastroValidator.telefoneValido(telefoneLimpo) ->
                                "Informe um telefone válido com DDD."

                            !CadastroValidator.senhaValida(senha) ->
                                "A senha deve ter pelo menos 8 caracteres."

                            !CadastroValidator.senhasCoincidem(senha, confirmarSenha) ->
                                "As senhas não coincidem."

                            !aceitouTermos ->
                                "Você precisa aceitar os termos para continuar."

                            else -> ""
                        }

                        if (mensagemErro.isEmpty()) {
                            cadastrando = true

                            FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(
                                    emailLimpo,
                                    senha
                                )
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val usuarioFirebase =
                                            task.result?.user

                                        if (usuarioFirebase == null) {
                                            cadastrando = false
                                            mensagemErro =
                                                "Não foi possível identificar o usuário cadastrado."
                                        } else {
                                            val uid = usuarioFirebase.uid

                                            val dadosUsuario = hashMapOf(
                                                "nome" to nomeLimpo,
                                                "email" to emailLimpo,
                                                "telefone" to numerosTelefone
                                            )

                                            FirebaseFirestore.getInstance()
                                                .collection("usuarios")
                                                .document(uid)
                                                .set(dadosUsuario)
                                                .addOnSuccessListener {
                                                    cadastrando = false

                                                    UserRepository.registerUser(
                                                        name = nomeLimpo,
                                                        email = emailLimpo,
                                                        carModel = ""
                                                    )

                                                    onCadastrarClick()
                                                }
                                                .addOnFailureListener { erro ->
                                                    cadastrando = false

                                                    mensagemErro =
                                                        "A conta foi criada, mas não foi possível salvar o perfil. " +
                                                                "Não tente cadastrar novamente com o mesmo e-mail. " +
                                                                "Detalhes: ${erro.localizedMessage ?: "Erro desconhecido"}"
                                                }
                                        }
                                    } else {
                                        cadastrando = false

                                        mensagemErro =
                                            when (task.exception) {
                                                is FirebaseAuthUserCollisionException ->
                                                    "Este e-mail já está cadastrado. Tente fazer login."

                                                else ->
                                                    "Não foi possível realizar o cadastro. Verifique sua conexão e tente novamente."
                                            }
                                    }
                                }
                        }
                    }
                },
                enabled = !cadastrando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CadastroBlue
                )
            ) {
                if (cadastrando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Criar conta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Já possui uma conta? ",
                    color = CadastroSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "Entrar",
                    color = CadastroBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        onJaTenhoContaClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
private fun CadastroCampo(
    titulo: String,
    valor: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = titulo,
            color = CadastroNavy,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF98ABC4),
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                androidx.compose.ui.text.input.VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CadastroBlue,
                unfocusedBorderColor = CadastroBorder,
                focusedContainerColor = CadastroBackground,
                unfocusedContainerColor = CadastroBackground
            )
        )
    }
}
