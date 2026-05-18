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
        var failedRequests = 0
        var lastError: String? = null

        fun completeOne() {
            completed += 1
            if (completed == 3) {
                if (failedRequests == 3) {
                    onFailure(lastError ?: "Unable to load trade data")
                } else {
                    onSuccess(offers, myPets, tradeablePets)
                }
            }
        }

        fun completeFailed(message: String) {
            failedRequests += 1
            lastError = message
            completeOne()
        }

        repository.getTradeOffers(
            onSuccess = {
                offers = it
                completeOne()
            },
            onFailure = {
                completeFailed(it)
            }
        )

        repository.getMyPets(
            onSuccess = {
                myPets = it
                completeOne()
            },
            onFailure = {
                completeFailed(it)
            }
        )

        repository.getTradeablePets(
            onSuccess = {
                tradeablePets = it
                completeOne()
            },
            onFailure = {
                completeFailed(it)
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
            onSuccess = onSuccess,
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
