package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.FarmActivityLog
import com.rio.rostry.domain.monitoring.repository.FarmActivityLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe farm activity log repository stub used during modular migration.
 */
@Singleton
class FarmActivityLogRepositoryImpl @Inject constructor() : FarmActivityLogRepository {

    override fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLog>> = flowOf(emptyList())

    override fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLog?> = flowOf(null)

    override fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLog>> =
        flowOf(emptyList())

    override fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLog>> =
        flowOf(emptyList())

    override fun observeForProduct(productId: String): Flow<List<FarmActivityLog>> = flowOf(emptyList())

    override suspend fun upsert(log: FarmActivityLog) = Unit

    override suspend fun logActivity(
        farmerId: String,
        productId: String?,
        activityType: String,
        amount: Double?,
        quantity: Double?,
        category: String?,
        description: String?,
        notes: String?
    ): FarmActivityLog = throw UnsupportedOperationException("Activity logging unavailable in stub")

    override suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double = 0.0

    override suspend fun getById(activityId: String): FarmActivityLog? = null

    override suspend fun deleteActivity(activityId: String) = Unit

    override suspend fun getTotalFeedQuantityForAsset(assetId: String): Double = 0.0
}
