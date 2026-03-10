package com.rio.rostry.core.model

/**
 * Domain model for breeding pairs.
 */
data class BreedingPair(
    val pairId: String,
    val farmerId: String,
    val maleProductId: String,
    val femaleProductId: String,
    val pairedAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE",
    val hatchSuccessRate: Double = 0.0,
    val eggsCollected: Int = 0,
    val hatchedEggs: Int = 0,
    val separatedAt: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
