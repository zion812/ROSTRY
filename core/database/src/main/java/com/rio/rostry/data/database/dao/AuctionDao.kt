package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.AuctionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuctionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(auction: AuctionEntity)

    @Update
    suspend fun update(auction: AuctionEntity)

    @Query("SELECT * FROM auctions WHERE auctionId = :id")
    fun observeById(id: String): Flow<AuctionEntity?>

    @Query("SELECT * FROM auctions WHERE auctionId = :id LIMIT 1")
    suspend fun findById(id: String): AuctionEntity?

    @Query("SELECT * FROM auctions WHERE isActive = 1 AND startsAt <= :now AND endsAt >= :now")
    fun activeAuctions(now: Long = System.currentTimeMillis()): Flow<List<AuctionEntity>>

    @Query("SELECT * FROM auctions WHERE productId = :productId LIMIT 1")
    suspend fun findByProduct(productId: String): AuctionEntity?
    
    // ========================================
    // Seller Controls
    // ========================================
    
    @Query("SELECT * FROM auctions WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getAuctionsBySeller(sellerId: String): Flow<List<AuctionEntity>>
    
    @Query("SELECT * FROM auctions WHERE sellerId = :sellerId AND status = :status ORDER BY endsAt ASC")
    fun getSellerAuctionsByStatus(sellerId: String, status: String): Flow<List<AuctionEntity>>
    
    @Query("UPDATE auctions SET status = 'CANCELLED', closedAt = :now, closedBy = 'SELLER', updatedAt = :now, dirty = 1 WHERE auctionId = :auctionId AND bidCount = 0")
    suspend fun cancelAuction(auctionId: String, now: Long): Int
    
    // ========================================
    // Auto-Close Worker Queries
    // ========================================
    
    /** Get auctions that should be closed (expired but still ACTIVE) */
    @Query("SELECT * FROM auctions WHERE status = 'ACTIVE' AND endsAt < :now")
    suspend fun getExpiredAuctions(now: Long): List<AuctionEntity>
    
    /** Close an auction with winner */
    @Query("UPDATE auctions SET status = :status, closedAt = :closedAt, closedBy = 'SYSTEM', updatedAt = :closedAt, dirty = 1 WHERE auctionId = :auctionId")
    suspend fun closeAuction(auctionId: String, status: String, closedAt: Long)
    
    // ========================================
    // Bid Tracking
    // ========================================
    
    /** Increment bid count and update current price */
    @Query("UPDATE auctions SET currentPrice = :amount, winnerId = :winnerId, bidCount = bidCount + 1, isReserveMet = CASE WHEN reservePrice IS NOT NULL AND :amount >= reservePrice THEN 1 ELSE isReserveMet END, updatedAt = :now, dirty = 1 WHERE auctionId = :auctionId")
    suspend fun updateBidState(auctionId: String, amount: Double, winnerId: String, now: Long)
    
    /** Extend auction (soft-close) */
    @Query("UPDATE auctions SET endsAt = :newEndsAt, extensionCount = extensionCount + 1, updatedAt = :now, dirty = 1 WHERE auctionId = :auctionId AND extensionCount < maxExtensions")
    suspend fun extendAuction(auctionId: String, newEndsAt: Long, now: Long): Int
    
    /** Increment view count */
    @Query("UPDATE auctions SET viewCount = viewCount + 1 WHERE auctionId = :auctionId")
    suspend fun incrementViewCount(auctionId: String)
    
    // ========================================
    // Buy Now
    // ========================================
    
    @Query("UPDATE auctions SET status = 'SOLD', currentPrice = buyNowPrice, winnerId = :buyerId, closedAt = :now, closedBy = 'BUYER', updatedAt = :now, dirty = 1 WHERE auctionId = :auctionId AND buyNowPrice IS NOT NULL")
    suspend fun executeBuyNow(auctionId: String, buyerId: String, now: Long): Int
}

