package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FarmActivityLogDao
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for FarmActivityLogEntity operations.
 * Handles farm-level activities like expenses, sanitation, maintenance, etc.
 */
interface FarmActivityLogRepository {
    fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLogEntity>>
    fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLogEntity>>
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLogEntity>>
    fun observeForProduct(productId: String): Flow<List<FarmActivityLogEntity>>
    suspend fun upsert(log: FarmActivityLogEntity)
    suspend fun logActivity(
        farmerId: String,
        productId: String?,
        activityType: String,
        amount: Double? = null,
        quantity: Double? = null,
        category: String? = null,
        description: String? = null,
        notes: String? = null
    ): FarmActivityLogEntity
    suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double
    suspend fun getById(activityId: String): FarmActivityLogEntity?
}

@Singleton
class FarmActivityLogRepositoryImpl @Inject constructor(
    private val dao: FarmActivityLogDao
) : FarmActivityLogRepository {

    override fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLogEntity>> =
        dao.observeForFarmer(farmerId)

    override fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLogEntity>> =
        dao.observeForFarmerByType(farmerId, type)

    override fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLogEntity>> =
        dao.observeForFarmerBetween(farmerId, start, end)

    override fun observeForProduct(productId: String): Flow<List<FarmActivityLogEntity>> =
        dao.observeForProduct(productId)

    override suspend fun upsert(log: FarmActivityLogEntity) {
        dao.upsert(log)
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
    ): FarmActivityLogEntity {
        val now = System.currentTimeMillis()
        val entity = FarmActivityLogEntity(
            activityId = UUID.randomUUID().toString(),
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
        dao.upsert(entity)
        return entity
    }

    override suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double =
        dao.getTotalExpensesBetween(farmerId, start, end) ?: 0.0

    override suspend fun getById(activityId: String): FarmActivityLogEntity? =
        dao.getById(activityId)
}
