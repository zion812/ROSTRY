package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Result

/**
 * Repository contract for managing transfer workflow operations.
 * 
 * Handles the complete transfer lifecycle including initiation, verification,
 * approval, completion, disputes, and documentation.
 */
interface TransferWorkflowRepository {
    /**
     * Initiates a new transfer with seller evidence.
     */
    suspend fun initiate(
        productId: String,
        fromUserId: String,
        toUserId: String,
        amount: Double,
        currency: String,
        sellerPhotoUrl: String?,
        gpsLat: Double?,
        gpsLng: Double?,
        conditionsJson: String? = null,
        timeoutAt: Long? = null,
    ): Result<String> // returns transferId

    /**
     * Appends seller evidence to an existing transfer.
     */
    suspend fun appendSellerEvidence(
        transferId: String,
        photoBeforeUrl: String?,
        photoAfterUrl: String?,
        photoBeforeMetaJson: String?,
        photoAfterMetaJson: String?
    ): Result<Unit>

    /**
     * Buyer verifies the transfer with their evidence.
     */
    suspend fun buyerVerify(
        transferId: String,
        buyerPhotoUrl: String?,
        buyerGpsLat: Double?,
        buyerGpsLng: Double?,
        identityDocType: String?,
        identityDocRef: String?,
        identityDocNumber: String?,
        buyerPhotoMetaJson: String? = null,
        gpsExplanation: String? = null,
    ): Result<Unit>

    /**
     * Platform approves the transfer if needed (high-value transfers).
     */
    suspend fun platformApproveIfNeeded(transferId: String): Result<Unit>

    /**
     * Platform reviews and approves/rejects the transfer.
     */
    suspend fun platformReview(
        transferId: String,
        approved: Boolean,
        notes: String?,
        actorUserId: String?
    ): Result<Unit>

    /**
     * Completes the transfer and updates product ownership.
     */
    suspend fun complete(transferId: String): Result<Unit>

    /**
     * Cancels the transfer with a reason.
     */
    suspend fun cancel(transferId: String, reason: String?): Result<Unit>

    /**
     * Raises a dispute for the transfer.
     */
    suspend fun raiseDispute(
        transferId: String,
        raisedByUserId: String,
        reason: String
    ): Result<String> // returns disputeId

    /**
     * Resolves a dispute.
     */
    suspend fun resolveDispute(
        disputeId: String,
        resolutionNotes: String,
        resolved: Boolean
    ): Result<Unit>

    /**
     * Computes a trust score for the transfer based on verifications.
     */
    suspend fun computeTrustScore(transferId: String): Result<Int>

    /**
     * Generates documentation package for the transfer.
     */
    suspend fun generateDocumentation(transferId: String): Result<String> // JSON package

    /**
     * Validates if a product is eligible for transfer.
     */
    suspend fun validateTransferEligibility(
        productId: String,
        fromUserId: String,
        toUserId: String?,
        logOnFailure: Boolean = true
    ): Result<Unit>

    /**
     * Retries a failed transfer.
     */
    suspend fun retryFailedTransfer(transferId: String): Result<Unit>
}

