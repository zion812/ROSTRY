package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.EnthusiastVerificationEntity
import kotlinx.coroutines.flow.Flow

interface EnthusiastVerificationRepository {
    fun getVerificationStatus(userId: String): Flow<EnthusiastVerificationEntity?>
    suspend fun submitVerification(
        userId: String,
        experienceYears: Int,
        birdCount: Int,
        specializations: List<String>,
        achievementsDescription: String,
        referenceContacts: List<String>,
        documentUris: List<String>,
        profilePhotoUri: String?,
        farmPhotoUris: List<String>
    ): Result<Unit>
    suspend fun getVerificationHistory(userId: String): List<EnthusiastVerificationEntity>
    fun getPendingVerifications(): Flow<List<EnthusiastVerificationEntity>>
    suspend fun reviewVerification(
        verificationId: String,
        approved: Boolean,
        reviewerId: String,
        rejectionReason: String?
    ): Result<Unit>
}
