package com.rio.rostry.domain.integrity

import android.util.Log
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "OrphanedProductHandler"

/**
 * Handles detection and assignment of orphaned products.
 * 
 * Orphaned products are products that have a sellerId that doesn't exist
 * in the users table. Instead of leaving them in an invalid state or
 * assigning to placeholder users, they are assigned to a designated
 * system account.
 * 
 * Requirements: 4.1
 */
@Singleton
class OrphanedProductHandler @Inject constructor(
    private val database: AppDatabase
) {

    companion object {
        private const val SYSTEM_ACCOUNT_ID = "system_orphaned_products"
        private const val SYSTEM_ACCOUNT_NAME = "System Account"
        private const val SYSTEM_ACCOUNT_EMAIL = "system@rostry.internal"
    }

    /**
     * Ensures the system account exists in the database.
     * Creates it if it doesn't exist.
     * 
     * @return The system account user entity
     */
    private suspend fun ensureSystemAccountExists(): UserEntity {
        return withContext(Dispatchers.IO) {
            val existing = database.userDao().findById(SYSTEM_ACCOUNT_ID)
            
            if (existing != null) {
                existing
            } else {
                Log.i(TAG, "Creating system account for orphaned products")
                val now = Date()
                val systemAccount = UserEntity(
                    userId = SYSTEM_ACCOUNT_ID,
                    fullName = SYSTEM_ACCOUNT_NAME,
                    email = SYSTEM_ACCOUNT_EMAIL,
                    phoneNumber = null,
                    userType = "SYSTEM",
                    profilePictureUrl = null,
                    createdAt = now,
                    updatedAt = now
                )
                database.userDao().insertUserReplace(systemAccount)
                Log.i(TAG, "System account created successfully")
                systemAccount
            }
        }
    }

    /**
     * Detects orphaned products by checking for products whose sellerId
     * doesn't exist in the users table.
     * 
     * @return List of orphaned product entities
     */
    suspend fun detectOrphanedProducts(): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val allProducts = database.productDao().getAllProductsSnapshot()
                val orphanedProducts = mutableListOf<ProductEntity>()
                
                for (product in allProducts) {
                    if (product.sellerId.isNotBlank()) {
                        val seller = database.userDao().findById(product.sellerId)
                        if (seller == null) {
                            orphanedProducts.add(product)
                            Log.w(TAG, "Detected orphaned product: ${product.productId} with missing seller: ${product.sellerId}")
                        }
                    } else {
                        // Product has empty sellerId, also considered orphaned
                        orphanedProducts.add(product)
                        Log.w(TAG, "Detected orphaned product with empty sellerId: ${product.productId}")
                    }
                }
                
                Log.i(TAG, "Detected ${orphanedProducts.size} orphaned products")
                orphanedProducts
            } catch (e: Exception) {
                Log.e(TAG, "Error detecting orphaned products", e)
                emptyList()
            }
        }
    }

    /**
     * Assigns orphaned products to the system account.
     * 
     * @param products List of orphaned products to assign
     * @return Number of products successfully assigned
     */
    suspend fun assignOrphanedProductsToSystem(products: List<ProductEntity>): Int {
        return withContext(Dispatchers.IO) {
            try {
                // Ensure system account exists
                val systemAccount = ensureSystemAccountExists()
                
                var assignedCount = 0
                
                for (product in products) {
                    try {
                        val updatedProduct = product.copy(
                            sellerId = systemAccount.userId,
                            updatedAt = System.currentTimeMillis(),
                            lastModifiedAt = System.currentTimeMillis(),
                            dirty = true // Mark as dirty for sync
                        )
                        
                        database.productDao().upsert(updatedProduct)
                        assignedCount++
                        
                        Log.i(TAG, "Assigned orphaned product ${product.productId} to system account")
                        
                        // Log the assignment for audit trail
                        logOrphanedProductAssignment(
                            productId = product.productId,
                            originalSellerId = product.sellerId,
                            newSellerId = systemAccount.userId
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to assign product ${product.productId} to system account", e)
                    }
                }
                
                Log.i(TAG, "Successfully assigned $assignedCount out of ${products.size} orphaned products")
                assignedCount
            } catch (e: Exception) {
                Log.e(TAG, "Error assigning orphaned products to system", e)
                0
            }
        }
    }

    /**
     * Detects and assigns all orphaned products in one operation.
     * 
     * @return Number of products assigned to system account
     */
    suspend fun detectAndAssignOrphanedProducts(): Int {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, "Starting orphaned product detection and assignment")
            val orphanedProducts = detectOrphanedProducts()
            
            if (orphanedProducts.isEmpty()) {
                Log.i(TAG, "No orphaned products found")
                return@withContext 0
            }
            
            val assignedCount = assignOrphanedProductsToSystem(orphanedProducts)
            Log.i(TAG, "Orphaned product handling complete. Assigned $assignedCount products")
            assignedCount
        }
    }

    /**
     * Logs orphaned product assignment for audit trail.
     */
    private fun logOrphanedProductAssignment(
        productId: String,
        originalSellerId: String,
        newSellerId: String
    ) {
        Log.i(
            TAG,
            "AUDIT - Orphaned product assignment: " +
            "productId=$productId, " +
            "originalSellerId=$originalSellerId, " +
            "newSellerId=$newSellerId, " +
            "timestamp=${System.currentTimeMillis()}"
        )
    }

    /**
     * Checks if a specific product is orphaned.
     * 
     * @param productId The product ID to check
     * @return True if the product is orphaned, false otherwise
     */
    suspend fun isProductOrphaned(productId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val product = database.productDao().findById(productId)
                if (product == null) {
                    return@withContext false
                }
                
                if (product.sellerId.isBlank()) {
                    return@withContext true
                }
                
                val seller = database.userDao().findById(product.sellerId)
                seller == null
            } catch (e: Exception) {
                Log.e(TAG, "Error checking if product is orphaned", e)
                false
            }
        }
    }
}
