package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sellerId"])]
)
data class ProductEntity(
    @PrimaryKey val productId: String,
    val sellerId: String, // Foreign key to UserEntity
    val name: String,
    val description: String,
    val category: String, // e.g., "Fruits", "Vegetables", "Grains", "Equipment"
    val price: Double,
    val quantity: Double, // e.g., 100 (for units), 50.5 (for kg/liters)
    val unit: String, // e.g., "kg", "liter", "piece", "acre"
    val location: String, // e.g., "Farm Address" or Lat/Lng coordinates
    val imageUrls: List<String> = emptyList(), // Store as JSON string or use a type converter
    val status: String = "available", // e.g., "available", "sold_out", "pending_approval"
    val condition: String? = null, // e.g. "organic", "fresh", "used" (for equipment)
    val harvestDate: Long? = null, // Timestamp
    val expiryDate: Long? = null, // Timestamp
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
