package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.PageInfo
import com.dev.petmarket_android.common.model.PaginatedResponse
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferRequest
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.util.PetListingRules
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TradeRepository(context: Context) {

    private val api = ApiClient.getService(context)
    private val sessionManager = SessionManager(context.applicationContext)
    private val gson = Gson()
    private val tradeEndpoints = listOf("/api/trades", "/trades")

    fun getTradeOffers(
        onSuccess: (List<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        getTradeOffersWithPagination(0, 100, { response ->
            onSuccess(response.content)
        }, onFailure)
    }

    fun getTradeOffersWithPagination(
        page: Int,
        pageSize: Int,
        onSuccess: (PaginatedResponse<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        executeRawTradeOfferList(
            endpoints = tradeEndpoints,
            page = page,
            pageSize = pageSize,
            onSuccess = { items ->
                onSuccess(
                    PaginatedResponse(
                        content = items,
                        pageInfo = PageInfo(
                            page = page,
                            pageSize = pageSize,
                            totalElements = items.size.toLong(),
                            totalPages = 1,
                            hasNext = false,
                            hasPrevious = page > 0
                        )
                    )
                )
            },
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
            onSuccess = { response ->
                val availableOwnedPets = response.content.filter { pet ->
                    PetListingRules.isAvailable(pet) && PetListingRules.supportsTrade(pet)
                }
                if (availableOwnedPets.isNotEmpty()) {
                    onSuccess(availableOwnedPets)
                } else {
                    getAvailableOwnedPetsFromMarketplace(onSuccess, onFailure)
                }
            },
            onFailure = {
                getAvailableOwnedPetsFromMarketplace(onSuccess, onFailure)
            }
        )
    }

    fun getTradeablePets(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint -> api.getPets(endpoint, emptyMap(), 0, 100) },
            onSuccess = { response ->
                onSuccess(
                    response.content.filter { pet ->
                        PetListingRules.isAvailable(pet) && PetListingRules.supportsTrade(pet)
                    }
                )
            },
            onFailure = onFailure
        )
    }

    private fun getAvailableOwnedPetsFromMarketplace(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint -> api.getPets(endpoint, emptyMap(), 0, 100) },
            onSuccess = { response ->
                onSuccess(
                    response.content.filter { pet ->
                        PetListingRules.isAvailable(pet) &&
                            PetListingRules.supportsTrade(pet) &&
                            PetListingRules.isOwnedByCurrentUser(pet, sessionManager)
                    }
                )
            },
            onFailure = onFailure
        )
    }

    fun createTradeOffer(
        offeredPetId: Long,
        requestedPetId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = tradeEndpoints,
            callFactory = { endpoint ->
                api.createTradeOffer(
                    endpoint,
                    TradeOfferRequest(
                        offeredPetId = offeredPetId,
                        requestedPetId = requestedPetId
                    )
                )
            },
            onSuccess = { onSuccess() },
            onFailure = onFailure
        )
    }

    private fun executeRawTradeOfferList(
        endpoints: List<String>,
        page: Int,
        pageSize: Int,
        onSuccess: (List<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        fun attempt(index: Int, lastError: String?) {
            if (index >= endpoints.size) {
                onFailure(lastError ?: "Trade offers request failed")
                return
            }

            api.getTradeHistoryRaw(endpoints[index], page = page, pageSize = pageSize)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()?.string().orEmpty()
                        if (response.isSuccessful) {
                            runCatching { parseTradeOfferList(body) }
                                .onSuccess(onSuccess)
                                .onFailure { attempt(index + 1, it.message) }
                            return
                        }

                        attempt(index + 1, response.errorBody()?.string() ?: "HTTP ${response.code()}")
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        attempt(index + 1, t.message)
                    }
                })
        }

        attempt(index = 0, lastError = null)
    }

    private fun parseTradeOfferList(body: String): List<TradeOfferResponse> {
        if (body.isBlank()) {
            return emptyList()
        }

        val root = JsonParser.parseString(body)
        val array = findArray(
            element = root,
            keys = listOf("content", "tradeOffers", "trades", "items", "data", "results")
        )
        if (array != null) {
            return array.mapNotNull { item ->
                runCatching { gson.fromJson(item, TradeOfferResponse::class.java) }.getOrNull()
            }
        }

        return parseSingleTradeOffer(root)?.let(::listOf).orEmpty()
    }

    private fun parseSingleTradeOffer(root: JsonElement): TradeOfferResponse? {
        if (!root.isJsonObject) {
            return null
        }

        val obj = root.asJsonObject
        val nested = listOf("tradeOffer", "trade", "data", "result", "payload")
            .firstNotNullOfOrNull { key -> obj.get(key)?.takeIf { it.isJsonObject } }

        return gson.fromJson(nested ?: obj, TradeOfferResponse::class.java)
    }

    private fun findArray(element: JsonElement?, keys: List<String>): JsonArray? {
        if (element == null || element.isJsonNull) {
            return null
        }

        if (element.isJsonArray) {
            return element.asJsonArray
        }

        if (!element.isJsonObject) {
            return null
        }

        val obj = element.asJsonObject
        keys.forEach { key ->
            val value = obj.get(key)
            if (value?.isJsonArray == true) {
                return value.asJsonArray
            }
        }

        listOf("payload", "body", "response", "result").forEach { key ->
            val nested = findArray(obj.get(key), keys)
            if (nested != null) {
                return nested
            }
        }

        return null
    }

    fun acceptTradeOffer(
        tradeOfferId: Long,
        onSuccess: (TradeOfferResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = tradeEndpoints.map { endpoint -> "$endpoint/$tradeOfferId/accept" },
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
            endpoints = tradeEndpoints.map { endpoint -> "$endpoint/$tradeOfferId/reject" },
            callFactory = { endpoint -> api.rejectTradeOffer(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
