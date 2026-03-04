package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * BatchSummaryEntity is a lightweight summary of a farmer's batch/flock.
 *
 * This is the ONLY entity synced to Firestore for farm monitoring,
 * as part of the "Split-Brain" data architecture. Raw logs stay local.
 *
 * Updated by [LifecycleWorker] from aggregate queries on daily logs.
 */
@Entity(
    tableName = "batch_summaries",
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["updatedAt"]),
        Index(value = ["dirty"])
    ]
)
data class BatchSummaryEntity(
    @PrimaryKey val batchId: String,
    val farmerId: String,
    val batchName: String,
    
    // Aggregate metrics (computed by LifecycleWorker)
    val currentCount: Int = 0,
    val avgWeightGrams: Double = 0.0,
    val totalFeedKg: Double = 0.0,
    val fcr: Double = 0.0, // Feed Conversion Ratio
    val ageWeeks: Int = 0,
    val hatchDate: Long? = null,
    
    // Status
    val status: String = "ACTIVE", // ACTIVE, SOLD, HARVEST
    
    // Sync metadata (this entity DOES sync to Firestore)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null
) {
    // No-arg constructor for Firestore deserialization
    constructor() : this(
        batchId = "",
        farmerId = "",
        batchName = ""
    )
}
