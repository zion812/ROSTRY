package com.rio.rostry.domain.monitoring.model

import java.time.Instant

/**
 * Domain model for task.
 * 
 * Phase 2: Domain and Data Decoupling
 * Represents a farm management task.
 */
data class Task(
    val id: String,
    val farmerId: String,
    val title: String,
    val description: String?,
    val taskType: TaskType,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: Instant?,
    val completedAt: Instant?,
    val assignedTo: String?,
    val relatedAssetId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * Type of task.
 */
enum class TaskType {
    FEEDING,
    CLEANING,
    HEALTH_CHECK,
    VACCINATION,
    BREEDING,
    HARVESTING,
    MAINTENANCE,
    OTHER
}

/**
 * Priority level of task.
 */
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

/**
 * Status of task.
 */
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    OVERDUE
}
