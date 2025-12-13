package com.rio.rostry.data.database.entity

import androidx.room.Entity
import com.rio.rostry.domain.model.PaymentStatus
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
    /**
     * Allowed values: "PENDING", "SUCCESS", "FAILED", "REFUNDED". Legacy values like "Paid", "PAID", "PAID_SUCCESS" are supported and automatically normalized to canonical enum values when accessed via the [statusEnum()] extension function. Use [PaymentStatus] enum for type-safe access.
     */
    val status: String,
    val providerRef: String? = null,
    val upiUri: String? = null,
    val idempotencyKey: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun PaymentEntity.statusEnum(): PaymentStatus = PaymentStatus.fromString(this.status)

fun PaymentEntity.withStatus(newStatus: PaymentStatus): PaymentEntity = this.copy(status = newStatus.toStoredString())
