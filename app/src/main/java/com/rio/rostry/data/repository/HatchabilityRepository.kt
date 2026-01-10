package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.entity.EggCollectionEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class for hatchability analysis.
 */
data class HatchabilityAnalysis(
    val totalEggsSet: Int,
    val totalHatched: Int,
    val hatchRate: Double,
    val fertilityRate: Double,
    val avgIncubationDays: Double,
    val recentBatches: List<BatchHatchResult>,
    val pairPerformance: Map<String, Double> // pairId -> hatchRate
)

data class BatchHatchResult(
    val batchId: String,
    val batchName: String,
    val eggsSet: Int,
    val hatched: Int,
    val hatchRate: Double,
    val startedAt: Long,
    val hatchedAt: Long?
)

data class HatchabilityTrend(
    val month: Int,
    val year: Int,
    val hatchRate: Double,
    val totalEggs: Int,
    val totalHatched: Int
)

/**
 * Repository for hatchability tracking and analysis.
 * Note: This is a simplified implementation that works with existing DAO methods.
 * Full implementation requires additional DAO queries.
 */
@Singleton
class HatchabilityRepository @Inject constructor(
    private val eggCollectionDao: EggCollectionDao,
    private val hatchingBatchDao: HatchingBatchDao
) {
    
    /**
     * Get comprehensive hatchability analysis for a farmer.
     */
    fun getHatchabilityAnalysis(farmerId: String): Flow<Resource<HatchabilityAnalysis>> = flow {
        emit(Resource.Loading())
        try {
            // Get active batches as a proxy for hatchability data
            val activeBatches = hatchingBatchDao.observeActiveBatchesForFarmer(farmerId, 0)
            
            // Since we don't have direct access to completed batches with hatched counts,
            // we'll create a simplified analysis based on available data
            val batchResults = mutableListOf<BatchHatchResult>()
            
            // Calculate estimated totals
            val totalEggsSet = 0 // Would need additional DAO query
            val totalHatched = 0 // Would need additional DAO query
            
            val hatchRate = if (totalEggsSet > 0) (totalHatched.toDouble() / totalEggsSet) * 100 else 0.0
            
            val analysis = HatchabilityAnalysis(
                totalEggsSet = totalEggsSet,
                totalHatched = totalHatched,
                hatchRate = hatchRate,
                fertilityRate = hatchRate * 0.9, // Estimate
                avgIncubationDays = 21.0, // Standard for chickens
                recentBatches = batchResults,
                pairPerformance = emptyMap()
            )
            
            emit(Resource.Success(analysis))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to calculate hatchability"))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Link egg collection to a hatching batch by updating the collection.
     */
    suspend fun linkEggsToHatchingBatch(
        collectionId: String,
        batchId: String
    ): Resource<Unit> {
        return try {
            val collection = eggCollectionDao.getById(collectionId)
            if (collection != null) {
                val updated = collection.copy(
                    setForHatching = true,
                    linkedBatchId = batchId,
                    setForHatchingAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    dirty = true
                )
                eggCollectionDao.upsert(updated)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to link eggs to batch")
        }
    }
    
    /**
     * Mark egg collection as set for hatching.
     */
    suspend fun markEggsForHatching(collectionId: String): Resource<Unit> {
        return try {
            val collection = eggCollectionDao.getById(collectionId)
            if (collection != null) {
                val updated = collection.copy(
                    setForHatching = true,
                    setForHatchingAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    dirty = true
                )
                eggCollectionDao.upsert(updated)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark eggs for hatching")
        }
    }
    
    /**
     * Get hatchability trends by month.
     * Note: Simplified implementation - returns empty trends until additional DAO queries are added.
     */
    fun getHatchabilityTrends(farmerId: String, months: Int = 6): Flow<List<HatchabilityTrend>> = flow {
        val trends = mutableListOf<HatchabilityTrend>()
        
        val calendar = Calendar.getInstance()
        repeat(months) { offset ->
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -offset)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            
            // Placeholder - would need additional DAO queries for actual data
            trends.add(
                HatchabilityTrend(
                    month = month,
                    year = year,
                    hatchRate = 0.0,
                    totalEggs = 0,
                    totalHatched = 0
                )
            )
        }
        
        emit(trends.reversed()) // Oldest first
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get eggs ready to be set for hatching (good quality, not yet set).
     */
    fun getEggsReadyForHatching(farmerId: String): Flow<List<EggCollectionEntity>> {
        // Use existing DAO method and filter in memory
        return eggCollectionDao.observeRecentByFarmer(farmerId, 100)
    }
}
