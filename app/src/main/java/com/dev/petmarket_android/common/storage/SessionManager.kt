package com.dev.petmarket_android.common.storage

import android.content.Context
import com.dev.petmarket_android.common.security.JwtUtils

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(
        token: String,
        email: String?,
        fullName: String?,
        role: String?,
        profileImageUrl: String? = null,
        userId: Long? = null
    ) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_EMAIL, email)
            putString(KEY_FULL_NAME, fullName)
            putString(KEY_ROLE, role)
            putString(KEY_PROFILE_IMAGE_URL, profileImageUrl)
            userId?.let { putLong(KEY_USER_ID, it) } ?: remove(KEY_USER_ID)
        }.apply()
    }

    fun updateProfile(
        email: String?,
        fullName: String?,
        role: String?,
        profileImageUrl: String?,
        userId: Long? = null
    ) {
        prefs.edit().apply {
            email?.let { putString(KEY_EMAIL, it) }
            fullName?.let { putString(KEY_FULL_NAME, it) }
            role?.let { putString(KEY_ROLE, it) }
            profileImageUrl?.let { putString(KEY_PROFILE_IMAGE_URL, it) }
            userId?.let { putLong(KEY_USER_ID, it) }
        }.apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getFullName(): String? = prefs.getString(KEY_FULL_NAME, null)

    fun getUserId(): Long? {
        val stored = prefs.getLong(KEY_USER_ID, NO_USER_ID)
        return stored.takeIf { it != NO_USER_ID } ?: JwtUtils.extractUserId(getToken())
    }

    fun getRole(): String = prefs.getString(KEY_ROLE, "USER") ?: "USER"

    fun getProfileImageUrl(): String? = prefs.getString(KEY_PROFILE_IMAGE_URL, null)

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }

    companion object {
        private const val PREF_NAME = "petmarket_session"
        private const val KEY_TOKEN = "token"
        private const val KEY_EMAIL = "email"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_ROLE = "role"
        private const val KEY_PROFILE_IMAGE_URL = "profile_image_url"
        private const val KEY_USER_ID = "user_id"
        private const val NO_USER_ID = -1L
    }
}
