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
    val maleProductId: String,
    val femaleProductId: String,
    val farmerId: String,
    val status: String,
    val pairedAt: Long,
    val hatchSuccessRate: Double? = null,
    val eggsCollected: Int? = null,
    val hatchedEggs: Int? = null,
    val separatedAt: Long? = null,
    val notes: String? = null,
    val dirty: Boolean = false,
    val syncedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)