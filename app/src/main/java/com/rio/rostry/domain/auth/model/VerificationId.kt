package com.rio.rostry.domain.auth.model

/**
 * Value class wrapping Firebase verification ID.
 * Provides type safety and prevents mixing up verification IDs with other strings.
 */
@JvmInline
value class VerificationId(val value: String) {
    init {
        require(value.isNotBlank()) { "Verification ID cannot be blank" }
    }
}

/**
 * Value class wrapping phone number in E.164 format.
 * Ensures phone numbers are validated before use.
 */
@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(value.matches(Regex("^\\+[1-9]\\d{1,14}$"))) {
            "Phone number must be in E.164 format (e.g., +1234567890)"
        }
    }
    
    /**
     * Get masked phone number for display/logging (shows last 4 digits)
     */
    fun masked(): String = "***${value.takeLast(4)}"
}

/**
 * Value class wrapping OTP code.
 * Ensures OTP is valid before use.
 */
@JvmInline
value class OtpCode(val value: String) {
    init {
        require(value.length == 6 && value.all { it.isDigit() }) {
            "OTP must be exactly 6 digits"
        }
    }
}
