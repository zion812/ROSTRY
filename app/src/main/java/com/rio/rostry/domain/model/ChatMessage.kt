package com.rio.rostry.domain.model

import java.util.Date

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val orderId: String? = null,
    val message: String,
    val messageType: String,
    val isRead: Boolean = false,
    val imageUrl: String? = null,
    val fileUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)