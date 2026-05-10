package com.dev.petmarket_android.trades

import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse

interface TradesContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showError(message: String)
        fun showTradeOffers(items: List<TradeOfferResponse>)
        fun showMyPets(items: List<PetResponse>)
        fun showTradeablePets(items: List<PetResponse>)
        fun showSuccess(message: String)
    }

    interface Presenter {
        fun loadData()
        fun onCreateTradeOffer(offeredPetId: Long?, requestedPetId: Long?)
        fun onAcceptTradeOffer(tradeOfferId: Long)
        fun onRejectTradeOffer(tradeOfferId: Long)
        fun onDestroy()
    }
}
