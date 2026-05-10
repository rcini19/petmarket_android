package com.dev.petmarket_android.common.security

import android.util.Base64
import org.junit.Assert.*
import org.junit.Test

class JwtUtilsTest {

    @Test
    fun isTokenUsable_validToken_returnsTrue() {
        // Create a valid JWT token with future expiration
        val futureExp = (System.currentTimeMillis() / 1000) + 3600 // 1 hour from now
        val header = Base64.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val payload = Base64.encodeToString("""{"exp":$futureExp,"sub":"user123"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val signature = "fake_signature"
        val token = "$header.$payload.$signature"

        val result = JwtUtils.isTokenUsable(token)
        assertTrue("Token with future expiration should be usable", result)
    }

    @Test
    fun isTokenUsable_expiredToken_returnsFalse() {
        // Create an expired JWT token
        val pastExp = (System.currentTimeMillis() / 1000) - 3600 // 1 hour ago
        val header = Base64.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val payload = Base64.encodeToString("""{"exp":$pastExp,"sub":"user123"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val signature = "fake_signature"
        val token = "$header.$payload.$signature"

        val result = JwtUtils.isTokenUsable(token)
        assertFalse("Expired token should not be usable", result)
    }

    @Test
    fun isTokenUsable_tokenWithoutExpiration_returnsTrue() {
        // Token without exp claim is considered usable
        val header = Base64.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val payload = Base64.encodeToString("""{"sub":"user123"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val signature = "fake_signature"
        val token = "$header.$payload.$signature"

        val result = JwtUtils.isTokenUsable(token)
        assertTrue("Token without expiration claim should be usable", result)
    }

    @Test
    fun isTokenUsable_nullToken_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            JwtUtils.isTokenUsable(null)
        }
    }

    @Test
    fun isTokenUsable_emptyToken_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            JwtUtils.isTokenUsable("")
        }
    }

    @Test
    fun isTokenUsable_nullStringToken_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            JwtUtils.isTokenUsable("null")
        }
    }

    @Test
    fun isTokenUsable_invalidFormat_throwsException() {
        // Token with less than 3 parts
        assertThrows(IllegalArgumentException::class.java) {
            JwtUtils.isTokenUsable("invalid.token")
        }
    }

    @Test
    fun isTokenUsable_invalidBase64_throwsException() {
        // Token with invalid base64 encoding
        assertThrows(IllegalArgumentException::class.java) {
            JwtUtils.isTokenUsable("!!!.!!!.!!!")
        }
    }

    @Test
    fun isTokenUsable_invalidJsonPayload_throwsException() {
        // Token with invalid JSON in payload
        val header = Base64.encodeToString("""{"alg":"HS256"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val payload = Base64.encodeToString("""invalid json""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val signature = "fake"

        assertThrows(IllegalArgumentException::class.java) {
            JwtUtils.isTokenUsable("$header.$payload.$signature")
        }
    }

    @Test
    fun isTokenUsableSafely_validToken_returnsTrue() {
        val futureExp = (System.currentTimeMillis() / 1000) + 3600
        val header = Base64.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val payload = Base64.encodeToString("""{"exp":$futureExp}""".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val token = "$header.$payload.sig"

        val result = JwtUtils.isTokenUsableSafely(token)
        assertTrue(result)
    }

    @Test
    fun isTokenUsableSafely_invalidToken_returnsFalse() {
        // Safe method should return false for any error instead of throwing
        val result = JwtUtils.isTokenUsableSafely("invalid.token")
        assertFalse(result)
    }

    @Test
    fun isTokenUsableSafely_nullToken_returnsFalse() {
        val result = JwtUtils.isTokenUsableSafely(null)
        assertFalse(result)
    }
}
