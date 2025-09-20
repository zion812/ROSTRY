package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val orderId: String? = null,
    val amount: Double,
    val type: String, // payment, refund
    val status: String, // pending, completed, failed
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)