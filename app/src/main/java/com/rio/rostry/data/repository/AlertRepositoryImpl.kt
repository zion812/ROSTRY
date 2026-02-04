package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.AlertDao
import com.rio.rostry.data.database.entity.AlertEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao,
    private val currentUserProvider: CurrentUserProvider
) : BaseRepository(), AlertRepository {

    private val userId: String
        get() = currentUserProvider.userIdOrNull() ?: ""

    override fun streamAlerts(): Flow<List<AlertEntity>> {
        val uid = userId
        if (uid.isEmpty()) return emptyFlow()
        return alertDao.streamAlerts(uid)
    }

    override fun streamUnreadCount(): Flow<Int> {
        val uid = userId
        if (uid.isEmpty()) return emptyFlow()
        return alertDao.countUnread(uid)
    }

    override suspend fun markAsRead(alertId: String) {
        try {
            alertDao.markAsRead(alertId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark alert as read: $alertId")
        }
    }

    override suspend fun markAllAsRead() {
        try {
            val uid = userId
            if (uid.isNotEmpty()) {
                alertDao.markAllAsRead(uid)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark all alerts as read")
        }
    }

    override suspend fun dismissAlert(alertId: String) {
        try {
            alertDao.dismiss(alertId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to dismiss alert: $alertId")
        }
    }

    override suspend fun createAlert(
        title: String,
        message: String,
        severity: String,
        type: String,
        relatedId: String?
    ) {
        try {
            val uid = userId
            if (uid.isNotEmpty()) {
                val alert = AlertEntity(
                    id = UUID.randomUUID().toString(),
                    userId = uid,
                    title = title,
                    message = message,
                    severity = severity,
                    type = type,
                    relatedId = relatedId
                )
                alertDao.insert(alert)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to create alert")
        }
    }
}
