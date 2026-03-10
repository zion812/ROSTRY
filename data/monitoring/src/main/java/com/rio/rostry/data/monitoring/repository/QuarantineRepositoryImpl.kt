package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.QuarantineRecord
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.monitoring.mapper.QuarantineRecordMapper
import com.rio.rostry.domain.monitoring.repository.QuarantineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of QuarantineRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.3 - Data modules implement domain repository interfaces
 */
@Singleton
class QuarantineRepositoryImpl @Inject constructor(
    private val dao: QuarantineRecordDao
) : QuarantineRepository {
    
    override fun observe(productId: String): Flow<List<QuarantineRecord>> {
        return dao.observeForProduct(productId).map { entities ->
            entities.map { QuarantineRecordMapper.toDomain(it) }
        }
    }
    
    override fun observeByStatus(status: String): Flow<List<QuarantineRecord>> {
        return dao.observeByStatus(status).map { entities ->
            entities.map { QuarantineRecordMapper.toDomain(it) }
        }
    }
    
    override suspend fun insert(record: QuarantineRecord) {
        dao.insert(QuarantineRecordMapper.toEntity(record))
    }
    
    override suspend fun update(record: QuarantineRecord) {
        dao.update(QuarantineRecordMapper.toEntity(record))
    }
}
