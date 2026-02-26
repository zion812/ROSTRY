package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Transfer analytics entity for tracking transfer metrics.
 * 
 * Tracks:
 * - Transfer participants (sender, recipient)
 * - Product transferred
 * - Timing information (initiated, completed, duration)
 * - Conflict information
 */
@Entity(
    tableName = "transfer_analytics",
    foreignKeys = [
        ForeignKey(
            entity = TransferEntity::class,
            parentColumns = ["transferId"],
            childColumns = ["transferId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["recipientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["transferId"]),
        Index(value = ["senderId"]),
        Index(value = ["recipientId"]),
        Index(value = ["productId"]),
        Index(value = ["initiatedAt"])
    ]
)
data class TransferAnalyticsEntity(
    @PrimaryKey val id: String,
    val transferId: String,
    val senderId: String,
    val recipientId: String,
    val productId: String,
    val initiatedAt: Long,
    val completedAt: Long? = null,
    val durationSeconds: Long? = null,
    val hadConflicts: Boolean = false,
    val conflictCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
