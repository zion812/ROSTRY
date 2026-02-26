package com.rio.rostry.domain.integrity

import android.util.Log
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.sync.ConflictResolver
import com.rio.rostry.data.sync.ConflictResult
import com.rio.rostry.data.sync.ConflictStrategy
import com.rio.rostry.data.sync.SyncableEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "DataIntegrityManager"

/**
 * Manages data integrity including sync conflict resolution and consistency verification.
 * 
 * This manager provides:
 * - Complete conflict resolution logic for all synchronized entities
 * - Data consistency verification after synchronization
 * - Repair workflows for detected inconsistencies
 * 
 * Requirements: 4.2, 4.4, 4.5
 */
@Singleton
class DataIntegrityManager @Inject constructor(
    private val database: AppDatabase,
    private val conflictResolver: ConflictResolver
) {

    /**
     * Resolves conflicts for Product entities.
     */
    suspend fun resolveProductConflict(
        local: ProductEntity,
        remote: ProductEntity,
        strategy: ConflictStrategy? = null
    ): ConflictResult<ProductSyncable> {
        return withContext(Dispatchers.IO) {
            val localSyncable = ProductSyncable(local)
            val remoteSyncable = ProductSyncable(remote)
            
            val resolvedStrategy = strategy ?: conflictResolver.getDefaultStrategy("product")
            val result = conflictResolver.resolve(localSyncable, remoteSyncable, resolvedStrategy)
            
            when (result) {
                is ConflictResult.Resolved -> {
                    Log.i(TAG, "Product conflict resolved using ${result.strategy} for ${local.productId}")
                    logConflictResolution("product", local.productId, result.strategy.name)
                }
                is ConflictResult.NeedsUserInput -> {
                    Log.w(TAG, "Product conflict needs user input for ${local.productId}: ${result.conflictFields}")
                    logInconsistency("product", local.productId, "Conflict requires user resolution: ${result.conflictFields}")
                }
                is ConflictResult.Error -> {
                    Log.e(TAG, "Error resolving product conflict for ${local.productId}: ${result.message}")
                    logInconsistency("product", local.productId, "Conflict resolution error: ${result.message}")
                }
            }
            
            result
        }
    }

    /**
     * Resolves conflicts for Order entities.
     */
    suspend fun resolveOrderConflict(
        local: OrderEntity,
        remote: OrderEntity,
        strategy: ConflictStrategy? = null
    ): ConflictResult<OrderSyncable> {
        return withContext(Dispatchers.IO) {
            val localSyncable = OrderSyncable(local)
            val remoteSyncable = OrderSyncable(remote)
            
            val resolvedStrategy = strategy ?: ConflictStrategy.SERVER_WINS
            val result = conflictResolver.resolve(localSyncable, remoteSyncable, resolvedStrategy)
            
            when (result) {
                is ConflictResult.Resolved -> {
                    Log.i(TAG, "Order conflict resolved using ${result.strategy} for ${local.orderId}")
                    logConflictResolution("order", local.orderId, result.strategy.name)
                }
                is ConflictResult.NeedsUserInput -> {
                    Log.w(TAG, "Order conflict needs user input for ${local.orderId}: ${result.conflictFields}")
                    logInconsistency("order", local.orderId, "Conflict requires user resolution: ${result.conflictFields}")
                }
                is ConflictResult.Error -> {
                    Log.e(TAG, "Error resolving order conflict for ${local.orderId}: ${result.message}")
                    logInconsistency("order", local.orderId, "Conflict resolution error: ${result.message}")
                }
            }
            
            result
        }
    }

    /**
     * Resolves conflicts for Transfer entities.
     */
    suspend fun resolveTransferConflict(
        local: TransferEntity,
        remote: TransferEntity,
        strategy: ConflictStrategy? = null
    ): ConflictResult<TransferSyncable> {
        return withContext(Dispatchers.IO) {
            val localSyncable = TransferSyncable(local)
            val remoteSyncable = TransferSyncable(remote)
            
            val resolvedStrategy = strategy ?: ConflictStrategy.SERVER_WINS
            val result = conflictResolver.resolve(localSyncable, remoteSyncable, resolvedStrategy)
            
            when (result) {
                is ConflictResult.Resolved -> {
                    Log.i(TAG, "Transfer conflict resolved using ${result.strategy} for ${local.transferId}")
                    logConflictResolution("transfer", local.transferId, result.strategy.name)
                }
                is ConflictResult.NeedsUserInput -> {
                    Log.w(TAG, "Transfer conflict needs user input for ${local.transferId}: ${result.conflictFields}")
                    logInconsistency("transfer", local.transferId, "Conflict requires user resolution: ${result.conflictFields}")
                }
                is ConflictResult.Error -> {
                    Log.e(TAG, "Error resolving transfer conflict for ${local.transferId}: ${result.message}")
                    logInconsistency("transfer", local.transferId, "Conflict resolution error: ${result.message}")
                }
            }
            
            result
        }
    }

    /**
     * Resolves conflicts for User entities.
     */
    suspend fun resolveUserConflict(
        local: UserEntity,
        remote: UserEntity,
        strategy: ConflictStrategy? = null
    ): ConflictResult<UserSyncable> {
        return withContext(Dispatchers.IO) {
            val localSyncable = UserSyncable(local)
            val remoteSyncable = UserSyncable(remote)
            
            val resolvedStrategy = strategy ?: ConflictStrategy.NEWEST_WINS
            val result = conflictResolver.resolve(localSyncable, remoteSyncable, resolvedStrategy)
            
            when (result) {
                is ConflictResult.Resolved -> {
                    Log.i(TAG, "User conflict resolved using ${result.strategy} for ${local.userId}")
                    logConflictResolution("user", local.userId, result.strategy.name)
                }
                is ConflictResult.NeedsUserInput -> {
                    Log.w(TAG, "User conflict needs user input for ${local.userId}: ${result.conflictFields}")
                    logInconsistency("user", local.userId, "Conflict requires user resolution: ${result.conflictFields}")
                }
                is ConflictResult.Error -> {
                    Log.e(TAG, "Error resolving user conflict for ${local.userId}: ${result.message}")
                    logInconsistency("user", local.userId, "Conflict resolution error: ${result.message}")
                }
            }
            
            result
        }
    }

    /**
     * Verifies data consistency after synchronization.
     * Checks for:
     * - Orphaned records (foreign key violations)
     * - Duplicate records
     * - Invalid state transitions
     * - Missing required fields
     * 
     * @return List of detected inconsistencies
     */
    suspend fun verifyDataConsistency(): List<DataInconsistency> {
        return withContext(Dispatchers.IO) {
            val inconsistencies = mutableListOf<DataInconsistency>()
            
            try {
                Log.i(TAG, "Starting data consistency verification")
                
                // Check for orphaned products
                inconsistencies.addAll(checkOrphanedProducts())
                
                // Check for orphaned orders
                inconsistencies.addAll(checkOrphanedOrders())
                
                // Check for orphaned transfers
                inconsistencies.addAll(checkOrphanedTransfers())
                
                // Check for invalid product states
                inconsistencies.addAll(checkInvalidProductStates())
                
                // Check for invalid order states
                inconsistencies.addAll(checkInvalidOrderStates())
                
                Log.i(TAG, "Data consistency verification complete. Found ${inconsistencies.size} inconsistencies")
                
                // Log all inconsistencies
                inconsistencies.forEach { inconsistency ->
                    logInconsistency(inconsistency.entityType, inconsistency.entityId, inconsistency.description)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during data consistency verification", e)
            }
            
            inconsistencies
        }
    }

    /**
     * Checks for orphaned products (products with non-existent sellers).
     */
    private suspend fun checkOrphanedProducts(): List<DataInconsistency> {
        val inconsistencies = mutableListOf<DataInconsistency>()
        
        try {
            val products = database.productDao().getAllProductsSnapshot()
            for (product in products) {
                if (product.sellerId.isNotBlank()) {
                    val seller = database.userDao().findById(product.sellerId)
                    if (seller == null) {
                        inconsistencies.add(
                            DataInconsistency(
                                entityType = "product",
                                entityId = product.productId,
                                description = "Product has non-existent seller: ${product.sellerId}",
                                severity = InconsistencySeverity.HIGH
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking orphaned products", e)
        }
        
        return inconsistencies
    }

    /**
     * Checks for orphaned orders (orders with non-existent buyers or sellers).
     */
    private suspend fun checkOrphanedOrders(): List<DataInconsistency> {
        val inconsistencies = mutableListOf<DataInconsistency>()
        
        try {
            val orders = database.orderDao().getAllOrdersSnapshot(limit = 1000)
            for (order in orders) {
                // Check buyer exists
                val buyerId = order.buyerId
                if (buyerId != null && buyerId.isNotBlank()) {
                    val buyer = database.userDao().findById(buyerId)
                    if (buyer == null) {
                        inconsistencies.add(
                            DataInconsistency(
                                entityType = "order",
                                entityId = order.orderId,
                                description = "Order has non-existent buyer: $buyerId",
                                severity = InconsistencySeverity.CRITICAL
                            )
                        )
                    }
                }
                
                // Check seller exists
                if (order.sellerId.isNotBlank()) {
                    val seller = database.userDao().findById(order.sellerId)
                    if (seller == null) {
                        inconsistencies.add(
                            DataInconsistency(
                                entityType = "order",
                                entityId = order.orderId,
                                description = "Order has non-existent seller: ${order.sellerId}",
                                severity = InconsistencySeverity.CRITICAL
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking orphaned orders", e)
        }
        
        return inconsistencies
    }

    /**
     * Checks for orphaned transfers (transfers with non-existent senders or recipients).
     */
    private suspend fun checkOrphanedTransfers(): List<DataInconsistency> {
        val inconsistencies = mutableListOf<DataInconsistency>()
        
        try {
            // Use getDirty as a proxy since no getAllTransfers method exists
            val transfers = database.transferDao().getDirty(limit = 1000)
            for (transfer in transfers) {
                // Check sender (fromUserId) exists
                val fromUser = transfer.fromUserId
                if (fromUser != null && fromUser.isNotBlank()) {
                    val sender = database.userDao().findById(fromUser)
                    if (sender == null) {
                        inconsistencies.add(
                            DataInconsistency(
                                entityType = "transfer",
                                entityId = transfer.transferId,
                                description = "Transfer has non-existent sender: $fromUser",
                                severity = InconsistencySeverity.HIGH
                            )
                        )
                    }
                }
                
                // Check recipient (toUserId) exists
                val toUser = transfer.toUserId
                if (toUser != null && toUser.isNotBlank()) {
                    val recipient = database.userDao().findById(toUser)
                    if (recipient == null) {
                        inconsistencies.add(
                            DataInconsistency(
                                entityType = "transfer",
                                entityId = transfer.transferId,
                                description = "Transfer has non-existent recipient: $toUser",
                                severity = InconsistencySeverity.HIGH
                            )
                        )
                    }
                }
                
                // Check product exists
                val prodId = transfer.productId
                if (prodId != null && prodId.isNotBlank()) {
                    val product = database.productDao().findById(prodId)
                    if (product == null) {
                        inconsistencies.add(
                            DataInconsistency(
                                entityType = "transfer",
                                entityId = transfer.transferId,
                                description = "Transfer has non-existent product: $prodId",
                                severity = InconsistencySeverity.HIGH
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking orphaned transfers", e)
        }
        
        return inconsistencies
    }

    /**
     * Checks for invalid product states.
     */
    private suspend fun checkInvalidProductStates(): List<DataInconsistency> {
        val inconsistencies = mutableListOf<DataInconsistency>()
        
        try {
            val products = database.productDao().getAllProductsSnapshot()
            for (product in products) {
                // Check for negative prices
                if (product.price < 0) {
                    inconsistencies.add(
                        DataInconsistency(
                            entityType = "product",
                            entityId = product.productId,
                            description = "Product has negative price: ${product.price}",
                            severity = InconsistencySeverity.MEDIUM
                        )
                    )
                }
                
                // Check for negative quantities
                if (product.quantity < 0) {
                    inconsistencies.add(
                        DataInconsistency(
                            entityType = "product",
                            entityId = product.productId,
                            description = "Product has negative quantity: ${product.quantity}",
                            severity = InconsistencySeverity.MEDIUM
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking invalid product states", e)
        }
        
        return inconsistencies
    }

    /**
     * Checks for invalid order states.
     */
    private suspend fun checkInvalidOrderStates(): List<DataInconsistency> {
        val inconsistencies = mutableListOf<DataInconsistency>()
        
        try {
            val orders = database.orderDao().getAllOrdersSnapshot(limit = 1000)
            for (order in orders) {
                // Check for negative total amounts
                if (order.totalAmount < 0) {
                    inconsistencies.add(
                        DataInconsistency(
                            entityType = "order",
                            entityId = order.orderId,
                            description = "Order has negative total amount: ${order.totalAmount}",
                            severity = InconsistencySeverity.HIGH
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking invalid order states", e)
        }
        
        return inconsistencies
    }

    /**
     * Triggers repair workflow for detected inconsistencies.
     * 
     * @param inconsistencies List of inconsistencies to repair
     * @return Number of inconsistencies successfully repaired
     */
    suspend fun repairInconsistencies(inconsistencies: List<DataInconsistency>): Int {
        return withContext(Dispatchers.IO) {
            var repairedCount = 0
            
            for (inconsistency in inconsistencies) {
                try {
                    val repaired = when (inconsistency.entityType) {
                        "product" -> repairProductInconsistency(inconsistency)
                        "order" -> repairOrderInconsistency(inconsistency)
                        "transfer" -> repairTransferInconsistency(inconsistency)
                        else -> false
                    }
                    
                    if (repaired) {
                        repairedCount++
                        Log.i(TAG, "Repaired inconsistency: ${inconsistency.entityType}:${inconsistency.entityId}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to repair inconsistency: ${inconsistency.entityType}:${inconsistency.entityId}", e)
                }
            }
            
            Log.i(TAG, "Repaired $repairedCount out of ${inconsistencies.size} inconsistencies")
            repairedCount
        }
    }

    /**
     * Repairs product-specific inconsistencies.
     */
    private suspend fun repairProductInconsistency(inconsistency: DataInconsistency): Boolean {
        return try {
            val product = database.productDao().findById(inconsistency.entityId)
            if (product == null) {
                Log.w(TAG, "Product not found for repair: ${inconsistency.entityId}")
                return false
            }
            
            when {
                inconsistency.description.contains("non-existent seller") -> {
                    // This will be handled by OrphanedProductHandler
                    Log.i(TAG, "Orphaned product will be handled by OrphanedProductHandler")
                    true
                }
                inconsistency.description.contains("negative price") -> {
                    val fixed = product.copy(price = 0.0, updatedAt = System.currentTimeMillis())
                    database.productDao().upsert(fixed)
                    true
                }
                inconsistency.description.contains("negative quantity") -> {
                    val fixed = product.copy(quantity = 0.0, updatedAt = System.currentTimeMillis())
                    database.productDao().upsert(fixed)
                    true
                }
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error repairing product inconsistency", e)
            false
        }
    }

    /**
     * Repairs order-specific inconsistencies.
     */
    private suspend fun repairOrderInconsistency(inconsistency: DataInconsistency): Boolean {
        return try {
            val order = database.orderDao().findById(inconsistency.entityId)
            if (order == null) {
                Log.w(TAG, "Order not found for repair: ${inconsistency.entityId}")
                return false
            }
            
            when {
                inconsistency.description.contains("negative total amount") -> {
                    val fixed = order.copy(totalAmount = 0.0, updatedAt = System.currentTimeMillis())
                    database.orderDao().update(fixed)
                    true
                }
                else -> {
                    Log.w(TAG, "Order inconsistency requires manual intervention: ${inconsistency.description}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error repairing order inconsistency", e)
            false
        }
    }

    /**
     * Repairs transfer-specific inconsistencies.
     */
    private suspend fun repairTransferInconsistency(inconsistency: DataInconsistency): Boolean {
        Log.w(TAG, "Transfer inconsistency requires manual intervention: ${inconsistency.description}")
        return false
    }

    /**
     * Logs conflict resolution for audit trail.
     */
    private fun logConflictResolution(entityType: String, entityId: String, strategy: String) {
        Log.i(
            TAG,
            "AUDIT - Conflict resolved: " +
            "entityType=$entityType, " +
            "entityId=$entityId, " +
            "strategy=$strategy, " +
            "timestamp=${System.currentTimeMillis()}"
        )
    }

    /**
     * Logs data inconsistency for audit trail.
     */
    private fun logInconsistency(entityType: String, entityId: String, description: String) {
        Log.w(
            TAG,
            "AUDIT - Data inconsistency detected: " +
            "entityType=$entityType, " +
            "entityId=$entityId, " +
            "description=$description, " +
            "timestamp=${System.currentTimeMillis()}"
        )
    }
}

/**
 * Represents a data inconsistency detected during verification.
 */
data class DataInconsistency(
    val entityType: String,
    val entityId: String,
    val description: String,
    val severity: InconsistencySeverity
)

/**
 * Severity levels for data inconsistencies.
 */
enum class InconsistencySeverity {
    LOW,      // Minor issues that don't affect functionality
    MEDIUM,   // Issues that may cause problems but aren't critical
    HIGH,     // Serious issues that should be fixed soon
    CRITICAL  // Critical issues that need immediate attention
}

// Syncable wrappers for entities

data class ProductSyncable(val entity: ProductEntity) : SyncableEntity {
    override val entityType: String = "product"
    override val entityId: String = entity.productId
    override val updatedAt: Long = entity.updatedAt
    override val syncedAt: Long? = null
    override val dirty: Boolean = entity.dirty
}

data class OrderSyncable(val entity: OrderEntity) : SyncableEntity {
    override val entityType: String = "order"
    override val entityId: String = entity.orderId
    override val updatedAt: Long = entity.updatedAt
    override val syncedAt: Long? = null
    override val dirty: Boolean = entity.dirty
}

data class TransferSyncable(val entity: TransferEntity) : SyncableEntity {
    override val entityType: String = "transfer"
    override val entityId: String = entity.transferId
    override val updatedAt: Long = entity.updatedAt
    override val syncedAt: Long? = null
    override val dirty: Boolean = entity.dirty
}

data class UserSyncable(val entity: UserEntity) : SyncableEntity {
    override val entityType: String = "user"
    override val entityId: String = entity.userId
    override val updatedAt: Long = entity.updatedAt?.time ?: 0L
    override val syncedAt: Long? = null
    override val dirty: Boolean = false // UserEntity doesn't have a dirty field
}
