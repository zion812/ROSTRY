package com.rio.rostry.domain.model

import java.util.Date

data class ProductTracking(
    val id: String,
    val productId: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val notes: String? = null,
    val imageUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)