package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a notification entity stored in the database.
 *
 * This entity supports notification batching for offline-first functionality:
 * - Notifications are persisted immediately when created.
 * - Device notifications are displayed only when online.
 * - During offline periods, notifications are batched (isBatched = true) and flushed when connectivity is restored.
 * - displayedAt is set when the device notification is actually shown.
 */
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
    indices = [
        Index(value = ["userId"]),
        Index(value = ["isBatched"]),
        Index(value = ["domain"])
    ]
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
    val createdAt: Long = System.currentTimeMillis(),
    val isBatched: Boolean = false, // indicates notification is queued for batch display
    val batchedAt: Long? = null, // timestamp when notification was batched
    val displayedAt: Long? = null, // timestamp when device notification was displayed
    val domain: String? = null // domain category: "FARM", "TRANSFER", "ORDER", "SOCIAL"
)