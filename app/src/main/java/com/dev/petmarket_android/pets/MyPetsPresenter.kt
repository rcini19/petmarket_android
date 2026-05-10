package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse

class MyPetsPresenter(
    private var view: MyPetsContract.View?,
    private val model: MyPetsModel
) : MyPetsContract.Presenter {

    private var currentPets = emptyList<PetResponse>()

    override fun loadMyPets() {
        view?.showLoading(true)
        model.loadMyPets(
            onSuccess = { pets ->
                currentPets = pets
                view?.showLoading(false)
                view?.showPets(pets)
            },
            onFailure = { error ->
                currentPets = emptyList()
                view?.showLoading(false)
                view?.showError(error)
                view?.showPets(emptyList())
            }
        )
    }

    override fun onCreateListingClicked() {
        view?.navigateToCreateListing()
    }

    override fun onEditPetClicked(petId: Long) {
        view?.navigateToEditListing(petId)
    }

    override fun onDeletePetClicked(petId: Long) {
        val pet = currentPets.firstOrNull { it.id == petId }
        if (!pet?.status.orEmpty().equals("AVAILABLE", ignoreCase = true)) {
            view?.showError("Purchased pets cannot be deleted. Only available listings can be removed.")
            return
        }

        view?.showLoading(true)
        model.deletePet(
            petId = petId,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Listing removed")
                loadMyPets()
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
