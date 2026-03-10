package com.rio.rostry.core.model

/**
 * Domain model for enthusiast verification.
 */
data class EnthusiastVerification(
    val verificationId: String,
    val userId: String,
    val experienceYears: Int,
    val birdCount: Int,
    val specializations: List<String>,
    val achievementsDescription: String,
    val referenceContacts: List<String>,
    val verificationDocumentUrls: List<String>,
    val profilePhotoUrl: String?,
    val farmPhotoUrls: List<String>,
    val status: VerificationStatus,
    val submittedAt: Long,
    val reviewedAt: Long?,
    val reviewedBy: String?,
    val rejectionReason: String?,
    val adminNotes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

enum class VerificationStatus {
    PENDING,
    VERIFIED,
    REJECTED
}
