package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val senderId: String,
    val receiverId: String,
    val orderId: String? = null,
    val message: String,
    val messageType: String, // text, image, file
    val isRead: Boolean = false,
    val imageUrl: String? = null,
    val fileUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
    // Temporarily removing isDeleted and deletedAt for compilation
    // val isDeleted: Boolean = false,
    // val deletedAt: Date? = null
)