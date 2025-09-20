package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["type"]),
        Index(value = ["isRead"])
    ]
)
data class Notification(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // order, payment, system
    val isRead: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isDeleted: Boolean = false, // Soft delete flag
    val deletedAt: Date? = null // Soft delete timestamp
)