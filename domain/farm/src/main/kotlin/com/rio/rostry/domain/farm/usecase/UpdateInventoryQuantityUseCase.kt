package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for updating inventory quantity.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface UpdateInventoryQuantityUseCase {
    /**
     * Update the available quantity of an inventory item.
     * @param itemId The inventory item ID
     * @param quantity The new quantity
     * @return Result indicating success or error
     */
    suspend operator fun invoke(itemId: String, quantity: Int): Result<Unit>
}
