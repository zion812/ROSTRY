package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class NotificationEntity(
    @PrimaryKey val notificationId: String,
    val userId: String, // Foreign key to UserEntity
    val title: String,
    val message: String,
    val type: String, // e.g., "ORDER_UPDATE", "NEW_PRODUCT", "PROMOTION", "SYSTEM_ALERT"
    val deepLinkUrl: String? = null, // For navigating to a specific part of the app
    val isRead: Boolean = false,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
