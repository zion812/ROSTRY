package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.MarketListing
import kotlinx.coroutines.flow.Flow

/**
 * Use case for searching market listings.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface SearchListingsUseCase {
    /**
     * Search for market listings by query.
     * @param query The search query
     * @return Flow of matching listings
     */
    operator fun invoke(query: String): Flow<List<MarketListing>>
}
