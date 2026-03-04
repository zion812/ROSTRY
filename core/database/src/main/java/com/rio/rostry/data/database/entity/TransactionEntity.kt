package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index("orderId"),
        Index("userId"),
        Index("timestamp"),
        Index("status")
    ]
)
data class TransactionEntity(
    @PrimaryKey val transactionId: String,
    val orderId: String,
    val userId: String, // Payer ID
    val amount: Double,
    val currency: String = "INR",
    val status: String, // SUCCESS, FAILED, PENDING
    val paymentMethod: String, // CASH, UPI, CARD
    val gatewayReference: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
