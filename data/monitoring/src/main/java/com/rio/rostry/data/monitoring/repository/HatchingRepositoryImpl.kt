package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.HatchingBatch
import com.rio.rostry.core.model.HatchingLog
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.data.monitoring.mapper.HatchingMapper
import com.rio.rostry.domain.monitoring.repository.HatchingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HatchingRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.3 - Data modules implement domain repository interfaces
 */
@Singleton
class HatchingRepositoryImpl @Inject constructor(
    private val batchDao: HatchingBatchDao,
    private val logDao: HatchingLogDao
) : HatchingRepository {
    
    override fun observeBatches(): Flow<List<HatchingBatch>> {
        return batchDao.observeBatches().map { entities ->
            entities.map { HatchingMapper.batchToDomain(it) }
        }
    }
    
    override fun observeLogs(batchId: String): Flow<List<HatchingLog>> {
        return logDao.observeForBatch(batchId).map { entities ->
            entities.map { HatchingMapper.logToDomain(it) }
        }
    }
    
    override suspend fun upsert(batch: HatchingBatch) {
        batchDao.upsert(HatchingMapper.batchToEntity(batch))
    }
    
    override suspend fun insert(log: HatchingLog) {
        logDao.insert(HatchingMapper.logToEntity(log))
    }
}
