package com.dev.petmarket_android.login

import com.dev.petmarket_android.common.model.AuthResponse
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.data.UserRepository

class LoginModel(private val repository: UserRepository) {

    fun login(
        email: String,
        password: String,
        loginAs: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.login(email, password, loginAs, onSuccess, onFailure)
    }

    fun loadProfile(
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getProfile(onSuccess, onFailure)
    }
}
