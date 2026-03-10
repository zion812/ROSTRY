package com.rio.rostry.core.model

/**
 * Market listing model for marketplace items.
 */
data class MarketListing(
    val id: String,
    val inventoryItemId: String,
    val sellerId: String,
    val price: Double,
    val currency: String = "USD",
    val minimumOrderQuantity: Int = 1,
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(),
    val status: MarketListingStatus = MarketListingStatus.ACTIVE,
    val shippingOptions: List<String> = emptyList(),
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val viewsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class MarketListingStatus {
    ACTIVE,
    INACTIVE,
    SOLD,
    RESERVED,
    SUSPENDED
}

