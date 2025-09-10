package com.rio.rostry.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("buyerId"),
        Index("sellerId"),
        Index("productId"),
        Index(value = ["buyerId", "sellerId", "createdAt"], unique = false)
    ]
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val buyerId: String,
    val sellerId: String,
    val productId: String,
    val quantity: Int,
    val status: String,
    val createdAt: Long = System.currentTimeMillis(),
    // New fields with safe defaults
    val paymentMethod: String = "COD",        // ONLINE, COD, COIN, ADVANCE
    val paymentStatus: String = "INITIATED",  // INITIATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED
    val amountSubtotal: Long = 0,              // paise
    val gstPercentage: Int = 0,                // e.g., 5, 12
    val gstAmount: Long = 0,                   // paise
    val amountTotal: Long = 0,                 // paise
    val advanceAmount: Long = 0,               // paise
    val codEligible: Boolean = true,
    val hubId: String? = null,
    val expectedDeliveryAt: Long? = null,
    val updatedAt: Long = createdAt
)

