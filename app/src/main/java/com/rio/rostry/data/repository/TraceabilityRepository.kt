package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProductTraitDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.ProductTrackingDao
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
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
}

@Singleton
class TraceabilityRepositoryImpl @Inject constructor(
    private val breedingDao: BreedingRecordDao,
    private val productDao: ProductDao,
    private val lifecycleDao: LifecycleEventDao,
    private val productTraitDao: ProductTraitDao,
    private val transferDao: TransferDao,
    private val productTrackingDao: ProductTrackingDao,
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
            Resource.Success(chain)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to compose transfer chain")
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
