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
    MARKET_STAND,   // Birds listed for sale
    QUARANTINE,     // Sick or isolated birds (Hospital Area)
    MAIN_COOP       // General housing for others
}

/**
 * Time of day determines sky colors and ambient lighting
 */
enum class TimeOfDay {
    MORNING,    // 6:00 - 12:00 - warm golden light
    AFTERNOON,  // 12:00 - 18:00 - bright daylight
    EVENING,    // 18:00 - 21:00 - sunset colors
    NIGHT;      // 21:00 - 6:00 - dark blue tones

    companion object {
        /**
         * Calculates current time of day based on device time
         */
        fun fromCurrentTime(): TimeOfDay {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 6..11 -> MORNING
                in 12..17 -> AFTERNOON
                in 18..20 -> EVENING
                else -> NIGHT
            }
        }
    }
}

/**
 * Weather effects for atmospheric visuals
 */
enum class WeatherType {
    SUNNY,      // Clear sky, sun rays
    CLOUDY,     // More/darker clouds
    RAINY,      // Rain particles falling
    WINDY       // Animated grass and trees
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
    NEW_ARRIVAL,      // Sparkle effect (just added)
    READY_FOR_SALE    // Ready to be sold
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
    val isSelected: Boolean = false,
    // --- New Properties for Drag & Animation ---
    val currentAnimationTarget: Offset = Offset.Zero,
    val isDragging: Boolean = false,
    val motherId: String? = null, // For nursery orbiting
    val batchId: String? = null,  // For breeding unit grouping
    val isReadyForSale: Boolean = false,
    val isListed: Boolean = false,
    val isQuarantined: Boolean = false, // Visual Quarantine: shows warning overlay
    val metadataJson: String? = null, // Bird Studio custom appearance data
    val birdCode: String? = null // Physical ID for on-canvas display (e.g. BLK-RIR-001)
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
    val quarantine: List<VisualBird> = emptyList(),
    val mainCoop: List<VisualBird> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalBirds: Int
        get() = nurseries.sumOf { it.chicksCount + 1 } +
                breedingUnits.sumOf { (if (it.rooster != null) 1 else 0) + it.hensCount } +
                freeRange.size +
                growOut.size +
                readyDisplay.size +
                marketReady.size +
                quarantine.size +
                mainCoop.size
    
    val isEmpty: Boolean
        get() = totalBirds == 0
    
    val hasEggsToLog: Boolean
        get() = breedingUnits.any { it.showGhostEggs }

    /** Flat list of every bird across all zones — used for search/filter */
    val allBirds: List<VisualBird>
        get() = nurseries.flatMap { listOf(it.mother) + it.chicks } +
                breedingUnits.flatMap { listOfNotNull(it.rooster) + it.hens } +
                freeRange + growOut + readyDisplay + marketReady + quarantine + mainCoop

    /** Bird count per zone for zone labels */
    fun countForZone(zone: DigitalFarmZone): Int = when (zone) {
        DigitalFarmZone.NURSERY -> nurseries.sumOf { it.chicksCount + 1 }
        DigitalFarmZone.BREEDING_UNIT -> breedingUnits.sumOf { (if (it.rooster != null) 1 else 0) + it.hensCount }
        DigitalFarmZone.FREE_RANGE -> freeRange.size
        DigitalFarmZone.GROW_OUT -> growOut.size
        DigitalFarmZone.READY_DISPLAY -> readyDisplay.size
        DigitalFarmZone.MARKET_STAND -> marketReady.size
        DigitalFarmZone.QUARANTINE -> quarantine.size
        DigitalFarmZone.MAIN_COOP -> mainCoop.size
    }
}

/**
 * Farm statistics for top bar display (like coins, eggs count)
 */
data class FarmStats(
    val totalBirds: Int = 0,
    val totalEggsToday: Int = 0,
    val birdsReadyForSale: Int = 0,
    val vaccinesDueToday: Int = 0,
    val coins: Int = 0, // Gamification currency
    // Farmer persona metrics
    val totalBatches: Int = 0,
    val feedUsageKg: Float = 0f
)

/**
 * Tap action result when user interacts with the farm
 */
