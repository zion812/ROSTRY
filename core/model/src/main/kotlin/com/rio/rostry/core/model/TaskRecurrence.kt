package com.rio.rostry.core.model

/**
 * Domain model representing a task recurrence schedule.
 */
data class TaskRecurrence(
    val recurrenceId: String,
    val taskId: String,
    val pattern: String,
    val interval: Int,
    val daysOfWeek: String? = null,
    val endDate: Long? = null,
    val maxOccurrences: Int? = null,
    val currentOccurrence: Int = 0,
    val lastGenerated: Long? = null,
    val nextDue: Long? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
