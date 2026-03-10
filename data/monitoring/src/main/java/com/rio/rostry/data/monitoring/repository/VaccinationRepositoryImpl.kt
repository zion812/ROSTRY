package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.VaccinationRecord
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.monitoring.mapper.VaccinationRecordMapper
import com.rio.rostry.domain.monitoring.repository.VaccinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VaccinationRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.3 - Data modules implement domain repository interfaces
 */
@Singleton
class VaccinationRepositoryImpl @Inject constructor(
    private val dao: VaccinationRecordDao
) : VaccinationRepository {
    
    override fun observe(productId: String): Flow<List<VaccinationRecord>> {
        return dao.observeForProduct(productId).map { entities ->
            entities.map { VaccinationRecordMapper.toDomain(it) }
        }
    }
    
    override fun observeByFarmer(farmerId: String): Flow<List<VaccinationRecord>> {
        return dao.observeByFarmer(farmerId).map { entities ->
            entities.map { VaccinationRecordMapper.toDomain(it) }
        }
    }
    
    override suspend fun upsert(record: VaccinationRecord) {
        dao.upsert(VaccinationRecordMapper.toEntity(record))
    }
    
    override suspend fun dueReminders(byTime: Long): List<VaccinationRecord> {
        return dao.dueReminders(byTime).map { VaccinationRecordMapper.toDomain(it) }
    }
}
