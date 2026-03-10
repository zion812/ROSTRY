package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.TaskRecurrence
import com.rio.rostry.data.database.entity.TaskRecurrenceEntity

fun TaskRecurrenceEntity.toTaskRecurrence(): TaskRecurrence {
    return TaskRecurrence(
        recurrenceId = this.recurrenceId,
        taskId = this.taskId,
        pattern = this.pattern,
        interval = this.interval,
        daysOfWeek = this.daysOfWeek,
        endDate = this.endDate,
        maxOccurrences = this.maxOccurrences,
        currentOccurrence = this.currentOccurrence,
        lastGenerated = this.lastGenerated,
        nextDue = this.nextDue,
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun TaskRecurrence.toEntity(): TaskRecurrenceEntity {
    return TaskRecurrenceEntity(
        recurrenceId = this.recurrenceId,
        taskId = this.taskId,
        pattern = this.pattern,
        interval = this.interval,
        daysOfWeek = this.daysOfWeek,
        endDate = this.endDate,
        maxOccurrences = this.maxOccurrences,
        currentOccurrence = this.currentOccurrence,
        lastGenerated = this.lastGenerated,
        nextDue = this.nextDue,
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
