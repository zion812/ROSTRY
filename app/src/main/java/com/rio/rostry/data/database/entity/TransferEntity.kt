package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * TransferEntity represents a transfer transaction.
 *
 * Sync state fields:
 * - dirty: true means local changes pending sync
 * - syncedAt: timestamp when last synced to remote, null if never synced
 * - mergedAt: timestamp of last merge operation, null if never merged
 * - mergeCount: number of merge operations performed
 */
@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["fromUserId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["toUserId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = OrderEntity::class, // Optional: link transfer to an order
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["fromUserId"]), Index(value = ["toUserId"]), Index(value = ["orderId"]), Index(value = ["productId"]), Index(value = ["syncedAt"])]
)
data class TransferEntity(
    @PrimaryKey val transferId: String,
    val productId: String? = null, // Optional direct link to product for traceability
    val fromUserId: String?, // Can be null if system initiated (e.g. platform fee)
    val toUserId: String?,   // Can be null if system initiated (e.g. payment to platform)
    val orderId: String? = null, // Associated order, if applicable
    val amount: Double,
    val currency: String = "USD", // Or your local currency
    val type: String, // e.g., "PAYMENT", "PAYOUT", "REFUND", "PLATFORM_FEE", "COIN_PURCHASE"
    val status: String, // e.g., "PENDING", "COMPLETED", "FAILED", "CANCELLED"
    val transactionReference: String? = null, // Reference from payment gateway
    val notes: String? = null,
    // Verification related (optional, filled during workflow)
    val gpsLat: Double? = null,
    val gpsLng: Double? = null,
    val sellerPhotoUrl: String? = null,
    val buyerPhotoUrl: String? = null,
    val timeoutAt: Long? = null,
    val conditionsJson: String? = null,
    val initiatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val syncedAt: Long? = null,
    val mergedAt: Long? = null,
    val mergeCount: Int = 0
)
