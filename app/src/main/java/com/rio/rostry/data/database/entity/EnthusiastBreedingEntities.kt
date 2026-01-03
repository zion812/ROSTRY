package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.annotation.Keep

// Enthusiast-specific breeding entities

@Entity(
    tableName = "mating_logs",
    foreignKeys = [
        ForeignKey(
            entity = BreedingPairEntity::class,
            parentColumns = ["pairId"],
            childColumns = ["pairId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("pairId"),
        Index("farmerId"),
        Index("matedAt")
    ]
)
data class MatingLogEntity(
    @PrimaryKey val logId: String,
    val pairId: String,
    val farmerId: String,
    val matedAt: Long,
    val observedBehavior: String? = null,
    // JSON string containing environmental conditions (e.g., temperature, humidity)
    val environmentalConditions: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)

@Entity(
    tableName = "egg_collections",
    foreignKeys = [
        ForeignKey(
            entity = BreedingPairEntity::class,
            parentColumns = ["pairId"],
            childColumns = ["pairId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("pairId"),
        Index("farmerId"),
        Index("collectedAt")
    ]
)
data class EggCollectionEntity(
    @PrimaryKey val collectionId: String,
    val pairId: String,
    val farmerId: String,
    val eggsCollected: Int,
    val collectedAt: Long,
    // A/B/C
    val qualityGrade: String,
    val weight: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)

@Keep
@Entity(
    tableName = "enthusiast_dashboard_snapshots",
    indices = [
        Index("userId"),
        Index("weekStartAt")
    ]
)
data class EnthusiastDashboardSnapshotEntity(
    @PrimaryKey val snapshotId: String = "",
    val userId: String = "",
    val weekStartAt: Long = 0L,
    val weekEndAt: Long = 0L,
    val hatchRateLast30Days: Double = 0.0,
    val breederSuccessRate: Double = 0.0,
    val disputedTransfersCount: Int = 0,
    // JSON array summarizing top bloodlines engagement
    val topBloodlinesEngagement: String? = null,
    val activePairsCount: Int = 0,
    val eggsCollectedCount: Int = 0,
    val hatchingDueCount: Int = 0,
    val transfersPendingCount: Int = 0,
    // NEW: Additional Enthusiast KPIs for cache-first pattern
    val pairsToMateCount: Int = 0,        // Pairs needing mating (>7 days since last)
    val incubatingCount: Int = 0,          // Batches currently incubating
    val sickBirdsCount: Int = 0,           // Birds with health alerts
    val eggsCollectedToday: Int = 0,       // Daily egg count for quick display
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
