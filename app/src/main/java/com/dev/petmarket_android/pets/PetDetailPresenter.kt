package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.util.PetListingRules

class PetDetailPresenter(
    private var view: PetDetailContract.View?,
    private val model: PetDetailModel,
    private val sessionManager: SessionManager
) : PetDetailContract.Presenter {

    private var currentPet: PetResponse? = null

    override fun loadPet(petId: Long) {
        view?.showLoading(true)
        model.loadPet(
            petId = petId,
            onSuccess = { pet ->
                currentPet = pet
                view?.showLoading(false)
                view?.showPet(pet)
            },
            onFailure = { error ->
                view?.showLoading(false)
                view?.showError(error)
            }
        )
    }

    override fun onPurchaseClicked() {
        val pet = currentPet
        if (pet == null) {
            view?.showError("Pet details are not loaded")
            return
        }

        val price = pet.price

        if (PetListingRules.isOwnedByCurrentUser(pet, sessionManager)) {
            view?.showError("You cannot purchase your own pet")
            return
        }

        if (!PetListingRules.supportsPurchase(pet) || !PetListingRules.isAvailable(pet) || price == null || price <= 0.0) {
            view?.showError("This listing is not purchasable")
            return
        }

        view?.showLoading(true)
        model.createOrder(
            petId = pet.id,
            totalPrice = price,
            onSuccess = {
                view?.showLoading(false)
                val petName = pet.name.orEmpty().ifBlank { "this pet" }
                val message = "Congratulations! You've purchased $petName for $${"%.0f".format(price)}."
                view?.showPurchaseSuccess(message)
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
