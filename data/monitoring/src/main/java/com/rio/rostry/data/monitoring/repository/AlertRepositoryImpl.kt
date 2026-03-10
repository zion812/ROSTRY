package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.domain.monitoring.repository.AlertRepository
import com.rio.rostry.core.model.Alert
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.monitoring.mapper.toAlert
import com.rio.rostry.data.database.dao.AlertDao
import com.rio.rostry.data.database.entity.AlertEntity
import com.rio.rostry.core.common.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AlertRepository.
 * 
 * Handles system alerts, notifications, and warnings for farm monitoring,
 * health issues, and important events.
 */
@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao,
    private val currentUserProvider: CurrentUserProvider
) : AlertRepository {

    private val userId: String
        get() = currentUserProvider.userIdOrNull() ?: ""

    override fun streamAlerts(): Flow<Result<List<Alert>>> {
        val uid = userId
        if (uid.isEmpty()) return emptyFlow()
        
        return alertDao.streamAlerts(uid)
            .map { entities -> Result.success(entities.map { it.toAlert() }) }
            .catch { e ->
                Timber.e(e, "Failed to stream alerts")
                val exception = if (e is Exception) e else Exception(e)
                emit(Result.Error(exception))
            }
    }

    override fun streamUnreadCount(): Flow<Int> {
        val uid = userId
        if (uid.isEmpty()) return emptyFlow()
        return alertDao.countUnread(uid)
    }

    override suspend fun markAsRead(alertId: String): Result<Unit> {
        return try {
            alertDao.markAsRead(alertId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark alert as read: $alertId")
            Result.Error(e)
        }
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val uid = userId
            if (uid.isEmpty()) {
                return Result.Error(Exception("User not authenticated"))
            }
            alertDao.markAllAsRead(uid)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark all alerts as read")
            Result.Error(e)
        }
    }

    override suspend fun dismissAlert(alertId: String): Result<Unit> {
        return try {
            alertDao.dismiss(alertId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to dismiss alert: $alertId")
            Result.Error(e)
        }
    }

    override suspend fun createAlert(
        title: String,
        message: String,
        severity: String,
        type: String,
        relatedId: String?
    ): Result<Unit> {
        return try {
            val uid = userId
            if (uid.isEmpty()) {
                return Result.Error(Exception("User not authenticated"))
            }
            
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create alert")
            Result.Error(e)
        }
    }
}
