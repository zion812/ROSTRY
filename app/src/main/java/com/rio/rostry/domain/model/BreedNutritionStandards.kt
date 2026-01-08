package com.rio.rostry.domain.model

/**
 * Breed-specific nutrition standards based on poultry industry guidelines.
 * 
 * Feed types and protein requirements vary by:
 * 1. Lifecycle stage (Chick, Grower, Layer, Breeder)
 * 2. Breed category (Broiler, Layer, Dual-Purpose, Gamefowl)
 * 
 * Reference: Standard poultry nutrition guidelines from veterinary sources.
 */

/**
 * Categories of poultry breeds with different nutritional needs.
 */
enum class BreedCategory(val displayName: String) {
    BROILER("Broiler"),           // Meat production - needs high protein
    LAYER("Layer"),               // Egg production - needs calcium
    DUAL_PURPOSE("Dual Purpose"), // Both meat and eggs
    GAMEFOWL("Gamefowl"),         // Show/sport birds - needs conditioning feed
    ORNAMENTAL("Ornamental")      // Show birds - maintenance diet
}

/**
 * Types of poultry feed with protein content and purpose.
 */
enum class FeedType(
    val displayName: String,
    val proteinPercentMin: Double,
    val proteinPercentMax: Double,
    val description: String,
    val emoji: String
) {
    STARTER(
        displayName = "Starter",
        proteinPercentMin = 20.0,
        proteinPercentMax = 22.0,
        description = "High protein for rapid chick growth",
        emoji = "🐣"
    ),
    GROWER(
        displayName = "Grower",
        proteinPercentMin = 16.0,
        proteinPercentMax = 18.0,
        description = "Medium protein for development",
        emoji = "🐔"
    ),
    LAYER(
        displayName = "Layer",
        proteinPercentMin = 15.0,
        proteinPercentMax = 17.0,
        description = "Calcium-rich for egg production",
        emoji = "🥚"
    ),
    BREEDER(
        displayName = "Breeder",
        proteinPercentMin = 14.0,
        proteinPercentMax = 16.0,
        description = "Vitamins for fertility and breeding",
        emoji = "💕"
    ),
    CONDITIONING(
        displayName = "Conditioning",
        proteinPercentMin = 18.0,
        proteinPercentMax = 20.0,
        description = "For gamefowl training and shows",
        emoji = "💪"
    ),
    MAINTENANCE(
        displayName = "Maintenance",
        proteinPercentMin = 12.0,
        proteinPercentMax = 14.0,
        description = "Basic diet for adult birds",
        emoji = "🌾"
    )
}

/**
 * Nutrition standard for a specific lifecycle stage and breed category.
 */
data class NutritionStandard(
    val stage: LifecycleStage,
    val breedCategory: BreedCategory,
    val recommendedFeedType: FeedType,
    val dailyFeedGramsPerBird: Int,      // grams per bird per day
    val waterMlPerBird: Int,             // ml per bird per day
    val additionalNotes: String? = null
)

/**
 * Feed recommendation for a farmer's flock.
 */
data class FeedRecommendation(
    val feedType: FeedType,
    val dailyFeedKg: Double,             // Total kg for all birds
    val weeklyFeedKg: Double,            // Projected weekly need
    val proteinTarget: String,           // e.g., "18-20%"
    val birdCount: Int,
    val stage: LifecycleStage,
    val notes: String,
    val isLowInventoryAlert: Boolean = false
)

/**
 * Breed nutrition standards lookup table.
 * Industry-standard values for common poultry breeds.
 */
object BreedNutritionStandards {
    
    /**
     * Get nutrition standard for a given stage and breed category.
     */
    fun getStandard(stage: LifecycleStage, category: BreedCategory): NutritionStandard {
        return when (stage) {
            LifecycleStage.CHICK -> getChickStandard(category)
            LifecycleStage.GROWER -> getGrowerStandard(category)
            LifecycleStage.LAYER -> getLayerStandard(category)
            LifecycleStage.BREEDER -> getBreederStandard(category)
        }
    }
    
