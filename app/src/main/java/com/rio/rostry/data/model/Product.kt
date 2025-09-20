package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
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