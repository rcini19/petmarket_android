package com.dev.petmarket_android.profile

import com.dev.petmarket_android.common.model.OrderHistoryResponse
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse

interface ProfileContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun showProfile(profile: ProfileResponse)
        fun showOrderHistory(items: List<OrderHistoryResponse>)
        fun showTradeHistory(items: List<TradeOfferResponse>)
    }

    interface Presenter {
        fun loadData()
        fun onSaveProfile(fullName: String, email: String)
        fun onSaveProfileImage(profileImageUrl: String)
        fun onChangePassword(currentPassword: String, newPassword: String, confirmPassword: String)
        fun onDestroy()
    }
}
