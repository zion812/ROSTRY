package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["productId"]),
        Index(value = ["taskType"]),
        Index(value = ["dueAt"]),
        Index(value = ["completedAt"]),
        Index(value = ["mergedAt"])
    ]
)
/**
 * Represents a task entity in the database.
 * Task conflicts are resolved by preferring the most recent `updatedAt` timestamp.
 */
data class TaskEntity(
    @PrimaryKey val taskId: String = "",
    val farmerId: String = "",
    val productId: String? = null,
    val batchId: String? = null,
    val taskType: String = "", // VACCINATION, GROWTH_UPDATE, QUARANTINE_CHECK, INCUBATION_CHECK, HATCH_CHECK, BREEDING, FEED_SCHEDULE, MEDICATION
    val title: String = "",
    val description: String? = null,
    val dueAt: Long = 0L,
    val completedAt: Long? = null,
    val completedBy: String? = null,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, URGENT
    val recurrence: String? = null, // DAILY, WEEKLY, MONTHLY
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null,
    val snoozeUntil: Long? = null,
    val metadata: String? = null, // JSON blob for task-specific metadata
    /** Timestamp of the last merge operation, null if never merged. */
    val mergedAt: Long? = null,
    /** Number of times this task has been merged, defaults to 0. */
    val mergeCount: Int = 0
)
