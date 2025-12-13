package com.rio.rostry.data.repository

import com.rio.rostry.utils.Resource
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.database.dao.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.BirdIdGenerator
import com.rio.rostry.domain.model.LifecycleStage
import java.util.UUID
import androidx.room.withTransaction

interface EnthusiastBreedingRepository {
    suspend fun createPair(farmerId: String, maleProductId: String, femaleProductId: String, notes: String?): Resource<String>
    suspend fun logMating(pairId: String, observedBehavior: String?, conditions: String?): Resource<Unit>
    fun observePairsToMate(farmerId: String): Flow<List<BreedingPairEntity>>
    suspend fun logHatch(batchId: String, productId: String?, eventType: String, notes: String?): Resource<Unit>
    suspend fun completeHatch(batchId: String, successCount: Int, failureCount: Int, culledCount: Int): Resource<Unit>
    fun observeEggsCollectedToday(farmerId: String): Flow<Int>
    fun observeIncubationTimers(farmerId: String): Flow<List<HatchingBatchEntity>>
    fun observeHatchingDue(farmerId: String, withinDays: Int = 1): Flow<List<HatchingBatchEntity>>
    suspend fun collectEggs(pairId: String, count: Int, grade: String, weight: Double?): Resource<Unit>
    suspend fun startIncubation(collectionId: String, expectedAt: Long, temp: Double?, humidity: Double?): Resource<Unit>
}

