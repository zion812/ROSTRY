package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.data.database.dao.MatingLogDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.data.database.entity.EggCollectionEntity
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity
import com.rio.rostry.data.database.entity.MatingLogEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import androidx.room.withTransaction
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.utils.notif.EnthusiastNotifier

interface EnthusiastBreedingRepository {
    suspend fun createPair(
        farmerId: String,
        maleProductId: String,
        femaleProductId: String,
        notes: String?
    ): Resource<String>

    suspend fun logMating(
        pairId: String,
        observedBehavior: String?,
        conditions: String?
    ): Resource<String>

    suspend fun collectEggs(
        pairId: String,
        count: Int,
        qualityGrade: String,
        weight: Double?
    ): Resource<String>

    suspend fun startIncubation(
        collectionId: String,
        expectedHatchAt: Long,
        temperature: Double?,
        humidity: Double?
    ): Resource<String>

    suspend fun logHatch(
        batchId: String,
        productId: String?,
        eventType: String,
        notes: String?
    ): Resource<String>

    fun observePairsToMate(farmerId: String): Flow<List<BreedingPairEntity>>
    fun observeEggsCollectedToday(farmerId: String): Flow<Int>
    fun observeIncubationTimers(farmerId: String): Flow<List<HatchingBatchEntity>>
    fun observeHatchingDue(farmerId: String, withinDays: Int): Flow<List<HatchingBatchEntity>>
    suspend fun getPairPerformance(pairId: String): Resource<PairPerformanceData>

    suspend fun completeHatch(
        batchId: String,
        successCount: Int,
        failureCount: Int,
        culledCount: Int
    ): Resource<String>
}

data class PairPerformanceData(
    val pairId: String,
    val eggsCollectedTotal: Int,
    val hatchSuccessRate: Double
)

