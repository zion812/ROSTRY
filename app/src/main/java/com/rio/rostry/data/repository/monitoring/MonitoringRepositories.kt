package com.rio.rostry.data.repository.monitoring

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GrowthRepository {
    fun observe(productId: String): Flow<List<GrowthRecordEntity>>
    suspend fun upsert(record: GrowthRecordEntity)
}

class GrowthRepositoryImpl @Inject constructor(
    private val dao: GrowthRecordDao
): GrowthRepository {
    override fun observe(productId: String) = dao.observeForProduct(productId)
    override suspend fun upsert(record: GrowthRecordEntity) = dao.upsert(record)
}

interface QuarantineRepository {
    fun observe(productId: String): Flow<List<QuarantineRecordEntity>>
    fun observeByStatus(status: String): Flow<List<QuarantineRecordEntity>>
    suspend fun insert(record: QuarantineRecordEntity)
    suspend fun update(record: QuarantineRecordEntity)
}

class QuarantineRepositoryImpl @Inject constructor(
    private val dao: QuarantineRecordDao
): QuarantineRepository {
    override fun observe(productId: String) = dao.observeForProduct(productId)
    override fun observeByStatus(status: String) = dao.observeByStatus(status)
    override suspend fun insert(record: QuarantineRecordEntity) = dao.insert(record)
    override suspend fun update(record: QuarantineRecordEntity) = dao.update(record)
}

interface MortalityRepository {
    fun observeAll(): Flow<List<MortalityRecordEntity>>
    suspend fun insert(record: MortalityRecordEntity)
}

/**
 * MortalityRepositoryImpl with Inventory Auto-Sync.
 * When a mortality record is inserted, the linked FarmAsset's quantity is decremented.
 */
class MortalityRepositoryImpl @Inject constructor(
    private val dao: MortalityRecordDao,
    private val farmAssetDao: FarmAssetDao
): MortalityRepository {
    override fun observeAll() = dao.observeAll()
    
    override suspend fun insert(record: MortalityRecordEntity) {
        // 1. Insert the mortality record
        dao.insert(record)
        
        // 2. Decrement FarmAsset quantity if productId is present
        record.productId?.let { assetId ->
            val currentQty = farmAssetDao.getCurrentQuantity(assetId) ?: 0.0
            val newQty = (currentQty - record.quantity).coerceAtLeast(0.0)
            farmAssetDao.updateQuantity(assetId, newQty, System.currentTimeMillis())
        }
    }
}

interface VaccinationRepository {
    fun observe(productId: String): Flow<List<VaccinationRecordEntity>>
    suspend fun upsert(record: VaccinationRecordEntity)
    suspend fun dueReminders(byTime: Long): List<VaccinationRecordEntity>
    fun observeByFarmer(farmerId: String): Flow<List<VaccinationRecordEntity>>
}

class VaccinationRepositoryImpl @Inject constructor(
    private val dao: VaccinationRecordDao
): VaccinationRepository {
    override fun observe(productId: String) = dao.observeForProduct(productId)
    override suspend fun upsert(record: VaccinationRecordEntity) = dao.upsert(record)
    override suspend fun dueReminders(byTime: Long) = dao.dueReminders(byTime)
    override fun observeByFarmer(farmerId: String) = dao.observeByFarmer(farmerId)
}

interface HatchingRepository {
    fun observeBatches(): Flow<List<HatchingBatchEntity>>
    fun observeLogs(batchId: String): Flow<List<HatchingLogEntity>>
    suspend fun upsert(batch: HatchingBatchEntity)
    suspend fun insert(log: HatchingLogEntity)
}

class HatchingRepositoryImpl @Inject constructor(
    private val batchDao: HatchingBatchDao,
    private val logDao: HatchingLogDao
): HatchingRepository {
    override fun observeBatches() = batchDao.observeBatches()
    override fun observeLogs(batchId: String) = logDao.observeForBatch(batchId)
    override suspend fun upsert(batch: HatchingBatchEntity) = batchDao.upsert(batch)
    override suspend fun insert(log: HatchingLogEntity) = logDao.insert(log)
}

// Simple farm performance repository placeholder
interface FarmPerformanceRepository {
    fun observeLifecycle(productId: String): Flow<List<LifecycleEventEntity>>
}

class FarmPerformanceRepositoryImpl @Inject constructor(
    private val lifecycleDao: LifecycleEventDao
): FarmPerformanceRepository {
    override fun observeLifecycle(productId: String) = lifecycleDao.observeForProduct(productId)
}
