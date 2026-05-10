package com.dev.petmarket_android.common.security

import android.util.Base64
import org.json.JSONObject

object JwtUtils {

    /**
     * Validates if a JWT token is usable (not null, properly formatted, and not expired).
     * Throws descriptive exceptions for different failure modes.
     *
     * @param token The JWT token to validate
     * @return true if token is valid and not expired, false if expired
     * @throws IllegalArgumentException if token format is invalid
     */
    fun isTokenUsable(token: String?): Boolean {
        if (token.isNullOrBlank()) {
            throw IllegalArgumentException("Token is null or blank")
        }

        val normalized = token.trim().removeSurrounding("\"")
        if (normalized.equals("null", true) || normalized.equals("undefined", true)) {
            throw IllegalArgumentException("Token is null or undefined string")
        }

        val parts = normalized.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT format: expected 3 parts, got ${parts.size}")
        }

        // Validate header
        try {
            val headerJson = decodeBase64Part(parts[0])
            JSONObject(headerJson) // Validate it's valid JSON
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JWT header: ${e.message}", e)
        }

        // Validate payload and check expiration
        return try {
            val payloadJson = decodeBase64Part(parts[1])
            val payload = JSONObject(payloadJson)
            val expSeconds = payload.optLong("exp", 0L)

            if (expSeconds <= 0L) {
                // No expiration claim, token is usable
                return true
            }

            val nowSeconds = System.currentTimeMillis() / 1000
            if (expSeconds <= nowSeconds) {
                // Token has expired
                return false
            }

            // Token is valid and not expired
            true
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JWT payload: ${e.message}", e)
        }
    }

    /**
     * Safely decodes a Base64-URL encoded string without padding.
     */
    private fun decodeBase64Part(part: String): String {
        return try {
            val decoded = Base64.decode(part, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            String(decoded, Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Failed to decode Base64 part: ${e.message}", e)
        }
    }

    /**
     * Validates token and returns a safe boolean without throwing.
     * Returns false for any validation error (expired, invalid, or null).
     */
    fun isTokenUsableSafely(token: String?): Boolean {
        return try {
            isTokenUsable(token)
        } catch (e: Exception) {
            false
        }
    }
}
