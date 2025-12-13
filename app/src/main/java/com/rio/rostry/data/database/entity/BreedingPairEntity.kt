package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeding_pairs",
    indices = [
        Index("maleProductId"),
        Index("femaleProductId"),
        Index("farmerId"),
        Index("status")
    ]
)
data class BreedingPairEntity(
    @PrimaryKey val pairId: String,
    val farmerId: String,
    val maleProductId: String,
    val femaleProductId: String,
    val pairedAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // ACTIVE, RETIRED, SEPARATED
    val hatchSuccessRate: Double = 0.0,
    val eggsCollected: Int = 0,
    val hatchedEggs: Int = 0,
    val separatedAt: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
