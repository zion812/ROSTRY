package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "auctions",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("productId"),
        Index("sellerId"),
        Index("status"),
        Index(value = ["status", "endsAt"])
    ]
)
@Keep
data class AuctionEntity(
    @PrimaryKey val auctionId: String = "",
    val productId: String = "",
    val sellerId: String = "",              // Auction owner
    
    // Timing
    val startsAt: Long = 0L,
    val endsAt: Long = 0L,
    val closedAt: Long? = null,             // Actual close time
    val closedBy: String? = null,           // SYSTEM, SELLER, BUYER (buy now)
    
    // Pricing
    val minPrice: Double = 0.0,             // Starting price
    val currentPrice: Double = 0.0,         // Current highest bid
    val reservePrice: Double? = null,       // Hidden minimum seller accepts
    val buyNowPrice: Double? = null,        // Instant purchase option
    val bidIncrement: Double = 10.0,
    
    // Bidding State
    val bidCount: Int = 0,                  // Denormalized for display
    val winnerId: String? = null,           // Current winning bidder
    val isReserveMet: Boolean = false,      // Reserve price met
    
    // Soft-Close Protection
    val extensionCount: Int = 0,            // Times extended
    val maxExtensions: Int = 3,             // Limit sniping protection
    val extensionMinutes: Int = 5,          // Extension duration
    
    // Status
    val status: String = "UPCOMING",        // UPCOMING, ACTIVE, ENDED, CANCELLED, SOLD
    val isActive: Boolean = true,
    
    // Analytics
    val viewCount: Int = 0,
    
    // Metadata
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val dirty: Boolean = false
)
