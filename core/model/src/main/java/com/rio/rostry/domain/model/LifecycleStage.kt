package com.rio.rostry.domain.model

/**
 * Lifecycle stages for poultry management.
 * - CHICK: 0-8 weeks (Nursery phase)
 * - GROWER: 8-18 weeks (Development phase)
 * - LAYER: 18-52 weeks (Egg production phase)
 * - BREEDER: 52+ weeks (Breeding phase)
 */
enum class LifecycleStage(val displayName: String, val minWeeks: Int, val maxWeeks: Int?) {
    CHICK("Chick", 0, 8),
    GROWER("Grower", 8, 18),
    LAYER("Layer", 18, 52),
    BREEDER("Breeder", 52, null);

    companion object {
        fun fromWeeks(weeks: Int): LifecycleStage = when {
            weeks < 8 -> CHICK
            weeks < 18 -> GROWER
            weeks < 52 -> LAYER
            else -> BREEDER
        }
    }
}
