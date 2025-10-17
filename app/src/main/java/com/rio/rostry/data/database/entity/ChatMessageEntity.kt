package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ChatMessageEntity represents a chat message between users.
 *
 * Sync state fields: Messages are queued locally first (dirty = true), then synced when online.
 * syncedAt tracks the last successful sync timestamp. deviceTimestamp provides offline audit trail.
 */
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("senderId"), Index("receiverId"), Index(value = ["senderId", "receiverId"]), Index("syncedAt")]
)
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val receiverId: String,
    val body: String,
    val mediaUrl: String? = null,
    val sentAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val readAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val syncedAt: Long? = null,
    val deviceTimestamp: Long = System.currentTimeMillis()
)
