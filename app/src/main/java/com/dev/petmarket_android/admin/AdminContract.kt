package com.dev.petmarket_android.admin

import com.dev.petmarket_android.common.model.AdminUserResponse
import com.dev.petmarket_android.common.model.PetResponse

interface AdminContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun showAdminPets(items: List<PetResponse>)
        fun showAdminUsers(items: List<AdminUserResponse>)
    }

    interface Presenter {
        fun loadData()
        fun onDeletePet(petId: Long)
        fun onSuspendUser(userId: Long)
        fun onDestroy()
    }
}
