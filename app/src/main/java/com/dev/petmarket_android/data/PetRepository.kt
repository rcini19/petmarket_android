package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.OrderRequest
import com.dev.petmarket_android.common.model.OrderResponse
import com.dev.petmarket_android.common.model.PetRequest
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.PaginatedResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor

class PetRepository(context: Context) {

    private val api = ApiClient.getService(context)

    // Original method for backward compatibility
    fun getPets(
        search: String,
        listingType: String,
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Use pagination with default params
        getPetsWithPagination(search, listingType, 0, 20, { response ->
            onSuccess(response.content)
        }, onFailure)
    }

    // New paginated method
    fun getPetsWithPagination(
        search: String,
        listingType: String,
        page: Int,
        pageSize: Int,
        onSuccess: (PaginatedResponse<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val params = mutableMapOf<String, String>()
        if (search.isNotBlank()) {
            params["search"] = search.trim()
        }
        if (listingType.isNotBlank() && listingType != "ALL") {
            params["listingType"] = listingType
        }

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint -> api.getPets(endpoint, params, page, pageSize) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getPetById(
        petId: Long,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets/$petId", "/pets/$petId"),
            callFactory = { endpoint -> api.getPetById(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun createOrder(
        petId: Long,
        totalPrice: Double,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = OrderRequest(petId = petId, totalPrice = totalPrice)
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/orders", "/api/purchases", "/orders", "/purchases"),
            callFactory = { endpoint -> api.createOrder(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun purchasePet(
        petId: Long,
        totalPrice: Double,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = OrderRequest(petId = petId, totalPrice = totalPrice)
        ApiExecutor.executeStatusAcrossFallbacks(
            endpoints = listOf(
                "/api/pets/$petId/purchase",
                "/api/pets/$petId/buy",
                "/pets/$petId/purchase",
                "/pets/$petId/buy"
            ),
            callFactory = { endpoint -> api.purchasePet(endpoint) },
            onSuccess = onSuccess,
            onFailure = {
                ApiExecutor.executeStatusAcrossFallbacks(
                    endpoints = listOf(
                        "/api/pets/$petId/purchase",
                        "/api/pets/$petId/buy",
                        "/api/purchases",
                        "/api/orders",
                        "/pets/$petId/purchase",
                        "/pets/$petId/buy",
                        "/purchases",
                        "/orders"
                    ),
                    callFactory = { endpoint -> api.purchasePet(endpoint, payload) },
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
        )
    }

    // Original method for backward compatibility
    fun getMyPets(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        getMyPetsWithPagination(0, 20, { response ->
            onSuccess(response.content)
        }, onFailure)
    }

    // New paginated method
    fun getMyPetsWithPagination(
        page: Int,
        pageSize: Int,
        onSuccess: (PaginatedResponse<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets/mine", "/pets/mine"),
            callFactory = { endpoint -> api.getMyPets(endpoint, page, pageSize) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun createPet(
        payload: PetRequest,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint -> api.createPet(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun updatePet(
        petId: Long,
        payload: PetRequest,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets/$petId", "/pets/$petId"),
            callFactory = { endpoint -> api.updatePet(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deletePet(
        petId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeNoContentWithFallback(
            endpoints = listOf("/api/pets/$petId", "/pets/$petId"),
            callFactory = { endpoint -> api.deletePet(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
