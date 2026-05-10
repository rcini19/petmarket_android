package com.dev.petmarket_android.login

interface LoginContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showError(message: String)
        fun navigateToDashboard()
        fun navigateToRegister()
    }

    interface Presenter {
        fun onLoginClicked(email: String, password: String, loginAs: String)
        fun onRegisterClicked()
        fun onDestroy()
    }
}
