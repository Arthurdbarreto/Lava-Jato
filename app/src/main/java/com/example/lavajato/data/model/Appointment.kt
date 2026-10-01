package com.example.lavajato.data.model

/**
 * Representa um agendamento do usuário.
 * Dados fictícios, utilizados apenas para exibição na tela "Meus Agendamentos".
 */
data class Appointment(
    val day: String,
    val month: String,
    val serviceName: String,
    val time: String,
    val addressLine1: String,
    val addressLine2: String,
    val status: String
)

object AppointmentRepository {
    val upcoming = listOf(
        Appointment(
            day = "25",
            month = "Set",
            serviceName = "Lavagem Simples",
            time = "14:00",
            addressLine1 = "Av. Recife, 123",
            addressLine2 = "Boa Viagem - Recife/PE",
            status = "Confirmado"
        ),
        Appointment(
            day = "28",
            month = "Set",
            serviceName = "Higienização Interna",
            time = "09:30",
            addressLine1 = "Av. Recife, 123",
            addressLine2 = "Boa Viagem - Recife/PE",
            status = "Confirmado"
        ),
        Appointment(
            day = "02",
            month = "Out",
            serviceName = "Completa",
            time = "16:00",
            addressLine1 = "Av. Recife, 123",
            addressLine2 = "Boa Viagem - Recife/PE",
            status = "Confirmado"
        )
    )

    val history: List<Appointment> = emptyList()
}
