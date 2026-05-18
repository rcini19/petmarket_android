package com.dev.petmarket_android.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.dashboard.DashboardActivity
import com.dev.petmarket_android.data.UserRepository
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var presenter: RegisterContract.Presenter

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var ddRegisterAs: AutoCompleteTextView
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bindViews()

        val repository = UserRepository(applicationContext)
        val model = RegisterModel(repository)
        presenter = RegisterPresenter(this, model, SessionManager(applicationContext))

        setupRegisterAsDropdown()

        btnRegister.setOnClickListener {
            presenter.onRegisterClicked(
                fullName = etFullName.text?.toString().orEmpty(),
                email = etEmail.text?.toString().orEmpty(),
                password = etPassword.text?.toString().orEmpty(),
                confirmPassword = etConfirmPassword.text?.toString().orEmpty(),
                role = ddRegisterAs.text?.toString().orEmpty().ifBlank { "USER" }
            )
        }

        tvLogin.setOnClickListener {
            presenter.onLoginClicked()
        }
    }

    private fun bindViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        ddRegisterAs = findViewById(R.id.ddRegisterAs)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvGoToLogin)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRegisterAsDropdown() {
        val options = listOf("USER", "ADMIN")
        val adapter = ArrayAdapter(this, R.layout.item_role_dropdown, options)
        ddRegisterAs.setAdapter(adapter)
        ddRegisterAs.setText("USER", false)
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !isLoading
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun navigateToLogin() {
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
