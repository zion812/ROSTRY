package com.rio.rostry.domain.model

import java.util.Date

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val unit: String, // kg, bunch, crate, etc.
    val farmerId: String,
    val imageUrl: String? = null,
    val category: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)