package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rio.rostry.data.database.dao.AuctionDao
import com.rio.rostry.data.database.dao.BidDao
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.database.entity.BidEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuctionRepository {
    fun observeAuction(auctionId: String): Flow<AuctionEntity?>
    suspend fun createAuction(auction: AuctionEntity): Resource<String>
    suspend fun placeBid(auctionId: String, userId: String, amount: Double): Resource<Unit>
    suspend fun getAuctionByProductId(productId: String): Resource<AuctionEntity?>
}

@Singleton
class AuctionRepositoryImpl @Inject constructor(
    private val auctionDao: AuctionDao,
    private val bidDao: BidDao,
    private val firestore: FirebaseFirestore
) : AuctionRepository {

    override fun observeAuction(auctionId: String): Flow<AuctionEntity?> = callbackFlow {
        // Listen to real-time updates from Firestore for the most up-to-date state
        val registration = firestore.collection("auctions").document(auctionId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val auction = snapshot.toObject(AuctionEntity::class.java)
                    trySend(auction)
                } else {
                    trySend(null)
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun createAuction(auction: AuctionEntity): Resource<String> = try {
        require(auction.endsAt > auction.startsAt) { "Auction end time must be after start time" }
        
        // Save to Firestore
        firestore.collection("auctions").document(auction.auctionId).set(auction).await()
        
        // Also save locally
        auctionDao.upsert(auction)
        
        Resource.Success(auction.auctionId)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to create auction")
    }

    override suspend fun placeBid(auctionId: String, userId: String, amount: Double): Resource<Unit> = try {
        val auctionRef = firestore.collection("auctions").document(auctionId)
        
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(auctionRef)
            val auction = snapshot.toObject(AuctionEntity::class.java) 
                ?: throw IllegalStateException("Auction not found")
            
            val now = System.currentTimeMillis()
            
            // Validation
            if (!auction.isActive || now < auction.startsAt || now > auction.endsAt) {
                throw IllegalStateException("Auction is not active")
            }
            
            if (auction.status == "CANCELLED" || auction.status == "ENDED") {
                throw IllegalStateException("Auction is closed")
            }

            val minBid = if (auction.currentPrice > 0) {
                auction.currentPrice + auction.bidIncrement
            } else {
                auction.minPrice
            }

            if (amount < minBid) {
                throw IllegalStateException("Bid must be at least ₹$minBid")
            }

            // Soft Close Logic: If bid is placed within last 5 minutes, extend by 5 minutes
            var newEndsAt = auction.endsAt
            val timeRemaining = auction.endsAt - now
            if (timeRemaining < 5 * 60 * 1000) {
                newEndsAt = now + 5 * 60 * 1000
            }

            // Update Auction
            transaction.update(auctionRef, mapOf(
                "currentPrice" to amount,
                "winnerId" to userId,
                "updatedAt" to now,
                "endsAt" to newEndsAt,
                "status" to "ACTIVE" // Ensure status is active
            ))

            // Create Bid Document
            val bidId = java.util.UUID.randomUUID().toString()
            val bidRef = auctionRef.collection("bids").document(bidId)
            val bid = BidEntity(
                bidId = bidId,
                auctionId = auctionId,
                userId = userId,
                amount = amount,
                placedAt = now
            )
            transaction.set(bidRef, bid)
            
            // Note: We don't update local DB here inside transaction. 
            // The observeAuction flow will pick up the change and update UI.
        }.await()
        
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Bid failed")
    }

    override suspend fun getAuctionByProductId(productId: String): Resource<AuctionEntity?> = try {
        val snapshot = firestore.collection("auctions")
            .whereEqualTo("productId", productId)
            .whereEqualTo("status", "ACTIVE") // Or check for UPCOMING as well if needed
            .get()
            .await()
        
        if (!snapshot.isEmpty) {
            val auction = snapshot.documents[0].toObject(AuctionEntity::class.java)
            Resource.Success(auction)
        } else {
            Resource.Success(null)
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch auction")
    }
}
