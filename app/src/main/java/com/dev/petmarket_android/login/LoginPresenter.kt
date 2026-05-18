package com.dev.petmarket_android.login

import com.dev.petmarket_android.common.security.JwtUtils
import com.dev.petmarket_android.common.storage.SessionManager

class LoginPresenter(
    private var view: LoginContract.View?,
    private val model: LoginModel,
    private val sessionManager: SessionManager
) : LoginContract.Presenter {

    override fun onLoginClicked(email: String, password: String, loginAs: String) {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank() || password.isBlank()) {
            view?.showError("Email and password are required")
            return
        }

        view?.showLoading(true)
        model.login(
            email = normalizedEmail,
            password = password,
            loginAs = loginAs,
            onSuccess = { response ->
                val token = response.token
                if (!JwtUtils.isTokenUsableSafely(token)) {
                    view?.showLoading(false)
                    view?.showError("Invalid token received from server")
                    return@login
                }

                sessionManager.saveSession(
                    token = token!!,
                    email = response.email,
                    fullName = response.fullName,
                    role = response.role,
                    profileImageUrl = response.resolvedProfileImageUrl,
                    userId = response.id
                )
                model.loadProfile(
                    onSuccess = { profile ->
                        sessionManager.updateProfile(
                            email = profile.email,
                            fullName = profile.fullName,
                            role = profile.role,
                            profileImageUrl = profile.resolvedProfileImageUrl,
                            userId = profile.id
                        )
                        view?.showLoading(false)
                        view?.navigateToDashboard()
                    },
                    onFailure = {
                        view?.showLoading(false)
                        view?.navigateToDashboard()
                    }
                )
            },
            onFailure = { message ->
                view?.showLoading(false)
                view?.showError(message)
            }
        )
    }

    override fun onRegisterClicked() {
        view?.navigateToRegister()
    }

    override fun onDestroy() {
        view = null
    }
}
