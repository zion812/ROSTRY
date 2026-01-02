package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rio.rostry.data.database.dao.AuctionDao
import com.rio.rostry.data.database.dao.BidDao
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.database.entity.BidEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface AuctionRepository {
    fun observeAuction(auctionId: String): Flow<AuctionEntity?>
    fun observeAuctionBids(auctionId: String): Flow<List<BidEntity>>
    suspend fun createAuction(auction: AuctionEntity): Resource<String>
    suspend fun placeBid(auctionId: String, userId: String, amount: Double): Resource<Unit>
    suspend fun getAuctionByProductId(productId: String): Resource<AuctionEntity?>
    suspend fun cancelAuction(auctionId: String, sellerId: String): Resource<Unit>
    suspend fun buyNow(auctionId: String, buyerId: String): Resource<Unit>
}

@Singleton
class AuctionRepositoryImpl @Inject constructor(
    private val auctionDao: AuctionDao,
    private val bidDao: BidDao,
    private val firestore: FirebaseFirestore
) : AuctionRepository {

    /**
     * Realtime listener for auction updates (replaces polling)
     */
    override fun observeAuction(auctionId: String): Flow<AuctionEntity?> = callbackFlow {
        var registration: ListenerRegistration? = null
        
        try {
            registration = firestore.collection("auctions").document(auctionId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error, "Auction listener error")
                        trySend(null)
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null && snapshot.exists()) {
                        val auction = snapshot.toObject(AuctionEntity::class.java)
                        trySend(auction)
                        
                        // Also update local cache
                        auction?.let { 
                            kotlinx.coroutines.GlobalScope.launch {
                                auctionDao.upsert(it)
                            }
                        }
                    } else {
                        trySend(null)
                    }
                }
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup auction listener")
            trySend(null)
        }
        
        awaitClose { registration?.remove() }
    }
    
    /**
     * Observe bids for an auction in realtime
     */
    override fun observeAuctionBids(auctionId: String): Flow<List<BidEntity>> = callbackFlow {
        val registration = firestore.collection("auctions").document(auctionId)
            .collection("bids")
            .orderBy("amount", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val bids = snapshot.toObjects(BidEntity::class.java)
                trySend(bids)
            }
        
        awaitClose { registration.remove() }
    }

    override suspend fun createAuction(auction: AuctionEntity): Resource<String> = try {
        require(auction.endsAt > auction.startsAt) { "Auction end time must be after start time" }
        require(auction.sellerId.isNotEmpty()) { "Seller ID required" }
        
        // Save to Firestore
        firestore.collection("auctions").document(auction.auctionId).set(auction).await()
        
        // Also save locally
        auctionDao.upsert(auction)
        
        Timber.d("Auction created: ${auction.auctionId}")
        Resource.Success(auction.auctionId)
    } catch (e: Exception) {
        Timber.e(e, "Failed to create auction")
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
            
            if (auction.status == "CANCELLED" || auction.status == "ENDED" || auction.status == "SOLD") {
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

            // Soft Close Logic: If bid is placed within extension window, extend (if allowed)
            var newEndsAt = auction.endsAt
            val timeRemaining = auction.endsAt - now
            val extensionMs = auction.extensionMinutes * 60 * 1000L
            
            if (timeRemaining < extensionMs && auction.extensionCount < auction.maxExtensions) {
                newEndsAt = now + extensionMs
            }
            
            // Check if reserve is now met
            val isReserveMet = auction.reservePrice?.let { amount >= it } ?: true

            // Update Auction
            transaction.update(auctionRef, mapOf(
                "currentPrice" to amount,
                "winnerId" to userId,
                "bidCount" to auction.bidCount + 1,
                "isReserveMet" to isReserveMet,
                "updatedAt" to now,
                "endsAt" to newEndsAt,
                "extensionCount" to if (newEndsAt > auction.endsAt) auction.extensionCount + 1 else auction.extensionCount,
                "status" to "ACTIVE"
            ))

            // Create Bid Document
            val bidId = java.util.UUID.randomUUID().toString()
            val bidRef = auctionRef.collection("bids").document(bidId)
            val bid = BidEntity(
                bidId = bidId,
                auctionId = auctionId,
                userId = userId,
                amount = amount,
                placedAt = now,
                isWinning = true
            )
            transaction.set(bidRef, bid)
            
            // Mark previous highest bidder as outbid (if exists) and Notify
            if (auction.winnerId != null && auction.winnerId != userId) {
                // Determine previous winner ID (it's auction.winnerId)
                val outbidUserId = auction.winnerId
                
                // Create Outbid Notification
                val notificationId = java.util.UUID.randomUUID().toString()
                val notificationRef = firestore.collection("users").document(outbidUserId)
                    .collection("notifications").document(notificationId)
                
                val notification = com.rio.rostry.data.database.entity.NotificationEntity(
                    notificationId = notificationId,
                    userId = outbidUserId,
                    title = "You were outbid!",
                    message = "A higher bid of ₹$amount was placed on your watched item.",
                    type = "OUTBID",
                    deepLinkUrl = "rostry://auction/$auctionId",
                    isRead = false,
                    imageUrl = null, // AuctionEntity doesn't store product image directly
                    createdAt = now,
                    domain = "AUCTION"
                )
                
                transaction.set(notificationRef, notification)
            }
        }.await()
        
        Timber.d("Bid placed: $amount on auction $auctionId")
        Resource.Success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Bid failed")
        Resource.Error(e.message ?: "Bid failed")
    }

    override suspend fun getAuctionByProductId(productId: String): Resource<AuctionEntity?> = try {
        val snapshot = firestore.collection("auctions")
            .whereEqualTo("productId", productId)
            .whereIn("status", listOf("ACTIVE", "UPCOMING"))
            .limit(1)
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
    
    /**
     * Cancel auction (seller can only cancel if no bids placed)
     */
    override suspend fun cancelAuction(auctionId: String, sellerId: String): Resource<Unit> {
        return try {
        val auction = auctionDao.findById(auctionId)
            ?: return Resource.Error("Auction not found")
        
        if (auction.sellerId != sellerId) {
            return Resource.Error("Not authorized")
        }
        
        if (auction.bidCount > 0) {
            return Resource.Error("Cannot cancel auction with bids")
        }
        
        val now = System.currentTimeMillis()
        val updated = auction.copy(
            status = "CANCELLED",
            closedAt = now,
            closedBy = "SELLER",
            updatedAt = now,
            dirty = true
        )
        
        firestore.collection("auctions").document(auctionId).set(updated).await()
        auctionDao.upsert(updated)
        
        Timber.d("Auction cancelled: $auctionId")
        Resource.Success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Failed to cancel auction")
        Resource.Error(e.message ?: "Failed to cancel")
    }
    }
    
    /**
     * Buy Now - instant purchase at buyNowPrice
     */
    override suspend fun buyNow(auctionId: String, buyerId: String): Resource<Unit> {
        return try {
        val auction = auctionDao.findById(auctionId)
            ?: return Resource.Error("Auction not found")
        
        if (auction.buyNowPrice == null) {
            return Resource.Error("Buy Now not available")
        }
        
        if (auction.status != "ACTIVE" && auction.status != "UPCOMING") {
            return Resource.Error("Auction is closed")
        }
        
        val now = System.currentTimeMillis()
        val updated = auction.copy(
            status = "SOLD",
            currentPrice = auction.buyNowPrice,
            winnerId = buyerId,
            closedAt = now,
            closedBy = "BUYER",
            updatedAt = now,
            dirty = true
        )
        
        firestore.collection("auctions").document(auctionId).set(updated).await()
        auctionDao.upsert(updated)
        
        Timber.d("Buy Now executed: $auctionId by $buyerId")
        Resource.Success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Buy Now failed")
        Resource.Error(e.message ?: "Buy Now failed")
    }
    }
}
