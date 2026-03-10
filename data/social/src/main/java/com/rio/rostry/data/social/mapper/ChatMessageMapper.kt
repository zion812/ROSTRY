package com.rio.rostry.data.social.mapper

import com.rio.rostry.core.model.ChatMessage
import com.rio.rostry.data.database.entity.ChatMessageEntity

/**
 * Converts ChatMessageEntity to domain model.
 */
fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        messageId = this.messageId,
        senderId = this.senderId,
        receiverId = this.receiverId,
        body = this.body,
        sentAt = this.sentAt,
        isDeleted = this.isDeleted,
        deletedAt = this.deletedAt,
        updatedAt = this.updatedAt,
        lastModifiedAt = this.lastModifiedAt
    )
}

/**
 * Converts domain model to ChatMessageEntity.
 */
fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = this.messageId,
        senderId = this.senderId,
        receiverId = this.receiverId,
        body = this.body,
        sentAt = this.sentAt,
        isDeleted = this.isDeleted,
        deletedAt = this.deletedAt,
        updatedAt = this.updatedAt,
        lastModifiedAt = this.lastModifiedAt,
        dirty = false
    )
}
