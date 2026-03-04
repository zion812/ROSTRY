package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "market_listings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InventoryItemEntity::class,
            parentColumns = ["inventoryId"],
            childColumns = ["inventoryId"],
            onDelete = ForeignKey.CASCADE // If inventory is gone, listing is gone
        )
    ],
    indices = [
        Index(value = ["sellerId"]),
        Index(value = ["inventoryId"]),
        Index(value = ["status"]),
        Index(value = ["category"])
    ]
)
data class MarketListingEntity(
    @PrimaryKey val listingId: String,
    val sellerId: String,
    
    // The "Product" being sold
    val inventoryId: String, 
    
    // Commercial Details
    val title: String, // Marketing title
    val description: String,
    val price: Double,
    val currency: String = "USD", // Should arguably be local currency
    val priceUnit: String = "unit", // e.g., "per tray", "per kg"
    
    // Categorization (Denormalized for faster search)
    val category: String,
    val tags: List<String> = emptyList(),
    
    // Logistics
    val deliveryOptions: List<String> = emptyList(), // PICKUP, LOCAL_DELIVERY, SHIPPING
    val deliveryCost: Double? = null,
    val locationName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val minOrderQuantity: Double = 1.0,
    val maxOrderQuantity: Double? = null,
    
    // Media
    val imageUrls: List<String> = emptyList(),
    
    // Status
    val status: String = "DRAFT", // DRAFT, PUBLISHED, SUSPENDED, SOLD_OUT
    val isActive: Boolean = true,
    
    val viewsCount: Int = 0,
    val inquiriesCount: Int = 0,
    val leadTimeDays: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null, // Auto-delist
    val dirty: Boolean = false
)
