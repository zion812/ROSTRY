package com.rio.rostry.core.model

/**
 * Domain model representing a bid in an auction.
 */
data class Bid(
    val id: String,
    val auctionId: String,
    val bidderId: String,
    val amount: Double,
    val timestamp: Long
)
