package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse

interface ListingFormContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showPet(pet: PetResponse)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun closeScreen()
    }

    interface Presenter {
        fun loadExistingPet(petId: Long)
        fun submit(
            petId: Long?,
            name: String,
            species: String,
            breed: String,
            ageInput: String,
            listingType: String,
            priceInput: String,
            description: String,
            imageUrl: String
        )
        fun onDestroy()
    }
}
