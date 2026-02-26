package com.rio.rostry.domain.integrity

import android.util.Log
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.domain.validation.EntityValidator
import com.rio.rostry.domain.validation.InputValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RefIntegrityChecker"

/**
 * Checks and enforces referential integrity for entity relationships.
 * 
 * This checker provides:
 * - Foreign key constraint validation before batch operations
 * - Cascade rule implementation for entity deletion
 * - Dependent entity handling according to cascade rules
 * 
 * Requirements: 4.3, 4.6, 4.7
 */
@Singleton
class ReferentialIntegrityChecker @Inject constructor(
    private val database: AppDatabase,
    private val entityValidator: EntityValidator
) {

    /**
     * Validates foreign key constraints before batch operations.
     * 
     * @param entityType Type of entity being validated
     * @param entityIds List of entity IDs to validate
     * @return Validation result with valid and invalid indices
     */
    suspend fun validateForeignKeysForBatch(
        entityType: String,
        entityIds: List<String>
    ): BatchForeignKeyValidationResult {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, "Validating foreign keys for batch of ${entityIds.size} $entityType entities")
            
            val validIndices = mutableListOf<Int>()
            val invalidMap = mutableMapOf<Int, String>()
            
            entityIds.forEachIndexed { index, entityId ->
                val isValid = when (entityType) {
                    "user" -> validateUserForeignKeys(entityId)
                    "product" -> validateProductForeignKeys(entityId)
                    "order" -> validateOrderForeignKeys(entityId)
                    "transfer" -> validateTransferForeignKeys(entityId)
                    else -> {
                        Log.w(TAG, "Unknown entity type: $entityType")
                        false
                    }
                }
                
                if (isValid) {
                    validIndices.add(index)
                } else {
                    invalidMap[index] = "Foreign key validation failed for $entityType:$entityId"
                }
            }
            
            Log.i(TAG, "Batch validation complete. Valid: ${validIndices.size}, Invalid: ${invalidMap.size}")
            
            BatchForeignKeyValidationResult(
                valid = validIndices,
                invalid = invalidMap
            )
        }
    }

    /**
     * Validates foreign keys for a user entity.
     */
    private suspend fun validateUserForeignKeys(userId: String): Boolean {
        return try {
            val user = database.userDao().findById(userId)
            user != null
        } catch (e: Exception) {
            Log.e(TAG, "Error validating user foreign keys for $userId", e)
            false
        }
    }

    /**
     * Validates foreign keys for a product entity.
     */
    private suspend fun validateProductForeignKeys(productId: String): Boolean {
        return try {
            val product = database.productDao().findById(productId) ?: return false
            
            // Validate seller exists
            if (product.sellerId.isNotBlank()) {
                val seller = database.userDao().findById(product.sellerId)
                if (seller == null) {
                    Log.w(TAG, "Product $productId has invalid seller: ${product.sellerId}")
                    return false
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error validating product foreign keys for $productId", e)
            false
        }
    }

    /**
     * Validates foreign keys for an order entity.
     */
    private suspend fun validateOrderForeignKeys(orderId: String): Boolean {
        return try {
            val order = database.orderDao().findById(orderId) ?: return false
            
            // Validate buyer exists (buyerId is nullable)
            val buyerId = order.buyerId
            if (buyerId != null && buyerId.isNotBlank()) {
                val buyer = database.userDao().findById(buyerId)
                if (buyer == null) {
                    Log.w(TAG, "Order $orderId has invalid buyer: $buyerId")
                    return false
                }
            }
            
            // Validate seller exists
            if (order.sellerId.isNotBlank()) {
                val seller = database.userDao().findById(order.sellerId)
                if (seller == null) {
                    Log.w(TAG, "Order $orderId has invalid seller: ${order.sellerId}")
                    return false
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error validating order foreign keys for $orderId", e)
            false
        }
    }

    /**
     * Validates foreign keys for a transfer entity.
     */
    private suspend fun validateTransferForeignKeys(transferId: String): Boolean {
        return try {
            val transfer = database.transferDao().findById(transferId) ?: return false
            
            // Validate sender (fromUserId) exists
            val fromUser = transfer.fromUserId
            if (fromUser != null && fromUser.isNotBlank()) {
                val sender = database.userDao().findById(fromUser)
                if (sender == null) {
                    Log.w(TAG, "Transfer $transferId has invalid sender: $fromUser")
                    return false
                }
            }
            
            // Validate recipient (toUserId) exists
            val toUser = transfer.toUserId
            if (toUser != null && toUser.isNotBlank()) {
                val recipient = database.userDao().findById(toUser)
                if (recipient == null) {
                    Log.w(TAG, "Transfer $transferId has invalid recipient: $toUser")
                    return false
                }
            }
            
            // Validate product exists
            val prodId = transfer.productId
            if (prodId != null && prodId.isNotBlank()) {
                val product = database.productDao().findById(prodId)
                if (product == null) {
                    Log.w(TAG, "Transfer $transferId has invalid product: $prodId")
                    return false
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error validating transfer foreign keys for $transferId", e)
            false
        }
    }

    /**
     * Handles entity deletion according to cascade rules.
     * 
     * @param entityType Type of entity being deleted
     * @param entityId ID of entity being deleted
     * @param cascadeRule Cascade rule to apply
     * @return Result of the deletion operation
     */
    suspend fun handleEntityDeletion(
        entityType: String,
        entityId: String,
        cascadeRule: CascadeRule
    ): DeletionResult {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, "Handling deletion of $entityType:$entityId with rule $cascadeRule")
            
            try {
                when (entityType) {
                    "user" -> handleUserDeletion(entityId, cascadeRule)
                    "product" -> handleProductDeletion(entityId, cascadeRule)
                    "order" -> handleOrderDeletion(entityId, cascadeRule)
                    else -> {
                        Log.w(TAG, "Unknown entity type for deletion: $entityType")
                        DeletionResult.Error("Unknown entity type: $entityType")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling entity deletion", e)
                DeletionResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Handles user deletion with cascade rules.
     */
    private suspend fun handleUserDeletion(userId: String, cascadeRule: CascadeRule): DeletionResult {
        return try {
            // Find dependent products (synchronously)
            val products = database.productDao().getProductsBySellerSuspend(userId)
            val dependentCount = products.size
            
            when (cascadeRule) {
                CascadeRule.CASCADE -> {
                    // Delete all dependent products
                    products.forEach { database.productDao().deleteProduct(it.productId) }
                    
                    // Delete the user
                    val user = database.userDao().findById(userId)
                    if (user != null) {
                        database.userDao().deleteUserById(userId)
                    }
                    
                    Log.i(TAG, "Cascaded deletion of user $userId and $dependentCount dependent entities")
                    DeletionResult.Success(deletedCount = dependentCount + 1)
                }
                
                CascadeRule.SET_NULL -> {
                    Log.w(TAG, "SET_NULL not applicable for user deletion")
                    DeletionResult.Error("SET_NULL not applicable for user deletion")
                }
                
                CascadeRule.RESTRICT -> {
                    if (dependentCount > 0) {
                        Log.w(TAG, "Cannot delete user $userId: has $dependentCount dependent entities")
                        DeletionResult.Restricted(
                            message = "Cannot delete user: has $dependentCount dependent entities",
                            dependentCount = dependentCount
                        )
                    } else {
                        val user = database.userDao().findById(userId)
                        if (user != null) {
                            database.userDao().deleteUserById(userId)
                        }
                        DeletionResult.Success(deletedCount = 1)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling user deletion", e)
            DeletionResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Handles product deletion with cascade rules.
     */
    private suspend fun handleProductDeletion(productId: String, cascadeRule: CascadeRule): DeletionResult {
        return try {
            // Find dependent transfers
            val transfers = database.transferDao().getTransfersByProduct(productId)
            val dependentCount = transfers.size
            
            when (cascadeRule) {
                CascadeRule.CASCADE -> {
                    // Delete all dependent transfers
                    transfers.forEach { database.transferDao().deleteTransferById(it.transferId) }
                    
                    // Delete the product
                    database.productDao().deleteProduct(productId)
                    
                    Log.i(TAG, "Cascaded deletion of product $productId and $dependentCount dependent entities")
                    DeletionResult.Success(deletedCount = dependentCount + 1)
                }
                
                CascadeRule.SET_NULL -> {
                    // Set productId to empty in dependent transfers
                    transfers.forEach { transfer ->
                        val updated = transfer.copy(
                            productId = "",
                            updatedAt = System.currentTimeMillis()
                        )
                        database.transferDao().upsert(updated)
                    }
                    
                    // Delete the product
                    database.productDao().deleteProduct(productId)
                    
                    Log.i(TAG, "Deleted product $productId and set $dependentCount foreign keys to null")
                    DeletionResult.Success(deletedCount = 1)
                }
                
                CascadeRule.RESTRICT -> {
                    if (dependentCount > 0) {
                        Log.w(TAG, "Cannot delete product $productId: has $dependentCount dependent entities")
                        DeletionResult.Restricted(
                            message = "Cannot delete product: has $dependentCount dependent entities",
                            dependentCount = dependentCount
                        )
                    } else {
                        database.productDao().deleteProduct(productId)
                        DeletionResult.Success(deletedCount = 1)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling product deletion", e)
            DeletionResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Handles order deletion with cascade rules.
     */
    private suspend fun handleOrderDeletion(orderId: String, cascadeRule: CascadeRule): DeletionResult {
        return try {
            when (cascadeRule) {
                CascadeRule.CASCADE, CascadeRule.SET_NULL -> {
                    database.orderDao().deleteOrderById(orderId)
                    
                    Log.i(TAG, "Deleted order $orderId")
                    DeletionResult.Success(deletedCount = 1)
                }
                
                CascadeRule.RESTRICT -> {
                    val order = database.orderDao().findById(orderId)
                    if (order != null && order.status in listOf("PENDING", "PROCESSING", "SHIPPED")) {
                        Log.w(TAG, "Cannot delete order $orderId: status is ${order.status}")
                        DeletionResult.Restricted(
                            message = "Cannot delete order in ${order.status} status",
                            dependentCount = 0
                        )
                    } else {
                        database.orderDao().deleteOrderById(orderId)
                        DeletionResult.Success(deletedCount = 1)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling order deletion", e)
            DeletionResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Checks referential integrity for all entity relationships.
     * 
     * @return List of referential integrity violations
     */
    suspend fun checkAllReferentialIntegrity(): List<ReferentialIntegrityViolation> {
        return withContext(Dispatchers.IO) {
            val violations = mutableListOf<ReferentialIntegrityViolation>()
            
            try {
                Log.i(TAG, "Checking referential integrity for all entities")
                
                // Check products
                violations.addAll(checkProductReferentialIntegrity())
                
                // Check orders
                violations.addAll(checkOrderReferentialIntegrity())
                
                // Check transfers
                violations.addAll(checkTransferReferentialIntegrity())
                
                Log.i(TAG, "Referential integrity check complete. Found ${violations.size} violations")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking referential integrity", e)
            }
            
            violations
        }
    }

    /**
     * Checks referential integrity for products.
     */
    private suspend fun checkProductReferentialIntegrity(): List<ReferentialIntegrityViolation> {
        val violations = mutableListOf<ReferentialIntegrityViolation>()
        
        try {
            val products = database.productDao().getAllProductsSnapshot()
            for (product in products) {
                if (product.sellerId.isNotBlank()) {
                    val seller = database.userDao().findById(product.sellerId)
                    if (seller == null) {
                        violations.add(
                            ReferentialIntegrityViolation(
                                entityType = "product",
                                entityId = product.productId,
                                foreignKeyField = "sellerId",
                                foreignKeyValue = product.sellerId,
                                referencedEntityType = "user"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking product referential integrity", e)
        }
        
        return violations
    }

    /**
     * Checks referential integrity for orders.
     */
    private suspend fun checkOrderReferentialIntegrity(): List<ReferentialIntegrityViolation> {
        val violations = mutableListOf<ReferentialIntegrityViolation>()
        
        try {
            val orders = database.orderDao().getAllOrdersSnapshot(limit = 1000)
            for (order in orders) {
                // Check buyer
                val buyerId = order.buyerId
                if (buyerId != null && buyerId.isNotBlank()) {
                    val buyer = database.userDao().findById(buyerId)
                    if (buyer == null) {
                        violations.add(
                            ReferentialIntegrityViolation(
                                entityType = "order",
                                entityId = order.orderId,
                                foreignKeyField = "buyerId",
                                foreignKeyValue = buyerId,
                                referencedEntityType = "user"
                            )
                        )
                    }
                }
                
                // Check seller
                if (order.sellerId.isNotBlank()) {
                    val seller = database.userDao().findById(order.sellerId)
                    if (seller == null) {
                        violations.add(
                            ReferentialIntegrityViolation(
                                entityType = "order",
                                entityId = order.orderId,
                                foreignKeyField = "sellerId",
                                foreignKeyValue = order.sellerId,
                                referencedEntityType = "user"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking order referential integrity", e)
        }
        
        return violations
    }

    /**
     * Checks referential integrity for transfers.
     */
    private suspend fun checkTransferReferentialIntegrity(): List<ReferentialIntegrityViolation> {
        val violations = mutableListOf<ReferentialIntegrityViolation>()
        
        try {
            // Use getDirty as a proxy to get available transfers (no getAllTransfers method)
            val transfers = database.transferDao().getDirty(limit = 1000)
            for (transfer in transfers) {
                // Check sender (fromUserId)
                val fromUser = transfer.fromUserId
                if (fromUser != null && fromUser.isNotBlank()) {
                    val sender = database.userDao().findById(fromUser)
                    if (sender == null) {
                        violations.add(
                            ReferentialIntegrityViolation(
                                entityType = "transfer",
                                entityId = transfer.transferId,
                                foreignKeyField = "fromUserId",
                                foreignKeyValue = fromUser,
                                referencedEntityType = "user"
                            )
                        )
                    }
                }
                
                // Check recipient (toUserId)
                val toUser = transfer.toUserId
                if (toUser != null && toUser.isNotBlank()) {
                    val recipient = database.userDao().getUserById(toUser)
                    if (recipient == null) {
                        violations.add(
                            ReferentialIntegrityViolation(
                                entityType = "transfer",
                                entityId = transfer.transferId,
                                foreignKeyField = "toUserId",
                                foreignKeyValue = toUser,
                                referencedEntityType = "user"
                            )
                        )
                    }
                }
                
                // Check product
                val prodId = transfer.productId
                if (prodId != null && prodId.isNotBlank()) {
                    val product = database.productDao().getProductById(prodId)
                    if (product == null) {
                        violations.add(
                            ReferentialIntegrityViolation(
                                entityType = "transfer",
                                entityId = transfer.transferId,
                                foreignKeyField = "productId",
                                foreignKeyValue = prodId,
                                referencedEntityType = "product"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking transfer referential integrity", e)
        }
        
        return violations
    }
}

/**
 * Result of batch foreign key validation.
 */
data class BatchForeignKeyValidationResult(
    val valid: List<Int>,
    val invalid: Map<Int, String>
)

/**
 * Cascade rules for entity deletion.
 */
enum class CascadeRule {
    /** Delete all dependent entities when parent is deleted. */
    CASCADE,
    /** Set foreign key to null in dependent entities when parent is deleted. */
    SET_NULL,
    /** Prevent deletion if there are dependent entities. */
    RESTRICT
}

/**
 * Result of entity deletion operation.
 */
sealed class DeletionResult {
    data class Success(val deletedCount: Int) : DeletionResult()
    data class Restricted(val message: String, val dependentCount: Int) : DeletionResult()
    data class Error(val message: String) : DeletionResult()
}

/**
 * Represents a referential integrity violation.
 */
data class ReferentialIntegrityViolation(
    val entityType: String,
    val entityId: String,
    val foreignKeyField: String,
    val foreignKeyValue: String,
    val referencedEntityType: String
)
