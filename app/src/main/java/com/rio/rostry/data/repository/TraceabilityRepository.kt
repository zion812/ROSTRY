package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProductTraitDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.ProductTrackingDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

interface TraceabilityRepository {
    suspend fun addBreedingRecord(record: BreedingRecordEntity): Resource<Unit>
    suspend fun ancestors(productId: String, maxDepth: Int = 5): Resource<Map<Int, List<String>>>
    suspend fun descendants(productId: String, maxDepth: Int = 5): Resource<Map<Int, List<String>>>
    suspend fun breedingSuccess(parentId: String, partnerId: String): Resource<Pair<Int, Int>>
    suspend fun addLifecycleEvent(event: LifecycleEventEntity): Resource<Unit>
    suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int = 10): Resource<Boolean>
    suspend fun verifyParentage(childId: String, parentId: String, partnerId: String): Resource<Boolean>
    suspend fun getTransferChain(productId: String): Resource<List<Any>>
    suspend fun validateProductLineage(productId: String, expectedParentMaleId: String?, expectedParentFemaleId: String?): Resource<Boolean>
    suspend fun getProductHealthScore(productId: String): Resource<Int>
    suspend fun getTransferEligibilityReport(productId: String): Resource<Map<String, Any>>
    /** Fetches metadata for a single node, optimized for individual queries. */
    suspend fun getNodeMetadata(productId: String): Resource<NodeMetadata>
    /** Fetches metadata for multiple nodes in parallel for efficiency. */
    suspend fun getNodeMetadataBatch(productIds: List<String>): Resource<Map<String, NodeMetadata>>
    fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String?
}

data class NodeMetadata(
    val productId: String,
    val name: String,
    val breed: String?,
    val stage: LifecycleStage?,
    val ageWeeks: Int?,
    val healthScore: Int,
    val lifecycleStatus: String?
)

