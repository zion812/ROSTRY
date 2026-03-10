package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

interface MarketListingRepository {
    fun getListingsBySeller(sellerId: String): Flow<Result<List<MarketListing>>>
    fun getPublicListings(): Flow<Result<List<MarketListing>>>
    fun getListingById(listingId: String): Flow<Result<MarketListing?>>
    fun searchListings(query: String): Flow<Result<List<MarketListing>>>
    suspend fun publishListing(listing: MarketListing): Result<String>
    suspend fun updateListing(listing: MarketListing): Result<Unit>
    suspend fun deleteListing(listingId: String): Result<Unit>
    suspend fun changeListingStatus(listingId: String, status: String, isActive: Boolean): Result<Unit>
    suspend fun filterInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): Result<List<MarketListing>>
    suspend fun syncListings(): Result<Unit>

    @Deprecated("Use FarmAssetRepository.createSnapshotListing instead", ReplaceWith("farmAssetRepository.createSnapshotListing(assetId, price, quantity, title, description)"))
    suspend fun createListingFromAsset(
        assetId: String,
        price: Double,
        quantity: Double,
        title: String,
        description: String
    ): Result<String>
}
