package com.rio.rostry.core.model

/**
 * Domain model representing an auction.
 */
data class Auction(
    val id: String,
    val productId: String,
    val sellerId: String,
    val startingPrice: Double,
    val currentBid: Double,
    val highestBidderId: String?,
    val startTime: Long,
    val endTime: Long,
    val status: String, // ACTIVE, SOLD, CANCELLED
    val buyerId: String? = null,
    val cancelledBy: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
