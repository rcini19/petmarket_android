package com.dev.petmarket_android.common.util

import org.junit.Assert.*
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun validateEmail_validEmail_returnsSuccess() {
        val result = ValidationUtils.validateEmail("user@example.com")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateEmail_invalidEmail_returnsError() {
        val result = ValidationUtils.validateEmail("invalid-email")
        assertFalse(result.isSuccess())
        assertEquals("Email format is invalid", result.getErrorMessage())
    }

    @Test
    fun validateEmail_emptyEmail_returnsError() {
        val result = ValidationUtils.validateEmail("")
        assertFalse(result.isSuccess())
        assertEquals("Email is required", result.getErrorMessage())
    }

    @Test
    fun validateEmail_nullEmail_returnsError() {
        val result = ValidationUtils.validateEmail(null)
        assertFalse(result.isSuccess())
    }

    @Test
    fun validatePassword_validPassword_returnsSuccess() {
        val result = ValidationUtils.validatePassword("MyPassword123")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validatePassword_shortPassword_returnsError() {
        val result = ValidationUtils.validatePassword("Pass123")
        assertFalse(result.isSuccess())
        assertEquals("Password must be at least 8 characters", result.getErrorMessage())
    }

    @Test
    fun validatePassword_emptyPassword_returnsError() {
        val result = ValidationUtils.validatePassword("")
        assertFalse(result.isSuccess())
        assertEquals("Password is required", result.getErrorMessage())
    }

    @Test
    fun validatePasswordMatch_passwordsMatch_returnsSuccess() {
        val result = ValidationUtils.validatePasswordMatch("MyPassword123", "MyPassword123")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validatePasswordMatch_passwordsDontMatch_returnsError() {
        val result = ValidationUtils.validatePasswordMatch("MyPassword123", "DifferentPass456")
        assertFalse(result.isSuccess())
        assertEquals("Passwords do not match", result.getErrorMessage())
    }

    @Test
    fun validateFullName_validName_returnsSuccess() {
        val result = ValidationUtils.validateFullName("John Doe")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateFullName_shortName_returnsError() {
        val result = ValidationUtils.validateFullName("J")
        assertFalse(result.isSuccess())
        assertEquals("Name must be at least 2 characters", result.getErrorMessage())
    }

    @Test
    fun validateFullName_emptyName_returnsError() {
        val result = ValidationUtils.validateFullName("")
        assertFalse(result.isSuccess())
        assertEquals("Full name is required", result.getErrorMessage())
    }

    @Test
    fun validatePetName_validName_returnsSuccess() {
        val result = ValidationUtils.validatePetName("Fluffy")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateSpecies_validSpecies_returnsSuccess() {
        val result = ValidationUtils.validateSpecies("Dog")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateBreed_validBreed_returnsSuccess() {
        val result = ValidationUtils.validateBreed("Golden Retriever")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateAge_validAge_returnsSuccess() {
        val result = ValidationUtils.validateAge("5")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateAge_negativeAge_returnsError() {
        val result = ValidationUtils.validateAge("-5")
        assertFalse(result.isSuccess())
        assertEquals("Age cannot be negative", result.getErrorMessage())
    }

    @Test
    fun validateAge_invalidNumber_returnsError() {
        val result = ValidationUtils.validateAge("abc")
        assertFalse(result.isSuccess())
        assertEquals("Age must be a valid number", result.getErrorMessage())
    }

    @Test
    fun validatePrice_validPrice_returnsSuccess() {
        val result = ValidationUtils.validatePrice("99.99")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validatePrice_negativePrice_returnsError() {
        val result = ValidationUtils.validatePrice("-50.00")
        assertFalse(result.isSuccess())
        assertEquals("Price cannot be negative", result.getErrorMessage())
    }

    @Test
    fun validatePrice_invalidNumber_returnsError() {
        val result = ValidationUtils.validatePrice("invalid")
        assertFalse(result.isSuccess())
        assertEquals("Price must be a valid number", result.getErrorMessage())
    }

    @Test
    fun validateDescription_emptyDescription_returnsSuccess() {
        val result = ValidationUtils.validateDescription("")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateDescription_validDescription_returnsSuccess() {
        val result = ValidationUtils.validateDescription("This is a beautiful pet")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateImageUrl_validUrl_returnsSuccess() {
        val result = ValidationUtils.validateImageUrl("https://example.com/image.jpg")
        assertTrue(result.isSuccess())
    }

    @Test
    fun validateImageUrl_invalidUrl_returnsError() {
        val result = ValidationUtils.validateImageUrl("not a valid url")
        assertFalse(result.isSuccess())
        assertEquals("Image URL is invalid", result.getErrorMessage())
    }

    @Test
    fun validateImageUrl_emptyUrl_returnsSuccess() {
        val result = ValidationUtils.validateImageUrl("")
        assertTrue(result.isSuccess())
    }
}
