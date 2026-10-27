package com.example.lavajato.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lavajato.data.model.UserRepository
import com.example.lavajato.ui.components.BottomNavigationBar
import com.example.lavajato.ui.components.BottomTab
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

private val PerfilNavy = Color(0xFF061C36)
private val PerfilBlue = Color(0xFF1677FF)
private val PerfilBackground = Color(0xFFF5F7FB)
private val PerfilSecondary = Color(0xFF77869B)
private val PerfilRed = Color(0xFFD63C45)

private data class VeiculoPerfil(
    val id: String = "",
    val marca: String = "",
    val modelo: String = "",
    val placa: String = "",
    val cor: String = "",
    val ano: String = "",
    val principal: Boolean = false
)

@Composable
fun PerfilScreen(
    onInicioClick: () -> Unit,
    onAgendamentosClick: () -> Unit,
    onSairClick: () -> Unit
) {
    val nome = UserRepository.registeredName
        .ifBlank { "Usuário Lava+" }

    val email = UserRepository.lastSavedEmail
        .ifBlank { "E-mail não informado" }

    val iniciais = nome
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifBlank { "L+" }

    val usuario = FirebaseAuth.getInstance().currentUser
    val uid = usuario?.uid

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    var veiculos by remember {
        mutableStateOf<List<VeiculoPerfil>>(emptyList())
    }

    var carregandoVeiculos by remember {
        mutableStateOf(true)
    }

    var mensagemVeiculos by remember {
        mutableStateOf("")
    }

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var veiculoEditando by remember {
        mutableStateOf<VeiculoPerfil?>(null)
    }

    var veiculoExcluindo by remember {
        mutableStateOf<VeiculoPerfil?>(null)
    }

    var salvando by remember {
        mutableStateOf(false)
    }

    var excluindo by remember {
        mutableStateOf(false)
    }

    // Escuta em tempo real os veículos do usuário autenticado.
    DisposableEffect(uid) {
        var registro: ListenerRegistration? = null

        if (uid != null) {
            registro = firestore
                .collection("usuarios")
                .document(uid)
                .collection("veiculos")
                .addSnapshotListener { snapshot, erro ->
                    carregandoVeiculos = false

                    if (erro != null) {
                        mensagemVeiculos =
                            "Não foi possível carregar os veículos."
                        return@addSnapshotListener
                    }

                    mensagemVeiculos = ""

                    veiculos = snapshot?.documents
                        ?.map { documento ->
                            VeiculoPerfil(
                                id = documento.id,
                                marca = documento.getString("marca").orEmpty(),
                                modelo = documento.getString("modelo").orEmpty(),
                                placa = documento.getString("placa").orEmpty(),
                                cor = documento.getString("cor").orEmpty(),
                                ano = documento.getString("ano").orEmpty(),
                                principal = documento.getBoolean("principal") ?: false
                            )
                        }
                        ?.sortedWith(
                            compareBy<VeiculoPerfil>(
                                { !it.principal },
                                { it.marca.lowercase() },
                                { it.modelo.lowercase() }
                            )
                        )
                        ?: emptyList()
                }
        } else {
            carregandoVeiculos = false
            mensagemVeiculos = "Usuário não autenticado."
        }

        onDispose {
            registro?.remove()
        }
    }

    Scaffold(
        containerColor = PerfilBackground,
        bottomBar = {
            BottomNavigationBar(
                selected = BottomTab.PERFIL,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab.INICIO -> onInicioClick()

                        BottomTab.AGENDAMENTOS ->
                            onAgendamentosClick()

                        BottomTab.PERFIL -> Unit
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PerfilNavy)
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 24.dp,
                        bottom = 32.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PerfilBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Text(
                        text = "Lava+",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .background(PerfilBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciais,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = nome,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = email,
                    color = Color(0xFFB6C7DB),
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 24.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Minha conta",
                    color = PerfilNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    PerfilItem(
                        titulo = "Meus dados",
                        descricao = nome,
                        icon = Icons.Default.Person
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        color = PerfilBackground
                    )

                    PerfilItem(
                        titulo = "E-mail",
                        descricao = email,
                        icon = Icons.Default.Email
                    )
                }

                // Seção de múltiplos veículos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Meus veículos",
                        color = PerfilNavy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = {
                            veiculoEditando = null
                            mostrarFormulario = true
                        },
                        enabled = uid != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text("Adicionar")
                    }
                }

                if (carregandoVeiculos) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = PerfilBlue
                    )
                }

                if (mensagemVeiculos.isNotBlank()) {
                    Text(
                        text = mensagemVeiculos,
                        color = PerfilRed,
                        fontSize = 13.sp
                    )
                }

                if (!carregandoVeiculos && veiculos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = PerfilBlue,
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Nenhum veículo cadastrado",
                            color = PerfilNavy,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Adicione seus veículos para utilizá-los nos agendamentos.",
                            color = PerfilSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                veiculos.forEach { veiculo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = PerfilBlue,
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${veiculo.marca} ${veiculo.modelo}",
                                color = PerfilNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            if (veiculo.principal) {
                                Text(
                                    text = "★ Veículo principal",
                                    color = PerfilBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = "${veiculo.placa} • ${veiculo.cor}" +
                                        if (veiculo.ano.isNotBlank()) " • ${veiculo.ano}" else "",
                                color = PerfilSecondary,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                veiculoEditando = veiculo
                                mostrarFormulario = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar veículo",
                                tint = PerfilBlue
                            )
                        }

                        IconButton(
                            onClick = {
                                veiculoExcluindo = veiculo
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir veículo",
                                tint = PerfilRed
                            )
                        }
                    }
                }

                Text(
                    text = "Agendamentos",
                    color = PerfilNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    PerfilItem(
                        titulo = "Meus agendamentos",
                        descricao = "Consulte seus serviços agendados",
                        icon = Icons.Default.CalendarMonth,
                        onClick = onAgendamentosClick
                    )
                }

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        UserRepository.clearUser()
                        onSairClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE9EA),
                        contentColor = PerfilRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Sair da conta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioVeiculoDialog(
            veiculo = veiculoEditando,
            salvando = salvando,
            onDismiss = {
                if (!salvando) {
                    mostrarFormulario = false
                }
            },
            onSalvar = { marca, modelo, placa, cor, ano, principal ->
                if (uid != null && !salvando) {
                    salvando = true

                    val colecao = firestore
                        .collection("usuarios")
                        .document(uid)
                        .collection("veiculos")

                    val referencia = veiculoEditando?.let {
                        colecao.document(it.id)
                    } ?: colecao.document()

                    val dados = hashMapOf<String, Any>(
                        "marca" to marca,
                        "modelo" to modelo,
                        "placa" to placa,
                        "cor" to cor,
                        "ano" to ano,
                        "principal" to principal
                    )

                    // Uma operação em lote evita que dois veículos permaneçam
                    // marcados como principais após uma troca bem-sucedida.
                    val lote = firestore.batch()
                    if (principal) {
                        veiculos.filter { it.id != referencia.id && it.principal }
                            .forEach { outro ->
                                lote.update(colecao.document(outro.id), "principal", false)
                            }
                    }
                    lote.set(referencia, dados)
                    lote.commit()
                        .addOnSuccessListener {
                            salvando = false
                            mostrarFormulario = false
                            veiculoEditando = null
                            mensagemVeiculos = ""
                        }
                        .addOnFailureListener { erro ->
                            salvando = false
                            mensagemVeiculos =
                                "Não foi possível salvar o veículo: ${erro.localizedMessage ?: "erro desconhecido"}"
                        }
                }
            }
        )
    }

    veiculoExcluindo?.let { veiculo ->
        AlertDialog(
            onDismissRequest = {
                if (!excluindo) veiculoExcluindo = null
            },
            title = {
                Text("Excluir veículo?")
            },
            text = {
                Text(
                    "Deseja excluir ${veiculo.marca} ${veiculo.modelo}?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !excluindo,
                    onClick = {
                        if (uid != null) {
                            excluindo = true

                            firestore
                                .collection("usuarios")
                                .document(uid)
                                .collection("veiculos")
                                .document(veiculo.id)
                                .delete()
                                .addOnSuccessListener {
                                    excluindo = false
                                    veiculoExcluindo = null
                                }
                                .addOnFailureListener {
                                    excluindo = false
                                    veiculoExcluindo = null
                                    mensagemVeiculos =
                                        "Não foi possível excluir o veículo."
                                }
                        }
                    }
                ) {
                    Text(
                        if (excluindo) "Excluindo..." else "Excluir",
                        color = PerfilRed
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !excluindo,
                    onClick = {
                        veiculoExcluindo = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FormularioVeiculoDialog(
    veiculo: VeiculoPerfil?,
    salvando: Boolean,
    onDismiss: () -> Unit,
    onSalvar: (String, String, String, String, String, Boolean) -> Unit
) {
    var marca by remember(veiculo?.id) { mutableStateOf(veiculo?.marca.orEmpty()) }
    var modelo by remember(veiculo?.id) { mutableStateOf(veiculo?.modelo.orEmpty()) }
    var placa by remember(veiculo?.id) { mutableStateOf(veiculo?.placa.orEmpty()) }
    var cor by remember(veiculo?.id) { mutableStateOf(veiculo?.cor.orEmpty()) }
    var ano by remember(veiculo?.id) { mutableStateOf(veiculo?.ano.orEmpty()) }
    var principal by remember(veiculo?.id) { mutableStateOf(veiculo?.principal ?: false) }
    var erro by remember(veiculo?.id) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!salvando) onDismiss() },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = if (veiculo == null) "Cadastrar veículo" else "Editar veículo",
                    color = PerfilNavy,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Preencha os dados do veículo para seus agendamentos.",
                    color = PerfilSecondary,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it; erro = "" },
                    label = { Text("Marca") },
                    placeholder = { Text("Ex.: Toyota") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it; erro = "" },
                    label = { Text("Modelo") },
                    placeholder = { Text("Ex.: Corolla") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = placa,
                    onValueChange = {
                        placa = it.uppercase().filter { c -> c.isLetterOrDigit() }.take(7)
                        erro = ""
                    },
                    label = { Text("Placa") },
                    placeholder = { Text("ABC1D23") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = cor,
                    onValueChange = { cor = it; erro = "" },
                    label = { Text("Cor") },
                    placeholder = { Text("Ex.: Prata") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = ano,
                    onValueChange = {
                        ano = it.filter { c -> c.isDigit() }.take(4)
                        erro = ""
                    },
                    label = { Text("Ano") },
                    placeholder = { Text("Ex.: 2022") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Veículo principal", color = PerfilNavy, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Destacar este veículo na sua lista",
                            color = PerfilSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = principal,
                        onCheckedChange = { principal = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = PerfilBlue)
                    )
                }
                if (erro.isNotBlank()) {
                    Text(erro, color = PerfilRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !salvando,
                colors = ButtonDefaults.buttonColors(containerColor = PerfilBlue),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    val marcaLimpa = marca.trim()
                    val modeloLimpo = modelo.trim()
                    val placaLimpa = placa.trim()
                    val corLimpa = cor.trim()
                    val anoLimpo = ano.trim()
                    erro = when {
                        listOf(marcaLimpa, modeloLimpo, placaLimpa, corLimpa, anoLimpo)
                            .any { it.isBlank() } -> "Preencha todos os campos."
                        placaLimpa.length != 7 -> "A placa deve ter 7 caracteres."
                        anoLimpo.length != 4 || anoLimpo.toIntOrNull() == null ->
                            "Informe um ano com quatro dígitos."
                        anoLimpo.toInt() !in 1900..2100 -> "Informe um ano válido."
                        else -> ""
                    }
                    if (erro.isBlank()) {
                        onSalvar(marcaLimpa, modeloLimpo, placaLimpa, corLimpa, anoLimpo, principal)
                    }
                }
            ) {
                Text(if (salvando) "Salvando..." else "Salvar veículo")
            }
        },
        dismissButton = {
            TextButton(enabled = !salvando, onClick = onDismiss) {
                Text("Cancelar", color = PerfilSecondary)
            }
        }
    )
}

@Composable
private fun PerfilItem(
    titulo: String,
    descricao: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null
) {
    val itemModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(itemModifier)
            .padding(
                horizontal = 16.dp,
                vertical = 17.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    Color(0xFFEAF2FF),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PerfilBlue,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                color = PerfilNavy,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descricao,
                color = PerfilSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (onClick != null) {
            Text(
                text = "›",
                color = PerfilSecondary,
                fontSize = 25.sp
            )
        }
    }
}