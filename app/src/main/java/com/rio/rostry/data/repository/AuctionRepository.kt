package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.AuctionDao
import com.rio.rostry.data.database.dao.BidDao
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.database.entity.BidEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

interface AuctionRepository {
    fun observeAuction(auctionId: String): Flow<AuctionEntity?>
    suspend fun createAuction(auction: AuctionEntity): Resource<String>
    suspend fun placeBid(auctionId: String, userId: String, amount: Double): Resource<Unit>
}

@Singleton
class AuctionRepositoryImpl @Inject constructor(
    private val auctionDao: AuctionDao,
    private val bidDao: BidDao
) : AuctionRepository {

    override fun observeAuction(auctionId: String): Flow<AuctionEntity?> = auctionDao.observeById(auctionId)

    override suspend fun createAuction(auction: AuctionEntity): Resource<String> = try {
        require(auction.endsAt > auction.startsAt) { "Auction end time must be after start time" }
        auctionDao.upsert(auction)
        Resource.Success(auction.auctionId)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to create auction")
    }

    override suspend fun placeBid(auctionId: String, userId: String, amount: Double): Resource<Unit> = try {
        val now = System.currentTimeMillis()
        val auction = observeAuction(auctionId).first() ?: return Resource.Error("Auction not found")
        require(auction.isActive && now in auction.startsAt..auction.endsAt) { "Auction not active" }

        // Enforce minimum increment of ₹10
        val highest = bidDao.getHighestBid(auctionId)
        val baseline = maxOf(auction.currentPrice, highest?.amount ?: auction.minPrice)
        require(amount >= baseline + 10.0) { "Minimum increment is ₹10" }

        val bid = BidEntity(
            bidId = java.util.UUID.randomUUID().toString(),
            auctionId = auctionId,
            userId = userId,
            amount = amount,
            placedAt = now
        )
        bidDao.insert(bid)
        // Optimistically update auction current price
        auctionDao.update(auction.copy(currentPrice = amount, updatedAt = now))
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Bid failed")
    }
}
