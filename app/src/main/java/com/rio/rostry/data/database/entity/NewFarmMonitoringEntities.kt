package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Breeding pair entity for tracking breeding operations
@Entity(
    tableName = "breeding_pairs",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["maleProductId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["femaleProductId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("farmerId"),
        Index("status"),
        Index("maleProductId"),
        Index("femaleProductId")
    ]
)
data class BreedingPairEntity(
    @PrimaryKey val pairId: String,
    val farmerId: String,
    val maleProductId: String,
    val femaleProductId: String,
    val pairedAt: Long,
    val status: String, // ACTIVE, RETIRED
    val eggsCollected: Int = 0,
    val hatchSuccessRate: Double = 0.0,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)

// Farm alert entity for notifications and reminders
@Entity(
    tableName = "farm_alerts",
    indices = [
        Index("farmerId"),
        Index("isRead"),
        Index("createdAt")
    ]
)
data class FarmAlertEntity(
    @PrimaryKey val alertId: String,
    val farmerId: String,
    val alertType: String, // VACCINATION_DUE, QUARANTINE_UPDATE, MORTALITY_SPIKE, HATCHING_DUE
    val severity: String, // INFO, WARNING, URGENT
    val message: String,
    val actionRoute: String? = null, // navigation deep-link
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)

// Listing draft entity for wizard state persistence
@Entity(
    tableName = "listing_drafts",
    indices = [
        Index("farmerId"),
        Index("updatedAt")
    ]
)
data class ListingDraftEntity(
    @PrimaryKey val draftId: String,
    val farmerId: String,
    val step: String, // BASICS, DETAILS, MEDIA, REVIEW
    val formDataJson: String, // serialized wizard state
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)

// Farmer dashboard snapshot for weekly KPIs
@Entity(
    tableName = "farmer_dashboard_snapshots",
    indices = [
        Index("farmerId"),
        Index("weekStartAt")
    ]
)
data class FarmerDashboardSnapshotEntity(
    @PrimaryKey val snapshotId: String,
    val farmerId: String,
    val weekStartAt: Long,
    val weekEndAt: Long,
    val revenueInr: Double = 0.0,
    val ordersCount: Int = 0,
    val hatchSuccessRate: Double = 0.0,
    val mortalityRate: Double = 0.0,
    val deathsCount: Int = 0, // Absolute death count for the week
    val vaccinationCompletionRate: Double = 0.0,
    val growthRecordsCount: Int = 0,
    val quarantineActiveCount: Int = 0,
    val productsReadyToListCount: Int = 0, // Farm-marketplace bridge: Products with growth records and not in quarantine
    // Daily log metrics (Sprint 1)
    val avgFeedKg: Double? = null,
    val medicationUsageCount: Int? = null,
    val dailyLogComplianceRate: Double? = null,
    val actionSuggestions: String? = null, // JSON array of suggestions
    val transfersInitiatedCount: Int = 0,
    val transfersCompletedCount: Int = 0,
    val complianceScore: Double = 0.0,
    val onboardingCount: Int = 0,
    val dailyGoalsCompletedCount: Int = 0,
    val analyticsInsightsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
