package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.VerificationStatus

@Keep
@Entity(
    tableName = "farm_verifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["farmerId"]), Index(value = ["status"])]
)
data class FarmVerificationEntity(
    @PrimaryKey val verificationId: String,
    val farmerId: String,
    val farmLocationLat: Double?,
    val farmLocationLng: Double?,
    val farmAddressLine1: String?,
    val farmAddressLine2: String?,
    val farmCity: String?,
    val farmState: String?,
    val farmPostalCode: String?,
    val farmCountry: String?,
    val verificationDocumentUrls: String, // stored as JSON String
    val gpsAccuracy: Float?,
    val gpsTimestamp: Long?,
    val status: VerificationStatus, // UNVERIFIED, PENDING, VERIFIED, REJECTED
    val submittedAt: Long?,
    val reviewedAt: Long?,
    val reviewedBy: String?,
    val rejectionReason: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)
