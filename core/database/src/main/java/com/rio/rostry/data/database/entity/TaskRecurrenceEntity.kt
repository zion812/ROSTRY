package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_recurrences")
data class TaskRecurrenceEntity(
    @PrimaryKey val recurrenceId: String,
    val taskId: String,
    val pattern: String, // DAILY, WEEKLY, MONTHLY, CUSTOM
    val interval: Int, // Every N days/weeks/months
    val daysOfWeek: String?, // JSON array for weekly patterns
    val endDate: Long?,
    val maxOccurrences: Int?,
    val currentOccurrence: Int = 0,
    val lastGenerated: Long?,
    val nextDue: Long?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
