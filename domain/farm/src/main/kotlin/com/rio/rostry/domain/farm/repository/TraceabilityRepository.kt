package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.BreedingRecord
import com.rio.rostry.core.model.FamilyTree
import com.rio.rostry.core.model.GraphData
import com.rio.rostry.core.model.LifecycleEvent
import com.rio.rostry.core.model.NodeMetadata
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing product traceability and lineage.
 * 
 * Handles breeding records, ancestry/descendancy tracking, health scoring,
 * transfer eligibility, and compliance monitoring.
 */
interface TraceabilityRepository {
    /**
     * Adds a breeding record with cycle prevention.
     */
    suspend fun addBreedingRecord(record: BreedingRecord): Result<Unit>
    
    /**
     * Gets ancestors of a product up to maxDepth levels.
     * Returns a map of depth -> list of product IDs.
     */
    suspend fun ancestors(productId: String, maxDepth: Int = 5): Result<Map<Int, List<String>>>
    
    /**
     * Gets descendants of a product up to maxDepth levels.
     * Returns a map of depth -> list of product IDs.
     */
    suspend fun descendants(productId: String, maxDepth: Int = 5): Result<Map<Int, List<String>>>
    
    /**
     * Gets breeding success rate for a parent pair.
     * Returns (successful breedings, total breedings).
     */
    suspend fun breedingSuccess(parentId: String, partnerId: String): Result<Pair<Int, Int>>
    
    /**
     * Adds a lifecycle event for a product.
     */
    suspend fun addLifecycleEvent(event: LifecycleEvent): Result<Unit>
    
    /**
     * Verifies if there's a path from productId to ancestorId.
     */
    suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int = 10): Result<Boolean>
    
    /**
     * Verifies parentage of a child product.
     */
    suspend fun verifyParentage(childId: String, parentId: String, partnerId: String): Result<Boolean>
    
    /**
     * Gets the complete transfer chain for a product.
     */
    suspend fun getTransferChain(productId: String): Result<List<Any>>
    
    /**
     * Validates product lineage against expected parents.
     */
    suspend fun validateProductLineage(
        productId: String,
        expectedParentMaleId: String?,
        expectedParentFemaleId: String?
    ): Result<Boolean>
    
    /**
     * Computes a health score for a product based on recent records.
     * Score ranges from 0-100.
     */
    suspend fun getProductHealthScore(productId: String): Result<Int>
    
    /**
     * Gets a transfer eligibility report for a product.
     * Returns a map with eligibility status, reasons, and metrics.
     */
    suspend fun getTransferEligibilityReport(productId: String): Result<Map<String, Any>>
    
    /**
     * Gets metadata for a single node (product).
     */
    suspend fun getNodeMetadata(productId: String): Result<NodeMetadata>
    
    /**
     * Gets metadata for multiple nodes in parallel.
     */
    suspend fun getNodeMetadataBatch(productIds: List<String>): Result<Map<String, NodeMetadata>>
    
    /**
     * Creates a family tree ID from parent IDs.
     */
    fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String?
    
    /**
     * Gets count of eligible products for a farmer.
     */
    suspend fun getEligibleProductsCount(farmerId: String): Result<Int>
    
    /**
     * Gets compliance alerts for a farmer's products.
     * Returns list of (productId, reasons).
     */
    suspend fun getComplianceAlerts(farmerId: String): Result<List<Pair<String, List<String>>>>
    
    /**
     * Observes KYC status for a user.
     */
    fun observeKycStatus(userId: String): Flow<Boolean>
    
    /**
     * Observes count of compliance alerts for a farmer.
     */
    fun observeComplianceAlertsCount(farmerId: String): Flow<Int>
    
    /**
     * Observes count of eligible products for a farmer.
     */
    fun observeEligibleProductsCount(farmerId: String): Flow<Int>
    
    /**
     * Gets a family tree by ID.
     */
    suspend fun getFamilyTree(familyTreeId: String): Result<FamilyTree>
    
    /**
     * Gets ancestry graph data for visualization.
     */
    suspend fun getAncestryGraph(productId: String, maxDepth: Int = 3): Result<GraphData>
    
    /**
     * Gets descendancy graph data for visualization.
     */
    suspend fun getDescendancyGraph(productId: String, maxDepth: Int = 3): Result<GraphData>
    
    /**
     * Gets siblings of a product.
     */
    suspend fun getSiblings(productId: String): Result<List<String>>
}

