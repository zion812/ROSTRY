package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId"), Index("userId"), Index("idempotencyKey", unique = true)]
)
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val orderId: String,
    val userId: String,
    val method: String, // UPI, CARD, WALLET, COD, COINS, ADVANCE
    val amount: Double,
    val currency: String = "INR",
    val status: String, // PENDING, SUCCESS, FAILED, REFUNDED
    val providerRef: String? = null,
    val upiUri: String? = null,
    val idempotencyKey: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
