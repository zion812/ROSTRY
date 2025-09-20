package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    suspend fun getNotificationById(id: String): Notification?
    fun getNotificationsByUserId(userId: String): Flow<List<Notification>>
    fun getUnreadNotificationsByUserId(userId: String): Flow<List<Notification>>
    suspend fun insertNotification(notification: Notification)
    suspend fun updateNotification(notification: Notification)
    suspend fun deleteNotification(notification: Notification)
}