package com.rio.rostry.core.model

/**
 * Domain model representing a system alert or notification.
 * 
 * Alerts notify users about important events, warnings, or issues
 * related to farm operations, health monitoring, or system events.
 */
data class Alert(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val severity: String,
    val type: String,
    val relatedId: String? = null,
    val isRead: Boolean = false,
    val isDismissed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
