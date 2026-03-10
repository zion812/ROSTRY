package com.rio.rostry.core.model

/**
 * Domain model for a chat message.
 * 
 * Represents a message in a one-on-one conversation between users.
 */
data class ChatMessage(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val body: String,
    val sentAt: Long,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val updatedAt: Long,
    val lastModifiedAt: Long
)
