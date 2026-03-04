package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "farm_inventory",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FarmAssetEntity::class,
            parentColumns = ["assetId"],
            childColumns = ["sourceAssetId"],
            onDelete = ForeignKey.SET_NULL // Keep inventory history even if asset is deleted
        )
    ],
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["sourceAssetId"]),
        Index(value = ["sku"])
    ]
)
data class InventoryItemEntity(
    @PrimaryKey val inventoryId: String,
    val farmerId: String, // Owner
    
    // Source
    val sourceAssetId: String? = null, // Optional link to the living asset
    val sourceBatchId: String? = null,
    
    // Product Definition
    val name: String, // e.g., "Tray of Eggs (Large)"
    val sku: String? = null, // Stock Keeping Unit
    val category: String, // "Produce", "Meat", "Livestock"
    
    // Stock Levels
    val quantityAvailable: Double = 0.0,
    val quantityReserved: Double = 0.0, // In carts or pending orders
    val unit: String = "units",
    
    // Expiry & Quality
    val producedAt: Long? = null, // Harvest date / Slaughter date
    val expiresAt: Long? = null,
    val qualityGrade: String? = null, // "Grade A", "Organic", "Premium"
    
    // Metadata
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false
)