class EnthusiastBreedingRepositoryImpl @Inject constructor(
    private val matingLogDao: MatingLogDao,
    private val eggCollectionDao: EggCollectionDao,
    private val breedingPairDao: BreedingPairDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao,
    private val productDao: ProductDao,
    private val db: com.rio.rostry.data.database.AppDatabase,
    private val analytics: com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker,
    private val taskRepository: com.rio.rostry.data.repository.monitoring.TaskRepository,
    private val vaccinationRepository: com.rio.rostry.data.repository.monitoring.VaccinationRepository,
) : EnthusiastBreedingRepository {

    override suspend fun createPair(
        farmerId: String,
        maleProductId: String,
        femaleProductId: String,
        notes: String?
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            if (maleProductId == femaleProductId) {
                return@withContext Resource.Error("Male and female cannot be the same product")
            }
            val male = productDao.findById(maleProductId)
                ?: return@withContext Resource.Error("Male product not found")
            val female = productDao.findById(femaleProductId)
                ?: return@withContext Resource.Error("Female product not found")

            val now = System.currentTimeMillis()
            val minBirth = now - 365L * 24 * 60 * 60 * 1000
            if (male.birthDate != null && male.birthDate!! > minBirth) {
                return@withContext Resource.Error("Male is not of breeding age (requires >= 12 months)")
            }
            if (female.birthDate != null && female.birthDate!! > minBirth) {
                return@withContext Resource.Error("Female is not of breeding age (requires >= 12 months)")
            }

            // Strict active pair membership validation
            if (breedingPairDao.countActiveByMale(farmerId, maleProductId) > 0) {
                return@withContext Resource.Error("Male already paired")
            }
            if (breedingPairDao.countActiveByFemale(farmerId, femaleProductId) > 0) {
                return@withContext Resource.Error("Female already paired")
            }

            var createdId: String? = null
            db.withTransaction {
                val pairId = UUID.randomUUID().toString()
                val entity = BreedingPairEntity(
                    pairId = pairId,
                    farmerId = farmerId,
                    maleProductId = maleProductId,
                    femaleProductId = femaleProductId,
                    pairedAt = now,
                    status = "ACTIVE",
                    eggsCollected = 0,
                    hatchSuccessRate = 0.0,
                    notes = notes,
                    createdAt = now,
                    updatedAt = now,
                    dirty = true,
                    syncedAt = null
                )
                breedingPairDao.upsert(entity)
                createdId = pairId
            }
            val id = createdId ?: return@withContext Resource.Error("Failed to create pair")
            analytics.trackPairCreate(id, maleProductId, femaleProductId)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create pair")
        }
    }

    override suspend fun logMating(
        pairId: String,
        observedBehavior: String?,
        conditions: String?
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val pair = breedingPairDao.getById(pairId)
                ?: return@withContext Resource.Error("Pair not found")
            val now = System.currentTimeMillis()
            val last24h = now - 24L * 60 * 60 * 1000
            val recentCount = matingLogDao.countByPairBetween(pairId, last24h, now)
            if (recentCount > 0) {
                return@withContext Resource.Error("Duplicate mating within 24 hours is not allowed")
            }
            val logId = UUID.randomUUID().toString()
            val entity = MatingLogEntity(
                logId = logId,
                pairId = pairId,
                farmerId = pair.farmerId,
                matedAt = now,
                observedBehavior = observedBehavior,
                environmentalConditions = conditions,
                notes = null,
                createdAt = now,
                updatedAt = now,
                dirty = true,
                syncedAt = null
            )
            matingLogDao.upsert(entity)
            // Analytics (non-blocking call semantics; do not wrap in try/catch)
            analytics.trackMatingLogAdd(pairId)
            Resource.Success(logId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to log mating")
        }
    }

    override suspend fun collectEggs(
        pairId: String,
        count: Int,
        qualityGrade: String,
        weight: Double?
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            if (count <= 0) return@withContext Resource.Error("Egg count must be > 0")
            val pair = breedingPairDao.getById(pairId)
                ?: return@withContext Resource.Error("Pair not found")
            val now = System.currentTimeMillis()
            val id = UUID.randomUUID().toString()
            val entity = EggCollectionEntity(
                collectionId = id,
                pairId = pairId,
                farmerId = pair.farmerId,
                eggsCollected = count,
                collectedAt = now,
                qualityGrade = qualityGrade,
                weight = weight,
                notes = null,
                createdAt = now,
                updatedAt = now,
                dirty = true,
                syncedAt = null
            )
            eggCollectionDao.upsert(entity)
            // Update pair eggsCollected aggregate
            val updated = pair.copy(
                eggsCollected = (pair.eggsCollected + count),
                updatedAt = now,
                dirty = true
            )
            breedingPairDao.upsert(updated)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to log egg collection")
        }
    }

    override suspend fun startIncubation(
        collectionId: String,
        expectedHatchAt: Long,
        temperature: Double?,
        humidity: Double?
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val collection = eggCollectionDao.getById(collectionId)
                ?: return@withContext Resource.Error("Egg collection not found")
            val pair = breedingPairDao.getById(collection.pairId)
                ?: return@withContext Resource.Error("Breeding pair not found for collection")
            val now = System.currentTimeMillis()
            val batchId = UUID.randomUUID().toString()
            val batch = HatchingBatchEntity(
                batchId = batchId,
                name = "Batch-$collectionId",
                farmerId = pair.farmerId,
                startedAt = now,
                expectedHatchAt = expectedHatchAt,
                temperatureC = temperature,
                humidityPct = humidity,
                eggsCount = collection.eggsCollected,
                sourceCollectionId = collection.collectionId,
                notes = null,
                updatedAt = now,
                dirty = true,
                syncedAt = null
            )
            hatchingBatchDao.upsert(batch)
            analytics.trackIncubationStart(batchId, batch.eggsCount ?: collection.eggsCollected)
            // Schedule daily incubation checks until expected hatch
            var cursor = now + 24L * 60 * 60 * 1000
            while (cursor <= expectedHatchAt) {
                taskRepository.generateIncubationCheckTask(batchId, pair.farmerId, cursor)
                cursor += 24L * 60 * 60 * 1000
            }
            Resource.Success(batchId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to start incubation")
        }
    }

    override suspend fun logHatch(
        batchId: String,
        productId: String?,
        eventType: String,
        notes: String?
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val batch = hatchingBatchDao.getById(batchId)
                ?: return@withContext Resource.Error("Batch not found")
            val now = System.currentTimeMillis()
            val logId = UUID.randomUUID().toString()
            val log = HatchingLogEntity(
                logId = logId,
                batchId = batchId,
                farmerId = batch.farmerId,
                productId = productId,
                eventType = eventType,
                qualityScore = null,
                temperatureC = null,
                humidityPct = null,
                notes = notes,
                createdAt = now,
                updatedAt = now,
                dirty = true,
                syncedAt = null
            )
            hatchingLogDao.upsert(log)
            // Analytics for hatch log
            analytics.trackHatchLogAdd(batchId, eventType)

            // Recompute performance metrics for related pair if source collection known
            val sourceCollectionId = batch.sourceCollectionId
            if (!sourceCollectionId.isNullOrBlank()) {
                val collection = eggCollectionDao.getById(sourceCollectionId!!)
                if (collection != null) {
                    val pair = breedingPairDao.getById(collection.pairId)
                    if (pair != null) {
                        val hatched = hatchingLogDao.countByBatchAndType(batchId, "HATCHED")
                        val eggsSet = batch.eggsCount ?: collection.eggsCollected
                        val rate = if (eggsSet > 0) hatched.toDouble() / eggsSet else 0.0
                        breedingPairDao.upsert(pair.copy(hatchSuccessRate = rate, updatedAt = now, dirty = true))
                        // Optional lifecycle step analytics
                        analytics.trackBreedingLifecycleStep(pair.pairId, "hatch")
                    }
                }
            }
            Resource.Success(logId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to log hatch event")
        }
    }

    override suspend fun completeHatch(
        batchId: String,
        successCount: Int,
        failureCount: Int,
        culledCount: Int
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            if (successCount < 0 || failureCount < 0 || culledCount < 0) return@withContext Resource.Error("Counts cannot be negative")
            val batch = hatchingBatchDao.getById(batchId) ?: return@withContext Resource.Error("Batch not found")
            val now = System.currentTimeMillis()
            val farmerId = batch.farmerId

            // Derive pair lineage if possible
            val pairIds = batch.sourceCollectionId?.let { cid -> eggCollectionDao.getById(cid)?.pairId }
            val pair = pairIds?.let { breedingPairDao.getById(it) }
            val male = pair?.maleProductId
            val female = pair?.femaleProductId

            db.withTransaction {
                // 1) Summary log
                val summaryLogId = UUID.randomUUID().toString()
                hatchingLogDao.upsert(
                    HatchingLogEntity(
                        logId = summaryLogId,
                        batchId = batchId,
                        farmerId = farmerId,
                        productId = null,
                        eventType = "HATCH_COMPLETED",
                        qualityScore = null,
                        temperatureC = null,
                        humidityPct = null,
                        notes = "success=$successCount, failure=$failureCount, culled=$culledCount",
                        createdAt = now,
                        updatedAt = now,
                        dirty = true,
                        syncedAt = null
                    )
                )

                // 2) Update batch status
                hatchingBatchDao.upsert(
                    batch.copy(
                        updatedAt = now,
                        dirty = true
                    )
                )

                // 3) Auto-create chicks
                repeat(successCount) { idx ->
                    val chickId = "chick_${now}_${idx}_${UUID.randomUUID().toString().take(6)}"
                    val product = ProductEntity(
                        productId = chickId,
                        sellerId = farmerId,
                        name = "Chick ${idx + 1}",
                        description = "Auto-created from batch $batchId",
                        category = "BIRD",
                        price = 0.0,
                        quantity = 1.0,
                        unit = "unit",
                        location = "",
                        imageUrls = emptyList(),
                        status = "available",
                        condition = null,
                        harvestDate = null,
                        expiryDate = null,
                        birthDate = now,
                        vaccinationRecordsJson = null,
                        weightGrams = null,
                        heightCm = null,
                        gender = null,
                        color = null,
                        breed = null,
                        familyTreeId = null,
                        parentIdsJson = null,
                        breedingStatus = null,
                        transferHistoryJson = null,
                        createdAt = now,
                        updatedAt = now,
                        lastModifiedAt = now,
                        isDeleted = false,
                        deletedAt = null,
                        dirty = true,
                        stage = "CHICK",
                        lifecycleStatus = "ACTIVE",
                        parentMaleId = male,
                        parentFemaleId = female,
                        ageWeeks = 0,
                        lastStageTransitionAt = now,
                        breederEligibleAt = null,
                        isBatch = false
                    )
                    productDao.upsert(product)

                    // Child log
                    hatchingLogDao.upsert(
                        HatchingLogEntity(
                            logId = UUID.randomUUID().toString(),
                            batchId = batchId,
                            farmerId = farmerId,
                            productId = chickId,
                            eventType = "HATCHED",
                            notes = null,
                            createdAt = now,
                            updatedAt = now,
                            dirty = true,
                            syncedAt = null
                        )
                    )

                    // Schedule Day-1 and Day-7 vaccines + growth check
                    val day1At = now + (1L * 24 * 60 * 60 * 1000)
                    val day7At = now + (7L * 24 * 60 * 60 * 1000)
                    val v1 = VaccinationRecordEntity(
                        vaccinationId = UUID.randomUUID().toString(),
                        productId = chickId,
                        farmerId = farmerId,
                        vaccineType = "STARTER_VACCINE_DAY1",
                        scheduledAt = day1At,
                        createdAt = now,
                        updatedAt = now,
                        dirty = true
                    )
                    val v2 = VaccinationRecordEntity(
                        vaccinationId = UUID.randomUUID().toString(),
                        productId = chickId,
                        farmerId = farmerId,
                        vaccineType = "BOOSTER_VACCINE_DAY7",
                        scheduledAt = day7At,
                        createdAt = now,
                        updatedAt = now,
                        dirty = true
                    )
                    vaccinationRepository.upsert(v1)
                    vaccinationRepository.upsert(v2)
                    taskRepository.generateVaccinationTask(chickId, farmerId, v1.vaccineType, day1At)
                    taskRepository.generateVaccinationTask(chickId, farmerId, v2.vaccineType, day7At)
                    taskRepository.generateGrowthTask(chickId, farmerId, 1, day7At)
                }

                // 4) Update pair KPI hatch success
                if (pair != null) {
                    val eggsSet = batch.eggsCount ?: eggCollectionDao.getById(batch.sourceCollectionId ?: "")?.eggsCollected ?: 0
                    val newHatchedTotal = hatchingLogDao.countByBatchAndType(batchId, "HATCHED") + successCount
                    val rate = if (eggsSet > 0) newHatchedTotal.toDouble() / eggsSet else 0.0
                    breedingPairDao.upsert(pair.copy(hatchSuccessRate = rate, updatedAt = now, dirty = true))
                }
            }

            // Notify - skip notification as context not available in repository
            // EnthusiastNotifier can be called from UI layer after success
            Resource.Success(batchId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to complete hatch")
        }
    }

    override fun observePairsToMate(farmerId: String): Flow<List<BreedingPairEntity>> {
        val sevenDaysMs = 7L * 24 * 60 * 60 * 1000
        return breedingPairDao.observeActive(farmerId)
            .combine(matingLogDao.observeLastMatedByFarmer(farmerId)) { pairs, lastList ->
                val lastMap = lastList.associateBy({ it.pairId }, { it.lastMated ?: 0L })
                val cutoff = System.currentTimeMillis() - sevenDaysMs
                pairs.filter { pair ->
                    val last = lastMap[pair.pairId]
                    last == null || last < cutoff
                }
            }
    }

    override fun observeEggsCollectedToday(farmerId: String): Flow<Int> {
        val startOfDay = startOfDayMillis()
        return eggCollectionDao.observeRecentByFarmer(farmerId, 200).map { list ->
            list.filter { it.collectedAt >= startOfDay }.sumOf { it.eggsCollected }
        }
    }

    override fun observeIncubationTimers(farmerId: String): Flow<List<HatchingBatchEntity>> {
        val now = System.currentTimeMillis()
        return hatchingBatchDao.observeActiveBatchesForFarmer(farmerId, now)
    }

    override fun observeHatchingDue(farmerId: String, withinDays: Int): Flow<List<HatchingBatchEntity>> {
        val now = System.currentTimeMillis()
        val within = now + withinDays * 24L * 60 * 60 * 1000
        return hatchingBatchDao.observeHatchingDue(farmerId, now, within)
    }

    override suspend fun getPairPerformance(pairId: String): Resource<PairPerformanceData> = withContext(Dispatchers.IO) {
        try {
            // Window: last 90 days (configurable as needed)
            val now = System.currentTimeMillis()
            val ninetyDaysAgo = now - 90L * 24 * 60 * 60 * 1000

            // 1) Eggs collected aggregate (per pair)
            // Use existing aggregate for all-time as baseline
            val eggsTotal = eggCollectionDao.getTotalEggsByPair(pairId)

            // For hatch success rate, approximate per pair by following linkage:
            // EggCollection(pairId) -> HatchingBatch(sourceCollectionId) -> HatchingLog(eventType='HATCHED')
            // Fetch recent collections for window and map to batches
            val recentCollections = eggCollectionDao.getCollectionsDueBetween(ninetyDaysAgo, now).filter { it.pairId == pairId }
            val collectionIds = recentCollections.map { it.collectionId }
            val batches = if (collectionIds.isNotEmpty()) hatchingBatchDao.getBySourceCollectionIds(collectionIds) else emptyList()

            // Sum eggs set across batches (prefer batch.eggsCount, fallback to collection.eggsCollected)
            var eggsSetWindow = 0
            var hatchedWindow = 0
            for (batch in batches) {
                val eggsSetForBatch = batch.eggsCount ?: run {
                    batch.sourceCollectionId?.let { id -> recentCollections.firstOrNull { it.collectionId == id }?.eggsCollected }
                } ?: 0
                eggsSetWindow += eggsSetForBatch
                // Count hatched per batch (no per-batch time filter available; acceptable proxy)
                hatchedWindow += hatchingLogDao.countByBatchAndType(batch.batchId, "HATCHED")
            }

            val rate = if (eggsSetWindow > 0) hatchedWindow.toDouble() / eggsSetWindow else 0.0
            val data = PairPerformanceData(pairId, eggsCollectedTotal = eggsTotal, hatchSuccessRate = rate)
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to compute pair performance")
        }
    }
    
    private fun startOfDayMillis(): Long {
        val now = System.currentTimeMillis()
        val oneDay = 24L * 60 * 60 * 1000
        return now - (now % oneDay)
    }
}
