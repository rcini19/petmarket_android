package com.dev.petmarket_android.common.util

/**
 * Centralized input validation utility for the application.
 * Provides validation for common fields like email, password, names, etc.
 */
object ValidationUtils {

    private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MIN_NAME_LENGTH = 2
    private const val MAX_NAME_LENGTH = 100
    private const val MAX_EMAIL_LENGTH = 254
    private const val MAX_DESCRIPTION_LENGTH = 5000

    /**
     * Validates an email address format.
     * @return ValidationResult with success or error message
     */
    fun validateEmail(email: String?): ValidationResult {
        if (email.isNullOrBlank()) {
            return ValidationResult.Error("Email is required")
        }

        val trimmed = email.trim()
        if (trimmed.length > MAX_EMAIL_LENGTH) {
            return ValidationResult.Error("Email is too long (max $MAX_EMAIL_LENGTH characters)")
        }

        if (!trimmed.matches(EMAIL_REGEX.toRegex())) {
            return ValidationResult.Error("Email format is invalid")
        }

        return ValidationResult.Success
    }

    /**
     * Validates a password for minimum security requirements.
     * @return ValidationResult with success or error message
     */
    fun validatePassword(password: String?): ValidationResult {
        if (password.isNullOrBlank()) {
            return ValidationResult.Error("Password is required")
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationResult.Error("Password must be at least $MIN_PASSWORD_LENGTH characters")
        }

        return ValidationResult.Success
    }

    /**
     * Validates two passwords match (for confirmation).
     * @return ValidationResult with success or error message
     */
    fun validatePasswordMatch(password: String?, confirmPassword: String?): ValidationResult {
        if (password.isNullOrBlank() || confirmPassword.isNullOrBlank()) {
            return ValidationResult.Error("Both passwords are required")
        }

        if (password != confirmPassword) {
            return ValidationResult.Error("Passwords do not match")
        }

        return ValidationResult.Success
    }

    /**
     * Validates a person's full name.
     * @return ValidationResult with success or error message
     */
    fun validateFullName(name: String?): ValidationResult {
        if (name.isNullOrBlank()) {
            return ValidationResult.Error("Full name is required")
        }

        val trimmed = name.trim()
        if (trimmed.length < MIN_NAME_LENGTH) {
            return ValidationResult.Error("Name must be at least $MIN_NAME_LENGTH characters")
        }

        if (trimmed.length > MAX_NAME_LENGTH) {
            return ValidationResult.Error("Name is too long (max $MAX_NAME_LENGTH characters)")
        }

        return ValidationResult.Success
    }

    /**
     * Validates a pet name.
     * @return ValidationResult with success or error message
     */
    fun validatePetName(name: String?): ValidationResult {
        if (name.isNullOrBlank()) {
            return ValidationResult.Error("Pet name is required")
        }

        val trimmed = name.trim()
        if (trimmed.length < MIN_NAME_LENGTH) {
            return ValidationResult.Error("Pet name must be at least $MIN_NAME_LENGTH characters")
        }

        if (trimmed.length > MAX_NAME_LENGTH) {
            return ValidationResult.Error("Pet name is too long (max $MAX_NAME_LENGTH characters)")
        }

        return ValidationResult.Success
    }

    /**
     * Validates a pet species field.
     * @return ValidationResult with success or error message
     */
    fun validateSpecies(species: String?): ValidationResult {
        if (species.isNullOrBlank()) {
            return ValidationResult.Error("Species is required")
        }

        val trimmed = species.trim()
        if (trimmed.length < MIN_NAME_LENGTH) {
            return ValidationResult.Error("Species must be at least $MIN_NAME_LENGTH characters")
        }

        if (trimmed.length > MAX_NAME_LENGTH) {
            return ValidationResult.Error("Species is too long")
        }

        return ValidationResult.Success
    }

    /**
     * Validates a pet breed field.
     * @return ValidationResult with success or error message
     */
    fun validateBreed(breed: String?): ValidationResult {
        if (breed.isNullOrBlank()) {
            return ValidationResult.Error("Breed is required")
        }

        val trimmed = breed.trim()
        if (trimmed.length < MIN_NAME_LENGTH) {
            return ValidationResult.Error("Breed must be at least $MIN_NAME_LENGTH characters")
        }

        if (trimmed.length > MAX_NAME_LENGTH) {
            return ValidationResult.Error("Breed is too long")
        }

        return ValidationResult.Success
    }

    /**
     * Validates a pet age.
     * @return ValidationResult with success or error message
     */
    fun validateAge(age: String?): ValidationResult {
        if (age.isNullOrBlank()) {
            return ValidationResult.Error("Age is required")
        }

        val trimmed = age.trim()
        return try {
            val ageValue = trimmed.toInt()
            if (ageValue < 0) {
                ValidationResult.Error("Age cannot be negative")
            } else if (ageValue > 100) {
                ValidationResult.Error("Age seems too high, please check")
            } else {
                ValidationResult.Success
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Age must be a valid number")
        }
    }

    /**
     * Validates a price field.
     * @return ValidationResult with success or error message
     */
    fun validatePrice(price: String?): ValidationResult {
        if (price.isNullOrBlank()) {
            return ValidationResult.Error("Price is required")
        }

        val trimmed = price.trim()
        return try {
            val priceValue = trimmed.toDouble()
            if (priceValue < 0) {
                ValidationResult.Error("Price cannot be negative")
            } else if (priceValue > 1_000_000) {
                ValidationResult.Error("Price seems too high, please check")
            } else {
                ValidationResult.Success
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Price must be a valid number")
        }
    }

    /**
     * Validates a description field.
     * @return ValidationResult with success or error message
     */
    fun validateDescription(description: String?): ValidationResult {
        // Description is optional
        if (description.isNullOrBlank()) {
            return ValidationResult.Success
        }

        val trimmed = description.trim()
        if (trimmed.length > MAX_DESCRIPTION_LENGTH) {
            return ValidationResult.Error("Description is too long (max $MAX_DESCRIPTION_LENGTH characters)")
        }

        return ValidationResult.Success
    }

    /**
     * Validates an image URL format.
     * @return ValidationResult with success or error message
     */
    fun validateImageUrl(url: String?): ValidationResult {
        // Image URL is optional
        if (url.isNullOrBlank()) {
            return ValidationResult.Success
        }

        val trimmed = url.trim()
        return try {
            java.net.URL(trimmed)
            ValidationResult.Success
        } catch (e: Exception) {
            ValidationResult.Error("Image URL is invalid")
        }
    }

    /**
     * Represents the result of a validation operation.
     */
    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()

        fun isSuccess(): Boolean = this is Success
        fun getErrorMessage(): String? = (this as? Error)?.message
    }
}
