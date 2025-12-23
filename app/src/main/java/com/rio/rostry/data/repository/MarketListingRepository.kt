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
}
