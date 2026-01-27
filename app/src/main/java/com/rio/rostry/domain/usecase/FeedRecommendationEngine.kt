package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.BreedCategory
import com.rio.rostry.domain.model.BreedNutritionStandards
import com.rio.rostry.domain.model.FeedRecommendation
import com.rio.rostry.domain.model.FeedType
import com.rio.rostry.domain.model.LifecycleStage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine for calculating feed recommendations based on flock composition.
 * 
 * Uses breed-specific nutrition standards to determine:
 * - Recommended feed type (Starter, Grower, Layer, etc.)
 * - Daily and weekly feed requirements
 * - Protein targets
 * - Low inventory alerts
 */
@Singleton
class FeedRecommendationEngine @Inject constructor() {

    /**
     * Calculate feed recommendation for a list of birds/batches.
     * Groups by lifecycle stage and returns the primary recommendation.
     */
    fun calculateRecommendation(
        products: List<ProductEntity>,
        availableInventoryKg: Double? = null
    ): FeedRecommendation? {
        if (products.isEmpty()) return null
        
        // Group birds by lifecycle stage
        val stageGroups = products.groupBy { product ->
            product.stage ?: LifecycleStage.fromWeeks(product.ageWeeks ?: 0)
        }
        
        // Find the largest group (most birds at same stage)
        val primaryGroup = stageGroups.maxByOrNull { (_, birds) ->
            birds.sumOf { it.quantity?.toInt() ?: 1 }
        } ?: return null
        
        val stage = primaryGroup.key
        val birdsAtStage = primaryGroup.value
        
        // Calculate total bird count (including batch quantities)
        val totalBirdCount = birdsAtStage.sumOf { product ->
            product.quantity?.toInt() ?: 1
        }
        
        // Determine primary breed category (most common in flock)
        val breedCounts = birdsAtStage.groupBy { product ->
            BreedNutritionStandards.inferCategory(product.breed)
        }.mapValues { (_, birds) -> 
            birds.sumOf { it.quantity?.toInt() ?: 1 }
        }
        
        val primaryCategory = breedCounts.maxByOrNull { it.value }?.key 
            ?: BreedCategory.DUAL_PURPOSE
            
        // Gender Analysis for Rooster logic
        var maleCount = 0
        var femaleCount = 0
        birdsAtStage.forEach { product ->
            val qty = product.quantity?.toInt() ?: 1
            when (product.gender?.lowercase()) {
                "male" -> maleCount += qty
                "female" -> femaleCount += qty
                else -> {
                    // Start guessing gender based on name if not set
                    if (product.name.contains("rooster", true) || product.name.contains("cock", true) || product.name.contains("male", true)) {
                        maleCount += qty
                    } else {
                        // Default to female/unknown bucket if we can't be sure
                        femaleCount += qty 
                    }
                }
            }
        }
        val isRoosterFlock = maleCount > femaleCount
        
        // Get nutrition standard for this stage and category
        var standard = BreedNutritionStandards.getStandard(stage, primaryCategory)
        
        // --- LOGIC FIX: Gender Awareness ---
        // If this is a "Layer" stage flock but predominantly Male (Roosters), 
        // they should NOT eat Layer feed (too much calcium).
        var feedOverride: FeedType? = null
        val extraNotes = mutableListOf<String>()
        
        if (stage == LifecycleStage.LAYER && isRoosterFlock) {
            feedOverride = if (primaryCategory == BreedCategory.GAMEFOWL) {
                FeedType.CONDITIONING
            } else {
                FeedType.MAINTENANCE
            }
            extraNotes.add("🐓 Rooster Alert: Recommending ${feedOverride.displayName} Feed instead of Layer Feed to avoid excess calcium.")
        }
        
        // Apply override if needed
        val finalFeedType = feedOverride ?: standard.recommendedFeedType
        // -----------------------------------
        
        // Calculate daily and weekly feed needs
        val dailyFeedGrams = standard.dailyFeedGramsPerBird * totalBirdCount
        val dailyFeedKg = dailyFeedGrams / 1000.0
        val weeklyFeedKg = dailyFeedKg * 7
        
        // Check for low inventory
        val isLowInventory = availableInventoryKg?.let { it < weeklyFeedKg } ?: false
        
        // Build notes with Aseel-specific advice if applicable
        val notes = buildNotes(stage, primaryCategory, standard.additionalNotes, extraNotes)
        
        return FeedRecommendation(
            feedType = finalFeedType,
            dailyFeedKg = dailyFeedKg,
            weeklyFeedKg = weeklyFeedKg,
            proteinTarget = "${finalFeedType.proteinPercentMin.toInt()}-${finalFeedType.proteinPercentMax.toInt()}%",
            birdCount = totalBirdCount,
            stage = stage,
            notes = notes,
            isLowInventoryAlert = isLowInventory,
            daysRemaining = if (availableInventoryKg != null && dailyFeedKg > 0) (availableInventoryKg / dailyFeedKg).toInt() else null,
            inventoryKg = availableInventoryKg
        )
    }
    
