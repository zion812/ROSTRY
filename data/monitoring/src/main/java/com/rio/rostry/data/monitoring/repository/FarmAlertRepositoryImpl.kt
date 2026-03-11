package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.FarmAlert
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.monitoring.mapper.toFarmAlert
import com.rio.rostry.data.monitoring.mapper.toEntity
import com.rio.rostry.domain.monitoring.repository.FarmAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FarmAlertRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class FarmAlertRepositoryImpl @Inject constructor(
    private val farmAlertDao: FarmAlertDao
) : FarmAlertRepository {

    override fun observeUnread(farmerId: String): Flow<List<FarmAlert>> {
        return farmAlertDao.observeUnread(farmerId)
            .map { entities -> entities.map { it.toFarmAlert() } }
    }

    override suspend fun countUnread(farmerId: String): Int {
        return farmAlertDao.countUnread(farmerId)
    }

    override suspend fun insert(alert: FarmAlert) {
        farmAlertDao.upsert(alert.toEntity())
    }

    override suspend fun markRead(alertId: String) {
        farmAlertDao.markRead(alertId)
    }

    override suspend fun cleanupExpired() {
        farmAlertDao.deleteExpired(System.currentTimeMillis())
    }

    override suspend fun getByFarmer(farmerId: String, limit: Int): List<FarmAlert> {
        return farmAlertDao.getByFarmer(farmerId, limit)
            .map { it.toFarmAlert() }
    }

    override suspend fun getDirty(): List<FarmAlert> {
        return farmAlertDao.getDirty()
            .map { it.toFarmAlert() }
    }

    override suspend fun clearDirty(alertIds: List<String>, syncedAt: Long) {
        farmAlertDao.clearDirty(alertIds, syncedAt)
    }
}
