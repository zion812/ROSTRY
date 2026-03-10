package com.rio.rostry.core.model

/**
 * Message model for messaging platform.
 */
data class Message(
    val id: String,
    val threadId: String,
    val senderId: String,
    val content: String,
    val message: String = "",
    val attachments: List<String> = emptyList(),
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
