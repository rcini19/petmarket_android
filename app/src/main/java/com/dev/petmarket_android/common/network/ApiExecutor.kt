package com.dev.petmarket_android.common.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import kotlin.math.pow

object ApiExecutor {

    private const val MAX_RETRY_ATTEMPTS = 3
    private const val INITIAL_RETRY_DELAY_MS = 100L

    fun <T> executeWithFallback(
        endpoints: List<String>,
        callFactory: (String) -> Call<T>,
        onSuccess: (T) -> Unit,
        onFailure: (String) -> Unit
    ) {
        attempt(
            index = 0,
            endpoints = endpoints,
            callFactory = callFactory,
            onSuccess = onSuccess,
            onFailure = onFailure,
            retryCount = 0
        )
    }

    fun executeNoContentWithFallback(
        endpoints: List<String>,
        callFactory: (String) -> Call<Unit>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        attemptNoContent(
            index = 0,
            endpoints = endpoints,
            callFactory = callFactory,
            onSuccess = onSuccess,
            onFailure = onFailure,
            retryCount = 0
        )
    }

    fun executeStatusWithFallback(
        endpoints: List<String>,
        callFactory: (String) -> Call<ResponseBody>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        attemptStatus(
            index = 0,
            endpoints = endpoints,
            callFactory = callFactory,
            onSuccess = onSuccess,
            onFailure = onFailure,
            retryCount = 0
        )
    }

    fun executeStatusAcrossFallbacks(
        endpoints: List<String>,
        callFactory: (String) -> Call<ResponseBody>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        attemptStatusAcrossFallbacks(
            index = 0,
            endpoints = endpoints,
            callFactory = callFactory,
            onSuccess = onSuccess,
            onFailure = onFailure,
            retryCount = 0,
            lastError = null
        )
    }

    private fun <T> attempt(
        index: Int,
        endpoints: List<String>,
        callFactory: (String) -> Call<T>,
        onSuccess: (T) -> Unit,
        onFailure: (String) -> Unit,
        retryCount: Int
    ) {
        if (index >= endpoints.size) {
            onFailure("Request failed after all attempts")
            return
        }

        callFactory(endpoints[index]).enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                    return
                }

                val status = response.code()

