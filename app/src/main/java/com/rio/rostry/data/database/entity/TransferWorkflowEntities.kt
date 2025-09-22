package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transfer_verifications",
    foreignKeys = [
        ForeignKey(
            entity = TransferEntity::class,
            parentColumns = ["transferId"],
            childColumns = ["transferId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transferId"), Index("status")]
)
data class TransferVerificationEntity(
    @PrimaryKey val verificationId: String,
    val transferId: String,
    val step: String, // SELLER_INIT, BUYER_VERIFY, PLATFORM_REVIEW
    val status: String, // PENDING, APPROVED, REJECTED, TIMEOUT
    val photoBeforeUrl: String? = null,
    val photoAfterUrl: String? = null,
    val photoBeforeMetaJson: String? = null, // EXIF metadata JSON
    val photoAfterMetaJson: String? = null,
    val gpsLat: Double? = null,
    val gpsLng: Double? = null,
    val identityDocType: String? = null,
    val identityDocRef: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "transfer_disputes",
    foreignKeys = [
        ForeignKey(
            entity = TransferEntity::class,
            parentColumns = ["transferId"],
            childColumns = ["transferId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transferId"), Index("status")]
)
data class DisputeEntity(
    @PrimaryKey val disputeId: String,
    val transferId: String,
    val raisedByUserId: String,
    val reason: String,
    val status: String, // OPEN, UNDER_REVIEW, RESOLVED, REJECTED
    val resolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "audit_logs",
    indices = [Index("refId"), Index("type")]
)
data class AuditLogEntity(
    @PrimaryKey val logId: String,
    val type: String, // TRANSFER, VERIFICATION, DISPUTE
    val refId: String, // e.g. transferId/verificationId/disputeId
    val action: String, // INITIATE, VERIFY_BUYER, APPROVE_PLATFORM, COMPLETE, CANCEL, RAISE_DISPUTE, RESOLVE_DISPUTE
    val actorUserId: String?,
    val detailsJson: String?,
    val createdAt: Long = System.currentTimeMillis(),
)
