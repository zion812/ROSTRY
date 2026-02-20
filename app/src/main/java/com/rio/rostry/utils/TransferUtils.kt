package com.rio.rostry.utils

import java.security.SecureRandom

object TransferUtils {
    private val secureRandom = SecureRandom()
    
    /**
     * Generates a cryptographically secure 6-digit code for enthusiast transfers.
     */
    fun generateSecureCode(): String {
        val nextInt = secureRandom.nextInt(1000000)
        return String.format("%06d", nextInt)
    }
}