    private fun getChickStandard(category: BreedCategory) = NutritionStandard(
        stage = LifecycleStage.CHICK,
        breedCategory = category,
        recommendedFeedType = FeedType.STARTER,
        dailyFeedGramsPerBird = when (category) {
            BreedCategory.BROILER -> 35      // Broilers eat more
            BreedCategory.LAYER -> 25
            BreedCategory.DUAL_PURPOSE -> 30
            BreedCategory.GAMEFOWL -> 28
            BreedCategory.ORNAMENTAL -> 20
        },
        waterMlPerBird = 60,
        additionalNotes = "Keep brooder at 32-35°C. Change water twice daily."
    )
    
    private fun getGrowerStandard(category: BreedCategory) = NutritionStandard(
        stage = LifecycleStage.GROWER,
        breedCategory = category,
        recommendedFeedType = when (category) {
            BreedCategory.GAMEFOWL -> FeedType.CONDITIONING
            else -> FeedType.GROWER
        },
        dailyFeedGramsPerBird = when (category) {
            BreedCategory.BROILER -> 120
            BreedCategory.LAYER -> 80
            BreedCategory.DUAL_PURPOSE -> 100
            BreedCategory.GAMEFOWL -> 90
            BreedCategory.ORNAMENTAL -> 70
        },
        waterMlPerBird = 200,
        additionalNotes = "Transition gradually from starter over 3-5 days."
    )
    
    private fun getLayerStandard(category: BreedCategory) = NutritionStandard(
        stage = LifecycleStage.LAYER,
        breedCategory = category,
        recommendedFeedType = when (category) {
            BreedCategory.LAYER, BreedCategory.DUAL_PURPOSE -> FeedType.LAYER
            BreedCategory.GAMEFOWL -> FeedType.CONDITIONING
            else -> FeedType.MAINTENANCE
        },
        dailyFeedGramsPerBird = when (category) {
            BreedCategory.BROILER -> 150
            BreedCategory.LAYER -> 110
            BreedCategory.DUAL_PURPOSE -> 120
            BreedCategory.GAMEFOWL -> 100
            BreedCategory.ORNAMENTAL -> 80
        },
        waterMlPerBird = 300,
        additionalNotes = when (category) {
            BreedCategory.LAYER -> "Add oyster shell for calcium. Ensure 14-16 hrs light."
            BreedCategory.GAMEFOWL -> "Adjust conditioning based on activity level."
            else -> null
        }
    )
    
    private fun getBreederStandard(category: BreedCategory) = NutritionStandard(
        stage = LifecycleStage.BREEDER,
        breedCategory = category,
        recommendedFeedType = FeedType.BREEDER,
        dailyFeedGramsPerBird = when (category) {
            BreedCategory.BROILER -> 160
            BreedCategory.LAYER -> 120
            BreedCategory.DUAL_PURPOSE -> 130
            BreedCategory.GAMEFOWL -> 110
            BreedCategory.ORNAMENTAL -> 90
        },
        waterMlPerBird = 350,
        additionalNotes = "Add vitamin E and selenium for fertility. Separate roosters during molt."
    )
    
    /**
     * Infer breed category from breed name.
     */
    fun inferCategory(breedName: String?): BreedCategory {
        val name = breedName?.lowercase() ?: return BreedCategory.DUAL_PURPOSE
        return when {
            // Broiler breeds
            name.contains("broiler") || name.contains("cornish") || 
            name.contains("cobb") || name.contains("ross") -> BreedCategory.BROILER
            
            // Layer breeds
            name.contains("leghorn") || name.contains("rhode island") ||
            name.contains("isa brown") || name.contains("lohmann") -> BreedCategory.LAYER
            
            // Gamefowl breeds
            name.contains("aseel") || name.contains("shamo") ||
            name.contains("kelso") || name.contains("hatch") ||
            name.contains("sweater") || name.contains("roundhead") ||
            name.contains("grey") -> BreedCategory.GAMEFOWL
            
            // Ornamental breeds
            name.contains("silkie") || name.contains("polish") ||
            name.contains("sebright") || name.contains("bantam") -> BreedCategory.ORNAMENTAL
            
            // Default to dual-purpose
            else -> BreedCategory.DUAL_PURPOSE
        }
    }
}
