package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.model.InventoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving inventory items.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetInventoryItemsUseCase {
    /**
     * Get all inventory items for a farmer.
     * @param farmerId The farmer ID
     * @return Flow of inventory items
     */
    operator fun invoke(farmerId: String): Flow<List<InventoryItem>>
}
