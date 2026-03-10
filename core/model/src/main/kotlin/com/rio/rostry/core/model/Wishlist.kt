package com.rio.rostry.core.model

/**
 * Domain model for wishlist item.
 */
data class Wishlist(
    val userId: String,
    val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)
