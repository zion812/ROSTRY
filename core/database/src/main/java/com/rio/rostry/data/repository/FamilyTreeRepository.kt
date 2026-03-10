package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import com.rio.rostry.core.common.security.RbacProvider
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.core.common.security.AuditLogger
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface FamilyTreeRepository {
    fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>>
    /**
     * Upserts a family tree node. Cross-owner lineage links are permitted as long as the child product belongs to the current user.
     */
    suspend fun upsert(node: FamilyTreeEntity)
    suspend fun softDelete(treeId: String)
}

@Singleton
class FamilyTreeRepositoryImpl @Inject constructor(
    private val dao: FamilyTreeDao,
    private val rbacProvider: RbacProvider,
    private val auditLogDao: AuditLogDao,
    private val currentUserProvider: CurrentUserProvider,
    private val productDao: ProductDao,
    private val farmAssetDao: com.rio.rostry.data.database.dao.FarmAssetDao,
    private val auditLogger: AuditLogger,
    private val gson: Gson
) : FamilyTreeRepository {
    override fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>> = dao.getForProduct(productId)

    override suspend fun upsert(node: FamilyTreeEntity) {
        validateRelationships(node)
        
        if (!rbacProvider.canEditLineage()) {
            throw SecurityException("You don't have permission to edit lineage")
        }
        
        if (node.productId == null && node.assetId == null) {
            throw IllegalArgumentException("Either productId or assetId is required for lineage")
        }
        
        if (node.productId != null) {
            val product = productDao.findById(node.productId!!) ?: throw SecurityException("Product not found")
            if (product.sellerId != currentUserProvider.userIdOrNull()) {
                throw SecurityException("You can only edit lineage for your own products")
            }
        } else if (node.assetId != null) {
            val asset = farmAssetDao.findById(node.assetId!!) ?: throw SecurityException("Asset not found")
            if (asset.farmerId != currentUserProvider.userIdOrNull()) {
                throw SecurityException("You can only edit lineage for your own assets")
            }
        }
        
        node.parentProductId?.let { parentId ->
            val parentProduct = productDao.findById(parentId) ?: throw SecurityException("Invalid parent product ID")
            if (parentProduct.sellerId != currentUserProvider.userIdOrNull()) {
                auditLogger.audit("LINEAGE_CROSS_OWNER_LINK", mapOf(
                    "parentProductId" to parentId,
                    "parentSellerId" to parentProduct.sellerId,
                    "childProductId" to node.productId,
                    "childSellerId" to currentUserProvider.userIdOrNull()
                ))
                auditLogDao.insert(AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "LINEAGE_CROSS_OWNER_LINK",
                    refId = node.treeId,
                    action = "UPSERT",
                    actorUserId = currentUserProvider.userIdOrNull(),
                    detailsJson = gson.toJson(mapOf(
                        "parentProductId" to parentId,
                        "parentSellerId" to parentProduct.sellerId,
                        "childProductId" to node.productId,
                        "childSellerId" to currentUserProvider.userIdOrNull()
                    )),
                    createdAt = System.currentTimeMillis()
                ))
            }
        }
        
        dao.upsert(node.copy(updatedAt = System.currentTimeMillis()))
        
        val now = System.currentTimeMillis()
        auditLogDao.insert(AuditLogEntity(
            logId = UUID.randomUUID().toString(),
            type = "LINEAGE",
            refId = node.treeId,
            action = "UPSERT",
            actorUserId = currentUserProvider.userIdOrNull() ?: "",
            detailsJson = gson.toJson(node),
            createdAt = now
        ))
    }
    
    override suspend fun softDelete(treeId: String) {
        val node = dao.findById(treeId) ?: throw SecurityException("Node not found")
        
        if (node.productId == null && node.assetId == null) {
            throw IllegalArgumentException("Node has no associated product or asset")
        }
        
        if (node.productId != null) {
            val product = productDao.findById(node.productId!!) ?: throw SecurityException("Product not found")
            if (product.sellerId != currentUserProvider.userIdOrNull()) {
                throw SecurityException("You can only delete lineage for your own products")
            }
        } else if (node.assetId != null) {
            val asset = farmAssetDao.findById(node.assetId!!) ?: throw SecurityException("Asset not found")
            if (asset.farmerId != currentUserProvider.userIdOrNull()) {
                throw SecurityException("You can only delete lineage for your own assets")
            }
        }
        
        if (!rbacProvider.canEditLineage()) {
            throw SecurityException("You don't have permission to edit lineage")
        }
        
        val now = System.currentTimeMillis()
        dao.upsert(node.copy(isDeleted = true, deletedAt = now, updatedAt = now))
        
        auditLogDao.insert(AuditLogEntity(
            logId = UUID.randomUUID().toString(),
            type = "LINEAGE",
            refId = treeId,
            action = "DELETE",
            actorUserId = currentUserProvider.userIdOrNull() ?: "",
            detailsJson = gson.toJson(mapOf("treeId" to treeId, "deletedAt" to now)),
            createdAt = now
        ))
    }
    
    private suspend fun validateRelationships(node: FamilyTreeEntity) {
        if (node.productId == null && node.assetId == null) {
            throw IllegalArgumentException("Either productId or assetId is required")
        }
        
        if (node.productId != null) {
            if (productDao.findById(node.productId!!) == null) {
                throw SecurityException("Invalid product ID")
            }
        } else if (node.assetId != null) {
            if (farmAssetDao.findById(node.assetId!!) == null) {
                throw SecurityException("Invalid asset ID")
            }
        }
        // Validate parent/child product links if provided
        node.parentProductId?.let { parentId ->
            if (productDao.findById(parentId) == null) {
                throw SecurityException("Invalid parent product ID")
            }
            if (parentId == node.productId) {
                throw SecurityException("Circular reference detected: parent equals product")
            }
        }
        node.childProductId?.let { childId ->
            if (productDao.findById(childId) == null) {
                throw SecurityException("Invalid child product ID")
            }
            if (childId == node.productId) {
                throw SecurityException("Circular reference detected: child equals product")
            }
        }
    }
}
