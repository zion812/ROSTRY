package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * ClutchEntity - Represents a clutch (batch) of eggs from a breeding pair.
 * 
 * Tracks:
 * - Egg collection and setting dates
 * - Incubation progress (fertility, development)
 * - Hatching results (hatchability, chick quality)
 * - Links to parent BreedingPairEntity and offspring ProductEntity
 * 
 * This formalizes breeding tracking beyond the existing EggCollectionEntity
 * by providing a complete lifecycle view from lay to hatch.
 */
@Entity(
    tableName = "clutches",
    foreignKeys = [
        ForeignKey(
            entity = BreedingPairEntity::class,
            parentColumns = ["pairId"],
            childColumns = ["breedingPairId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("breedingPairId"),
        Index("farmerId"),
        Index("status"),
        Index("setDate"),
        Index("expectedHatchDate")
    ]
)
data class ClutchEntity(
    @PrimaryKey val clutchId: String = UUID.randomUUID().toString(),
    
    // Parent reference (can be null if parents unknown)
    val breedingPairId: String? = null,
    
    // Owner/farm reference
    val farmerId: String,
    
    // Optional specific parent IDs (for tracking if pair not used)
    val sireId: String? = null,
    val damId: String? = null,
    
    // Clutch identification
    val clutchName: String? = null,             // e.g., "Batch A - Feb 2026"
    val clutchNumber: Int? = null,              // Sequential number for this pair
    
    // Egg collection
    val eggsCollected: Int = 0,                 // Total eggs collected
    val collectionStartDate: Long,              // First egg collected
    val collectionEndDate: Long? = null,        // Last egg collected
    
    // Setting/Incubation
    val setDate: Long? = null,                  // When eggs were set in incubator
    val eggsSet: Int = 0,                       // Eggs set for incubation (may differ from collected)
    val incubatorId: String? = null,            // Reference to incubator device/location
    val incubationNotes: String? = null,
    
    // Candling results
    val firstCandleDate: Long? = null,          // Day 7-10 candling
    val firstCandleFertile: Int = 0,            // Fertile eggs at first candle
    val firstCandleClear: Int = 0,              // Clear/infertile eggs
    val firstCandleEarlyDead: Int = 0,          // Early dead embryos
    
    val secondCandleDate: Long? = null,         // Day 14-18 candling
    val secondCandleAlive: Int = 0,             // Alive at second candle
    val secondCandleDead: Int = 0,              // Dead since first candle
    
    // Hatching
    val expectedHatchDate: Long? = null,        // Calculated from set date + breed days
    val actualHatchStartDate: Long? = null,     // First pip/hatch
    val actualHatchEndDate: Long? = null,       // Last hatch
    val chicksHatched: Int = 0,                 // Successfully hatched
    val chicksMale: Int = 0,                    // If sexed at hatch
    val chicksFemale: Int = 0,                  // If sexed at hatch
    val chicksUnsexed: Int = 0,                 // Unsexed at hatch
    val deadInShell: Int = 0,                   // Developed but didn't hatch
    val pippedNotHatched: Int = 0,              // Pipped but died
    
    // Quality metrics
    val averageChickWeight: Double? = null,     // Grams
    val chickQualityScore: Int? = null,         // 1-5 rating
    val qualityNotes: String? = null,
    
    // Calculated metrics (updated on save)
    val fertilityRate: Double? = null,          // (fertile / set) * 100
    val hatchabilityOfFertile: Double? = null,  // (hatched / fertile) * 100  
    val hatchabilityOfSet: Double? = null,      // (hatched / set) * 100
    
    // Status
    val status: String = "COLLECTING",          // COLLECTING, SET, INCUBATING, HATCHING, COMPLETE, FAILED
    
    // JSON arrays for offspring tracking
    val offspringIdsJson: String? = null,       // JSON array of ProductEntity IDs for hatched chicks
    
    // Notes and media
    val notes: String? = null,
    val mediaUrlsJson: String? = null,
    
    // Audit
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
) {
    /**
     * Calculate fertility rate from candling data.
     */
    fun calculateFertilityRate(): Double? {
        if (eggsSet <= 0) return null
        val fertile = firstCandleFertile
        return (fertile.toDouble() / eggsSet) * 100
    }
    
    /**
     * Calculate hatchability of fertile eggs.
     */
    fun calculateHatchabilityOfFertile(): Double? {
        if (firstCandleFertile <= 0) return null
        return (chicksHatched.toDouble() / firstCandleFertile) * 100
    }
    
    /**
     * Calculate overall hatchability.
     */
    fun calculateHatchabilityOfSet(): Double? {
        if (eggsSet <= 0) return null
        return (chicksHatched.toDouble() / eggsSet) * 100
    }
    
    /**
     * Get days in incubation.
     */
    fun daysInIncubation(): Int? {
        val start = setDate ?: return null
        val end = actualHatchEndDate ?: System.currentTimeMillis()
        return ((end - start) / (1000 * 60 * 60 * 24)).toInt()
    }
}

/**
 * Status progression for a clutch.
 */
enum class ClutchStatus {
    COLLECTING,     // Eggs being collected
    SET,            // Eggs placed in incubator
    INCUBATING,     // Active incubation (after first candle)
    HATCHING,       // Hatch in progress
    COMPLETE,       // Hatch finished, chicks moved
    FAILED          // Clutch failed (all died, abandoned, etc.)
}
