package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "outbox",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class OutboxEntity(
    @PrimaryKey val outboxId: String,
    val userId: String,
    val entityType: String, // ORDER, POST, CART_ITEM, etc.
    val entityId: String,
    val operation: String, // CREATE, UPDATE, DELETE
    val payloadJson: String,
    val createdAt: Long,
    val retryCount: Int = 0,
    val lastAttemptAt: Long? = null,
    val status: String = "PENDING" // PENDING, IN_PROGRESS, COMPLETED, FAILED
)