sealed class FarmTapResult {
    data class BirdTapped(val bird: VisualBird) : FarmTapResult()
    data class NurseryTapped(val nursery: NurseryGroup) : FarmTapResult()
    data class BreedingHutTapped(val unit: BreedingUnit) : FarmTapResult()
    data class MarketStandTapped(val birds: List<VisualBird>) : FarmTapResult()
    data class ZoneTapped(val zone: DigitalFarmZone, val birds: List<VisualBird>) : FarmTapResult()  // Lite mode
    data object EmptyAreaTapped : FarmTapResult()
}

/**
 * Drag state for the Digital Farm drag-and-drop feature
 */
data class DragState(
    val draggedBird: VisualBird? = null,
    val sourceZone: DigitalFarmZone? = null,
    val currentPosition: Offset = Offset.Zero,
    val isDragging: Boolean = false
)

/**
 * Cage display for ready-to-sell male birds with gold star overlay
 */
data class CageDisplay(
    val bird: VisualBird,
    val position: Offset,
    val showGoldStar: Boolean = true
)

/**
 * A drawable entity for Z-ordering in the renderer
 */
data class DrawableEntity(
    val id: String,
    val type: EntityType,
    val y: Float, // Used for Z-ordering (higher Y = drawn later = in front)
    val position: Offset,
    val data: Any // The actual entity data (VisualBird, BreedingUnit, etc.)
)

enum class EntityType {
    BIRD, HUT, CAGE, NEST, MARKET_STAND, FENCE, FEEDER
}

// ==================== BUILDING PLACEMENT SYSTEM ====================

/**
 * Types of buildings that can be placed on the farm
 */
enum class FarmBuildingType {
    COOP,           // Chicken coop
    BROODER,        // For chicks
    WATER_FOUNTAIN, // Drinking station
    FEEDER,         // Food dispenser
    NESTING_BOX,    // Egg laying area
    PERCH,          // Roosting perch
    DUST_BATH,      // Health/comfort
    SHADE_SHELTER   // Weather protection
}

/**
 * Represents a placeable building on the farm
 */
data class PlaceableBuilding(
    val id: String,
    val type: FarmBuildingType,
    val position: Offset,
    val rotation: Float = 0f,
    val level: Int = 1,  // Upgrade level
    val isUnlocked: Boolean = true,
    val capacity: Int = 10
)

// ==================== DAILY TASKS & GAMIFICATION ====================

/**
 * Daily task for gamification rewards
 */
enum class DailyTaskType {
    COLLECT_EGGS,       // Tap breeding huts
    FEED_BIRDS,         // Interact with feeders
    WEIGH_BIRDS,        // Check bird weights
    VACCINATE,          // Complete vaccinations
    CLEAN_COOP,         // Maintenance task
    LIST_FOR_SALE,      // Add birds to market
    COMPLETE_SALE       // Successful transaction
}

data class DailyTask(
    val id: String,
    val type: DailyTaskType,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val rewardCoins: Int,
    val isCompleted: Boolean = false,
    val expiresAt: Long = 0L
) {
    val progress: Float
        get() = (currentCount.toFloat() / targetCount).coerceIn(0f, 1f)
}

// ==================== RESOURCE MANAGEMENT ====================

/**
 * Farm resources that can be managed
 */
enum class ResourceType {
    FEED,           // Bird food
    WATER,          // Water supply
    MEDICINE,       // Health items
    BEDDING,        // Coop material
    COINS           // Virtual currency
}

data class FarmResource(
    val type: ResourceType,
    val currentAmount: Float,
    val maxCapacity: Float = 100f,
    val consumptionRate: Float = 0f, // Per hour
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val percentage: Float
        get() = (currentAmount / maxCapacity).coerceIn(0f, 1f)
    
    val isLow: Boolean
        get() = percentage < 0.25f
}

// ==================== PREDICTIVE ANALYTICS ====================

/**
 * Growth prediction data for birds
 */
data class GrowthPrediction(
    val birdId: String,
    val currentWeightGrams: Double,
    val predictedWeightIn7Days: Double,
    val daysToTargetWeight: Int,
    val growthRatePerDay: Double,
    val confidenceScore: Float = 0.85f
)

/**
 * Farm performance metrics for analytics
 */
data class FarmPerformanceMetrics(
    val eggProductionLast7Days: List<Int>,
    val averageEggsPerDay: Float,
    val hatchRate: Float,
    val mortalityRate: Float,
    val averageGrowthRate: Float,
    val sellThroughRate: Float, // Percentage of listed birds that sell
    val overallHealthScore: Float
)

