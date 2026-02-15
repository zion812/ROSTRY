package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * BirdTraitRecordEntity — Phenotypic trait measurements per bird.
 *
 * This is the foundation for real genetic selection and breeding value calculation.
 * Tracks physical, behavioral, and quality traits at specific ages to enable:
 * - Trait inheritance analysis across generations
 * - Breeding Value Index (BVI) computation
 * - Breed standard deviation comparison
 * - Generation-over-generation improvement tracking
 *
 * Trait categories:
 * - PHYSICAL: body_weight, shank_length, comb_type, plumage_color, plumage_pattern, eye_color, leg_color
 * - BEHAVIORAL: aggression, alertness, stamina, brooding_tendency, trainability
 * - PRODUCTION: eggs_per_month, egg_weight, fertility_rate, hatch_rate
 * - QUALITY: conformation_score, feather_quality, overall_breeder_rating, show_readiness
 */
@Entity(
    tableName = "bird_trait_records",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("productId"),
        Index("ownerId"),
        Index("traitCategory"),
        Index("traitName"),
        Index("recordedAt"),
        Index(value = ["productId", "traitName", "ageWeeks"])
    ]
)
data class BirdTraitRecordEntity(
    @PrimaryKey val recordId: String = UUID.randomUUID().toString(),
    
    // Bird reference (ProductEntity.productId)
    val productId: String,
    
    // Owner for farm-level queries
    val ownerId: String,
    
    // Trait classification
    val traitCategory: String,      // PHYSICAL, BEHAVIORAL, PRODUCTION, QUALITY
    val traitName: String,          // e.g. "body_weight", "aggression", "comb_type", "eggs_per_month"
    
    // Trait value — flexible string to handle numerics, enums, and text
    val traitValue: String,         // e.g. "2500", "8", "PEA", "BLACK_BREASTED_RED"
    val traitUnit: String? = null,  // "grams", "cm", "score_1_10", null for enums
    
    // Numeric value for aggregation (parsed from traitValue when applicable)
    val numericValue: Double? = null,
    
    // Age context — critical for growth tracking
    val ageWeeks: Int? = null,      // Age at measurement (4w, 8w, 16w, 24w, 52w milestone)
    
    // Measurement context
    val recordedAt: Long = System.currentTimeMillis(),
    val measuredBy: String? = null,  // Who took the measurement
    val notes: String? = null,
    
    // Audit
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null
) {
    companion object {
        // Trait categories
        const val CATEGORY_PHYSICAL = "PHYSICAL"
        const val CATEGORY_BEHAVIORAL = "BEHAVIORAL"
        const val CATEGORY_PRODUCTION = "PRODUCTION"
        const val CATEGORY_QUALITY = "QUALITY"
        
        // Common physical traits
        const val TRAIT_BODY_WEIGHT = "body_weight"
        const val TRAIT_SHANK_LENGTH = "shank_length"
        const val TRAIT_COMB_TYPE = "comb_type"
        const val TRAIT_PLUMAGE_COLOR = "plumage_color"
        const val TRAIT_PLUMAGE_PATTERN = "plumage_pattern"
        const val TRAIT_EYE_COLOR = "eye_color"
        const val TRAIT_LEG_COLOR = "leg_color"
        const val TRAIT_WINGSPAN = "wingspan"
        
        // Common behavioral traits
        const val TRAIT_AGGRESSION = "aggression"
        const val TRAIT_ALERTNESS = "alertness"
        const val TRAIT_STAMINA = "stamina"
        const val TRAIT_BROODING_TENDENCY = "brooding_tendency"
        
        // Common production traits
        const val TRAIT_EGGS_PER_MONTH = "eggs_per_month"
        const val TRAIT_EGG_WEIGHT = "egg_weight"
        const val TRAIT_FERTILITY_RATE = "fertility_rate"
        const val TRAIT_HATCH_RATE = "hatch_rate"
        
        // Common quality traits
        const val TRAIT_CONFORMATION = "conformation_score"
        const val TRAIT_FEATHER_QUALITY = "feather_quality"
        const val TRAIT_BREEDER_RATING = "overall_breeder_rating"
        const val TRAIT_SHOW_READINESS = "show_readiness"
        
        // Comb types
        val COMB_TYPES = listOf("SINGLE", "PEA", "ROSE", "WALNUT", "BUTTERCUP", "V_SHAPED", "CUSHION", "STRAWBERRY")
        
        // Standard measurement milestones (weeks)
        val AGE_MILESTONES = listOf(4, 8, 12, 16, 20, 24, 36, 52)
    }
}