    /**
     * Calculate feed recommendation for a single stage with known bird count.
     */
    fun calculateForStage(
        stage: LifecycleStage,
        birdCount: Int,
        breedName: String? = null,
        availableInventoryKg: Double? = null
    ): FeedRecommendation {
        val category = BreedNutritionStandards.inferCategory(breedName)
        val standard = BreedNutritionStandards.getStandard(stage, category)
        
        val dailyFeedGrams = standard.dailyFeedGramsPerBird * birdCount
        val dailyFeedKg = dailyFeedGrams / 1000.0
        val weeklyFeedKg = dailyFeedKg * 7
        
        val isLowInventory = availableInventoryKg?.let { it < weeklyFeedKg } ?: false
        
        return FeedRecommendation(
            feedType = standard.recommendedFeedType,
            dailyFeedKg = dailyFeedKg,
            weeklyFeedKg = weeklyFeedKg,
            proteinTarget = "${standard.recommendedFeedType.proteinPercentMin.toInt()}-${standard.recommendedFeedType.proteinPercentMax.toInt()}%",
            birdCount = birdCount,
            stage = stage,
            notes = buildNotes(stage, category, standard.additionalNotes),
            isLowInventoryAlert = isLowInventory,
            daysRemaining = if (availableInventoryKg != null && dailyFeedKg > 0) (availableInventoryKg / dailyFeedKg).toInt() else null,
            inventoryKg = availableInventoryKg
        )
    }
    
    private fun buildNotes(
        stage: LifecycleStage,
        category: BreedCategory,
        standardNotes: String?,
        extraNotes: List<String> = emptyList()
    ): String {
        val notes = mutableListOf<String>()
        
        // Add dynamic extra notes (e.g., Rooster overrides)
        notes.addAll(extraNotes)
        
        // Aseel-specific advice (primary breed for the user)
        if (category == BreedCategory.GAMEFOWL) {
            notes.add(when (stage) {
                LifecycleStage.CHICK -> "🐣 Aseel chicks need warmth and high-protein starter for strong bone development."
                LifecycleStage.GROWER -> "💪 Transition to conditioning feed. Add greens and exercise for muscle development."
                LifecycleStage.LAYER -> "⚔️ Keep Aseel males separated. Conditioning feed with supplements for peak form."
                LifecycleStage.BREEDER -> "💕 Breeding Aseels need vitamin E, selenium. Pair carefully for bloodline purity."
            })
        }
        
        standardNotes?.let { notes.add(it) }
        
        return notes.joinToString(" ")
    }
    
    /**
     * Get feed type emoji for UI display.
     */
    fun getFeedEmoji(feedType: FeedType): String = feedType.emoji
    
    /**
     * Format feed amount for display (e.g., "5.2 kg" or "500 g").
     */
    fun formatAmount(kg: Double): String {
        return if (kg >= 1.0) {
            String.format("%.1f kg", kg)
        } else {
            String.format("%.0f g", kg * 1000)
        }
    }
}
