package com.dev.petmarket_android.register

import com.dev.petmarket_android.common.security.JwtUtils
import com.dev.petmarket_android.common.storage.SessionManager

class RegisterPresenter(
    private var view: RegisterContract.View?,
    private val model: RegisterModel,
    private val sessionManager: SessionManager
) : RegisterContract.Presenter {

    override fun onRegisterClicked(fullName: String, email: String, password: String, confirmPassword: String, role: String) {
        val normalizedFullName = fullName.trim()
        val normalizedEmail = email.trim()
        val normalizedRole = role.trim().ifBlank { "USER" }.uppercase()

        if (normalizedFullName.isBlank() || normalizedEmail.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            view?.showError("All fields are required")
            return
        }

        if (normalizedRole != "USER" && normalizedRole != "ADMIN") {
            view?.showError("Please choose a valid role")
            return
        }

        if (password != confirmPassword) {
            view?.showError("Passwords do not match")
            return
        }

        view?.showLoading(true)
        model.register(
            fullName = normalizedFullName,
            email = normalizedEmail,
            password = password,
            confirmPassword = confirmPassword,
            role = normalizedRole,
            onSuccess = { response ->
                view?.showLoading(false)
                val token = response.token
                if (!JwtUtils.isTokenUsableSafely(token)) {
                    view?.showError("Invalid token received from server")
                    return@register
                }

                sessionManager.saveSession(
                    token = token!!,
                    email = response.email,
                    fullName = response.fullName,
                    role = response.role,
                    profileImageUrl = response.resolvedProfileImageUrl,
                    userId = response.id
                )
                view?.showSuccess(response.message ?: "Registration successful")
                view?.navigateToDashboard()
            },
            onFailure = { message ->
                view?.showLoading(false)
                view?.showError(message)
            }
        )
    }

    override fun onLoginClicked() {
        view?.navigateToLogin()
    }

    override fun onDestroy() {
        view = null
    }
}
