package com.rio.rostry.data.models

data class MarketListing(
    val listingId: String,
    val fowlId: String,
    val sellerId: String,
    val price: Double,
    val currency: String,
    val description: String,
    val status: ListingStatus,
    val createdAt: Long,
    val updatedAt: Long
)

enum class ListingStatus {
    ACTIVE,
    SOLD,
    INACTIVE
}