@Singleton
class TraceabilityRepositoryImpl @Inject constructor(
    private val breedingDao: BreedingRecordDao,
    private val productDao: ProductDao,
    private val lifecycleDao: LifecycleEventDao,
    private val productTraitDao: ProductTraitDao,
    private val transferDao: TransferDao,
    private val transferVerificationDao: TransferVerificationDao,
    private val disputeDao: DisputeDao,
    private val productTrackingDao: ProductTrackingDao,
    private val vaccinationDao: VaccinationRecordDao,
    private val dailyLogDao: DailyLogDao,
    private val growthDao: GrowthRecordDao,
    private val quarantineDao: QuarantineRecordDao,
) : TraceabilityRepository {

    // Simple in-memory LRU cache for traversals
    private val cache = object {
        private val cap = 64
        private val map = object : LinkedHashMap<String, Map<Int, List<String>>>(cap, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Map<Int, List<String>>>?): Boolean = size > cap
        }
        fun get(key: String): Map<Int, List<String>>? = map[key]
        fun put(key: String, value: Map<Int, List<String>>) { map[key] = value }
    }

    // Cache for health scores to reduce redundant queries
    private val healthScoreCache = mutableMapOf<String, Pair<Int, Long>>()

    override suspend fun addBreedingRecord(record: BreedingRecordEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Cycle prevention: ensure parent/partner are not descendants of child
            val desc = collectDescendants(record.childId, 10)
            if (desc.values.flatten().contains(record.parentId) || desc.values.flatten().contains(record.partnerId)) {
                return@withContext Resource.Error("Cycle detected in family tree")
            }
            breedingDao.insert(record)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add breeding record")
        }
    }

    override suspend fun ancestors(productId: String, maxDepth: Int): Resource<Map<Int, List<String>>> = withContext(Dispatchers.IO) {
        try {
            val key = "anc:$productId:$maxDepth"
            val cached = cache.get(key)
            if (cached != null) return@withContext Resource.Success(cached)
            val res = collectAncestors(productId, maxDepth)
            cache.put(key, res)
            Resource.Success(res)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to collect ancestors")
        }
    }

    override suspend fun descendants(productId: String, maxDepth: Int): Resource<Map<Int, List<String>>> = withContext(Dispatchers.IO) {
        try {
            val key = "desc:$productId:$maxDepth"
            val cached = cache.get(key)
            if (cached != null) return@withContext Resource.Success(cached)
            val res = collectDescendants(productId, maxDepth)
            cache.put(key, res)
            Resource.Success(res)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to collect descendants")
        }
    }

    override suspend fun breedingSuccess(parentId: String, partnerId: String): Resource<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        try {
            val success = breedingDao.successfulBreedings(parentId, partnerId)
            val total = breedingDao.totalBreedings(parentId, partnerId)
            Resource.Success(success to total)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to compute breeding success")
        }
    }

    override suspend fun addLifecycleEvent(event: LifecycleEventEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            lifecycleDao.insert(event)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add lifecycle event")
        }
    }

    override suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (productId == ancestorId) return@withContext Resource.Success(true)
            val anc = collectAncestors(productId, maxDepth)
            val found = anc.values.flatten().contains(ancestorId)
            Resource.Success(found)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to verify path")
        }
    }

    override suspend fun verifyParentage(childId: String, parentId: String, partnerId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            val recs = breedingDao.recordsByChild(childId)
            val ok = recs.any { (it.parentId == parentId && it.partnerId == partnerId) || (it.parentId == partnerId && it.partnerId == parentId) }
            Resource.Success(ok)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to verify parentage")
        }
    }

    override suspend fun getTransferChain(productId: String): Resource<List<Any>> = withContext(Dispatchers.IO) {
        try {
            // Compose chain from transfer records and product tracking events
            val transfers: List<com.rio.rostry.data.database.entity.TransferEntity> = transferDao.getTransfersByProduct(productId)
            val track = productTrackingDao.getByProduct(productId).first()
            val chain = mutableListOf<Any>()
            chain.addAll(transfers)
            chain.addAll(track)
            chain.sortBy {
                when (it) {
                    is com.rio.rostry.data.database.entity.TransferEntity -> it.initiatedAt
                    is com.rio.rostry.data.database.entity.ProductTrackingEntity -> it.timestamp
                    else -> Long.MAX_VALUE
                }
            }
            // Validation checks: verify each transfer has proper verification records and flag disputes
            val transfersWithIssues = transfers.filter { transfer ->
                val ver = transferVerificationDao.getByTransfer(transfer.transferId)
                val hasApproved = ver.any { it.status == "APPROVED" }
                val disputes = disputeDao.getByTransfer(transfer.transferId)
                val hasOpenDispute = disputes.any { it.status == "OPEN" || it.status == "UNDER_REVIEW" }
                !hasApproved || hasOpenDispute
            }
            if (transfersWithIssues.isNotEmpty()) {
                chain.add("Validation Note: ${transfersWithIssues.size} transfer(s) lack approval or have open disputes")
            }
            Resource.Success(chain)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to compose transfer chain")
        }
    }

    override suspend fun validateProductLineage(productId: String, expectedParentMaleId: String?, expectedParentFemaleId: String?): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            val recs = breedingDao.recordsByChild(productId)
            val actualParents = recs.flatMap { listOf(it.parentId, it.partnerId) }.distinct()
            val expectedParents = listOfNotNull(expectedParentMaleId, expectedParentFemaleId)
            if (actualParents.size != expectedParents.size || !actualParents.containsAll(expectedParents)) {
                return@withContext Resource.Error("Lineage mismatch detected")
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to validate lineage")
        }
    }

    override suspend fun getProductHealthScore(productId: String): Resource<Int> = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val cached = healthScoreCache[productId]
            if (cached != null && now - cached.second < 5 * 60 * 1000L) { // 5 minutes cache
                return@withContext Resource.Success(cached.first)
            }
            var score = 0
            val thirtyDaysAgo = now - 30 * 24 * 60 * 60 * 1000L
            val sevenDaysAgo = now - 7 * 24 * 60 * 60 * 1000L
            val fourteenDaysAgo = now - 14 * 24 * 60 * 60 * 1000L
            // Recent vaccination: +30 points
            val vaccinations = vaccinationDao.observeForProduct(productId).first()
            if (vaccinations.any { it.administeredAt != null && it.administeredAt!! >= thirtyDaysAgo }) {
                score += 30
            }
            // Recent health logs: +30 points
            val logs = dailyLogDao.observeForProduct(productId).first()
            if (logs.any { it.createdAt >= sevenDaysAgo }) {
                score += 30
            }
            // Recent growth records: +20 points
            val growths = growthDao.observeForProduct(productId).first()
            if (growths.any { it.createdAt >= fourteenDaysAgo }) {
                score += 20
            }
            // No active quarantine: +20 points
            val quarantines = quarantineDao.observeForProduct(productId).first()
            if (quarantines.none { it.status == "ACTIVE" }) {
                score += 20
            }
            healthScoreCache[productId] = score to now
            Resource.Success(score)
        } catch (e: Exception) {
            Resource.Error<Int>(e.message ?: "Failed to calculate health score")
        }
    }

    override suspend fun getTransferEligibilityReport(productId: String): Resource<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val reasons = mutableListOf<String>()
            val now = System.currentTimeMillis()
            val thirtyDaysAgo = now - 30 * 24 * 60 * 60 * 1000L
            val sevenDaysAgo = now - 7 * 24 * 60 * 60 * 1000L
            val fourteenDaysAgo = now - 14 * 24 * 60 * 60 * 1000L
            // Check product lifecycle
            val product = productDao.findById(productId)
            if (product == null) {
                reasons.add("Product not found")
            } else {
                if (product.lifecycleStatus == "QUARANTINE") reasons.add("Product in quarantine")
                if (product.lifecycleStatus == "DECEASED") reasons.add("Product deceased")
                if (product.lifecycleStatus == "TRANSFERRED") reasons.add("Product already transferred")
            }
            // Check farm data freshness
            val vaccinations = vaccinationDao.observeForProduct(productId).first()
            val lastVacc = vaccinations.filter { it.administeredAt != null }.maxByOrNull { it.administeredAt!! }?.administeredAt
            if (lastVacc == null || lastVacc < thirtyDaysAgo) {
                reasons.add("No recent vaccination")
            }
            val logs = dailyLogDao.observeForProduct(productId).first()
            val lastLog = logs.maxByOrNull { it.createdAt }?.createdAt
            if (lastLog == null || lastLog < sevenDaysAgo) {
                reasons.add("No recent health log")
            }
            val growths = growthDao.observeForProduct(productId).first()
            val lastGrowth = growths.maxByOrNull { it.createdAt }?.createdAt
            if (lastGrowth == null || lastGrowth < fourteenDaysAgo) {
                reasons.add("No recent growth record")
            }
            val quarantines = quarantineDao.observeForProduct(productId).first()
            val quarantineStatus = quarantines.firstOrNull { it.status == "ACTIVE" }?.status ?: "NONE"
            if (quarantineStatus == "ACTIVE") {
                reasons.add("Active quarantine")
            }
            val healthScore = getProductHealthScore(productId).data ?: 0
            val eligible = reasons.isEmpty()
            val report: Map<String, Any> = mapOf(
                "eligible" to eligible,
                "reasons" to reasons,
                "healthScore" to healthScore,
                "lastVaccination" to (lastVacc ?: -1L),
                "lastHealthLog" to (lastLog ?: -1L),
                "quarantineStatus" to quarantineStatus
            )
            Resource.Success<Map<String, Any>>(report)
        } catch (e: Exception) {
            Resource.Error<Map<String, Any>>(e.message ?: "Failed to get eligibility report")
        }
    }

    override suspend fun getNodeMetadata(productId: String): Resource<NodeMetadata> = withContext(Dispatchers.IO) {
        try {
            val product = productDao.findById(productId) ?: return@withContext Resource.Error("Product not found")
            val healthScore = getProductHealthScore(productId).data ?: 0
            val metadata = NodeMetadata(
                productId = product.productId,
                name = product.name,
                breed = product.breed,
                stage = product.stage,
                ageWeeks = product.ageWeeks,
                healthScore = healthScore,
                lifecycleStatus = product.lifecycleStatus
            )
            Resource.Success(metadata)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get node metadata")
        }
    }

    override suspend fun getNodeMetadataBatch(productIds: List<String>): Resource<Map<String, NodeMetadata>> = withContext(Dispatchers.IO) {
        try {
            val metadataMap = mutableMapOf<String, NodeMetadata>()
            val deferreds = productIds.map { id ->
                async {
                    val product = productDao.findById(id)
                    if (product != null) {
                        val healthScore = getProductHealthScore(id).data ?: 0
                        id to NodeMetadata(
                            productId = product.productId,
                            name = product.name,
                            breed = product.breed,
                            stage = product.stage,
                            ageWeeks = product.ageWeeks,
                            healthScore = healthScore,
                            lifecycleStatus = product.lifecycleStatus
                        )
                    } else null
                }
            }
            deferreds.forEach { deferred ->
                val result = deferred.await()
                if (result != null) {
                    metadataMap[result.first] = result.second
                }
            }
            Resource.Success(metadataMap)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get batch node metadata")
        }
    }

    override fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String? {
        return when {
            !maleId.isNullOrBlank() && !femaleId.isNullOrBlank() -> "FT_${'$'}maleId_${'$'}femaleId"
            !pairId.isNullOrBlank() -> "FT_PAIR_${'$'}pairId"
            else -> null
        }
    }

    private suspend fun collectAncestors(rootId: String, maxDepth: Int): Map<Int, List<String>> {
        // BFS upwards via breeding records where childId = current
        val levels = mutableMapOf<Int, MutableList<String>>()
        val visited = mutableSetOf<String>(rootId)
        var frontier = listOf(rootId)
        var depth = 0
        while (frontier.isNotEmpty() && depth < maxDepth) {
            val next = mutableListOf<String>()
            for (node in frontier) {
                val parents = breedingDao.recordsByChild(node)
                parents.forEach { rec ->
                    if (visited.add(rec.parentId)) next += rec.parentId
                    if (visited.add(rec.partnerId)) next += rec.partnerId
                }
            }
            if (next.isEmpty()) break
            depth += 1
            levels.getOrPut(depth) { mutableListOf() }.addAll(next)
            frontier = next.distinct()
        }
        return levels
    }

    private suspend fun collectDescendants(rootId: String, maxDepth: Int): Map<Int, List<String>> {
        // BFS downwards via breeding records where parentId/partnerId = current
        val levels = mutableMapOf<Int, MutableList<String>>()
        val visited = mutableSetOf<String>(rootId)
        var frontier = listOf(rootId)
        var depth = 0
        while (frontier.isNotEmpty() && depth < maxDepth) {
            val next = mutableListOf<String>()
            for (node in frontier) {
                val p = breedingDao.recordsByParent(node)
                p.forEach { rec ->
                    if (rec.parentId == node && visited.add(rec.childId)) next += rec.childId
                    if (rec.partnerId == node && visited.add(rec.childId)) next += rec.childId
                }
            }
            if (next.isEmpty()) break
            depth += 1
            levels.getOrPut(depth) { mutableListOf() }.addAll(next)
            frontier = next.distinct()
        }
        return levels
    }
}
