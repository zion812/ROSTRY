package com.rio.rostry.core.model

/**
 * Domain model representing a marketplace listing.
 */
data class Listing(
    val id: String,
    val productId: String,
    val sellerId: String,
    val sellerName: String?,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val quantity: Int,
    val minimumOrderQuantity: Int,
    val category: String,
    val images: List<String>,
    val status: ListingStatus,
    val isPromoted: Boolean,
    val promotionEndsAt: Long?,
    val views: Int,
    val favorites: Int,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val shippingOptions: List<ShippingOption>,
    val tags: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val expiresAt: Long?
) {
    /**
     * Checks if the listing is active and available.
     */
    fun isAvailable(): Boolean = status == ListingStatus.ACTIVE && quantity > 0
    
    /**
     * Checks if the listing is promoted and promotion is still valid.
     */
    fun isCurrentlyPromoted(): Boolean = 
        isPromoted && (promotionEndsAt == null || promotionEndsAt > System.currentTimeMillis())
    
    /**
     * Checks if the listing has expired.
     */
    fun isExpired(): Boolean = 
        expiresAt != null && expiresAt < System.currentTimeMillis()
    
    /**
     * Gets the cheapest shipping option.
     */
    fun getCheapestShipping(): ShippingOption? = 
        shippingOptions.minByOrNull { it.cost }
}

/**
 * Listing status enum.
 */
enum class ListingStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    SOLD_OUT,
    EXPIRED,
    REMOVED
}

/**
 * Shipping option for a listing.
 */
data class ShippingOption(
    val id: String,
    val name: String,
    val description: String?,
    val cost: Double,
    val estimatedDays: Int?,
    val carrier: String?
)

/**
 * Filter criteria for marketplace search.
 */
data class ListingFilter(
    val query: String? = null,
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val sellerId: String? = null,
    val location: String? = null,
    val radius: Double? = null,
    val centerLat: Double? = null,
    val centerLng: Double? = null,
    val tags: List<String>? = null,
    val onlyPromoted: Boolean = false,
    val sortBy: SortOption = SortOption.RELEVANCE,
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Sort options for marketplace listings.
 */
enum class SortOption {
    RELEVANCE,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    NEWEST_FIRST,
    MOST_POPULAR,
    NEAREST
}

/**
 * Search result wrapper.
 */
data class SearchResult<T>(
    val items: List<T>,
    val total: Int,
    val hasMore: Boolean,
    val query: String?
)
