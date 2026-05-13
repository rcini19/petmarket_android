package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferRequest
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.model.PaginatedResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor

class TradeRepository(context: Context) {

    private val api = ApiClient.getService(context)

    // Original method for backward compatibility
    fun getTradeOffers(
        onSuccess: (List<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        getTradeOffersWithPagination(0, 20, { response ->
            onSuccess(response.content)
        }, onFailure)
    }

    // New paginated method
    fun getTradeOffersWithPagination(
        page: Int,
        pageSize: Int,
        onSuccess: (PaginatedResponse<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/trade-offers", "/api/trades", "/trade-offers", "/trades"),
            callFactory = { endpoint -> api.getTradeOffers(endpoint, emptyMap(), page, pageSize) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getMyPets(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets/mine", "/pets/mine"),
            callFactory = { endpoint -> api.getMyPets(endpoint) },
            onSuccess = { response -> onSuccess(response.content) },
            onFailure = onFailure
        )
    }

    fun getTradeablePets(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint -> api.getPets(endpoint, mapOf("listingType" to "TRADE"), 0, 20) },
            onSuccess = { response ->
                onSuccess(response.content)
            },
            onFailure = onFailure
        )
    }

    fun createTradeOffer(
        offeredPetId: Long,
        requestedPetId: Long,
        onSuccess: (TradeOfferResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = TradeOfferRequest(offeredPetId = offeredPetId, requestedPetId = requestedPetId)
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/trade-offers", "/api/trades", "/trade-offers", "/trades"),
            callFactory = { endpoint -> api.createTradeOffer(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun acceptTradeOffer(
        tradeOfferId: Long,
        onSuccess: (TradeOfferResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf(
                "/api/trade-offers/$tradeOfferId/accept",
                "/api/trades/$tradeOfferId/accept",
                "/trade-offers/$tradeOfferId/accept",
                "/trades/$tradeOfferId/accept"
            ),
            callFactory = { endpoint -> api.acceptTradeOffer(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun rejectTradeOffer(
        tradeOfferId: Long,
        onSuccess: (TradeOfferResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf(
                "/api/trade-offers/$tradeOfferId/reject",
                "/api/trades/$tradeOfferId/reject",
                "/trade-offers/$tradeOfferId/reject",
                "/trades/$tradeOfferId/reject"
            ),
            callFactory = { endpoint -> api.rejectTradeOffer(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
