package com.rio.rostry.domain.model

import java.util.Date

data class Order(
    val id: String,
    val productId: String,
    val buyerId: String,
    val farmerId: String,
    val quantity: Int,
    val totalPrice: Double,
    val status: String, // pending, confirmed, shipped, delivered, cancelled
    val deliveryAddress: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)