package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a genetic trait that can be inherited
 */
@Entity(tableName = "genetic_traits")
data class GeneticTrait(
    @PrimaryKey
    val id: String,
    val name: String, // e.g., "Fast Growth", "Disease Resistance"
    val description: String,
    val inheritanceType: String, // DOMINANT, RECESSIVE, CODOMINANT
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)