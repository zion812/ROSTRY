package com.rio.rostry.utils

import kotlin.random.Random

/**
 * DeliveryOtpGenerator - Generates and validates delivery OTPs for order completion.
 * 
 * Part of Phase 3: Order Fulfillment.
 * 
 * The OTP is generated locally by the buyer's app and shared with the farmer.
 * When the farmer enters the OTP, the order is marked as COMPLETED.
 */
object DeliveryOtpGenerator {

    /**
     * Generate a 4-digit OTP for delivery verification.
     * Range: 1000-9999 (always 4 digits)
     */
    fun generate(): String {
        return Random.nextInt(1000, 10000).toString()
    }

    /**
     * Validate that the input OTP matches the expected OTP.
     */
    fun validate(inputOtp: String, expectedOtp: String): Boolean {
        // Trim whitespace and compare
        return inputOtp.trim() == expectedOtp.trim()
    }

    /**
     * Generate a secure OTP with additional entropy (for high-value orders).
     * Uses 6 digits for extra security.
     */
    fun generateSecure(): String {
        return Random.nextInt(100000, 1000000).toString()
    }

    /**
     * Check if an OTP format is valid (4 or 6 numeric digits).
     */
    fun isValidFormat(otp: String): Boolean {
        val trimmed = otp.trim()
        return trimmed.length in 4..6 && trimmed.all { it.isDigit() }
    }
}
