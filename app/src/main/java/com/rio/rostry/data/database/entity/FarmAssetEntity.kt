package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.LifecycleStage

@Keep
@Entity(
    tableName = "farm_assets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["assetType"]),
        Index(value = ["status"])
    ]
)
data class FarmAssetEntity(
    @PrimaryKey val assetId: String = "",
    val farmerId: String = "", // Owner
    val name: String = "", // e.g., "Batch 203 - Layers", "Tractor A"
    
    // Categorization
    val assetType: String = "", // FLOCK, BATCH, ANIMAL, EQUIPMENT, FEED, STRUCTURE
    val category: String = "", // Sub-category e.g., "Chicken", "Goat", "Tools"
    
    // Status & Visibility
    val status: String = "ACTIVE", // ACTIVE, QUARANTINED, ARCHIVED, CONSUMED, SOLD_OUT
    val isShowcase: Boolean = false, // If true, visible on public farm profile (read-only)
    
    // Location
    val locationName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Quantity & Metrics
    val quantity: Double = 1.0, // Current headcount or amount
    val initialQuantity: Double = 1.0, // For mortality tracking
    val unit: String = "units", // count, kg, liters
    
    // Biological / Lifecycle Data
    val birthDate: Long? = null,
    val ageWeeks: Int? = null, // Computed or cached
    val breed: String? = null,
    val gender: String? = null, // MALE, FEMALE, MIXED
    val color: String? = null,
    val healthStatus: String = "HEALTHY", // HEALTHY, SICK, INJURED, RECOVERING
    
    // Detail & Media
    val description: String = "",
    val imageUrls: List<String> = emptyList(),
    val notes: String? = null,
    val lifecycleSubStage: String? = null, // e.g. BROODING, GROWER_EARLY (from LifecycleSubStage)
    
    // Traceability
    val parentIdsJson: String? = null, // JSON list of parent asset IDs
    val batchId: String? = null, // If this animal belongs to a batch
    val origin: String? = null, // "HATCHED_ON_FARM", "PURCHASED", etc.
    val birdCode: String? = null, // Unique identifier (if individual)
    
    // Records
    val lastVaccinationDate: Long? = null,
    val nextVaccinationDate: Long? = null,

    val weightGrams: Double? = null,
    val metadataJson: String = "{}",
    
    // Marketplace Lifecycle
    val listedAt: Long? = null,         // When listed for sale
    val listingId: String? = null,      // Reference to MarketListingEntity
    
    // Sale Tracking
    val soldAt: Long? = null,           // When sold
    val soldToUserId: String? = null,   // Buyer ID
    val soldPrice: Double? = null,      // Sale price
    
    // Ownership Transfer (for buyer's copy)
    val previousOwnerId: String? = null, // Previous owner (for lineage)
    val transferredAt: Long? = null,     // When ownership transferred
    
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false // For sync
)
