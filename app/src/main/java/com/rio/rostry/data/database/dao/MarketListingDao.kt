package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.MarketListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketListingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(listing: MarketListingEntity)

    @Update
    suspend fun updateListing(listing: MarketListingEntity)

    @Query("SELECT * FROM market_listings WHERE listingId = :id")
    fun getListingById(id: String): Flow<MarketListingEntity?>

    @Query("SELECT * FROM market_listings WHERE sellerId = :sellerId ORDER BY updatedAt DESC")
    fun getListingsBySeller(sellerId: String): Flow<List<MarketListingEntity>>

    @Query("SELECT * FROM market_listings WHERE status = 'PUBLISHED' AND isActive = 1 ORDER BY updatedAt DESC")
    fun getAllPublicListings(): Flow<List<MarketListingEntity>>

    @Query("SELECT * FROM market_listings WHERE status = 'PUBLISHED' AND isActive = 1 AND (title LIKE :query OR description LIKE :query)")
    fun searchListings(query: String): Flow<List<MarketListingEntity>>

    @Query("""
        SELECT * FROM market_listings 
        WHERE status = 'PUBLISHED' AND isActive = 1
        AND (:minLat IS NULL OR latitude >= :minLat) 
        AND (:maxLat IS NULL OR latitude <= :maxLat) 
        AND (:minLng IS NULL OR longitude >= :minLng) 
        AND (:maxLng IS NULL OR longitude <= :maxLng)
    """)
    suspend fun filterInBounds(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?): List<MarketListingEntity>

    // Sync Support
    @Query("SELECT * FROM market_listings WHERE updatedAt > :sinceTime LIMIT :limit")
    suspend fun getUpdatedSince(sinceTime: Long, limit: Int = 100): List<MarketListingEntity>
    
    @Query("DELETE FROM market_listings WHERE status = 'SOLD_OUT' AND updatedAt < :staleTime")
    suspend fun purgeOldSoldListings(staleTime: Long)
}
