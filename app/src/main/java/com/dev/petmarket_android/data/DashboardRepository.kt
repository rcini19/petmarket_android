package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor

class DashboardRepository(context: Context) {

    private val api = ApiClient.getService(context)

    fun getAvailablePets(onSuccess: (List<PetResponse>) -> Unit, onFailure: (String) -> Unit) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint -> api.getPets(endpoint, emptyMap()) },
            onSuccess = { response -> onSuccess(response.content) },
            onFailure = onFailure
        )
    }

    fun getMyActivePets(onSuccess: (List<PetResponse>) -> Unit, onFailure: (String) -> Unit) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets/mine", "/pets/mine"),
            callFactory = { endpoint -> api.getMyPets(endpoint) },
            onSuccess = { response -> onSuccess(response.content) },
            onFailure = onFailure
        )
    }

    fun getTradeOffers(onSuccess: (List<TradeOfferResponse>) -> Unit, onFailure: (String) -> Unit) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/trade-offers", "/api/trades", "/trade-offers", "/trades"),
            callFactory = { endpoint -> api.getTradeOffers(endpoint) },
            onSuccess = { response -> onSuccess(response.content) },
            onFailure = onFailure
        )
    }
}
