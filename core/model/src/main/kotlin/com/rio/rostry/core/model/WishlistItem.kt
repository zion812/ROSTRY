package com.rio.rostry.core.model

/**
 * Domain model representing an item in the user's wishlist.
 */
data class WishlistItem(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val productImageUrl: String?,
    val price: Double,
    val currency: String = "USD",
    val sellerId: String,
    val sellerName: String?,
    val isAvailable: Boolean,
    val addedAt: Long
) {
    /**
     * Checks if this item is still available for purchase.
     */
    fun canPurchase(): Boolean = isAvailable && price >= 0
}
