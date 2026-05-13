package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.PaginatedResponse
import com.dev.petmarket_android.common.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepository(context: Context) {

    private val api = ApiClient.getService(context)

    fun getAvailablePetsCount(onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        executeFilteredPaginatedCount(
            endpoints = listOf("/api/pets", "/pets"),
            callFactory = { endpoint, page, pageSize ->
                api.getPets(
                    endpoint,
                    mapOf("status" to "AVAILABLE"),
                    page = page,
                    pageSize = pageSize
                )
            },
            itemMatches = { pet ->
                val status = pet.status.orEmpty()
                status.isBlank() || status.equals("AVAILABLE", ignoreCase = true)
            },
            canTrustTotal = { false },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getMyPetsCount(onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        executeFilteredPaginatedCount(
            endpoints = listOf("/api/pets/mine", "/pets/mine"),
            callFactory = { endpoint, page, pageSize -> api.getMyPets(endpoint, page = page, pageSize = pageSize) },
            itemMatches = { true },
            canTrustTotal = { true },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getPendingTradeOffersCount(onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        executeFilteredPaginatedCount(
            endpoints = listOf(
                "/api/trade-offers/mine",
                "/api/trades/mine",
                "/api/trade-offers/me",
                "/api/trades/me",
                "/api/trade-offers",
                "/api/trades",
                "/trade-offers/mine",
                "/trades/mine",
                "/trade-offers/me",
                "/trades/me",
                "/trade-offers",
                "/trades"
            ),
            callFactory = { endpoint, page, pageSize ->
                api.getTradeOffers(
                    endpoint,
                    mapOf("status" to "PENDING"),
                    page = page,
                    pageSize = pageSize
                )
            },
            itemMatches = { tradeOffer ->
                tradeOffer.status.orEmpty().equals("PENDING", ignoreCase = true)
            },
            canTrustTotal = { false },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun <T> executeFilteredPaginatedCount(
        endpoints: List<String>,
        callFactory: (String, Int, Int) -> Call<PaginatedResponse<T>>,
        itemMatches: (T) -> Boolean,
        canTrustTotal: (PaginatedResponse<T>) -> Boolean,
        onSuccess: (Int) -> Unit,
        onFailure: (String) -> Unit
    ) {
        fun attempt(index: Int, lastError: String?) {
            if (index >= endpoints.size) {
                onFailure(lastError ?: "Dashboard count request failed")
                return
            }

            fun loadPage(page: Int, accumulatedCount: Int) {
                callFactory(endpoints[index], page, DASHBOARD_COUNT_PAGE_SIZE).enqueue(
                    object : Callback<PaginatedResponse<T>> {
                        override fun onResponse(
                            call: Call<PaginatedResponse<T>>,
                            response: Response<PaginatedResponse<T>>
                        ) {
                            val body = response.body()
                            if (response.isSuccessful && body != null) {
                                val reportedTotal = body.pageInfo.totalElements.toInt()
                                if (page == 0 && canTrustTotal(body) && (reportedTotal > 0 || body.content.isEmpty())) {
                                    onSuccess(reportedTotal)
                                    return
                                }

                                val nextCount = accumulatedCount + body.content.count(itemMatches)
                                val hasNextPage = body.pageInfo.hasNext ||
                                    (body.pageInfo.totalPages > 0 && page + 1 < body.pageInfo.totalPages)
                                if (hasNextPage) {
                                    loadPage(page + 1, nextCount)
                                } else {
                                    onSuccess(nextCount)
                                }
                                return
                            }

                            attempt(index + 1, response.errorBody()?.string() ?: "HTTP ${response.code()}")
                        }

                        override fun onFailure(call: Call<PaginatedResponse<T>>, t: Throwable) {
                            attempt(index + 1, t.message)
                        }
                    }
                )
            }

            loadPage(page = 0, accumulatedCount = 0)
        }

        attempt(index = 0, lastError = null)
    }

    private companion object {
        const val DASHBOARD_COUNT_PAGE_SIZE = 100
    }
}
