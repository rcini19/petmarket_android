package com.dev.petmarket_android.trades

class TradesPresenter(
    private var view: TradesContract.View?,
    private val model: TradesModel
) : TradesContract.Presenter {

    override fun loadData() {
        view?.showLoading(true)
        model.loadData(
            onSuccess = { offers, myPets, tradeablePets ->
                view?.showLoading(false)
                view?.showTradeOffers(offers)
                view?.showMyPets(myPets)
                view?.showTradeablePets(tradeablePets)
            },
            onFailure = { error ->
                view?.showLoading(false)
                view?.showError(error)
                view?.showTradeOffers(emptyList())
                view?.showMyPets(emptyList())
                view?.showTradeablePets(emptyList())
            }
        )
    }

    override fun onCreateTradeOffer(offeredPetId: Long?, requestedPetId: Long?) {
        if (offeredPetId == null || requestedPetId == null) {
            view?.showError("Select both offered and requested pets")
            return
        }

        view?.showLoading(true)
        model.createTradeOffer(
            offeredPetId = offeredPetId,
            requestedPetId = requestedPetId,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Trade offer submitted")
                loadData()
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onAcceptTradeOffer(tradeOfferId: Long) {
        view?.showLoading(true)
        model.acceptTradeOffer(
            tradeOfferId = tradeOfferId,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Trade offer accepted")
                loadData()
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onRejectTradeOffer(tradeOfferId: Long) {
        view?.showLoading(true)
        model.rejectTradeOffer(
            tradeOfferId = tradeOfferId,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Trade offer rejected")
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
