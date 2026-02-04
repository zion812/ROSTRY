package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun streamAlerts(): Flow<List<AlertEntity>>
    fun streamUnreadCount(): Flow<Int>
    suspend fun markAsRead(alertId: String)
    suspend fun markAllAsRead()
    suspend fun dismissAlert(alertId: String)
    suspend fun createAlert(title: String, message: String, severity: String, type: String, relatedId: String? = null)
}
