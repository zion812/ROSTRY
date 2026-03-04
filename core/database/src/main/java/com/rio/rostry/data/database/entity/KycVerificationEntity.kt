package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for KYC verifications
 */
@Entity(
    tableName = "kyc_verifications",
    indices = [
        Index(value = ["userId"], unique = true),
        Index(value = ["status"])
    ]
)
data class KycVerificationEntity(
    @PrimaryKey val verificationId: String,
    val userId: String,
    val identityDocumentsJson: String, // JSON array of URLs
    val farmLocationLat: Double,
    val farmLocationLon: Double,
    val farmPhotosJson: String, // JSON array of URLs
    val status: String, // PENDING, APPROVED, REJECTED, REQUIRES_REVIEW
    val submittedAt: Long,
    val reviewedAt: Long? = null,
    val reviewedBy: String? = null,
    val rejectionReason: String? = null
)