                // Only retry on 5xx server errors
                if (isRetryableStatus(status) && retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attempt(index, endpoints, callFactory, onSuccess, onFailure, retryCount + 1)
                        }
                    )
                    return
                }

                // Try next endpoint only for 5xx errors
                if (isRetryableStatus(status) && index < endpoints.lastIndex) {
                    attempt(index + 1, endpoints, callFactory, onSuccess, onFailure, 0)
                    return
                }

                val errorMessage = response.errorBody()?.string() ?: "Request failed (HTTP $status)"
                onFailure(errorMessage)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                // Retry network errors with exponential backoff
                if (retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attempt(index, endpoints, callFactory, onSuccess, onFailure, retryCount + 1)
                        }
                    )
                    return
                }

                // Try next endpoint if available
                if (index < endpoints.lastIndex) {
                    attempt(index + 1, endpoints, callFactory, onSuccess, onFailure, 0)
                    return
                }

                onFailure(t.message ?: "Network request failed")
            }
        })
    }

    private fun attemptNoContent(
        index: Int,
        endpoints: List<String>,
        callFactory: (String) -> Call<Unit>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        retryCount: Int
    ) {
        if (index >= endpoints.size) {
            onFailure("Request failed after all attempts")
            return
        }

        callFactory(endpoints[index]).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                    return
                }

                val status = response.code()

                if (isRetryableStatus(status) && retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attemptNoContent(index, endpoints, callFactory, onSuccess, onFailure, retryCount + 1)
                        }
                    )
                    return
                }

                if (isRetryableStatus(status) && index < endpoints.lastIndex) {
                    attemptNoContent(index + 1, endpoints, callFactory, onSuccess, onFailure, 0)
                    return
                }

                val errorMessage = response.errorBody()?.string() ?: "Request failed (HTTP $status)"
                onFailure(errorMessage)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                if (retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attemptNoContent(index, endpoints, callFactory, onSuccess, onFailure, retryCount + 1)
                        }
                    )
                    return
                }

                if (index < endpoints.lastIndex) {
                    attemptNoContent(index + 1, endpoints, callFactory, onSuccess, onFailure, 0)
                    return
                }

                onFailure(t.message ?: "Network request failed")
            }
        })
    }

    private fun attemptStatus(
        index: Int,
        endpoints: List<String>,
        callFactory: (String) -> Call<ResponseBody>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        retryCount: Int
    ) {
        if (index >= endpoints.size) {
            onFailure("Request failed after all attempts")
            return
        }

        callFactory(endpoints[index]).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                    return
                }

                val status = response.code()

                if (isRetryableStatus(status) && retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attemptStatus(index, endpoints, callFactory, onSuccess, onFailure, retryCount + 1)
                        }
                    )
                    return
                }

                if (isRetryableStatus(status) && index < endpoints.lastIndex) {
                    attemptStatus(index + 1, endpoints, callFactory, onSuccess, onFailure, 0)
                    return
                }

                val errorMessage = response.errorBody()?.string() ?: "Request failed (HTTP $status)"
                onFailure(errorMessage)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attemptStatus(index, endpoints, callFactory, onSuccess, onFailure, retryCount + 1)
                        }
                    )
                    return
                }

                if (index < endpoints.lastIndex) {
                    attemptStatus(index + 1, endpoints, callFactory, onSuccess, onFailure, 0)
                    return
                }

                onFailure(t.message ?: "Network request failed")
            }
        })
    }

    private fun attemptStatusAcrossFallbacks(
        index: Int,
        endpoints: List<String>,
        callFactory: (String) -> Call<ResponseBody>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        retryCount: Int,
        lastError: String?
    ) {
        if (index >= endpoints.size) {
            onFailure(lastError ?: "Request failed after all attempts")
            return
        }

        callFactory(endpoints[index]).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                    return
                }

                val status = response.code()
                val errorMessage = response.errorBody()?.string() ?: "Request failed (HTTP $status)"

                if (isRetryableStatus(status) && retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attemptStatusAcrossFallbacks(
                                index,
                                endpoints,
                                callFactory,
                                onSuccess,
                                onFailure,
                                retryCount + 1,
                                errorMessage
                            )
                        }
                    )
                    return
                }

                attemptStatusAcrossFallbacks(
                    index = index + 1,
                    endpoints = endpoints,
                    callFactory = callFactory,
                    onSuccess = onSuccess,
                    onFailure = onFailure,
                    retryCount = 0,
                    lastError = errorMessage
                )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val errorMessage = t.message ?: "Network request failed"

                if (retryCount < MAX_RETRY_ATTEMPTS) {
                    scheduleRetry(
                        retryCount = retryCount + 1,
                        block = {
                            attemptStatusAcrossFallbacks(
                                index,
                                endpoints,
                                callFactory,
                                onSuccess,
                                onFailure,
                                retryCount + 1,
                                errorMessage
                            )
                        }
                    )
                    return
                }

                attemptStatusAcrossFallbacks(
                    index = index + 1,
                    endpoints = endpoints,
                    callFactory = callFactory,
                    onSuccess = onSuccess,
                    onFailure = onFailure,
                    retryCount = 0,
                    lastError = errorMessage
                )
            }
        })
    }

    /**
     * Determines if an HTTP status code should trigger a retry.
     * Only retries on 5xx server errors, not on client errors (4xx).
     */
    private fun isRetryableStatus(status: Int): Boolean {
        return status >= 500 && status < 600
    }

    /**
     * Schedules a retry with exponential backoff.
     * Delay = baseDelay * 2^retryCount (capped at reasonable limits)
     */
    private fun scheduleRetry(retryCount: Int, block: () -> Unit) {
        val delayMs = (INITIAL_RETRY_DELAY_MS * 2.0.pow(retryCount.toDouble())).toLong().coerceAtMost(5000)
        Thread {
            Thread.sleep(delayMs)
            block()
        }.start()
    }
}
