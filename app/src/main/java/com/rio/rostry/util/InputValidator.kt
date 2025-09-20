package com.rio.rostry.util

import android.util.Patterns

/**
 * Utility class for comprehensive input validation
 */
object InputValidator {

    /**
     * Validate email address
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate name (non-empty, reasonable length)
     * @param name The name to validate
     * @return true if valid, false otherwise
     */
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length <= 100 && name.matches(Regex("^[a-zA-Z\\s]+$"))
    }

    /**
     * Validate address (non-empty, reasonable length)
     * @param address The address to validate
     * @return true if valid, false otherwise
     */
    fun isValidAddress(address: String): Boolean {
        return address.isNotBlank() && address.length <= 200
    }

    /**
     * Validate location (non-empty, reasonable length)
     * @param location The location to validate
     * @return true if valid, false otherwise
     */
    fun isValidLocation(location: String): Boolean {
        return location.isNotBlank() && location.length <= 100
    }

    /**
     * Validate ID number (Aadhaar, PAN, etc.)
     * @param idNumber The ID number to validate
     * @return true if valid, false otherwise
     */
    fun isValidIdNumber(idNumber: String): Boolean {
        // Aadhaar: 12 digits
        // PAN: 10 characters (5 letters, 4 digits, 1 letter)
        return when {
            idNumber.length == 12 && idNumber.all { it.isDigit() } -> true // Aadhaar
            idNumber.length == 10 && 
                idNumber.substring(0, 5).all { it.isLetter() } &&
                idNumber.substring(5, 9).all { it.isDigit() } &&
                idNumber[9].isLetter() -> true // PAN
            else -> false
        }
    }

    /**
     * Validate all profile fields
     * @param name The user's name
     * @param email The user's email (optional)
     * @param address The user's address
     * @param location The user's location (required for farmers/enthusiasts)
     * @param idNumber The user's ID number (required for enthusiasts)
     * @param userType The user's type
     * @return List of validation errors, empty if all valid
     */
    fun validateProfile(
        name: String,
        email: String?,
        address: String,
        location: String?,
        idNumber: String?,
        userType: com.rio.rostry.domain.model.UserType
    ): List<String> {
        val errors = mutableListOf<String>()

        if (!isValidName(name)) {
            errors.add("Please enter a valid name")
        }

        email?.let {
            if (it.isNotBlank() && !isValidEmail(it)) {
                errors.add("Please enter a valid email address")
            }
        }

        if (!isValidAddress(address)) {
            errors.add("Please enter a valid address")
        }

        if (userType == com.rio.rostry.domain.model.UserType.FARMER || 
            userType == com.rio.rostry.domain.model.UserType.ENTHUSIAST) {
            location?.let {
                if (!isValidLocation(it)) {
                    errors.add("Please enter a valid location")
                }
            } ?: run {
                errors.add("Location is required for farmers and enthusiasts")
            }
        }

        if (userType == com.rio.rostry.domain.model.UserType.ENTHUSIAST) {
            idNumber?.let {
                if (!isValidIdNumber(it)) {
                    errors.add("Please enter a valid ID number (Aadhaar or PAN)")
                }
            } ?: run {
                errors.add("ID number is required for enthusiasts")
            }
        }

        return errors
    }
}