package com.rio.rostry.util

import java.util.regex.Pattern

/**
 * Utility class for validating Indian phone numbers
 */
object PhoneValidator {
    private const val INDIAN_PHONE_REGEX = "^[6-9]\\d{9}$"
    private val pattern = Pattern.compile(INDIAN_PHONE_REGEX)

    /**
     * Validate Indian phone number format
     * @param phoneNumber The phone number to validate
     * @return true if valid, false otherwise
     */
    fun isValidIndianPhoneNumber(phoneNumber: String): Boolean {
        return pattern.matcher(phoneNumber).matches()
    }

    /**
     * Format phone number for display
     * @param phoneNumber The phone number to format
     * @return Formatted phone number (e.g., XXX-XXX-XXXX)
     */
    fun formatPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.length != 10) return phoneNumber
        
        return "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6)}"
    }

    /**
     * Clean phone number by removing non-digit characters
     * @param phoneNumber The phone number to clean
     * @return Cleaned phone number with only digits
     */
    fun cleanPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^\\d]"), "")
    }
}