package com.rio.rostry.domain.model

/**
 * Digital Farm Mode - Determines the visual lens and behavior of the 2.5D Digital Farm.
 * 
 * FARMER mode emphasizes operational flock management with batch-level views.
 * ENTHUSIAST mode emphasizes individual champions, breeding pairs, and genetics.
 */
enum class DigitalFarmMode {
    FARMER,     // Operational flock view
    ENTHUSIAST  // Showroom + genetics lab
}

/**
 * Overlay types that can be rendered on top of farm entities.
 */
enum class DigitalFarmOverlay {
    BLOODLINE_BADGE,     // Top-performing bloodline indicator
    TRANSFER_INDICATOR,  // Pending/verified transfer status
    HEALTH_ALERT,        // Sick or vaccine-due alert
    GOLD_STAR,           // Ready for sale/showcase
    GHOST_EGGS           // Eggs not logged today reminder
}

/**
 * Actions available when tapping farm entities.
 */
enum class FarmTapAction {
    VIEW_DETAILS,
    LIST_FOR_SALE,
    OPEN_PEDIGREE,
    OPEN_ROOSTER_CARD,
    LOG_EGGS,
    CREATE_PAIR,
    START_TRANSFER
}

/**
 * Metrics slot identifiers for the stats bar.
 */
enum class FarmMetricSlot {
    TOTAL_BIRDS,
    BATCHES,
    READY_FOR_SALE,
    FEED_USAGE,
    ACTIVE_PAIRS,
    EGGS_TODAY,
    INCUBATING,
    TOP_BLOODLINE,
    HATCH_RATE
}

/**
 * Render rate for the Digital Farm canvas.
 * STATIC: Only re-render on data changes (battery/CPU efficient).
 * DYNAMIC: Continuous 60fps animation loop (premium experience).
 */
enum class RenderRate {
    STATIC,   // Render only on data change or interaction
    DYNAMIC   // Continuous animation loop (60fps)
}

/**
 * Grouping mode for bird visualization.
 * BY_BATCH: Render one avatar per batch with quantity badge (Lite).
 * INDIVIDUAL: Render each bird separately (Premium).
 */
enum class GroupingMode {
    BY_BATCH,    // 1 Avatar = N birds (e.g., "x50" badge)
    INDIVIDUAL   // Each bird rendered separately
}

/**
 * Configuration for persona-specific Digital Farm behavior.
 * 
 * This class controls:
 * - Which zones are emphasized/visible
 * - Which overlays are rendered
 * - What actions are available per entity type
 * - What metrics appear in the stats bar
 */
data class DigitalFarmConfig(
    val mode: DigitalFarmMode,
    val visibleZones: Set<DigitalFarmZone>,
    val enabledOverlays: Set<DigitalFarmOverlay>,
    val birdTapActions: List<FarmTapAction>,
    val breedingHutTapActions: List<FarmTapAction>,
    val metricsBar: List<FarmMetricSlot>,
    // Performance flags (Lite Mode support)
    val enableParticles: Boolean = true,
    val enableDayNightCycle: Boolean = true,
    val enableFlocking: Boolean = true,
    val renderRate: RenderRate = RenderRate.DYNAMIC,
    val groupingMode: GroupingMode = GroupingMode.INDIVIDUAL
) {
    companion object {
        /**
         * Farmer configuration: Operational flock management.
         * Emphasizes batch counts, age groups, and ready-for-sale pipeline.
         */
        val FARMER = DigitalFarmConfig(
            mode = DigitalFarmMode.FARMER,
            visibleZones = setOf(
                DigitalFarmZone.NURSERY,
                DigitalFarmZone.FREE_RANGE,
                DigitalFarmZone.GROW_OUT,
                DigitalFarmZone.READY_DISPLAY,
                DigitalFarmZone.MARKET_STAND
            ),
            enabledOverlays = setOf(
                DigitalFarmOverlay.GOLD_STAR,
                DigitalFarmOverlay.GHOST_EGGS,
                DigitalFarmOverlay.HEALTH_ALERT
            ),
            birdTapActions = listOf(
                FarmTapAction.VIEW_DETAILS,
                FarmTapAction.LIST_FOR_SALE
            ),
            breedingHutTapActions = listOf(
                FarmTapAction.LOG_EGGS,
                FarmTapAction.VIEW_DETAILS
            ),
            metricsBar = listOf(
                FarmMetricSlot.TOTAL_BIRDS,
                FarmMetricSlot.BATCHES,
                FarmMetricSlot.READY_FOR_SALE,
                FarmMetricSlot.FEED_USAGE
            ),
            // Lite Mode: Optimized for low-end devices
            enableParticles = false,
            enableDayNightCycle = false,
            enableFlocking = false,
            renderRate = RenderRate.STATIC,
            groupingMode = GroupingMode.BY_BATCH
        )

        /**
         * Enthusiast configuration: Showroom + genetics lab.
         * Emphasizes individual champions, breeding pairs, and bloodlines.
         */
        val ENTHUSIAST = DigitalFarmConfig(
            mode = DigitalFarmMode.ENTHUSIAST,
            visibleZones = setOf(
                DigitalFarmZone.BREEDING_UNIT,
                DigitalFarmZone.READY_DISPLAY,
                DigitalFarmZone.NURSERY,
                DigitalFarmZone.MARKET_STAND
            ),
            enabledOverlays = setOf(
                DigitalFarmOverlay.BLOODLINE_BADGE,
                DigitalFarmOverlay.TRANSFER_INDICATOR,
                DigitalFarmOverlay.GOLD_STAR,
                DigitalFarmOverlay.HEALTH_ALERT,
                DigitalFarmOverlay.GHOST_EGGS
            ),
            birdTapActions = listOf(
                FarmTapAction.OPEN_ROOSTER_CARD,
                FarmTapAction.OPEN_PEDIGREE,
                FarmTapAction.START_TRANSFER
            ),
            breedingHutTapActions = listOf(
                FarmTapAction.LOG_EGGS,
                FarmTapAction.CREATE_PAIR,
                FarmTapAction.VIEW_DETAILS
            ),
            metricsBar = listOf(
                FarmMetricSlot.ACTIVE_PAIRS,
                FarmMetricSlot.EGGS_TODAY,
                FarmMetricSlot.INCUBATING,
                FarmMetricSlot.HATCH_RATE
            )
        )

        /**
         * Get the appropriate configuration based on user role.
         */
        fun forRole(role: com.rio.rostry.domain.model.UserType): DigitalFarmConfig {
            return when (role) {
                UserType.ENTHUSIAST -> ENTHUSIAST
                UserType.FARMER -> FARMER
                else -> FARMER // Default to Farmer for GENERAL/ADMIN
            }
        }
    }
}
