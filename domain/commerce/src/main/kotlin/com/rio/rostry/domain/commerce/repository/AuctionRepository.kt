package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Auction
import com.rio.rostry.core.model.Bid
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for auction operations.
 */
interface AuctionRepository {
    /**
     * Observe an auction by ID.
     */
    fun observeAuction(auctionId: String): Flow<Auction?>
    
    /**
     * Observe bids for an auction.
     */
    fun observeAuctionBids(auctionId: String): Flow<List<Bid>>
    
    /**
     * Create a new auction.
     */
    suspend fun createAuction(auction: Auction): Result<String>
    
    /**
     * Place a bid on an auction.
     */
    suspend fun placeBid(auctionId: String, userId: String, amount: Double): Result<Unit>
    
    /**
     * Get auction by product ID.
     */
    suspend fun getAuctionByProductId(productId: String): Result<Auction?>
    
    /**
     * Cancel an auction.
     */
    suspend fun cancelAuction(auctionId: String, sellerId: String): Result<Unit>
    
    /**
     * Buy now an auction.
     */
    suspend fun buyNow(auctionId: String, buyerId: String): Result<Unit>
}
