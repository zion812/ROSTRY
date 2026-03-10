package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.model.Result

/**
 * Use case for getting a listing by ID.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetListingByIdUseCase {
    /**
     * Get a market listing by its ID.
     * @param listingId The listing ID
     * @return Result containing the listing or error
     */
    suspend operator fun invoke(listingId: String): Result<MarketListing>
}
