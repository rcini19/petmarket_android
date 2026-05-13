package com.dev.petmarket_android.profile

import com.dev.petmarket_android.common.model.OrderHistoryResponse
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.data.ProfileRepository

class ProfileModel(private val repository: ProfileRepository) {

    fun loadData(
        onSuccess: (ProfileResponse, List<OrderHistoryResponse>, List<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        var profile: ProfileResponse? = null
        var orders: List<OrderHistoryResponse> = emptyList()
        var trades: List<TradeOfferResponse> = emptyList()
        var completed = 0
        var failed = false

        fun completeOne() {
            completed += 1
            if (!failed && completed == 3 && profile != null) {
                onSuccess(profile!!, orders, trades)
            }
        }

        repository.getProfile(
            onSuccess = {
                profile = it
                completeOne()
            },
            onFailure = {
                if (!failed) {
                    failed = true
                    onFailure(it)
                }
            }
        )

        repository.getOrderHistory(
            onSuccess = {
                orders = it
                completeOne()
            },
            onFailure = {
                orders = emptyList()
                completeOne()
            }
        )

        repository.getTradeHistory(
            onSuccess = {
                trades = it
                completeOne()
            },
            onFailure = {
                trades = emptyList()
                completeOne()
            }
        )
    }

    fun updateProfile(
        fullName: String,
        email: String,
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.updateProfile(
            fullName = fullName,
            email = email,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun updateProfileImage(
        profileImageUrl: String,
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.updateProfileImage(
            profileImageUrl = profileImageUrl,
            onSuccess = onSuccess,
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
        repository.changePassword(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
