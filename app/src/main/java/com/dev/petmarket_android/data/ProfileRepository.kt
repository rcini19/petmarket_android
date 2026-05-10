package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.OrderHistoryResponse
import com.dev.petmarket_android.common.model.PasswordUpdateRequest
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.ProfileImageUpdateRequest
import com.dev.petmarket_android.common.model.ProfileUpdateRequest
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor

class ProfileRepository(context: Context) {

    private val api = ApiClient.getService(context)

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

    fun updateProfile(
        fullName: String,
        email: String,
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = ProfileUpdateRequest(fullName = fullName, email = email)
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/profile/me", "/profile/me"),
            callFactory = { endpoint -> api.updateProfile(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    // Original method for backward compatibility
    fun getOrderHistory(
        onSuccess: (List<OrderHistoryResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/profile/me/orders", "/api/profile/me/purchases", "/profile/me/orders", "/profile/me/purchases"),
            callFactory = { endpoint -> api.getOrderHistory(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun updateProfileImage(
        profileImageUrl: String,
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = ProfileImageUpdateRequest(profileImageUrl = profileImageUrl)
        ApiExecutor.executeWithFallback(
            endpoints = listOf(
                "/api/profile/me/image",
                "/api/profile/me/photo",
                "/profile/me/image",
                "/profile/me/photo"
            ),
            callFactory = { endpoint -> api.updateProfileImage(endpoint, payload) },
            onSuccess = { response ->
                response.profile?.let(onSuccess) ?: onFailure(response.message ?: "Profile photo update failed")
            },
            onFailure = onFailure
        )
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = PasswordUpdateRequest(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )
        ApiExecutor.executeStatusWithFallback(
            endpoints = listOf(
                "/api/profile/me/password",
                "/profile/me/password",
                "/api/auth/change-password",
                "/auth/change-password"
            ),
            callFactory = { endpoint -> api.changePassword(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    // Original method for backward compatibility
    fun getTradeHistory(
        onSuccess: (List<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/profile/me/trade-offers", "/api/profile/me/trades", "/profile/me/trade-offers", "/profile/me/trades"),
            callFactory = { endpoint -> api.getTradeHistory(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
