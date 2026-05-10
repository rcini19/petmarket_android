package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse

interface BrowsePetsContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showPets(items: List<PetResponse>)
        fun showError(message: String)
        fun navigateToPetDetail(petId: Long)
    }

    interface Presenter {
        fun loadPets(search: String, listingType: String)
        fun onPetClicked(petId: Long)
        fun onDestroy()
    }
}
