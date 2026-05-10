package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse

interface PetDetailContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showPet(pet: PetResponse)
        fun showError(message: String)
        fun showPurchaseSuccess(message: String)
    }

    interface Presenter {
        fun loadPet(petId: Long)
        fun onPurchaseClicked()
        fun onDestroy()
    }
}
