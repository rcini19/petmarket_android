package com.dev.petmarket_android.trades

import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.data.TradeRepository

class TradesModel(private val repository: TradeRepository) {

    fun loadData(
        onSuccess: (List<TradeOfferResponse>, List<PetResponse>, List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        var offers: List<TradeOfferResponse> = emptyList()
        var myPets: List<PetResponse> = emptyList()
        var tradeablePets: List<PetResponse> = emptyList()
        var completed = 0
        var failed = false

        fun completeOne() {
            completed += 1
            if (!failed && completed == 3) {
                onSuccess(offers, myPets, tradeablePets)
            }
        }

        repository.getTradeOffers(
            onSuccess = {
                offers = it
                completeOne()
            },
            onFailure = {
                if (!failed) {
                    failed = true
                    onFailure(it)
                }
            }
        )

        repository.getMyPets(
            onSuccess = {
                myPets = it
                completeOne()
            },
            onFailure = {
                if (!failed) {
                    failed = true
                    onFailure(it)
                }
            }
        )

        repository.getTradeablePets(
            onSuccess = {
                tradeablePets = it
                completeOne()
            },
            onFailure = {
                if (!failed) {
                    failed = true
                    onFailure(it)
                }
            }
        )
    }

    fun createTradeOffer(
        offeredPetId: Long,
        requestedPetId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.createTradeOffer(
            offeredPetId = offeredPetId,
            requestedPetId = requestedPetId,
            onSuccess = { onSuccess() },
            onFailure = onFailure
        )
    }

    fun acceptTradeOffer(
        tradeOfferId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.acceptTradeOffer(
            tradeOfferId = tradeOfferId,
            onSuccess = { onSuccess() },
            onFailure = onFailure
        )
    }

    fun rejectTradeOffer(
        tradeOfferId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.rejectTradeOffer(
            tradeOfferId = tradeOfferId,
            onSuccess = { onSuccess() },
            onFailure = onFailure
        )
    }
}
