
package com.example.lavajato.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserRepository {

    var registeredName by mutableStateOf("")
        private set

    var lastSavedEmail by mutableStateOf("")
        private set

    var registeredCarModel by mutableStateOf("")
        private set

    fun registerUser(
        name: String,
        email: String,
        carModel: String = ""
    ) {
        registeredName = name
        lastSavedEmail = email
        registeredCarModel = carModel
    }

    fun clearUser() {
        registeredName = ""
        lastSavedEmail = ""
        registeredCarModel = ""
    }
}
