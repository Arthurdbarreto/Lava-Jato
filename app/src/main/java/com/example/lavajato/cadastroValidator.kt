package com.example.lavajato

object CadastroValidator {

    // Verifica se a senha possui pelo menos 8 caracteres
    fun senhaValida(senha: String): Boolean {
        return senha.length >= 8
    }

    // Verifica se a senha e a confirmação são iguais
    fun senhasCoincidem(
        senha: String,
        confirmarSenha: String
    ): Boolean {
        return senha == confirmarSenha
    }

    // Verifica se o telefone possui 10 ou 11 dígitos
    fun telefoneValido(telefone: String): Boolean {
        val numeros = telefone.filter { it.isDigit() }
        return numeros.length in 10..11
    }
}