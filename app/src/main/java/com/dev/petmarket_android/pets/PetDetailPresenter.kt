package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse

class PetDetailPresenter(
    private var view: PetDetailContract.View?,
    private val model: PetDetailModel
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

        val listingType = pet.listingType.orEmpty().uppercase()
        val status = pet.status.orEmpty().uppercase()
        val price = pet.price

        if ((listingType != "SALE" && listingType != "BOTH") || status != "AVAILABLE" || price == null || price <= 0.0) {
            view?.showError("This listing is not purchasable")
            return
        }

        view?.showLoading(true)
        model.createOrder(
            petId = pet.id,
            totalPrice = price,
            onSuccess = { order ->
                view?.showLoading(false)
                val message = "Order #${order.id} completed"
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
