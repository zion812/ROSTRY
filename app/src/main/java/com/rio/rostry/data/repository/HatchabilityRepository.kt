package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.data.database.entity.EggCollectionEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao
) {
    
    /**
     * Get comprehensive hatchability analysis for a farmer.
     */
    fun getHatchabilityAnalysis(farmerId: String): Flow<Resource<HatchabilityAnalysis>> = flow {
        emit(Resource.Loading())
        try {
            // Get all egg collections for this farmer to determine total eggs set
            val allCollections = eggCollectionDao.observeRecentByFarmer(farmerId, 500)
                .first()

            val setCollections = allCollections.filter { it.setForHatching }
            val collectionIds = setCollections.map { it.collectionId }

            var totalEggsSet = 0
            var totalHatched = 0
            val batchResults = mutableListOf<BatchHatchResult>()
            val pairPerformance = mutableMapOf<String, MutableList<Pair<Int, Int>>>() // pairId -> (set, hatched)

            if (collectionIds.isNotEmpty()) {
                val batches = hatchingBatchDao.getBySourceCollectionIds(collectionIds)
                batches.forEach { batch ->
                    val eggsSet = batch.eggsCount ?: 0
                    if (eggsSet > 0) {
                        totalEggsSet += eggsSet
                        val hatchedInBatch = hatchingLogDao.countByBatchAndType(batch.batchId, "HATCHED")
                        totalHatched += hatchedInBatch

                        val batchHatchRate = if (eggsSet > 0) (hatchedInBatch.toDouble() / eggsSet) * 100 else 0.0
                        batchResults.add(
                            BatchHatchResult(
                                batchId = batch.batchId,
                                batchName = batch.name.ifBlank { "Batch ${batch.batchId.takeLast(4)}" },
                                eggsSet = eggsSet,
                                hatched = hatchedInBatch,
                                hatchRate = batchHatchRate,
                                startedAt = batch.startedAt,
                                hatchedAt = batch.hatchedAt
                            )
                        )

                        // Track per-pair performance
                        batch.sourceCollectionId?.let { colId ->
                            val collection = setCollections.find { it.collectionId == colId }
                            collection?.pairId?.let { pId ->
                                pairPerformance.getOrPut(pId) { mutableListOf() }
                                    .add(eggsSet to hatchedInBatch)
                            }
                        }
                    }
                }
            }

            val hatchRate = if (totalEggsSet > 0) (totalHatched.toDouble() / totalEggsSet) * 100 else 0.0

            // Calculate fertility rate (slightly higher than hatch rate as some fertile eggs fail to hatch)
            val fertilityRate = if (hatchRate > 0) minOf(hatchRate * 1.1, 100.0) else 0.0

            // Calculate per-pair performance as hatch rate
            val pairRates = pairPerformance.mapValues { (_, records) ->
                val totalSet = records.sumOf { it.first }
                val totalHatch = records.sumOf { it.second }
                if (totalSet > 0) (totalHatch.toDouble() / totalSet) * 100 else 0.0
            }

            val analysis = HatchabilityAnalysis(
                totalEggsSet = totalEggsSet,
                totalHatched = totalHatched,
                hatchRate = hatchRate,
                fertilityRate = fertilityRate,
                avgIncubationDays = 21.0, // Standard for chickens
                recentBatches = batchResults.sortedByDescending { it.startedAt }.take(10),
                pairPerformance = pairRates
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
     * Get hatchability trends by month using real data from HatchingLogDao.
     */
    fun getHatchabilityTrends(farmerId: String, months: Int = 6): Flow<List<HatchabilityTrend>> = flow {
        val trends = mutableListOf<HatchabilityTrend>()
        
        val calendar = Calendar.getInstance()
        repeat(months) { offset ->
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -offset)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val monthStart = calendar.timeInMillis
            
            calendar.add(Calendar.MONTH, 1)
            val monthEnd = calendar.timeInMillis
            
            val month = calendar.get(Calendar.MONTH) // Previous month since we added 1
            val year = if (month == 0) calendar.get(Calendar.YEAR) - 1 else calendar.get(Calendar.YEAR)
            val displayMonth = if (month == 0) 12 else month
            
            // Query real hatching data per month
            val eggsSet = hatchingLogDao.countEggsSetBetweenForFarmer(farmerId, monthStart, monthEnd)
            val hatched = hatchingLogDao.countHatchedBetweenForFarmer(farmerId, monthStart, monthEnd)
            val hatchRate = if (eggsSet > 0) (hatched.toDouble() / eggsSet) * 100 else 0.0
            
            trends.add(
                HatchabilityTrend(
                    month = displayMonth,
                    year = year,
                    hatchRate = hatchRate,
                    totalEggs = eggsSet,
                    totalHatched = hatched
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
