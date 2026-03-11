package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.FarmActivityLog
import com.rio.rostry.domain.monitoring.repository.FarmActivityLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe farm activity log repository stub used during modular migration.
 */
@Singleton
class FarmActivityLogRepositoryImpl @Inject constructor(
    private val farmActivityLogDao: com.rio.rostry.data.database.dao.FarmActivityLogDao
) : FarmActivityLogRepository {

    override fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLog>> =
        farmActivityLogDao.observeForFarmer(farmerId).map { entities ->
            entities.map { com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toDomain(it) }
        }

    override fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLog?> =
        farmActivityLogDao.observeLatestForFarmer(farmerId).map { entity ->
            entity?.let { com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toDomain(it) }
        }

    override fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLog>> =
        farmActivityLogDao.observeForFarmerByType(farmerId, type).map { entities ->
            entities.map { com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toDomain(it) }
        }

    override fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLog>> =
        farmActivityLogDao.observeForFarmerBetween(farmerId, start, end).map { entities ->
            entities.map { com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toDomain(it) }
        }

    override fun observeForProduct(productId: String): Flow<List<FarmActivityLog>> =
        farmActivityLogDao.observeForProduct(productId).map { entities ->
            entities.map { com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toDomain(it) }
        }

    override suspend fun upsert(log: FarmActivityLog) {
        farmActivityLogDao.upsert(com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toEntity(log))
    }

    override suspend fun logActivity(
        farmerId: String,
        productId: String?,
        activityType: String,
        amount: Double?,
        quantity: Double?,
        category: String?,
        description: String?,
        notes: String?
    ): FarmActivityLog {
        val now = System.currentTimeMillis()
        val activity = FarmActivityLog(
            id = java.util.UUID.randomUUID().toString(),
            farmerId = farmerId,
            productId = productId,
            activityType = activityType,
            amountInr = amount,
            quantity = quantity,
            category = category,
            description = description,
            notes = notes,
            createdAt = now,
            updatedAt = now,
            dirty = true
        )
        upsert(activity)
        return activity
    }

    override suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double =
        farmActivityLogDao.getTotalExpensesBetween(farmerId, start, end) ?: 0.0

    override suspend fun getById(activityId: String): FarmActivityLog? =
        farmActivityLogDao.getById(activityId)?.let { com.rio.rostry.data.monitoring.mapper.FarmActivityLogMapper.toDomain(it) }

    override suspend fun deleteActivity(activityId: String) {
        farmActivityLogDao.delete(activityId)
    }

    override suspend fun getTotalFeedQuantityForAsset(assetId: String): Double =
        farmActivityLogDao.getTotalFeedQuantityForAsset(assetId) ?: 0.0
}
