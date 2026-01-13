package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.VerificationStatus

/**
 * Entity for Enthusiast role verification requests.
 * Used when a Farmer upgrades to Enthusiast tier.
 */
@Keep
@Entity(
    tableName = "enthusiast_verifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["status"])]
)
data class EnthusiastVerificationEntity(
    @PrimaryKey val verificationId: String,
    val userId: String,
    
    // Enthusiast-specific verification fields
    val experienceYears: Int?,
    val birdCount: Int?,
    val specializations: String?, // JSON array of specializations e.g., ["breeding", "exhibition", "competition"]
    val achievementsDescription: String?,
    val referenceContacts: String?, // JSON array of references
    
    // Document uploads
    val verificationDocumentUrls: String, // JSON String of document URLs
    val profilePhotoUrl: String?,
    val farmPhotoUrls: String?, // JSON String of farm/collection photos
    
    // Status tracking
    val status: VerificationStatus, // UNVERIFIED, PENDING, VERIFIED, REJECTED
    val submittedAt: Long?,
    val reviewedAt: Long?,
    val reviewedBy: String?,
    val rejectionReason: String?,
    val adminNotes: String?,
    
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long
)
