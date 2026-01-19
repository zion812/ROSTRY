package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "farm_events",
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["scheduledAt"]),
        Index(value = ["eventType"]),
        Index(value = ["status"])
    ]
)
data class FarmEventEntity(
    @PrimaryKey
    val eventId: String = UUID.randomUUID().toString(),
    val farmerId: String,
    val eventType: FarmEventType,
    val title: String,
    val description: String,
    val scheduledAt: Long,
    val completedAt: Long? = null,
    val recurrence: RecurrenceType = RecurrenceType.ONCE,
    val productId: String? = null,
    val batchId: String? = null,
    val reminderBefore: Long = 0, // Minutes
    val status: EventStatus = EventStatus.PENDING,
    val metadata: String? = null, // JSON
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class FarmEventType {
    VACCINATION, DEWORMING, BIOSECURITY, FEEDING, WEIGHING, CLEANING, OTHER
}

enum class RecurrenceType {
    ONCE, DAILY, WEEKLY, MONTHLY
}

enum class EventStatus {
    PENDING, COMPLETED, CANCELLED
}
