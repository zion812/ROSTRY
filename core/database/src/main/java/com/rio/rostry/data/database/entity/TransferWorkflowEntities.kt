package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import java.util.UUID

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



/**
 * Entity for logging audit events in the transfer workflow, including validation failures.
 * Used to track all transfer-related activities, verifications, disputes, and validation checks.
 * For validation failures, type="VALIDATION_FAILURE" with actions like "LISTING_BLOCKED" or "TRANSFER_BLOCKED".
 */
@Entity(
    tableName = "audit_logs",
    indices = [Index("refId"), Index("type")]
)
data class AuditLogEntity(
    /**
     * Unique identifier for the audit log entry.
     */
    @PrimaryKey val logId: String,
    /**
     * Type of audit event.
     * - "TRANSFER" - transfer lifecycle events
     * - "VERIFICATION" - verification step events
     * - "DISPUTE" - dispute events
     * - "VALIDATION_FAILURE" - validation failures
     */
    val type: String,
    /**
     * Reference ID, e.g., transferId, verificationId, disputeId, or productId for validation failures.
     */
    val refId: String,
    /**
     * Specific action performed.
     * For validation: "LISTING_BLOCKED", "TRANSFER_BLOCKED", "VALIDATION_WARNING"
     * For transfers: "INITIATE", "VERIFY_BUYER", "APPROVE_PLATFORM", "COMPLETE", "CANCEL"
     * For disputes: "RAISE_DISPUTE", "RESOLVE_DISPUTE"
     */
    val action: String,
    /**
     * User ID of the actor who performed the action, if applicable.
     */
    val actorUserId: String?,
    /**
     * JSON string containing additional details about the event.
     * For validation failures, contains reasons and timestamp.
     */
    val detailsJson: String?,
    /**
     * Timestamp when the audit log was created.
     */
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        /**
         * Factory method to create an audit log entry for validation failures.
         *
         * @param refId Reference ID (productId or transferId)
         * @param action Action type (e.g., "LISTING_BLOCKED", "TRANSFER_BLOCKED")
         * @param actorUserId User ID of the actor
         * @param reasons List of validation failure reasons
         * @return AuditLogEntity for the validation failure
         */
        fun createValidationFailureLog(
            refId: String,
            action: String,
            actorUserId: String?,
            reasons: List<String>
        ): AuditLogEntity {
            val details = mapOf(
                "reasons" to reasons,
                "timestamp" to System.currentTimeMillis()
            )
            return AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "VALIDATION_FAILURE",
                refId = refId,
                action = action,
                actorUserId = actorUserId,
                detailsJson = Gson().toJson(details),
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