// ==================== PEDIGREE TREE ====================

/**
 * Represents a node in the pedigree tree
 */
data class PedigreeNode(
    val birdId: String,
    val name: String,
    val breed: String?,
    val generation: Int, // 0 = current bird, 1 = parents, 2 = grandparents
    val parentType: ParentType = ParentType.NONE,
    val traits: List<String> = emptyList(),
    val imageUrl: String? = null
)

enum class ParentType {
    NONE, FATHER, MOTHER
}

/**
 * Complete pedigree tree structure
 */
data class PedigreeTree(
    val rootBird: PedigreeNode,
    val father: PedigreeNode? = null,
    val mother: PedigreeNode? = null,
    val paternalGrandfather: PedigreeNode? = null,
    val paternalGrandmother: PedigreeNode? = null,
    val maternalGrandfather: PedigreeNode? = null,
    val maternalGrandmother: PedigreeNode? = null
) {
    fun getAllNodes(): List<PedigreeNode> = listOfNotNull(
        rootBird, father, mother,
        paternalGrandfather, paternalGrandmother,
        maternalGrandfather, maternalGrandmother
    )
}

// ==================== INVENTORY TRACKING ====================

/**
 * Farm inventory item for tracking supplies
 */
data class FarmInventoryItem(
    val id: String,
    val name: String,
    val category: InventoryCategory,
    val currentQuantity: Float,
    val unit: String, // kg, liters, pieces
    val reorderLevel: Float,
    val lastRestocked: Long,
    val costPerUnit: Float = 0f
) {
    val needsRestock: Boolean
        get() = currentQuantity <= reorderLevel
}

enum class InventoryCategory {
    FEED, MEDICINE, EQUIPMENT, BEDDING, SUPPLEMENTS, OTHER
}

// ==================== FARM SHARING ====================

/**
 * Represents a shared farm view for other users
 */
data class SharedFarm(
    val farmId: String,
    val ownerId: String,
    val ownerName: String,
    val farmName: String,
    val description: String,
    val thumbnailUrl: String?,
    val isPublic: Boolean = true,
    val shareCode: String? = null,
    val totalBirds: Int,
    val showcaseBirds: List<VisualBird> = emptyList(),
    val reputation: Int = 0,
    val followers: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Farm visitor for tracking who viewed the farm
 */
data class FarmVisitor(
    val visitorId: String,
    val visitorName: String,
    val visitedAt: Long,
    val leftReaction: FarmReaction? = null
)

enum class FarmReaction {
    LIKE, LOVE, WOW, INTERESTED, FOLLOW
}

// ==================== COMPETITIONS ====================

/**
 * Farm competition/challenge
 */
data class FarmCompetition(
    val id: String,
    val title: String,
    val description: String,
    val type: CompetitionType,
    val startDate: Long,
    val endDate: Long,
    val prizeCoins: Int,
    val requirements: String,
    val currentRank: Int = 0,
    val totalParticipants: Int = 0,
    val isJoined: Boolean = false
) {
    val isActive: Boolean
        get() = System.currentTimeMillis() in startDate..endDate
}

enum class CompetitionType {
    BEST_BREEDER,       // Most offspring
    TOP_SELLER,         // Most sales
    HEALTHIEST_FLOCK,   // Best health scores
    EGG_CHAMPION,       // Most eggs collected
    FASTEST_GROWTH,     // Best growth rates
    SHOWCASE            // Most popular farm
}

/**
 * Challenge progress tracking
 */
data class ChallengeProgress(
    val challengeId: String,
    val targetValue: Int,
    val currentValue: Int,
    val rewardClaimed: Boolean = false
) {
    val progress: Float
        get() = (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)
    
    val isCompleted: Boolean
        get() = currentValue >= targetValue
}

/**
 * Leaderboard entry
 */
data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val userName: String,
    val farmName: String,
    val score: Int,
    val avatarUrl: String? = null,
    val isCurrentUser: Boolean = false
)

// ==================== OFFLINE CAPABILITY ====================

/**
 * Cached farm state for offline viewing
 */
data class OfflineFarmSnapshot(
    val farmState: DigitalFarmState,
    val farmStats: FarmStats,
    val capturedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000L, // 24 hours
    val isStale: Boolean = false
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt
}
