package com.rio.rostry.data.repository

import com.rio.rostry.data.local.NotificationDao
import com.rio.rostry.data.model.Notification as DataNotification
import com.rio.rostry.domain.model.Notification as DomainNotification
import com.rio.rostry.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<DomainNotification>> {
        return notificationDao.getAllNotifications().map { notifications ->
            notifications.map { it.toDomainModel() }
        }
    }

    override suspend fun getNotificationById(id: String): DomainNotification? {
        return notificationDao.getNotificationById(id)?.toDomainModel()
    }

    override fun getNotificationsByUserId(userId: String): Flow<List<DomainNotification>> {
        return notificationDao.getNotificationsByUserId(userId).map { notifications ->
            notifications.map { it.toDomainModel() }
        }
    }

    override fun getUnreadNotificationsByUserId(userId: String): Flow<List<DomainNotification>> {
        return notificationDao.getUnreadNotificationsByUserId(userId).map { notifications ->
            notifications.map { it.toDomainModel() }
        }
    }

    override suspend fun insertNotification(notification: DomainNotification) {
        notificationDao.insertNotification(notification.toDataModel())
    }

    override suspend fun updateNotification(notification: DomainNotification) {
        notificationDao.updateNotification(notification.toDataModel())
    }

    override suspend fun deleteNotification(notification: DomainNotification) {
        notificationDao.deleteNotification(notification.id)
    }

    private fun DataNotification.toDomainModel(): DomainNotification {
        return DomainNotification(
            id = id,
            userId = userId,
            title = title,
            message = message,
            type = type,
            isRead = isRead,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainNotification.toDataModel(): DataNotification {
        return DataNotification(
            id = id,
            userId = userId,
            title = title,
            message = message,
            type = type,
            isRead = isRead,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}