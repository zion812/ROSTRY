package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * DashboardCacheEntity stores pre-computed dashboard statistics.
 *
 * This cache is populated by [LifecycleWorker] running in the background
 * (e.g., daily at 2 AM), enabling instant dashboard loading without
 * expensive on-the-fly calculations.
 *
 * Part of the "Split-Brain" data architecture for efficiency.
 */
@Entity(
    tableName = "dashboard_cache",
    indices = [Index(value = ["farmerId"], unique = true)]
)
data class DashboardCacheEntity(
    @PrimaryKey val cacheId: String,
    val farmerId: String,
    
    // Pre-computed stats
    val totalBirds: Int = 0,
    val totalBatches: Int = 0,
    val pendingVaccines: Int = 0,
    val overdueVaccines: Int = 0,
    val avgFcr: Double = 0.0, // Average Feed Conversion Ratio
    val totalFeedKgThisMonth: Double = 0.0,
    val totalMortalityThisMonth: Int = 0,
    val estimatedHarvestDate: Long? = null,
    val daysUntilHarvest: Int? = null,
    
    // Health metrics
    val healthyCount: Int = 0,
    val quarantinedCount: Int = 0,
    val alertCount: Int = 0,
    
    // Computation metadata
    val computedAt: Long = System.currentTimeMillis(),
    val computationDurationMs: Long = 0
)
