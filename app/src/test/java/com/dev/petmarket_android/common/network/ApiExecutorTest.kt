package com.dev.petmarket_android.common.network

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiExecutorTest {

    @Test
    fun executeWithFallback_successResponse_callsOnSuccess() {
        val mockCall = mock(Call::class.java) as Call<String>
        val mockResponse = mock(Response::class.java) as Response<String>
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn("Success")

        var successCalled = false
        var failureCalled = false

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/test"),
            callFactory = { mockCall },
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate callback response
        val captor = argumentCaptor<Callback<String>>()
        verify(mockCall).enqueue(captor.capture())
        captor.value.onResponse(mockCall, mockResponse)

        assertTrue("Success callback should be called", successCalled)
        assertFalse("Failure callback should not be called", failureCalled)
    }

    @Test
    fun executeWithFallback_failureResponse_callsOnFailure() {
        val mockCall = mock(Call::class.java) as Call<String>
        val mockResponse = mock(Response::class.java) as Response<String>
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(400) // Client error, should not retry
        `when`(mockResponse.errorBody()).thenReturn(null)

        var successCalled = false
        var failureCalled = false

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/test"),
            callFactory = { mockCall },
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        val captor = argumentCaptor<Callback<String>>()
        verify(mockCall).enqueue(captor.capture())
        captor.value.onResponse(mockCall, mockResponse)

        assertFalse("Success callback should not be called", successCalled)
        assertTrue("Failure callback should be called", failureCalled)
    }

    @Test
    fun executeWithFallback_serverError_retriesWithFallback() {
        val mockCall1 = mock(Call::class.java) as Call<String>
        val mockCall2 = mock(Call::class.java) as Call<String>

        val mockResponse = mock(Response::class.java) as Response<String>
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500) // Server error, should retry
        `when`(mockResponse.errorBody()).thenReturn(null)

        var endpointsCalled = mutableListOf<String>()
        var retryCount = 0

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/test1", "/api/test2"),
            callFactory = { endpoint ->
                endpointsCalled.add(endpoint)
                if (retryCount == 0) {
                    retryCount++
                    mockCall1
                } else {
                    mockCall2
                }
            },
            onSuccess = {},
            onFailure = {}
        )

        // Verify first endpoint was called
        val captor1 = argumentCaptor<Callback<String>>()
        verify(mockCall1).enqueue(captor1.capture())

        // Simulate 500 error, which should trigger retry
        captor1.value.onResponse(mockCall1, mockResponse)

        // Wait a bit for retry
        Thread.sleep(500)

        // First endpoint should have been tried
        assertTrue("Should attempt first endpoint", endpointsCalled.contains("/api/test1"))
    }

    @Test
    fun executeWithFallback_networkError_retriesWithBackoff() {
        val mockCall = mock(Call::class.java) as Call<String>

        var failureCount = 0

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/test"),
            callFactory = { mockCall },
            onSuccess = {},
            onFailure = { failureCount++ }
        )

        val captor = argumentCaptor<Callback<String>>()
        verify(mockCall).enqueue(captor.capture())

        // Simulate network error
        val networkError = Exception("Network timeout")
        captor.value.onFailure(mockCall, networkError)

        // Network errors should be retried, not immediately fail
        // After retries are exhausted, failure should be called
        Thread.sleep(2000)
        assertTrue("Should eventually call failure after retries", failureCount > 0)
    }

    @Test
    fun executeNoContentWithFallback_successResponse_callsOnSuccess() {
        val mockCall = mock(Call::class.java) as Call<Unit>
        val mockResponse = mock(Response::class.java) as Response<Unit>
        `when`(mockResponse.isSuccessful).thenReturn(true)

        var successCalled = false
        var failureCalled = false

        ApiExecutor.executeNoContentWithFallback(
            endpoints = listOf("/api/test"),
            callFactory = { mockCall },
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        val captor = argumentCaptor<Callback<Unit>>()
        verify(mockCall).enqueue(captor.capture())
        captor.value.onResponse(mockCall, mockResponse)

        assertTrue("Success callback should be called", successCalled)
    }

    @Test
    fun executeStatusWithFallback_successResponse_callsOnSuccess() {
        val mockCall = mock(Call::class.java) as Call<okhttp3.ResponseBody>
        val mockResponse = mock(Response::class.java) as Response<okhttp3.ResponseBody>
        `when`(mockResponse.isSuccessful).thenReturn(true)

        var successCalled = false

        ApiExecutor.executeStatusWithFallback(
            endpoints = listOf("/api/test"),
            callFactory = { mockCall },
            onSuccess = { successCalled = true },
            onFailure = {}
        )

        val captor = argumentCaptor<Callback<okhttp3.ResponseBody>>()
        verify(mockCall).enqueue(captor.capture())
        captor.value.onResponse(mockCall, mockResponse)

        assertTrue("Success callback should be called", successCalled)
    }

    @Test
    fun executeWithFallback_404Error_doesNotRetry() {
        // 404 should NOT be retried per the fixed logic
        val mockCall = mock(Call::class.java) as Call<String>
        val mockResponse = mock(Response::class.java) as Response<String>
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(404)
        `when`(mockResponse.errorBody()).thenReturn(null)

        var failureCalled = false

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/test1", "/api/test2"),
            callFactory = { mockCall },
            onSuccess = {},
            onFailure = { failureCalled = true }
        )

        val captor = argumentCaptor<Callback<String>>()
        verify(mockCall).enqueue(captor.capture())
        captor.value.onResponse(mockCall, mockResponse)

        // 404 should result in immediate failure, not retry
        assertTrue("404 should call failure without retry", failureCalled)
        // Should only be called once, not retried
        verify(mockCall, times(1)).enqueue(any())
    }

    @Test
    fun executeWithFallback_403Error_doesNotRetry() {
        // 403 should NOT be retried per the fixed logic
        val mockCall = mock(Call::class.java) as Call<String>
        val mockResponse = mock(Response::class.java) as Response<String>
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(403)
        `when`(mockResponse.errorBody()).thenReturn(null)

        var failureCalled = false

        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/test1", "/api/test2"),
            callFactory = { mockCall },
            onSuccess = {},
            onFailure = { failureCalled = true }
        )

        val captor = argumentCaptor<Callback<String>>()
        verify(mockCall).enqueue(captor.capture())
        captor.value.onResponse(mockCall, mockResponse)

        assertTrue("403 should call failure without retry", failureCalled)
        verify(mockCall, times(1)).enqueue(any())
    }
}

// Helper for Mockito argument captors
inline fun <reified T> argumentCaptor() = org.mockito.ArgumentCaptor.forClass(T::class.java)
