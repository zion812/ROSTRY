package com.rio.rostry.core.model

/**
 * Task model for farm task management.
 */
data class Task(
    val id: String,
    val farmerId: String,
    val assetId: String? = null,
    val title: String,
    val description: String = "",
    val taskType: TaskType,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val assignedTo: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TaskType {
    FEEDING,
    CLEANING,
    VACCINATION,
    HEALTH_CHECK,
    EGG_COLLECTION,
    MAINTENANCE,
    RECORD_KEEPING,
    OTHER
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
