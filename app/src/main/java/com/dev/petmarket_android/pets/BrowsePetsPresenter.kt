package com.dev.petmarket_android.pets

class BrowsePetsPresenter(
    private var view: BrowsePetsContract.View?,
    private val model: BrowsePetsModel
) : BrowsePetsContract.Presenter {

    override fun loadPets(search: String, listingType: String) {
        view?.showLoading(true)
        model.loadPets(
            search = search,
            listingType = listingType,
            onSuccess = { pets ->
                view?.showLoading(false)
                view?.showPets(pets)
            },
            onFailure = { error ->
                view?.showLoading(false)
                view?.showError(error)
                view?.showPets(emptyList())
            }
        )
    }

    override fun onPetClicked(petId: Long) {
        view?.navigateToPetDetail(petId)
    }

    override fun onDestroy() {
        view = null
    }
}
