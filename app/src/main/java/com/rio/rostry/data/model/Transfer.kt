package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["fromUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["toUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fromUserId"]),
        Index(value = ["toUserId"]),
        Index(value = ["orderId"]),
        Index(value = ["status"]),
        Index(value = ["type"])
    ]
)
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
    val updatedAt: Date = Date(),
    val isDeleted: Boolean = false, // Soft delete flag
    val deletedAt: Date? = null // Soft delete timestamp
)