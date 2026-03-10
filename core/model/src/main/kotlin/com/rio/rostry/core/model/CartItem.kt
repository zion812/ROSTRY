package com.rio.rostry.core.model

/**
 * Domain model for shopping cart item.
 */
data class CartItem(
    val id: String,
    val userId: String,
    val productId: String,
    val quantity: Double,
    val addedAt: Long
)
