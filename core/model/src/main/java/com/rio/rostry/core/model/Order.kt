package com.rio.rostry.core.model

/**
 * Order model for marketplace orders.
 */
data class LegacyOrder(
    val id: String,
    val buyerId: String,
    val sellerId: String,
    val listingId: String,
    val quantity: Int,
    val totalPrice: Double,
    val currency: String = "USD",
    val status: String = "PENDING",
    val paymentStatus: String = "PENDING",
    val shippingAddress: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

