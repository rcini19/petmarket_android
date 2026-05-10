package com.dev.petmarket_android.register

import com.dev.petmarket_android.common.model.AuthResponse
import com.dev.petmarket_android.data.UserRepository

class RegisterModel(private val repository: UserRepository) {

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.register(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            role = role,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
