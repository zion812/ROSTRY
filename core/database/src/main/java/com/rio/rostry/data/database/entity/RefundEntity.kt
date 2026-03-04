package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "refunds",
    foreignKeys = [
        ForeignKey(
            entity = PaymentEntity::class,
            parentColumns = ["paymentId"],
            childColumns = ["paymentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("paymentId"), Index("orderId")]
)
data class RefundEntity(
    @PrimaryKey val refundId: String,
    val paymentId: String,
    val orderId: String,
    val amount: Double,
    val reason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
