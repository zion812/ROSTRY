package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for admin-only product operations.
 * Separated from farmer-focused ProductRepository to maintain scope isolation.
 */
interface AdminProductRepository {
    /**
     * Flag a product for moderation with a reason.
     * Admin-only operation.
     */
    suspend fun flagProduct(productId: String, reason: String): Result<Unit>

    /**
     * Hide a product from public marketplace.
     * Sets status to "private" so it's only visible to owner.
     */
    suspend fun hideProduct(productId: String): Result<Unit>

    /**
     * Unhide a previously hidden product.
     */
    suspend fun unhideProduct(productId: String): Result<Unit>

    /**
     * Get all products for admin dashboard.
     * Includes hidden and flagged products.
     */
    fun getAllProductsAdmin(): Flow<Result<List<Product>>>

    /**
     * Get all flagged products for moderation queue.
     */
    fun getFlaggedProducts(): Flow<Result<List<Product>>>

    /**
     * Clear moderation flag from a product.
     */
    suspend fun clearFlag(productId: String): Result<Unit>

    /**
     * Alias for clearFlag - unflag a product.
     */
    suspend fun unflagProduct(productId: String): Result<Unit>

    /**
     * Delete a product permanently (soft delete - marks as deleted).
     */
    suspend fun deleteProduct(productId: String): Result<Unit>

    /**
     * Restore a hidden product back to active status.
     */
    suspend fun restoreProduct(productId: String): Result<Unit>
}

