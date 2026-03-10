package com.rio.rostry.data.commerce.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Auction
import com.rio.rostry.core.model.Bid
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.commerce.repository.AuctionRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuctionRepository using Firebase Firestore.
 */
@Singleton
class AuctionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AuctionRepository {

    private val auctionsCollection = firestore.collection("auctions")
    private val bidsCollection = firestore.collection("auction_bids")

    override fun observeAuction(auctionId: String): Flow<Auction?> = callbackFlow {
        val listener = auctionsCollection.document(auctionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val auction = snapshot?.toObject(Auction::class.java)
                trySend(auction)
            }
        awaitClose { listener.remove() }
    }

    override fun observeAuctionBids(auctionId: String): Flow<List<Bid>> = callbackFlow {
        val listener = bidsCollection.whereEqualTo("auctionId", auctionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val bids = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Bid::class.java)
                } ?: emptyList()
                trySend(bids)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createAuction(auction: Auction): Result<String> {
        return try {
            auctionsCollection.document(auction.id).set(auction).await()
            Result.Success(auction.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun placeBid(auctionId: String, userId: String, amount: Double): Result<Unit> {
        return try {
            val bid = Bid(
                id = java.util.UUID.randomUUID().toString(),
                auctionId = auctionId,
                bidderId = userId,
                amount = amount,
                timestamp = System.currentTimeMillis()
            )
            bidsCollection.document(bid.id).set(bid).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAuctionByProductId(productId: String): Result<Auction?> {
        return try {
            val query = auctionsCollection.whereEqualTo("productId", productId).limit(1).get().await()
            val auction = query.documents.firstOrNull()?.toObject(Auction::class.java)
            Result.Success(auction)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cancelAuction(auctionId: String, sellerId: String): Result<Unit> {
        return try {
            auctionsCollection.document(auctionId)
                .update("status", "CANCELLED", "cancelledBy", sellerId)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun buyNow(auctionId: String, buyerId: String): Result<Unit> {
        return try {
            auctionsCollection.document(auctionId)
                .update("status", "SOLD", "buyerId", buyerId)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
