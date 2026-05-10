package com.dev.petmarket_android.admin

class AdminPresenter(
    private var view: AdminContract.View?,
    private val model: AdminModel
) : AdminContract.Presenter {

    override fun loadData() {
        view?.showLoading(true)
        model.loadData(
            onSuccess = { pets, users ->
                view?.showLoading(false)
                view?.showAdminPets(pets)
                view?.showAdminUsers(users)
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onDeletePet(petId: Long) {
        view?.showLoading(true)
        model.deletePet(
            petId = petId,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Listing deleted")
                loadData()
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onSuspendUser(userId: Long) {
        view?.showLoading(true)
        model.suspendUser(
            userId = userId,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("User suspended")
                loadData()
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onDestroy() {
        view = null
    }
}
