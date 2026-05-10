package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetRequest

class ListingFormPresenter(
    private var view: ListingFormContract.View?,
    private val model: ListingFormModel
) : ListingFormContract.Presenter {

    override fun loadExistingPet(petId: Long) {
        view?.showLoading(true)
        model.getPetById(
            petId = petId,
            onSuccess = { pet ->
                view?.showLoading(false)
                view?.showPet(pet)
            },
            onFailure = { error ->
                view?.showLoading(false)
                view?.showError(error)
            }
        )
    }

    override fun submit(
        petId: Long?,
        name: String,
        species: String,
        breed: String,
        ageInput: String,
        listingType: String,
        priceInput: String,
        description: String,
        imageUrl: String
    ) {
        val normalizedName = name.trim()
        val normalizedSpecies = species.trim()
        val normalizedBreed = breed.trim()
        val normalizedDescription = description.trim()
        val normalizedImageUrl = imageUrl.trim()
        val normalizedType = listingType.trim().uppercase()

        if (normalizedName.isBlank() || normalizedSpecies.isBlank() || normalizedBreed.isBlank()) {
            view?.showError("Name, species, and breed are required")
            return
        }

        val age = ageInput.toIntOrNull()
        if (age == null || age < 0) {
            view?.showError("Age must be a valid non-negative number")
            return
        }

        if (normalizedType != "SALE" && normalizedType != "TRADE" && normalizedType != "BOTH") {
            view?.showError("Listing type must be SALE, TRADE, or BOTH")
            return
        }

        val parsedPrice = priceInput.toDoubleOrNull()
        val price = if (normalizedType == "SALE" || normalizedType == "BOTH") {
            if (parsedPrice == null || parsedPrice <= 0.0) {
                view?.showError("Price is required and must be greater than 0 for SALE or BOTH")
                return
            }
            parsedPrice
        } else {
            null
        }

        val payload = PetRequest(
            name = normalizedName,
            species = normalizedSpecies,
            breed = normalizedBreed,
            age = age,
            listingType = normalizedType,
            price = price,
            description = normalizedDescription,
            imageUrl = normalizedImageUrl
        )

        view?.showLoading(true)
        if (petId == null || petId <= 0L) {
            model.createPet(
                payload = payload,
                onSuccess = {
                    view?.showLoading(false)
                    view?.showSuccess("Listing created")
                    view?.closeScreen()
                },
                onFailure = { error ->
                    view?.showLoading(false)
                    view?.showError(error)
                }
            )
            return
        }

        model.updatePet(
            petId = petId,
            payload = payload,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Listing updated")
                view?.closeScreen()
            },
            onFailure = { error ->
                view?.showLoading(false)
                view?.showError(error)
            }
        )
    }

    override fun onDestroy() {
        view = null
    }
}
