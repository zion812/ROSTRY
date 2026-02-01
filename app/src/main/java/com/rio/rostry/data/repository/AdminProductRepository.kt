package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Admin-only product operations.
 * Separated from farmer-focused ProductRepository to maintain scope isolation.
 * 
 * Operations:
 * - flagProduct: Admin moderation flagging
 * - hideProduct: Set product to private status
 * - getAllProductsAdmin: Admin-only access to all products
 */
@Singleton
class AdminProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {

    private val productsCollection = firestore.collection("products")

    /**
     * Flag a product for moderation with a reason.
     * Admin-only operation.
     */
    suspend fun flagProduct(productId: String, reason: String): Resource<Unit> {
        return try {
            val updates = mapOf(
                "adminFlagged" to true,
                "moderationNote" to reason,
                "flaggedAt" to FieldValue.serverTimestamp()
            )
            productsCollection.document(productId).update(updates).await()
            
            // Update local cache via full product update
            val product = productDao.findById(productId)
            if (product != null) {
                productDao.updateProduct(product.copy(
                    adminFlagged = true,
                    moderationNote = reason,
                    updatedAt = System.currentTimeMillis()
                ))
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to flag product")
        }
    }

    /**
     * Hide a product from public marketplace.
     * Sets status to "private" so it's only visible to owner.
     */
    suspend fun hideProduct(productId: String): Resource<Unit> {
        return try {
            val updates = mapOf(
                "status" to "private",
                "hiddenByAdmin" to true,
                "hiddenAt" to FieldValue.serverTimestamp()
            )
            productsCollection.document(productId).update(updates).await()
            
            // Update local cache
            val product = productDao.findById(productId)
            if (product != null) {
                productDao.updateProduct(product.copy(
                    status = "private",
                    updatedAt = System.currentTimeMillis()
                ))
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to hide product")
        }
    }

    /**
     * Unhide a previously hidden product.
     */
    suspend fun unhideProduct(productId: String): Resource<Unit> {
        return try {
            val updates = mapOf(
                "status" to "active",
                "hiddenByAdmin" to false
            )
            productsCollection.document(productId).update(updates).await()

            val product = productDao.findById(productId)
            if (product != null) {
                productDao.updateProduct(product.copy(
                    status = "active",
                    updatedAt = System.currentTimeMillis()
                ))
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unhide product")
        }
    }

    /**
     * Get all products for admin dashboard.
     * Includes hidden and flagged products.
     * Fetches from Firestore to show all users' products, not just locally cached ones.
     */
    fun getAllProductsAdmin(): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            // Fetch all products from Firestore for admin visibility
            val snapshot = productsCollection
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(500) // Reasonable limit for dashboard
                .get()
                .await()
            
            val products = snapshot.toObjects(ProductEntity::class.java)
            
            // Optionally cache locally for faster subsequent access
            if (products.isNotEmpty()) {
                products.forEach { product ->
                    try {
                        productDao.upsert(product)
                    } catch (e: Exception) {
                        // Ignore caching errors
                    }
                }
            }
            
            emit(Resource.Success(products))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            // Fallback to local cache if Firestore fails
            try {
                val localProducts = productDao.getAllProductsSnapshot()
                emit(Resource.Success(localProducts))
            } catch (localError: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load products"))
            }
        }
    }

    /**
     * Get all flagged products for moderation queue.
     */
    fun getFlaggedProducts(): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val products = productDao.getAllProductsSnapshot().filter { it.adminFlagged == true }
            emit(Resource.Success(products))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            emit(Resource.Error(e.message ?: "Failed to load flagged products"))
        }
    }

    /**
     * Clear moderation flag from a product.
     */
    suspend fun clearFlag(productId: String): Resource<Unit> {
        return try {
            val updates = mapOf(
                "adminFlagged" to false,
                "moderationNote" to null
            )
            productsCollection.document(productId).update(updates).await()

            val product = productDao.findById(productId)
            if (product != null) {
                productDao.updateProduct(product.copy(
                    adminFlagged = false,
                    moderationNote = null,
                    updatedAt = System.currentTimeMillis()
                ))
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to clear flag")
        }
    }

    /**
     * Alias for clearFlag - unflag a product.
     */
    suspend fun unflagProduct(productId: String): Resource<Unit> = clearFlag(productId)

    /**
     * Delete a product permanently (soft delete - marks as deleted).
     */
    suspend fun deleteProduct(productId: String): Resource<Unit> {
        return try {
            val updates = mapOf(
                "status" to "deleted",
                "deletedByAdmin" to true,
                "deletedAt" to FieldValue.serverTimestamp()
            )
            productsCollection.document(productId).update(updates).await()

            // Remove from local cache
            productDao.deleteProductById(productId)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete product")
        }
    }

    /**
     * Restore a hidden product back to active status.
     */
    suspend fun restoreProduct(productId: String): Resource<Unit> {
        return try {
            val updates = mapOf(
                "status" to "active",
                "hiddenByAdmin" to false,
                "adminFlagged" to false
            )
            productsCollection.document(productId).update(updates).await()

            val product = productDao.findById(productId)
            if (product != null) {
                productDao.updateProduct(product.copy(
                    status = "active",
                    adminFlagged = false,
                    updatedAt = System.currentTimeMillis()
                ))
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to restore product")
        }
    }
}
