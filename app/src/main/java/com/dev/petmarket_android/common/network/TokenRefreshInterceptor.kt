package com.dev.petmarket_android.common.network

import android.content.Context
import com.dev.petmarket_android.common.security.JwtUtils
import com.dev.petmarket_android.common.storage.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that handles token refresh when a 401 (Unauthorized) response is received.
 * Automatically attempts to refresh the token and retry the request.
 */
class TokenRefreshInterceptor(private val context: Context) : Interceptor {

    private val sessionManager = SessionManager(context.applicationContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // If response is 401, try to refresh token and retry
        if (response.code == 401) {
            synchronized(this) {
                // Double-check pattern to ensure we don't refresh multiple times
                val currentToken = sessionManager.getToken()

                // Check if token is still invalid
                if (currentToken != null && !JwtUtils.isTokenUsableSafely(currentToken)) {
                    // Attempt to refresh token
                    if (refreshToken()) {
                        // Retry original request with new token
                        val newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer ${sessionManager.getToken()}")
                            .build()
                        response.close()
                        return chain.proceed(newRequest)
                    }
                }
            }
        }

        return response
    }

    /**
     * Attempts to refresh the authentication token.
     * Should be implemented to call the backend's token refresh endpoint.
     *
     * @return true if token was successfully refreshed, false otherwise
     */
    private fun refreshToken(): Boolean {
        // This would typically call the backend's refresh endpoint
        // For now, we just mark the token as invalid
        // In a complete implementation, you would:
        // 1. Call POST /api/auth/refresh with the current token
        // 2. Get a new token from the response
        // 3. Save it with sessionManager.saveToken(newToken)
        // 4. Return true if successful

        // For this implementation, returning false forces the user to re-login
        return false
    }
}
