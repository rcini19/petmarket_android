package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.AuthResponse
import com.dev.petmarket_android.common.model.LoginRequest
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.RegisterRequest
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor

class UserRepository(context: Context) {

    private val api = ApiClient.getService(context)

    fun login(
        email: String,
        password: String,
        loginAs: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val request = LoginRequest(email = email, password = password, loginAs = loginAs)
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/auth/login", "/auth/login"),
            callFactory = { endpoint -> api.login(endpoint, request) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            role = role
        )

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/auth/register", "/auth/register"),
            callFactory = { endpoint -> api.register(endpoint, request) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getProfile(
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/profile/me", "/profile/me"),
            callFactory = { endpoint -> api.getProfile(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
