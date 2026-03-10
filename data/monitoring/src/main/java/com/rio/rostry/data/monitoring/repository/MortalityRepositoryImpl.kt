package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.MortalityRecord
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.monitoring.mapper.MortalityRecordMapper
import com.rio.rostry.domain.monitoring.repository.MortalityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MortalityRepository with automatic inventory sync.
 * 
 * When a mortality record is inserted, the linked FarmAsset's quantity is decremented.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.3 - Data modules implement domain repository interfaces
 */
@Singleton
class MortalityRepositoryImpl @Inject constructor(
    private val dao: MortalityRecordDao,
    private val farmAssetDao: FarmAssetDao
) : MortalityRepository {
    
    override fun observeAll(): Flow<List<MortalityRecord>> {
        return dao.observeAll().map { entities ->
            entities.map { MortalityRecordMapper.toDomain(it) }
        }
    }
    
    override suspend fun insert(record: MortalityRecord) {
        // 1. Insert the mortality record
        dao.insert(MortalityRecordMapper.toEntity(record))
        
        // 2. Decrement FarmAsset quantity if productId is present
        record.productId?.let { assetId ->
            val currentQty = farmAssetDao.getCurrentQuantity(assetId) ?: 0.0
            val newQty = (currentQty - record.quantity).coerceAtLeast(0.0)
            farmAssetDao.updateQuantity(assetId, newQty, System.currentTimeMillis())
        }
    }
}
