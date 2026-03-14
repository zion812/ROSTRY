package com.rio.rostry.data.farm.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.BreedingRecord
import com.rio.rostry.core.model.FamilyTree
import com.rio.rostry.core.model.GraphData
import com.rio.rostry.core.model.LifecycleEvent
import com.rio.rostry.core.model.NodeMetadata
import com.rio.rostry.core.model.Result
import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.farm.mapper.toBreedingRecord
import com.rio.rostry.data.farm.mapper.toEntity
import com.rio.rostry.domain.farm.repository.TraceabilityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation of TraceabilityRepository using Room database and Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class TraceabilityRepositoryImpl @Inject constructor(
    private val breedingRecordDao: BreedingRecordDao,
    private val lifecycleEventDao: LifecycleEventDao,
    private val productDao: ProductDao,
    private val transferDao: TransferDao,
    private val firestore: FirebaseFirestore
) : TraceabilityRepository {

    private val productsCollection = firestore.collection("products")
    private val transfersCollection = firestore.collection("transfers")

    override suspend fun addBreedingRecord(record: BreedingRecord): Result<Unit> {
        try {
            breedingRecordDao.insert(record.toEntity())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun ancestors(productId: String, maxDepth: Int): Result<Map<Int, List<String>>> {
        return try {
            val result = mutableMapOf<Int, List<String>>()
            var currentId: String? = productId
            var currentDepth = 0
            
            while (currentId != null && currentDepth < maxDepth) {
                val product = productDao.findById(currentId)
                val ancestors = mutableListOf<String>()
                
                product?.parentMaleId?.let { ancestors.add(it) }
                product?.parentFemaleId?.let { ancestors.add(it) }
                
                if (ancestors.isNotEmpty()) {
                    result[currentDepth] = ancestors
                }
                
                // Move to next generation (use first parent)
                currentId = product?.parentMaleId ?: product?.parentFemaleId
                currentDepth++
            }
            
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun descendants(productId: String, maxDepth: Int): Result<Map<Int, List<String>>> {
        return try {
            val result = mutableMapOf<Int, List<String>>()
            val toProcess = mutableListOf(productId to 0)
            val processed = mutableSetOf<String>()
            
            while (toProcess.isNotEmpty()) {
                val (currentId, depth) = toProcess.removeAt(0)
                if (currentId in processed || depth >= maxDepth) continue
                processed.add(currentId)
                
                val offspring = breedingRecordDao.getOffspring(currentId)
                val descendantIds = offspring.map { it.childId }
                
                if (descendantIds.isNotEmpty()) {
                    result[depth] = descendantIds
                    descendantIds.forEach { descendantId ->
                        toProcess.add(descendantId to depth + 1)
                    }
                }
            }
            
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun breedingSuccess(parentId: String, partnerId: String): Result<Pair<Int, Int>> {
        val successful = breedingRecordDao.successfulBreedings(parentId, partnerId)
        val total = breedingRecordDao.totalBreedings(parentId, partnerId)
        return Result.Success(successful to total)
    }

    override suspend fun addLifecycleEvent(event: LifecycleEvent): Result<Unit> {
        try {
            lifecycleEventDao.insert(event.toEntity())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int): Result<Boolean> {
        val ancestors = ancestors(productId, maxDepth)
        return when (ancestors) {
            is Result.Success -> {
                val allAncestors = ancestors.data.values.flatten()
                Result.Success(ancestorId in allAncestors)
            }
            is Result.Error -> Result.Success(false)
        }
    }

    override suspend fun verifyParentage(childId: String, parentId: String, partnerId: String): Result<Boolean> {
        val records = breedingRecordDao.recordsByChild(childId)
        val exists = records.any { it.parentId == parentId && it.partnerId == partnerId }
        return Result.Success(exists)
    }

    override suspend fun getTransferChain(productId: String): Result<List<Any>> {
        return try {
            // Query Firestore for transfer history
            val snapshot = transfersCollection
                .whereArrayContains("productIds", productId)
                .orderBy("initiatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val transferChain = snapshot.documents.map { doc ->
                mapOf(
                    "transferId" to doc.id,
                    "fromUserId" to doc.getString("fromUserId"),
                    "toUserId" to doc.getString("toUserId"),
                    "status" to doc.getString("status"),
                    "initiatedAt" to doc.getLong("initiatedAt"),
                    "completedAt" to doc.getLong("completedAt")
                )
            }
            
            Result.Success(transferChain)
        } catch (e: Exception) {
            // Fallback to empty list on error
            Result.Success(emptyList<Any>())
        }
    }

    override suspend fun validateProductLineage(
        productId: String,
        expectedParentMaleId: String?,
        expectedParentFemaleId: String?
    ): Result<Boolean> {
        return try {
            val product = productDao.findById(productId)
            if (product == null) {
                return Result.Success(false)
            }
            
            val maleMatch = expectedParentMaleId == null || product.parentMaleId == expectedParentMaleId
            val femaleMatch = expectedParentFemaleId == null || product.parentFemaleId == expectedParentFemaleId
            
            Result.Success(maleMatch && femaleMatch)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getProductHealthScore(productId: String): Result<Int> {
        return try {
            val events = lifecycleEventDao.getEventsForProduct(productId)
            val healthEvents = events.filter { it.eventType == "HEALTH_CHECK" || it.eventType == "VACCINATION" }
            val totalEvents = events.size
            
            if (totalEvents == 0) {
                return Result.Success(100)
            }
            
            val healthyEvents = healthEvents.count { 
                (it.metadataJson?.contains("healthy") == true) || 
                (it.metadataJson?.contains("normal") == true) 
            }
            
            val score = ((healthyEvents.toDouble() / totalEvents) * 100).toInt()
            Result.Success(score.coerceIn(0, 100))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTransferEligibilityReport(productId: String): Result<Map<String, Any>> {
        return try {
            val healthScore = getProductHealthScore(productId)
            val healthValue = (healthScore as? Result.Success)?.data ?: 100
            
            val product = productDao.findById(productId)
            val isEligible = product?.let {
                it.status == "ACTIVE" && 
                it.quantity > 0 &&
                healthValue >= 70
            } ?: false
            
            val reasons = mutableListOf<String>()
            if (product?.status != "ACTIVE") reasons.add("Product is not active")
            if (product?.quantity ?: 0.0 <= 0) reasons.add("No quantity available")
            if (healthValue < 70) reasons.add("Health score below threshold")
            
            Result.Success(mapOf(
                "eligible" to isEligible,
                "reasons" to reasons,
                "healthScore" to healthValue
            ))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNodeMetadata(productId: String): Result<NodeMetadata> {
        return try {
            val product = productDao.findById(productId)
            if (product == null) {
                return Result.Error(IllegalStateException("Product not found: $productId"))
            }
            
            val metadata = NodeMetadata(
                productId = productId,
                name = product.name ?: "Unknown",
                breed = product.breed,
                stage = product.stage?.name,
                ageWeeks = product.ageWeeks,
                healthScore = getProductHealthScore(productId).getOrNull() ?: 100,
                lifecycleStatus = product.status
            )
            
            Result.Success(metadata)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNodeMetadataBatch(productIds: List<String>): Result<Map<String, NodeMetadata>> {
        return try {
            val result = mutableMapOf<String, NodeMetadata>()
            
            productIds.forEach { productId ->
                val metadata = getNodeMetadata(productId)
                when (metadata) {
                    is Result.Success -> result[productId] = metadata.data
                    else -> {
                        // Add placeholder for failed lookups
                        result[productId] = NodeMetadata(
                            productId = productId,
                            name = "Unknown",
                            breed = null,
                            stage = null,
                            ageWeeks = null,
                            healthScore = 0,
                            lifecycleStatus = null
                        )
                    }
                }
            }
            
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String? =
        when {
            !maleId.isNullOrBlank() && !femaleId.isNullOrBlank() -> "FT_${maleId}_${femaleId}"
            !pairId.isNullOrBlank() -> "FT_PAIR_${pairId}"
            else -> null
        }

    override suspend fun getEligibleProductsCount(farmerId: String): Result<Int> {
        return try {
            val products = productDao.getActiveBySellerList(farmerId)
            val eligible = products.count { 
                it.status == "ACTIVE" && 
                it.quantity > 0 && 
                (it.ageWeeks ?: 0) >= 8 
            }
            Result.Success(eligible)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getComplianceAlerts(farmerId: String): Result<List<Pair<String, List<String>>>> {
        return try {
            val alerts = mutableListOf<Pair<String, List<String>>>()
            val products = productDao.getActiveBySellerList(farmerId)
            
            // Check for missing traceability data
            val missingTraceability = products.filter { 
                it.familyTreeId.isNullOrBlank() && it.parentMaleId.isNullOrBlank() && it.parentFemaleId.isNullOrBlank() 
            }.map { it.productId }
            
            if (missingTraceability.isNotEmpty()) {
                alerts.add("Missing traceability data" to missingTraceability)
            }
            
            // Check for missing health records
            val now = System.currentTimeMillis()
            val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
            val needsHealthCheck = products.filter { product ->
                val lastHealthEvent = lifecycleEventDao.getEventsForProduct(product.productId)
                    .filter { it.eventType == "HEALTH_CHECK" }
                    .maxByOrNull { it.timestamp }
                
                lastHealthEvent == null || lastHealthEvent.timestamp < thirtyDaysAgo
            }.map { it.productId }
            
            if (needsHealthCheck.isNotEmpty()) {
                alerts.add("Health records needed" to needsHealthCheck)
            }
            
            Result.Success(alerts)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeKycStatus(userId: String): Flow<Boolean> = flow {
        try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val status = snapshot.getString("verificationStatus") == "VERIFIED"
            emit(status)
        } catch (e: Exception) {
            emit(false)
        }
    }

    override fun observeComplianceAlertsCount(farmerId: String): Flow<Int> = flow {
        try {
            val alerts = getComplianceAlerts(farmerId)
            when (alerts) {
                is Result.Success -> emit(alerts.data.size)
                else -> emit(0)
            }
        } catch (e: Exception) {
            emit(0)
        }
    }

    override fun observeEligibleProductsCount(farmerId: String): Flow<Int> = flow {
        try {
            val count = getEligibleProductsCount(farmerId)
            when (count) {
                is Result.Success -> emit(count.data)
                else -> emit(0)
            }
        } catch (e: Exception) {
            emit(0)
        }
    }

    override suspend fun getFamilyTree(familyTreeId: String): Result<FamilyTree> {
        return try {
            // Parse family tree ID to extract parent IDs
            val parts = familyTreeId.removePrefix("FT_").split("_")
            val maleId = parts.getOrNull(0)
            val femaleId = parts.getOrNull(1)
            
            val male = maleId?.let { productDao.findById(it) }
            val female = femaleId?.let { productDao.findById(it) }
            
            val offspring = breedingRecordDao.getOffspringBatch(listOfNotNull(maleId, femaleId))
            
            val familyTree = FamilyTree(
                familyTreeId = familyTreeId,
                maleParentId = maleId,
                femaleParentId = femaleId,
                maleParent = male?.toDomainModel(),
                femaleParent = female?.toDomainModel(),
                offspringIds = offspring.map { it.childId },
                createdAt = System.currentTimeMillis()
            )
            
            Result.Success(familyTree)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAncestryGraph(productId: String, maxDepth: Int): Result<GraphData> {
        return try {
            val ancestorsResult = ancestors(productId, maxDepth)
            when (ancestorsResult) {
                is Result.Success -> {
                    val nodes = mutableListOf<String>()
                    val edges = mutableListOf<Pair<String, String>>()
                    
                    var currentId: String? = productId
                    var currentDepth = 0
                    
                    while (currentId != null && currentDepth < maxDepth) {
                        nodes.add(currentId)
                        
                        val product = productDao.findById(currentId)
                        product?.parentMaleId?.let { parentId ->
                            edges.add(parentId to currentId)
                            if (parentId !in nodes) nodes.add(parentId)
                        }
                        product?.parentFemaleId?.let { parentId ->
                            edges.add(parentId to currentId)
                            if (parentId !in nodes) nodes.add(parentId)
                        }
                        
                        currentId = product?.parentMaleId ?: product?.parentFemaleId
                        currentDepth++
                    }
                    
                    Result.Success(GraphData(nodes, edges))
                }
                is Result.Error -> Result.Success(GraphData(emptyList(), emptyList()))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getDescendancyGraph(productId: String, maxDepth: Int): Result<GraphData> {
        return try {
            val descendantsResult = descendants(productId, maxDepth)
            when (descendantsResult) {
                is Result.Success -> {
                    val nodes = mutableListOf(productId)
                    val edges = mutableListOf<Pair<String, String>>()
                    
                    descendantsResult.data.forEach { (depth, descendants) ->
                        descendants.forEach { descendantId ->
                            edges.add(productId to descendantId)
                            if (descendantId !in nodes) nodes.add(descendantId)
                        }
                    }
                    
                    Result.Success(GraphData(nodes, edges))
                }
                is Result.Error -> Result.Success(GraphData(emptyList(), emptyList()))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getSiblings(productId: String): Result<List<String>> {
        return try {
            val product = productDao.findById(productId)
            if (product == null || (product.parentMaleId == null && product.parentFemaleId == null)) {
                return Result.Success(emptyList())
            }
            
            val siblings = breedingRecordDao.getOffspringBatch(
                listOfNotNull(product.parentMaleId, product.parentFemaleId)
            ).filter { it.childId != productId }.map { it.childId }
            
            Result.Success(siblings)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Extension function to convert ProductEntity to domain model.
     */
    private fun com.rio.rostry.data.database.entity.ProductEntity.toDomainModel() = com.rio.rostry.core.model.Product(
        id = productId,
        name = name,
        sellerId = sellerId,
        category = category,
        price = price,
        quantity = quantity,
        unit = unit,
        currency = currency,
        description = description,
        imageUrls = imageUrls?.split(",") ?: emptyList(),
        breed = breed,
        gender = gender,
        birthDate = birthDate,
        ageWeeks = ageWeeks,
        colorTag = colorTag,
        birdCode = birdCode,
        stage = stage?.name ?: "ADULT",
        lifecycleStatus = lifecycleStatus ?: "ACTIVE",
        lastStageTransitionAt = lastStageTransitionAt,
        latitude = latitude,
        longitude = longitude,
        location = location,
        familyTreeId = familyTreeId,
        parentMaleId = parentMaleId,
        parentFemaleId = parentFemaleId,
        isTraceable = isTraceable,
        isVerified = isVerified,
        verificationLevel = verificationLevel,
        qrCodeUrl = qrCodeUrl,
        metadata = null,
        recordsLockedAt = recordsLockedAt,
        autoLockAfterDays = autoLockAfterDays,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}