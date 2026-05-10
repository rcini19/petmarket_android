package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse

interface MyPetsContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showPets(items: List<PetResponse>)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun navigateToCreateListing()
        fun navigateToEditListing(petId: Long)
    }

    interface Presenter {
        fun loadMyPets()
        fun onCreateListingClicked()
        fun onEditPetClicked(petId: Long)
        fun onDeletePetClicked(petId: Long)
        fun onDestroy()
    }
}
