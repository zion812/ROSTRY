package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.GrowthRecord
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.monitoring.mapper.toEntity
import com.rio.rostry.data.monitoring.mapper.toGrowthRecord
import com.rio.rostry.domain.monitoring.repository.GrowthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GrowthRepository for managing growth records.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Monitoring Domain repository migration
 */
@Singleton
class GrowthRepositoryImpl @Inject constructor(
    private val dao: GrowthRecordDao
) : GrowthRepository {

    override fun observe(productId: String): Flow<List<GrowthRecord>> {
        return dao.observeForProduct(productId).map { entities ->
            entities.map { it.toGrowthRecord() }
        }
    }

    override suspend fun upsert(record: GrowthRecord) {
        dao.upsert(record.toEntity())
    }
}
