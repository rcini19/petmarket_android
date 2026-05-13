package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.OrderHistoryResponse
import com.dev.petmarket_android.common.model.PasswordUpdateRequest
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.ProfileImageUpdateRequest
import com.dev.petmarket_android.common.model.ProfileUpdateRequest
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository(context: Context) {

    private val api = ApiClient.getService(context)
    private val gson = Gson()

    fun getProfile(
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/profile/me", "/profile/me"),
            callFactory = { endpoint -> api.getProfile(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun updateProfile(
        fullName: String,
        email: String,
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = ProfileUpdateRequest(fullName = fullName, email = email)
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/profile/me", "/profile/me"),
            callFactory = { endpoint -> api.updateProfile(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    // Original method for backward compatibility
    fun getOrderHistory(
        onSuccess: (List<OrderHistoryResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        executeRawHistory(
            endpoints = listOf(
                "/api/profile/me/orders",
                "/api/profile/me/purchases",
                "/api/orders/me",
                "/api/purchases/me",
                "/api/orders/mine",
                "/api/purchases/mine",
                "/api/orders",
                "/api/purchases",
                "/profile/me/orders",
                "/profile/me/purchases",
                "/orders/me",
                "/purchases/me",
                "/orders/mine",
                "/purchases/mine",
                "/orders",
                "/purchases"
            ),
            callFactory = { endpoint -> api.getOrderHistoryRaw(endpoint, page = 0, pageSize = 100) },
            parser = { body ->
                parseHistoryList(
                    body = body,
                    keys = listOf("content", "orders", "purchases", "orderHistory", "purchaseHistory", "items", "data", "results"),
                    itemClass = OrderHistoryResponse::class.java
                )
            },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun updateProfileImage(
        profileImageUrl: String,
        onSuccess: (ProfileResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = ProfileImageUpdateRequest(profileImageUrl = profileImageUrl)
        ApiExecutor.executeWithFallback(
            endpoints = listOf(
                "/api/profile/me/image",
                "/api/profile/me/photo",
                "/profile/me/image",
                "/profile/me/photo"
            ),
            callFactory = { endpoint -> api.updateProfileImage(endpoint, payload) },
            onSuccess = { response ->
                response.profile?.let(onSuccess) ?: onFailure(response.message ?: "Profile photo update failed")
            },
            onFailure = onFailure
        )
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = PasswordUpdateRequest(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )
        ApiExecutor.executeStatusWithFallback(
            endpoints = listOf(
                "/api/profile/me/password",
                "/profile/me/password",
                "/api/auth/change-password",
                "/auth/change-password"
            ),
            callFactory = { endpoint -> api.changePassword(endpoint, payload) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    // Original method for backward compatibility
    fun getTradeHistory(
        onSuccess: (List<TradeOfferResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        executeRawHistory(
            endpoints = listOf(
                "/api/profile/me/trade-offers",
                "/api/profile/me/trades",
                "/api/trade-offers/me",
                "/api/trades/me",
                "/api/trade-offers/mine",
                "/api/trades/mine",
                "/api/trade-offers/history",
                "/api/trades/history",
                "/api/trade-offers",
                "/api/trades",
                "/profile/me/trade-offers",
                "/profile/me/trades",
                "/trade-offers/me",
                "/trades/me",
                "/trade-offers/mine",
                "/trades/mine",
                "/trade-offers/history",
                "/trades/history",
                "/trade-offers",
                "/trades"
            ),
            callFactory = { endpoint -> api.getTradeHistoryRaw(endpoint, page = 0, pageSize = 100) },
            parser = { body ->
                parseHistoryList(
                    body = body,
                    keys = listOf("content", "tradeOffers", "trades", "tradeHistory", "items", "data", "results"),
                    itemClass = TradeOfferResponse::class.java
                )
            },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun <T> executeRawHistory(
        endpoints: List<String>,
        callFactory: (String) -> Call<ResponseBody>,
        parser: (String) -> List<T>,
        onSuccess: (List<T>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        fun attempt(index: Int, lastError: String?) {
            if (index >= endpoints.size) {
                onFailure(lastError ?: "History request failed")
                return
            }

            callFactory(endpoints[index]).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val body = response.body()?.string()
                    if (response.isSuccessful && !body.isNullOrBlank()) {
                        runCatching { parser(body) }
                            .onSuccess { parsed ->
                                if (parsed.isNotEmpty() || index == endpoints.lastIndex) {
                                    onSuccess(parsed)
                                } else {
                                    attempt(index + 1, "Empty history response")
                                }
                            }
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

    private fun <T> parseHistoryList(
        body: String,
        keys: List<String>,
        itemClass: Class<T>
    ): List<T> {
        val root = JsonParser.parseString(body)
        val array = findArray(root, keys) ?: return emptyList()
        return array.mapNotNull { item ->
            runCatching { gson.fromJson(item, itemClass) }.getOrNull()
        }
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
}
