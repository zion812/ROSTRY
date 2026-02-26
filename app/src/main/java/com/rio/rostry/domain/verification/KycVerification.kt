package com.rio.rostry.domain.verification

import com.google.firebase.firestore.GeoPoint

/**
 * Represents a KYC (Know Your Customer) verification for enthusiast users
 */
data class KycVerification(
    val userId: String,
    val identityDocuments: List<String>, // URLs to identity documents
    val farmLocation: GeoPoint,
    val farmPhotos: List<String>, // URLs to farm photos
    val status: KycStatus,
    val submittedAt: Long,
    val reviewedAt: Long? = null,
    val reviewedBy: String? = null,
    val rejectionReason: String? = null
)

/**
 * Status of a KYC verification
 */
enum class KycStatus {
    PENDING,         // Submitted, awaiting review
    APPROVED,        // Approved by admin
    REJECTED,        // Rejected by admin
    REQUIRES_REVIEW  // Flagged for additional review
}

/**
 * Request to submit KYC verification
 */
data class KycSubmissionRequest(
    val userId: String,
    val identityDocuments: List<String>,
    val farmLocation: GeoPoint,
    val farmPhotos: List<String>
)

/**
 * Result of KYC submission
 */
sealed class KycSubmissionResult {
    data class Success(val verificationId: String) : KycSubmissionResult()
    data class ValidationError(val errors: List<String>) : KycSubmissionResult()
    data class Failure(val error: String) : KycSubmissionResult()
}
