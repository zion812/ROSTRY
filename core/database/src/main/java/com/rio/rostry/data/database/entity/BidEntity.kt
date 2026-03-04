package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bids",
    foreignKeys = [
        ForeignKey(
            entity = AuctionEntity::class,
            parentColumns = ["auctionId"],
            childColumns = ["auctionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("auctionId"),
        Index("userId"),
        Index(value = ["auctionId", "amount"])
    ]
)
data class BidEntity(
    @PrimaryKey val bidId: String,
    val auctionId: String,
    val userId: String,
    val amount: Double,
    val placedAt: Long = System.currentTimeMillis(),
    
    // Auto-Bid (Proxy Bidding)
    val isAutoBid: Boolean = false,       // Was this placed by auto-bid system
    val maxAmount: Double? = null,        // Proxy bid ceiling (user's max)
    
    // Status
    val isWinning: Boolean = false,       // Currently winning (denormalized)
    val wasOutbid: Boolean = false,       // Someone bid higher
    val outbidAt: Long? = null,           // When outbid
    
    // Notifications
    val outbidNotified: Boolean = false,  // User notified about outbid
    
    // Admin
    val isRetracted: Boolean = false,     // Rare, admin only
    val retractedReason: String? = null
)

