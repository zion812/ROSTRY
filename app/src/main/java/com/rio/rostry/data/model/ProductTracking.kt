package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "product_tracking")
data class ProductTracking(
    @PrimaryKey
    val id: String,
    val productId: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val notes: String? = null,
    val imageUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
    // Temporarily removing isDeleted and deletedAt for compilation
    // val isDeleted: Boolean = false,
    // val deletedAt: Date? = null
)