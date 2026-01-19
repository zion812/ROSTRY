package com.rio.rostry.domain.model

/**
 * Granular lifecycle stages for poultry monitoring and task generation.
 */
enum class LifecycleSubStage(val label: String, val startWeek: Int, val endWeek: Int) {
    BROODING("Brooding", 0, 4),
    GROWER_EARLY("Early Grower", 5, 12),
    GROWER_LATE("Late Grower", 13, 18),
    PRE_LAY("Pre-Lay", 19, 21),
    LAYING_PEAK("Peak Laying", 22, 40),
    LAYING_MID("Mid Laying", 41, 60),
    LAYING_LATE("Late Laying", 61, 72),
    RETIRED("Retired", 73, 999);

    companion object {
        fun fromAge(weeks: Int): LifecycleSubStage {
            return entries.find { weeks in it.startWeek..it.endWeek } ?: RETIRED
        }
    }
}
