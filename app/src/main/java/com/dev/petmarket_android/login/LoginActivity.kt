package com.dev.petmarket_android.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dev.petmarket_android.databinding.ActivityLoginBinding
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.ui.BaseViewBindingActivity
import com.dev.petmarket_android.dashboard.DashboardActivity
import com.dev.petmarket_android.data.UserRepository
import com.dev.petmarket_android.register.RegisterActivity

class LoginActivity : BaseViewBindingActivity<ActivityLoginBinding>(), LoginContract.View {

    override fun createBinding() = ActivityLoginBinding.inflate(layoutInflater)

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = UserRepository(applicationContext)
        val model = LoginModel(repository)
        presenter = LoginPresenter(this, model, SessionManager(applicationContext))

        setupLoginAsDropdown()

        binding.btnLogin.setOnClickListener {
            presenter.onLoginClicked(
                email = binding.etEmail.text?.toString().orEmpty(),
                password = binding.etPassword.text?.toString().orEmpty(),
                loginAs = binding.ddLoginAs.text?.toString().orEmpty().ifBlank { "USER" }
            )
        }

        binding.tvGoToRegister.setOnClickListener {
            presenter.onRegisterClicked()
        }
    }

    private fun setupLoginAsDropdown() {
        val options = listOf("USER", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        binding.ddLoginAs.setAdapter(adapter)
        binding.ddLoginAs.setText("USER", false)
    }

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
