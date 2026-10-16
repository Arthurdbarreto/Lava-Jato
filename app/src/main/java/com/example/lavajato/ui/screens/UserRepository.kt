package com.example.lavajato.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserRepository {
    var registeredName by mutableStateOf("Arthur")
    var lastSavedEmail by mutableStateOf("arthur@email.com")
    var registeredCarModel by mutableStateOf("Civic Sedan") // Adicionada a propriedade faltante

    fun registerUser(name: String, email: String, carModel: String = "Civic Sedan") {
        registeredName = name
        lastSavedEmail = email
        registeredCarModel = carModel
    }
}
