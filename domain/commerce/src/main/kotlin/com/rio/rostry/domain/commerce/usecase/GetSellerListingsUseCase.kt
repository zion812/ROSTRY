package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.MarketListing
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting listings by seller.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetSellerListingsUseCase {
    /**
     * Get all listings for a specific seller.
     * @param sellerId The seller ID
     * @return Flow of seller's listings
     */
    operator fun invoke(sellerId: String): Flow<List<MarketListing>>
}
