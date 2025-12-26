package com.rio.rostry.utils

import java.security.SecureRandom

import kotlin.math.*

object VerificationUtils {
    
    /**
     * Generates a 4-digit secure random numeric OTP.
     */
    fun generateNumericOtp(length: Int = 4): String {
        val random = SecureRandom()
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(random.nextInt(10))
        }
        return sb.toString()
    }
    
    /**
     * Verifies if the provided input matches the generated OTP.
     * Uses constant-time comparison to prevent timing attacks (though less critical for 4-digit low-stakes OTP).
     */
    fun verifyOtp(generated: String?, input: String?): Boolean {
        if (generated.isNullOrBlank() || input.isNullOrBlank()) return false
        if (generated.length != input.length) return false
        
        var result = 0
        for (i in generated.indices) {
            result = result or (generated[i].code xor input[i].code)
        }
        return result == 0
    }

    /**
     * Calculates the distance between two coordinates in meters using the Haversine formula.
     */
    fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * Calculates if the distance between two coordinates is within the specified radius in meters.
     */
    fun withinRadius(lat1: Double, lon1: Double, lat2: Double, lon2: Double, radiusMeters: Double): Boolean {
        return distanceMeters(lat1, lon1, lat2, lon2) <= radiusMeters
    }
}
