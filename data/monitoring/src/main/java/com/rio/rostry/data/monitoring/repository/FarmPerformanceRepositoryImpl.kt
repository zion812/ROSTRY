package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.LifecycleEvent
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.monitoring.mapper.LifecycleEventMapper
import com.rio.rostry.domain.monitoring.repository.FarmPerformanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FarmPerformanceRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.3 - Data modules implement domain repository interfaces
 */
@Singleton
class FarmPerformanceRepositoryImpl @Inject constructor(
    private val lifecycleDao: LifecycleEventDao
) : FarmPerformanceRepository {
    
    override fun observeLifecycle(productId: String): Flow<List<LifecycleEvent>> {
        return lifecycleDao.observeForProduct(productId).map { entities ->
            entities.map { LifecycleEventMapper.toDomain(it) }
        }
    }
}
