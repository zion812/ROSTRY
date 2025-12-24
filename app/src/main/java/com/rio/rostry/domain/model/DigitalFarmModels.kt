package com.rio.rostry.domain.model

import androidx.compose.ui.geometry.Offset

/**
 * Digital Farm Domain Models
 * 
 * These models power the "Evolutionary Visuals" feature for Enthusiast users.
 * Each model maps to a visual zone on the Digital Farm canvas.
 */

/**
 * Lifecycle stage determines WHERE the bird appears on the farm
 * and HOW it is rendered visually.
 */
enum class DigitalFarmZone {
    NURSERY,        // Mother hen with orbiting chicks (0-4 weeks)
    FREE_RANGE,     // Young birds wandering in grass (4-12 weeks)
    GROW_OUT,       // Medium birds in fenced area (3-6 months)
    BREEDING_UNIT,  // Hut with rooster + hens + eggs
    READY_DISPLAY,  // Champion rooster with gold star
    MARKET_STAND    // Birds listed for sale
}

/**
 * Visual status indicators rendered as floating icons
 */
enum class BirdStatusIndicator {
    NONE,
    VACCINE_DUE,      // Red syringe icon
    WEIGHT_READY,     // Gold star badge
    WEIGHT_WARNING,   // Yellow warning (below target)
    SICK,             // Red cross
    NEW_ARRIVAL       // Sparkle effect (just added)
}

/**
 * Represents a single bird's visual state on the farm
 */
data class VisualBird(
    val productId: String,
    val name: String,
    val breed: String?,
    val gender: String?,
    val ageWeeks: Int,
    val weightGrams: Double?,
    val color: String?,
    val zone: DigitalFarmZone,
    val statusIndicator: BirdStatusIndicator = BirdStatusIndicator.NONE,
    val position: Offset = Offset.Zero, // Calculated position on canvas
    val isSelected: Boolean = false
) {
    val ageText: String
        get() = when {
            ageWeeks < 1 -> "${ageWeeks * 7} days"
            ageWeeks < 4 -> "$ageWeeks weeks"
            else -> "${ageWeeks / 4} months"
        }
    
    val weightText: String?
        get() = weightGrams?.let { "${it / 1000}kg" }
}

/**
 * Nursery zone: Mother hen with her chicks
 */
data class NurseryGroup(
    val mother: VisualBird,
    val chicks: List<VisualBird>,
    val nestPosition: Offset = Offset.Zero
) {
    val chicksCount: Int get() = chicks.size
    val ageText: String get() = chicks.firstOrNull()?.ageText ?: "New"
}

/**
 * Breeding unit: Rooster + hens in a hut with eggs
 */
data class BreedingUnit(
    val unitId: String,
    val rooster: VisualBird?,
    val hens: List<VisualBird>,
    val eggsCollectedToday: Int,
    val lastEggLogDate: Long?,
    val hutPosition: Offset = Offset.Zero
) {
    val hensCount: Int get() = hens.size
    val showGhostEggs: Boolean
        get() {
            val now = System.currentTimeMillis()
            val today = now / (24 * 60 * 60 * 1000)
            val lastLog = lastEggLogDate?.let { it / (24 * 60 * 60 * 1000) } ?: 0
            val currentHour = ((now % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)).toInt()
            
            // Show ghost eggs if: after 10 AM AND eggs not logged today
            return currentHour >= 10 && today != lastLog
        }
}

/**
 * Complete farm state organized by zones
 */
data class DigitalFarmState(
    val nurseries: List<NurseryGroup> = emptyList(),
    val breedingUnits: List<BreedingUnit> = emptyList(),
    val freeRange: List<VisualBird> = emptyList(),
    val growOut: List<VisualBird> = emptyList(),
    val readyDisplay: List<VisualBird> = emptyList(),
    val marketReady: List<VisualBird> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalBirds: Int
        get() = nurseries.sumOf { it.chicksCount + 1 } +
                breedingUnits.sumOf { (if (it.rooster != null) 1 else 0) + it.hensCount } +
                freeRange.size +
                growOut.size +
                readyDisplay.size +
                marketReady.size
    
    val isEmpty: Boolean
        get() = totalBirds == 0
    
    val hasEggsToLog: Boolean
        get() = breedingUnits.any { it.showGhostEggs }
}

/**
 * Farm statistics for top bar display (like coins, eggs count)
 */
data class FarmStats(
    val totalBirds: Int = 0,
    val totalEggsToday: Int = 0,
    val birdsReadyForSale: Int = 0,
    val vaccinesDueToday: Int = 0,
    val coins: Int = 0 // Gamification currency
)

/**
 * Tap action result when user interacts with the farm
 */
sealed class FarmTapResult {
    data class BirdTapped(val bird: VisualBird) : FarmTapResult()
    data class NurseryTapped(val nursery: NurseryGroup) : FarmTapResult()
    data class BreedingHutTapped(val unit: BreedingUnit) : FarmTapResult()
    data class MarketStandTapped(val birds: List<VisualBird>) : FarmTapResult()
    data object EmptyAreaTapped : FarmTapResult()
}
