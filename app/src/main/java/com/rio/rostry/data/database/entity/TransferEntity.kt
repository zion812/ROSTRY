package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    indices = [Index(value = ["fromUserId"]), Index(value = ["toUserId"]), Index(value = ["orderId"]), Index(value = ["productId"])]
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
    val initiatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false
)
