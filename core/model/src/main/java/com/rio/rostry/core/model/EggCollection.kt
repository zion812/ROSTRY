package com.rio.rostry.core.model

/**
 * Domain model representing an egg collection record.
 * 
 * Tracks egg collection events with quality grading and hatchability tracking.
 */
data class EggCollection(
    val collectionId: String,
    val pairId: String,
    val farmerId: String,
    val eggsCollected: Int,
    val collectedAt: Long,
    val qualityGrade: String,
    val weight: Double? = null,
    val notes: String? = null,
    
    // Enhanced Egg Log Fields
    val goodCount: Int = 0,
    val damagedCount: Int = 0,
    val brokenCount: Int = 0,
    val trayLayoutJson: String? = null,
    
    // Hatchability Tracking Fields
    val setForHatching: Boolean = false,
    val linkedBatchId: String? = null,
    val setForHatchingAt: Long? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
