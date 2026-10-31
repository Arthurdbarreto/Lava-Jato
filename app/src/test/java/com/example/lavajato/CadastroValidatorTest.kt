package com.example.lavajato

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CadastroValidatorTest {

    // CTU-01: Senha com 8 caracteres deve ser válida
    @Test
    fun senhaComOitoCaracteres_deveSerValida() {

        // Arrange - Preparação
        val senha = "12345678"

        // Act - Execução
        val resultado = CadastroValidator.senhaValida(senha)

        // Assert - Verificação
        assertTrue(resultado)
    }

    // CTU-02: Senha com menos de 8 caracteres deve ser inválida
    @Test
    fun senhaCurta_deveSerInvalida() {

        // Arrange
        val senha = "12345"

        // Act
        val resultado = CadastroValidator.senhaValida(senha)

        // Assert
        assertFalse(resultado)
    }

    // CTU-03: Senhas iguais devem coincidir
    @Test
    fun senhasIguais_devemCoincidir() {

        // Arrange
        val senha = "senha123"
        val confirmacao = "senha123"

        // Act
        val resultado = CadastroValidator.senhasCoincidem(
            senha,
            confirmacao
        )

        // Assert
        assertTrue(resultado)
    }

    // CTU-04: Senhas diferentes não devem coincidir
    @Test
    fun senhasDiferentes_naoDevemCoincidir() {

        // Arrange
        val senha = "senha123"
        val confirmacao = "senha456"

        // Act
        val resultado = CadastroValidator.senhasCoincidem(
            senha,
            confirmacao
        )

        // Assert
        assertFalse(resultado)
    }

    // CTU-05: Telefone com DDD e 11 dígitos deve ser válido
    @Test
    fun telefoneComOnzeDigitos_deveSerValido() {

        // Arrange
        val telefone = "(81) 99999-9999"

        // Act
        val resultado = CadastroValidator.telefoneValido(telefone)

        // Assert
        assertTrue(resultado)
    }

    // CTU-06: Telefone com poucos dígitos deve ser inválido
    @Test
    fun telefoneComPoucosDigitos_deveSerInvalido() {

        // Arrange
        val telefone = "12345"

        // Act
        val resultado = CadastroValidator.telefoneValido(telefone)

        // Assert
        assertFalse(resultado)
    }
}