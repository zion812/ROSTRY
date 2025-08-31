package com.rio.rostry.data.models.market

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String = "",
    val listingId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0
)
