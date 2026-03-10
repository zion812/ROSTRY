package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.ProductTracking
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for product tracking operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Farm Domain repository migration
 */
interface TrackingRepository {
    /**
     * Get all tracking entries for a product.
     * @param productId The product ID
     * @return Flow of tracking entries
     */
    fun getByProduct(productId: String): Flow<List<ProductTracking>>

    /**
     * Add or update a tracking entry.
     * @param entry The tracking entry to add or update
     * @return Result indicating success or error
     */
    suspend fun addOrUpdate(entry: ProductTracking): Result<Unit>

    /**
     * Mark a tracking entry as deleted.
     * @param trackingId The tracking ID to mark as deleted
     * @return Result indicating success or error
     */
    suspend fun markDeleted(trackingId: String): Result<Unit>
}
