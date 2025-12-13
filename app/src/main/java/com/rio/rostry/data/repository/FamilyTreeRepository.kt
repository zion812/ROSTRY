package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.security.SecurityManager
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
    suspend fun softDelete(nodeId: String)
}

@Singleton
class FamilyTreeRepositoryImpl @Inject constructor(
    private val dao: FamilyTreeDao,
    private val rbacGuard: RbacGuard,
    private val auditLogDao: AuditLogDao,
    private val currentUserProvider: CurrentUserProvider,
    private val productDao: ProductDao,
    private val gson: Gson
) : FamilyTreeRepository {
    override fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>> = dao.getForProduct(productId)

    override suspend fun upsert(node: FamilyTreeEntity) {
        validateRelationships(node)
        
        if (!rbacGuard.canEditLineage()) {
            throw SecurityException("You don't have permission to edit lineage")
        }
        
        val product = productDao.findById(node.productId) ?: throw SecurityException("Product not found")
        if (product.sellerId != currentUserProvider.userIdOrNull()) {
            throw SecurityException("You can only edit lineage for your own products")
        }
        
        node.parentProductId?.let { parentId ->
            val parentProduct = productDao.findById(parentId) ?: throw SecurityException("Invalid parent product ID")
            if (parentProduct.sellerId != currentUserProvider.userIdOrNull()) {
                SecurityManager.audit("LINEAGE_CROSS_OWNER_LINK", mapOf(
                    "parentProductId" to parentId,
                    "parentSellerId" to parentProduct.sellerId,
                    "childProductId" to node.productId,
                    "childSellerId" to currentUserProvider.userIdOrNull()
                ))
                auditLogDao.insert(AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "LINEAGE_CROSS_OWNER_LINK",
                    refId = node.nodeId,
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
            refId = node.nodeId,
            action = "UPSERT",
            actorUserId = currentUserProvider.userIdOrNull() ?: "",
            detailsJson = gson.toJson(node),
            createdAt = now
        ))
    }
    
    override suspend fun softDelete(nodeId: String) {
        val node = dao.findById(nodeId) ?: throw SecurityException("Node not found")
        
        val product = productDao.findById(node.productId) ?: throw SecurityException("Product not found")
        if (product.sellerId != currentUserProvider.userIdOrNull()) {
            throw SecurityException("You can only delete lineage for your own products")
        }
        
        if (!rbacGuard.canEditLineage()) {
            throw SecurityException("You don't have permission to edit lineage")
        }
        
        val now = System.currentTimeMillis()
        dao.upsert(node.copy(isDeleted = true, deletedAt = now, updatedAt = now))
        
        auditLogDao.insert(AuditLogEntity(
            logId = UUID.randomUUID().toString(),
            type = "LINEAGE",
            refId = nodeId,
            action = "DELETE",
            actorUserId = currentUserProvider.userIdOrNull() ?: "",
            detailsJson = gson.toJson(mapOf("nodeId" to nodeId, "deletedAt" to now)),
            createdAt = now
        ))
    }
    
    private suspend fun validateRelationships(node: FamilyTreeEntity) {
        // Check target product exists
        if (productDao.findById(node.productId) == null) {
            throw SecurityException("Invalid product ID")
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