@Singleton
class EnthusiastBreedingRepositoryImpl @Inject constructor(
    private val matingLogDao: MatingLogDao,
    private val eggCollectionDao: EggCollectionDao,
    private val breedingPairDao: BreedingPairDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao,
    private val productDao: ProductDao,
    private val db: AppDatabase,
    private val analytics: EnthusiastAnalyticsTracker,
    private val taskRepository: TaskRepository,
    private val vaccinationRepository: VaccinationRepository,
    private val traceabilityRepository: TraceabilityRepository
) : EnthusiastBreedingRepository {

    override suspend fun createPair(farmerId: String, maleProductId: String, femaleProductId: String, notes: String?): Resource<String> {
        if (maleProductId.isBlank() || femaleProductId.isBlank()) {
            return Resource.Error("Invalid product IDs")
        }
        return try {
            val pairId = UUID.randomUUID().toString()
            val pair = BreedingPairEntity(
                pairId = pairId,
                farmerId = farmerId,
                maleProductId = maleProductId,
                femaleProductId = femaleProductId,
                pairedAt = System.currentTimeMillis(),
                status = "ACTIVE",
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            breedingPairDao.upsert(pair)
            analytics.trackPairCreate(pairId, maleProductId, femaleProductId)
            Resource.Success(pairId)
        } catch (e: Exception) {
            Resource.Error("Failed to create pair: ${e.message}")
        }
    }

    override suspend fun logMating(pairId: String, observedBehavior: String?, conditions: String?): Resource<Unit> {
        val pair = breedingPairDao.getById(pairId) ?: return Resource.Error("Pair not found")
        return try {
            val log = MatingLogEntity(
                logId = UUID.randomUUID().toString(),
                pairId = pairId,
                farmerId = pair.farmerId,
                observedBehavior = observedBehavior,
                environmentalConditions = conditions,
                matedAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            matingLogDao.upsert(log)
            analytics.trackMatingLogAdd(pairId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to log mating: ${e.message}")
        }
    }

    override fun observePairsToMate(farmerId: String): Flow<List<BreedingPairEntity>> {
        val activePairs = breedingPairDao.observeActive(farmerId)
        val lastMated = matingLogDao.observeLastMatedByFarmer(farmerId)
        return activePairs.combine(lastMated) { pairs, matings ->
            val matingMap = matings.associateBy { it.pairId }
            val cutoff = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
            pairs.filter { pair ->
                val lastMating = matingMap[pair.pairId]
                // Explicitly handle nullability and type for comparison
                val lastMatedTime = lastMating?.lastMated ?: 0L
                lastMating == null || lastMatedTime < cutoff
            }
        }
    }

    override suspend fun logHatch(batchId: String, productId: String?, eventType: String, notes: String?): Resource<Unit> {
        val batch = hatchingBatchDao.getById(batchId) ?: return Resource.Error("Batch not found")
        return try {
            db.withTransaction {
                val log = HatchingLogEntity(
                    logId = UUID.randomUUID().toString(),
                    batchId = batchId,
                    farmerId = batch.farmerId,
                    productId = productId,
                    eventType = eventType,
                    notes = notes,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                hatchingLogDao.upsert(log)
                // Update pair hatch rate if applicable
                if (batch.sourceCollectionId != null) {
                    val collection = eggCollectionDao.getById(batch.sourceCollectionId!!)
                    val pair = breedingPairDao.getById(collection?.pairId ?: "")
                    if (pair != null) {
                        val hatched = hatchingLogDao.countByBatchAndType(batchId, "HATCHED")
                        val eggs = batch.eggsCount ?: collection?.eggsCollected ?: 0
                        val rate = if (eggs > 0) hatched.toDouble() / eggs else 0.0
                        breedingPairDao.upsert(pair.copy(hatchSuccessRate = rate, updatedAt = System.currentTimeMillis()))
                    }
                }
            }
            analytics.trackHatchLogAdd(batchId, eventType)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to log hatch: ${e.message}")
        }
    }

    override suspend fun completeHatch(batchId: String, successCount: Int, failureCount: Int, culledCount: Int): Resource<Unit> {
        val batch = hatchingBatchDao.getById(batchId) ?: return Resource.Error("Batch not found")
        return try {
            db.withTransaction {
                // Get parent products
                val collection = batch.sourceCollectionId?.let { eggCollectionDao.getById(it) }
                val pair = collection?.pairId?.let { breedingPairDao.getById(it) }
                val maleProduct = pair?.maleProductId?.let { productDao.findById(it) }
                val femaleProduct = pair?.femaleProductId?.let { productDao.findById(it) }
                // Derive color and breed from parent products; fallback to "Mixed" and "Unknown Breed" if parents are not available or lack these fields
                val color = femaleProduct?.color ?: maleProduct?.color ?: "Mixed"
                val breed = femaleProduct?.breed ?: maleProduct?.breed ?: "Unknown Breed"
                val farmerId = batch.farmerId
                val now = System.currentTimeMillis()
                for (i in 1..successCount) {
                    val productId = UUID.randomUUID().toString()
                    val birdCode = BirdIdGenerator.generate(color, breed, farmerId, productId)
                    val colorTag = BirdIdGenerator.colorTag(color)
                    val chick = ProductEntity(
                        productId = productId,
                        sellerId = farmerId,
                        name = "Chick $i from batch $batchId",
                        category = "Poultry",
                        stage = LifecycleStage.CHICK,
                        lifecycleStatus = "hatched",
                        parentMaleId = maleProduct?.productId,
                        parentFemaleId = femaleProduct?.productId,
                        color = color,
                        breed = breed,
                        birdCode = birdCode,
                        colorTag = colorTag,
                        batchId = batchId,
                        createdAt = now,
                        updatedAt = now
                    )
                    productDao.upsert(chick)
                }
                // Update pair hatch rate if applicable
                if (batch.sourceCollectionId != null) {
                    val hatched = hatchingLogDao.countByBatchAndType(batchId, "HATCHED")
                    val eggs = batch.eggsCount ?: collection?.eggsCollected ?: 0
                    val rate = if (eggs > 0) hatched.toDouble() / eggs else 0.0
                    pair?.let {
                        breedingPairDao.upsert(it.copy(hatchSuccessRate = rate, updatedAt = System.currentTimeMillis()))
                    }
                }
            }
            analytics.trackHatchLogAdd(batchId, "COMPLETED")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to complete hatch: ${e.message}")
        }
    }

    override fun observeEggsCollectedToday(farmerId: String): Flow<Int> {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % (24 * 60 * 60 * 1000L)) // Start of today
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000L) - 1 // End of today
        return eggCollectionDao.observeRecentByFarmer(farmerId, 50) // Use a reasonable limit
            .map { collections ->
                collections.filter { it.collectedAt in startOfDay..endOfDay }
                    .sumOf { it.eggsCollected }
            }
    }

    override fun observeIncubationTimers(farmerId: String): Flow<List<HatchingBatchEntity>> {
        return hatchingBatchDao.observeActiveBatchesForFarmer(farmerId, System.currentTimeMillis())
    }

    override fun observeHatchingDue(farmerId: String, withinDays: Int): Flow<List<HatchingBatchEntity>> {
        val now = System.currentTimeMillis()
        val end = now + (withinDays * 24 * 60 * 60 * 1000L)
        return hatchingBatchDao.observeHatchingDue(farmerId, now, end)
    }

    override suspend fun collectEggs(pairId: String, count: Int, grade: String, weight: Double?): Resource<Unit> {
        val pair = breedingPairDao.getById(pairId) ?: return Resource.Error("Pair not found")
        return try {
            val collection = EggCollectionEntity(
                collectionId = UUID.randomUUID().toString(),
                pairId = pairId,
                farmerId = pair.farmerId,
                eggsCollected = count,
                collectedAt = System.currentTimeMillis(),
                qualityGrade = grade,
                weight = weight,
                notes = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                dirty = false,
                syncedAt = null
            )
            eggCollectionDao.upsert(collection)
            analytics.trackEggCollect(pairId, count)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to collect eggs: ${e.message}")
        }
    }

    override suspend fun startIncubation(collectionId: String, expectedAt: Long, temp: Double?, humidity: Double?): Resource<Unit> {
        val collection = eggCollectionDao.getById(collectionId) ?: return Resource.Error("Collection not found")
        val pair = breedingPairDao.getById(collection.pairId) ?: return Resource.Error("Pair not found")
        return try {
            val batch = HatchingBatchEntity(
                batchId = UUID.randomUUID().toString(),
                name = "Hatching Batch - ${collection.collectedAt}",
                farmerId = pair.farmerId,
                startedAt = System.currentTimeMillis(),
                expectedHatchAt = expectedAt,
                temperatureC = temp,
                humidityPct = humidity,
                eggsCount = collection.eggsCollected,
                sourceCollectionId = collectionId,
                notes = null,
                status = "ACTIVE"
            )
            hatchingBatchDao.upsert(batch)
            analytics.trackIncubationStart(batch.batchId, collection.eggsCollected)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to start incubation: ${e.message}")
        }
    }
}