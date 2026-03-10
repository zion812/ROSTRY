package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.EnthusiastVerification
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for enthusiast verification operations.
 */
interface EnthusiastVerificationRepository {
    
    /**
     * Gets the verification status for a user.
     */
    fun getVerificationStatus(userId: String): Flow<EnthusiastVerification?>
    
    /**
     * Submits a new verification request.
     */
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
    
    /**
     * Gets verification history for a user.
     */
    suspend fun getVerificationHistory(userId: String): List<EnthusiastVerification>
    
    /**
     * Gets all pending verifications (admin use).
     */
    fun getPendingVerifications(): Flow<List<EnthusiastVerification>>
    
    /**
     * Reviews a verification request (admin use).
     */
    suspend fun reviewVerification(
        verificationId: String,
        approved: Boolean,
        reviewerId: String,
        rejectionReason: String?
    ): Result<Unit>
}

