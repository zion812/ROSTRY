package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.FarmVerificationEntity
import kotlinx.coroutines.flow.Flow

interface FarmVerificationRepository {
    fun getVerificationStatus(farmerId: String): Flow<FarmVerificationEntity?>
    suspend fun submitVerification(
        farmerId: String,
        farmAddress: Map<String, String>,
        location: Map<String, Double?>,
        documentUris: List<String>,
        gpsAccuracy: Float?
    ): Result<Unit>
    suspend fun getVerificationHistory(farmerId: String): List<FarmVerificationEntity>
}
