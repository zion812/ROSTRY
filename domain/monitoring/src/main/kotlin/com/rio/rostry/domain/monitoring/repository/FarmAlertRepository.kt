package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.FarmAlert
import kotlinx.coroutines.flow.Flow

/**
 * Repository for farm-related alerts and status updates.
 */
interface FarmAlertRepository {
    fun observeUnread(farmerId: String): Flow<List<FarmAlert>>
    suspend fun countUnread(farmerId: String): Int
    suspend fun insert(alert: FarmAlert)
    suspend fun markRead(alertId: String)
    suspend fun cleanupExpired()
}
