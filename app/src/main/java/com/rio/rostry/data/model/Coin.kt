package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "coins",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"])
    ]
)
data class Coin(
    @PrimaryKey
    val id: String,
    val userId: String,
    val balance: Double,
    val totalEarned: Double,
    val totalSpent: Double,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isDeleted: Boolean = false, // Soft delete flag
    val deletedAt: Date? = null // Soft delete timestamp
)