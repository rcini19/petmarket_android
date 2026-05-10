package com.dev.petmarket_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dev.petmarket_android.common.security.JwtUtils
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.dashboard.DashboardActivity
import com.dev.petmarket_android.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val token = sessionManager.getToken()

        // Use safe version to avoid crashes on invalid tokens
        val nextScreen = if (JwtUtils.isTokenUsableSafely(token)) {
            DashboardActivity::class.java
        } else {
            sessionManager.clearSession()
            LoginActivity::class.java
        }

        val intent = Intent(this, nextScreen)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}