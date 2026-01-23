package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.LifecycleStage

@Keep
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
    indices = [
        Index(value = ["sellerId"]),
        Index(value = ["category"]),
        Index(value = ["status"]),
        Index(value = ["sourceAssetId"]),
        // Performance indexes for farmer dashboard queries
        Index(value = ["sellerId", "lifecycleStatus"]),    // Filter active products by farmer
        Index(value = ["sellerId", "isBatch"]),             // Filter batches by farmer
        Index(value = ["birthDate"]),                       // Age-based queries
        Index(value = ["updatedAt"]),                       // Recently modified queries
        Index(value = ["createdAt"])                        // Recently added queries
    ]
)
data class ProductEntity(
    @PrimaryKey val productId: String = "",
    val sellerId: String = "", // Foreign key to UserEntity
    val name: String = "",
    val description: String = "",
    val category: String = "", // e.g., "Fruits", "Vegetables", "Grains", "Equipment"
    val price: Double = 0.0,
    val quantity: Double = 0.0, // e.g., 100 (for units), 50.5 (for kg/liters)
    val unit: String = "", // e.g., "kg", "liter", "piece", "acre"
    val location: String = "", // textual location for display
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrls: List<String> = emptyList(), // Store as JSON string or use a type converter
    val status: String = "private", // e.g., "private", "available", "sold_out", "pending_approval"
    val condition: String? = null, // e.g. "organic", "fresh", "used" (for equipment)
    val harvestDate: Long? = null, // Timestamp
    val expiryDate: Long? = null, // Timestamp

    // Age-specific attributes
    val birthDate: Long? = null,
    val vaccinationRecordsJson: String? = null, // JSON array of vaccination entries
    val weightGrams: Double? = null,
    val heightCm: Double? = null,
    val gender: String? = null, // male, female, unknown
    val color: String? = null,
    val breed: String? = null,
    val healthStatus: String? = null, // "OK", "Sick", etc.

    /**
     * Human-readable bird identifier (e.g., 'BLK-RIR-001'). Should be populated at product
     * creation time using BirdIdGenerator.generate() and should not be recomputed later to
     * ensure stability. Use for local, in-person exchanges and QR displays.
     *
     * For legacy products with null birdCode or colorTag, a one-time backfill migration is
     * available via `ProductRepository.backfillBirdCodes()`. Implementers should invoke this
     * method during app startup, database migration, or via an admin tool, guarded by a flag
     * to prevent repeated execution. Lazy initialization on read is available as a fallback
     * for exceptional cases but is not the primary mechanism.
     *
     * Once populated, these fields should not be recomputed to ensure stability across app
     * sessions and QR code scans.
     */
    val birdCode: String? = null,

    /**
     * Standardized color classification (BLACK, WHITE, BROWN, YELLOW, MIXED) for visual
     * identification and filtering. Should be populated at product creation time using
     * BirdIdGenerator.colorTag() and should not be recomputed later to ensure stability.
     *
     * For legacy products with null birdCode or colorTag, a one-time backfill migration is
     * available via `ProductRepository.backfillBirdCodes()`. Implementers should invoke this
     * method during app startup, database migration, or via an admin tool, guarded by a flag
     * to prevent repeated execution. Lazy initialization on read is available as a fallback
     * for exceptional cases but is not the primary mechanism.
     *
     * Once populated, these fields should not be recomputed to ensure stability across app
     * sessions and QR code scans.
     */
    val colorTag: String? = null,

    // Traceability
    val familyTreeId: String? = null,
    
    /**
     * Bridge to FarmAssetEntity - links this marketplace listing to the source farm asset.
     * When a farmer creates a listing from their farm inventory, this field stores the
     * assetId of the FarmAssetEntity. This enables:
     * - Querying if an asset already has a listing
     * - Syncing updates between asset and listing
     * - Maintaining farm vs marketplace separation per Option C
     */
    val sourceAssetId: String? = null,
    
    val parentIdsJson: String? = null, // JSON array of parent product IDs
    val breedingStatus: String? = null,
    val transferHistoryJson: String? = null, // JSON array of past transfers (summary)
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val lastModifiedAt: Long = 0L,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,

    // Lifecycle tracking (nullable for backward compatibility)
    val stage: LifecycleStage? = null,
    val lifecycleStatus: String? = null,
    val parentMaleId: String? = null,
    val parentFemaleId: String? = null,
    val ageWeeks: Int? = null,
    val lastStageTransitionAt: Long? = null,
    val breederEligibleAt: Long? = null,
    val isBatch: Boolean? = null,
    val batchId: String? = null,
    val splitAt: Long? = null,
    val splitIntoIds: String? = null,
    val documentUrls: List<String> = emptyList(),
    val qrCodeUrl: String? = null,
    val customStatus: String? = null, // Custom farmer-defined status (e.g., "Ready to Ship", "Reserved")
    val debug: Boolean = false,

    // Delivery & Logistics
    val deliveryOptions: List<String> = emptyList(), // e.g., "SELF_PICKUP", "FARMER_DELIVERY"
    val deliveryCost: Double? = null,
    val leadTimeDays: Int? = null, // Days notice required

    // Digital Farm - Evolutionary Visuals (Enthusiast Feature)
    val motherId: String? = null, // Links chicks to their mother hen for nursery scene
    val isBreedingUnit: Boolean = false, // Marks this as part of a breeding group
    val eggsCollectedToday: Int = 0, // For "Ghost Eggs" feature
    val lastEggLogDate: Long? = null, // Track when eggs were last logged
    val readyForSale: Boolean = false, // Triggers gold star display
    val targetWeight: Double? = null, // Weight goal for ready status
    
    // Showcase & External Media (Feature-based storage control)
    val isShowcased: Boolean = false,           // True if in profile showcase
    val externalVideoUrl: String? = null,       // YouTube/Instagram link (Enthusiast only)
    
    // Data Integrity & Audit (Phase 1 - Trust & Traceability)
    val recordsLockedAt: Long? = null,          // Manual lock timestamp (by transfer or admin)
    val autoLockAfterDays: Int = 30,            // Time-based lock fallback
    val lineageHistoryJson: String? = null,     // Audit trail for lineage corrections
    val editCount: Int = 0,                     // Number of edits to core fields
    val lastEditedBy: String? = null,            // User who made last edit

    // Moderation
    val adminFlagged: Boolean = false,
    val moderationNote: String? = null
) {
    /**
     * Returns true if this product is a public market listing (requires verification to create).
     * Returns false if the product is private (for local farm management only).
     */
    val isPublic: Boolean
        get() = !status.isNullOrBlank() && status != "private"
}
