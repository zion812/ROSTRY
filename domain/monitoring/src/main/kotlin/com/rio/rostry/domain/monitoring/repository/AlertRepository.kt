package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Alert
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for alert management.
 * 
 * Handles system alerts, notifications, and warnings for farm monitoring,
 * health issues, and important events.
 */
interface AlertRepository {
    
    /**
     * Stream all alerts for the current user.
     */
    fun streamAlerts(): Flow<Result<List<Alert>>>
    
    /**
     * Stream count of unread alerts.
     */
    fun streamUnreadCount(): Flow<Int>
    
    /**
     * Mark a specific alert as read.
     */
    suspend fun markAsRead(alertId: String): Result<Unit>
    
    /**
     * Mark all alerts as read for the current user.
     */
    suspend fun markAllAsRead(): Result<Unit>
    
    /**
     * Dismiss an alert (remove from view).
     */
    suspend fun dismissAlert(alertId: String): Result<Unit>
    
    /**
     * Create a new alert.
     */
    suspend fun createAlert(
        title: String,
        message: String,
        severity: String,
        type: String,
        relatedId: String? = null
    ): Result<Unit>
}
