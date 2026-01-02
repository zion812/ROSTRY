package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.database.dao.AuctionDao
import com.rio.rostry.data.database.dao.BidDao
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.entity.NotificationEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID

@HiltWorker
class AuctionCloserWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val auctionDao: AuctionDao,
    private val bidDao: BidDao,
    private val notificationDao: NotificationDao,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            val expiredAuctions = auctionDao.getExpiredAuctions(now)
            
            Timber.d("AuctionCloserWorker: Found ${expiredAuctions.size} expired auctions")
            
            expiredAuctions.forEach { auction ->
                closeAuction(auction.auctionId)
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "AuctionCloserWorker failed")
            Result.retry()
        }
    }
    
    private suspend fun closeAuction(auctionId: String) {
        try {
            val auction = auctionDao.findById(auctionId) ?: return
            if (auction.status != "ACTIVE") return
            
            val winningBid = bidDao.getHighestBid(auctionId)
            val now = System.currentTimeMillis()
            
            val closeResult: CloseResult
            val newStatus: String
            
            if (winningBid != null) {
                val reserveMet = auction.reservePrice == null || winningBid.amount >= auction.reservePrice
                if (reserveMet) {
                    closeResult = CloseResult.Sold
                    newStatus = "SOLD"
                    
                    // Notify winner
                    createNotification(
                        winningBid.userId,
                        "Congratulations! You won the auction!",
                        "You won with a bid of ₹${winningBid.amount}"
                    )
                    
                    // Notify seller
                    createNotification(
                        auction.sellerId,
                        "Your auction sold!",
                        "Sold for ₹${winningBid.amount}"
                    )
                } else {
                    closeResult = CloseResult.Ended
                    newStatus = "ENDED"
                    
                    // Notify seller - reserve not met
                    createNotification(
                        auction.sellerId,
                        "Auction ended - Reserve not met",
                        "Highest bid was ₹${winningBid.amount}"
                    )
                }
            } else {
                closeResult = CloseResult.NoBids
                newStatus = "NO_BIDS"
                
                // Notify seller
                createNotification(
                    auction.sellerId,
                    "Auction ended with no bids",
                    "Your item received no bids"
                )
            }
            
            // Update auction status
            auctionDao.closeAuction(auctionId, newStatus, now)
            
            // Sync to Firestore
            firestore.collection("auctions").document(auctionId)
                .update(
                    mapOf(
                        "status" to newStatus,
                        "closedAt" to now,
                        "winnerId" to winningBid?.userId,
                        "updatedAt" to now
                    )
                ).await()
                
            Timber.d("Closed auction $auctionId with result: $closeResult")
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to close auction $auctionId")
        }
    }

    private suspend fun createNotification(userId: String, title: String, body: String) {
        try {
            val notificationId = UUID.randomUUID().toString()
            val notification = NotificationEntity(
                notificationId = notificationId,
                userId = userId,
                title = title,
                message = body,
                type = "AUCTION",
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
            
            // Write to Firestore for remote delivery
            firestore.collection("users").document(userId)
                .collection("notifications").document(notificationId)
                .set(notification)
                .await()
                
            // Also save locally (optional, but good for consistency if it's self-notification)
            notificationDao.insertNotification(notification)
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to create notification for $userId")
        }
    }
    
    sealed class CloseResult {
        object Sold : CloseResult()
        object Ended : CloseResult()
        object NoBids : CloseResult()
    }
    
    companion object {
        const val WORK_NAME = "auction_closer_worker"
    }
}

