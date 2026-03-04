package com.rio.rostry.domain.model

/**
 * Classification of birds by their raising purpose.
 *
 * Determines marketplace visibility, documentation requirements, and pricing expectations:
 * - MEAT: Lower cost, minimal documentation needed. Buyer wants fresh native breed nearby.
 * - ADOPTION: Higher value, full documentation required. Buyer wants pedigree, health, FCR, lineage.
 * - BREEDING: Kept for the farmer's own breeding program (not typically listed).
 * - EGG_PRODUCTION: Layer birds kept primarily for egg revenue.
 */
enum class BirdPurpose(
    val displayName: String,
    val requiresFullDocumentation: Boolean,
    val description: String
) {
    MEAT(
        displayName = "For Meat",
        requiresFullDocumentation = false,
        description = "Raised for consumption — native breed, backyard-raised, fresh"
    ),
    ADOPTION(
        displayName = "For Adoption",
        requiresFullDocumentation = true,
        description = "High-value bird with full documentation — pedigree, health, FCR, lineage"
    ),
    BREEDING(
        displayName = "For Breeding",
        requiresFullDocumentation = true,
        description = "Kept for the breeding program — requires lineage and health records"
    ),
    EGG_PRODUCTION(
        displayName = "For Eggs",
        requiresFullDocumentation = false,
        description = "Layer birds raised primarily for egg production"
    );

    companion object {
        fun fromString(value: String?): BirdPurpose? =
            entries.find { it.name.equals(value, ignoreCase = true) }
    }
}
