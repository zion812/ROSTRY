package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Unified outbox entity for queuing all data mutations (logs, transfers, listings, chats, etc.)
 * before syncing to remote. Supports priority-based processing and retry logic with exponential backoff.
 * Max retries default to 10 for critical data, ensuring 100% data survival through app kills and offline periods.
 */
@Entity(
    tableName = "outbox",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"]),
        Index(value = ["priority"]),
        Index(value = ["status", "priority", "createdAt"])
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
    val status: String = "PENDING", // PENDING, IN_PROGRESS, COMPLETED, FAILED
    val priority: String = "NORMAL", // LOW, NORMAL, HIGH, CRITICAL
    val maxRetries: Int = 10, // Allow per-entity retry limits
    val contextJson: String? = null // Additional context for filtering
) {
    companion object {
        const val TYPE_ORDER = "ORDER"
        const val TYPE_POST = "POST"
        const val TYPE_DAILY_LOG = "DAILY_LOG"
        const val TYPE_TRANSFER = "TRANSFER"
        const val TYPE_LISTING = "LISTING"
        const val TYPE_CHAT_MESSAGE = "CHAT_MESSAGE"
        const val TYPE_GROUP_MESSAGE = "GROUP_MESSAGE"
        const val TYPE_TASK = "TASK"
        const val TYPE_PRODUCT = "PRODUCT"
        const val TYPE_EXPENSE = "EXPENSE"
        const val TYPE_PROOF = "PROOF"
        const val TYPE_GENETIC_ANALYSIS = "GENETIC_ANALYSIS"
        const val TYPE_IOT_DEVICE = "IOT_DEVICE"
        const val TYPE_IOT_DATA = "IOT_DATA"
        const val TYPE_PRODUCT_TRACKING = "PRODUCT_TRACKING"
    }
}
