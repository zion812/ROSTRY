package com.rio.rostry.domain.verification

import com.google.firebase.firestore.GeoPoint
import com.rio.rostry.domain.validation.InputValidationResult

/**
 * Main interface for the verification system.
 * Handles draft merging, KYC workflow, and verification validation.
 */
interface VerificationSystem {
    /**
     * Create a new verification draft for a product
     */
    suspend fun createDraft(
        productId: String,
        verifierId: String,
        fields: Map<String, Any>
    ): VerificationDraft

    /**
     * Merge multiple drafts into a final verification record
     * Returns ConflictsDetected if conflicts exist and resolutions not provided
     */
    suspend fun mergeDrafts(request: DraftMergeRequest): VerificationResult

    /**
     * Validate verification status before product listing
     */
    suspend fun validateVerificationStatus(productId: String): InputValidationResult

    /**
     * Submit KYC verification for enthusiast users
     */
    suspend fun submitKyc(request: KycSubmissionRequest): KycSubmissionResult

    /**
     * Validate farm location coordinates
     */
    suspend fun validateFarmLocation(location: GeoPoint): InputValidationResult

    /**
     * Prevent duplicate verifications for the same product
     */
    suspend fun checkDuplicateVerification(productId: String): Boolean

    /**
     * Validate verifier credentials
     */
    suspend fun validateVerifierCredentials(verifierId: String): InputValidationResult

    /**
     * Get all drafts for a product
     */
    suspend fun getDraftsForProduct(productId: String): List<VerificationDraft>

    /**
     * Update KYC status (admin only)
     */
    suspend fun updateKycStatus(
        verificationId: String,
        status: KycStatus,
        reviewerId: String,
        rejectionReason: String? = null
    ): Result<Unit>
}
