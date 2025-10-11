package com.rio.rostry.domain.model

enum class LifecycleStage(val displayName: String, val minWeeks: Int, val maxWeeks: Int?) {
    CHICK("Chick", 0, 5),
    JUVENILE("Juvenile", 5, 20),
    ADULT("Adult", 20, 52),
    BREEDER("Breeder", 52, null);

    companion object {
        fun fromWeeks(weeks: Int): LifecycleStage = when {
            weeks < 5 -> CHICK
            weeks < 20 -> JUVENILE
            weeks < 52 -> ADULT
            else -> BREEDER
        }
    }
}
