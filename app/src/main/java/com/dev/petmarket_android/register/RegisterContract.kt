package com.dev.petmarket_android.register

interface RegisterContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun navigateToDashboard()
        fun navigateToLogin()
    }

    interface Presenter {
        fun onRegisterClicked(fullName: String, email: String, password: String, confirmPassword: String, role: String)
        fun onLoginClicked()
        fun onDestroy()
    }
}
