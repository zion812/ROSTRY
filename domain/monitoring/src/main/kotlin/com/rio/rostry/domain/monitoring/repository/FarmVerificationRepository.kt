package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.FarmVerification
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for farm verification management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface FarmVerificationRepository {
    /**
     * Get the verification status for a farmer.
     */
    fun getVerificationStatus(farmerId: String): Flow<FarmVerification?>
    
    /**
     * Submit a farm verification request.
     */
    suspend fun submitVerification(
        farmerId: String,
        farmAddress: Map<String, String>,
        location: Map<String, Double?>,
        documentUris: List<String>,
        gpsAccuracy: Float?
    ): Result<Unit>
    
    /**
     * Get verification history for a farmer.
     */
    suspend fun getVerificationHistory(farmerId: String): List<FarmVerification>
}

