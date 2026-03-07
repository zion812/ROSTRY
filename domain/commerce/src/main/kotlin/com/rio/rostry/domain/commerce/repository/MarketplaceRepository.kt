package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for marketplace operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface MarketplaceRepository {
    /**
     * Get all market listings.
     * @return Flow of market listings
     */
    fun getListings(): Flow<List<MarketListing>>

    /**
     * Get listing by ID.
     * @param listingId The listing ID
     * @return Result containing the listing or error
     */
    suspend fun getListingById(listingId: String): Result<MarketListing>

    /**
     * Create a new market listing.
     * @param listing The listing to create
     * @return Result containing the created listing or error
     */
    suspend fun createListing(listing: MarketListing): Result<MarketListing>

    /**
     * Update a market listing.
     * @param listing The updated listing
     * @return Result indicating success or error
     */
    suspend fun updateListing(listing: MarketListing): Result<Unit>

    /**
     * Delete a market listing.
     * @param listingId The listing ID to delete
     * @return Result indicating success or error
     */
    suspend fun deleteListing(listingId: String): Result<Unit>

    /**
     * Search listings by query.
     * @param query The search query
     * @return Flow of matching listings
     */
    fun searchListings(query: String): Flow<List<MarketListing>>

    /**
     * Get listings by seller ID.
     * @param sellerId The seller ID
     * @return Flow of seller's listings
     */
    fun getListingsBySeller(sellerId: String): Flow<List<MarketListing>>
}
