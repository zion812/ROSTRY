package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MarketListingRepository {
    
    fun getListingsBySeller(sellerId: String): Flow<Resource<List<MarketListingEntity>>>
    
    fun getPublicListings(): Flow<Resource<List<MarketListingEntity>>>
    
    fun getListingById(listingId: String): Flow<Resource<MarketListingEntity?>>
    
    fun searchListings(query: String): Flow<Resource<List<MarketListingEntity>>>
    
    suspend fun publishListing(listing: MarketListingEntity): Resource<String>
    
    suspend fun updateListing(listing: MarketListingEntity): Resource<Unit>
    
    suspend fun deleteListing(listingId: String): Resource<Unit>
    
    suspend fun changeListingStatus(listingId: String, status: String, isActive: Boolean): Resource<Unit>
    
    suspend fun filterInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): Resource<List<MarketListingEntity>>
    
    suspend fun syncListings(): Resource<Unit>
    
    /**
     * Creates a market listing from an existing farm asset.
     * This is the core of the farm-to-market bridge feature.
     * 
     * Flow:
     * 1. Loads the source FarmAssetEntity
     * 2. Creates an InventoryItemEntity (for stock tracking)
     * 3. Creates a MarketListingEntity with dirty=true
     * 4. Marks the source asset as LISTED
     * 
     * All writes go to Room first (offline-first). SyncWorker handles Firestore upload.
     * 
     * @param assetId The source farm asset ID
     * @param price Listing price
     * @param quantity Quantity to list (must be <= asset.quantity)
     * @param title Marketing title for the listing
     * @param description Description for the listing
     * @return Resource containing the new listing ID on success
     */
    suspend fun createListingFromAsset(
        assetId: String,
        price: Double,
        quantity: Double,
        title: String,
        description: String
    ): Resource<String>
}
