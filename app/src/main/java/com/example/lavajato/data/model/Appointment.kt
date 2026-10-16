package com.example.lavajato.data.model

import androidx.compose.runtime.mutableStateListOf

enum class AppointmentStatus(val label: String) {
    CONFIRMADO("Confirmado"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado")
}

data class Appointment(
    val id: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val address: String = "Av. Recife, 123 - Boa Viagem - Recife/PE",
    var status: AppointmentStatus = AppointmentStatus.CONFIRMADO
)

object AppointmentRepository {
    val list = mutableStateListOf(
        Appointment("1", "Lavagem Simples", "25 Set", "14:00", status = AppointmentStatus.CONFIRMADO),
        Appointment("2", "Higienização Interna", "28 Set", "09:30", status = AppointmentStatus.CONFIRMADO),
        Appointment("3", "Completa", "02 Out", "16:00", status = AppointmentStatus.CONFIRMADO),
        Appointment("4", "Lavagem Simples", "10 Ago", "10:00", status = AppointmentStatus.FINALIZADO)
    )

    fun addAppointment(serviceName: String, date: String, time: String) {
        list.add(
            0,
            Appointment(
                id = System.currentTimeMillis().toString(),
                serviceName = serviceName,
                date = date,
                time = time,
                status = AppointmentStatus.CONFIRMADO
            )
        )
    }

    fun updateStatus(id: String, newStatus: AppointmentStatus) {
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = list[index].copy(status = newStatus)
        }
    }
}