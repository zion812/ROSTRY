package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.model.Result

/**
 * Use case for creating a market listing.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface CreateListingUseCase {
    /**
     * Create a new market listing.
     * @param request The listing creation request
     * @return Result containing the created listing or error
     */
    suspend operator fun invoke(request: CreateListingRequest): Result<MarketListing>
}

/**
 * Request data for creating a market listing.
 */
data class CreateListingRequest(
    val inventoryItemId: String,
    val price: Double,
    val minimumOrderQuantity: Int,
    val description: String,
    val images: List<String>
)
