package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.model.InventoryItem
import com.rio.rostry.core.model.Result

/**
 * Use case for harvesting a farm asset and creating inventory.
 * 
 * Phase 2: Domain and Data Decoupling
 * Phase 3: ADR-004 Inside Modularization
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 * Requirement 5.4 - Asset transition creates inventory
 */
interface HarvestAssetUseCase {
    /**
     * Harvest a farm asset and create corresponding inventory item.
     * @param request The harvest request
     * @return Result containing the created inventory item or error
     */
    suspend operator fun invoke(request: HarvestAssetRequest): Result<InventoryItem>
}

/**
 * Request data for harvesting an asset.
 */
data class HarvestAssetRequest(
    val assetId: String,
    val quantity: Int,
    val unit: String,
    val qualityGrade: String,
    val storageLocation: String?
)
