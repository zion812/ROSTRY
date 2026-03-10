package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing growth records.
 * 
 * Tracks growth measurements and progress for farm assets.
 */
interface GrowthRepository {
    /**
     * Observes growth records for a product.
     * 
     * @param productId The product/asset ID
     * @return Flow emitting list of growth records
     */
    fun observe(productId: String): Flow<List<GrowthRecord>>
    
    /**
     * Inserts or updates a growth record.
     * 
     * @param record The growth record to save
     */
    suspend fun upsert(record: GrowthRecord)
}
