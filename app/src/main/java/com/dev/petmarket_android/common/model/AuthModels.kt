package com.dev.petmarket_android.common.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("loginAs") val loginAs: String = "USER"
)

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("role") val role: String = "USER"
)

data class AuthResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("profilePictureUrl") val profilePictureUrl: String? = null,
    @SerializedName("profilePicture") val profilePicture: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
) {
    val resolvedProfileImageUrl: String?
        get() = firstNonBlank(profileImageUrl, profilePictureUrl, profilePicture, avatarUrl, imageUrl)
}

data class ErrorResponse(
    @SerializedName("error") val error: String? = null,
    @SerializedName("message") val message: String? = null
)

private fun firstNonBlank(vararg values: String?): String? {
    return values.firstOrNull { !it.isNullOrBlank() }?.trim()
}
