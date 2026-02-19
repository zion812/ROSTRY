package com.rio.rostry.domain.digitaltwin.lifecycle

/**
 * 🧬 AgeProfile — Core identity of a bird's biological age
 *
 * Connects calendar age to biological maturity, enabling
 * age-bound morphological transformations.
 *
 * maturityIndex = ageInDays / fullMaturityDays
 * For Aseel: fullMaturityDays ≈ 365-420
 */
data class AgeProfile(
    /** Age in days from hatch (0 = hatch day) */
    val ageInDays: Int = 0,

    /** Current biological stage */
    val stage: BiologicalStage = BiologicalStage.EGG,

    /**
     * Maturity index: 0.0 (hatchling) → 1.0 (fully mature adult)
     * Continues past 1.0 for senior (slight decline effects)
     */
    val maturityIndex: Float = 0f,

    /** Gender affects morph curves (males develop hackles, spurs, sickles) */
    val isMale: Boolean = true,

    /** Growth mode: Manual (user jumps stages) or Auto (daily progression) */
    val growthMode: GrowthMode = GrowthMode.AUTO_BIOLOGICAL
) {
    companion object {
        /** Full maturity days for Aseel breed */
        const val ASEEL_FULL_MATURITY_DAYS = 390 // ~13 months

        /**
         * Create AgeProfile from age in days.
         */
        fun fromDays(days: Int, isMale: Boolean = true, growthMode: GrowthMode = GrowthMode.AUTO_BIOLOGICAL): AgeProfile {
            val stage = BiologicalStage.fromAgeDays(days, isMale)
            val maturityIndex = (days.toFloat() / ASEEL_FULL_MATURITY_DAYS).coerceAtLeast(0f)
            return AgeProfile(
                ageInDays = days,
                stage = stage,
                maturityIndex = maturityIndex,
                isMale = isMale,
                growthMode = growthMode
            )
        }

        /**
         * Create AgeProfile from weeks.
         */
        fun fromWeeks(weeks: Int, isMale: Boolean = true): AgeProfile {
            return fromDays(weeks * 7, isMale)
        }
    }
}

/**
 * Growth mode determines how morphology updates.
 */
enum class GrowthMode {
    /** Morphs update automatically based on age progression */
    AUTO_BIOLOGICAL,

    /** User manually selects stage, morphs snap to stage defaults */
    MANUAL_STAGE,

    /** User has full control over all sliders regardless of age */
    MANUAL_FREE
}

/**
 * 🐣 8-Stage Biological Lifecycle for Visual Morphology
 *
 * Each stage defines the biological appearance progression:
 * ```
 * EGG → HATCHLING → CHICK → GROWER → SUB_ADULT → ADULT → MATURE_ADULT → SENIOR
 * ```
 */
enum class BiologicalStage(
    val displayName: String,
    val teluguName: String,
    val emoji: String,
    val minDays: Int,
    val maxDays: Int?,
    val stageIndex: Int
) {
    /** 0-21 days incubation. Static egg visual only. */
    EGG("Egg", "గుడ్డు", "🥚", 0, 21, 0),

    /** 0-7 days post-hatch. Oversized head, puffy down, no comb. */
    HATCHLING("Hatchling", "పొదగు పిల్ల", "🐣", 0, 7, 1),

    /** 1-6 weeks. Legs grow faster, first feathers, small tail stub. */
    CHICK("Chick", "పిల్ల", "🐥", 7, 42, 2),

    /** 6-16 weeks. Awkward proportions, patchy feathers, no spurs. */
    GROWER("Grower", "పెరుగుదల", "🐤", 42, 112, 3),

    /** 4-8 months. Sexual dimorphism starts. Hackles, sickles forming. */
    SUB_ADULT("Sub-Adult", "ముందు-పెద్ద", "🐔", 112, 240, 4),

    /** 8-12 months. Full structure visible. Strong sickles, deep chest. */
    ADULT("Adult", "పెద్ద", "🐓", 240, 365, 5),

    /** 12+ months. Maximum everything. Peak form. */
    MATURE_ADULT("Mature Adult", "పూర్తి పెద్ద", "🏆", 365, 730, 6),

    /** 2+ years. Slight dulling, heavier spurs, neck thickens further. */
    SENIOR("Senior", "సీనియర్", "🦅", 730, null, 7);

    val isEgg: Boolean get() = this == EGG
    val canRenderBird: Boolean get() = stageIndex >= HATCHLING.stageIndex
    val hasFeathers: Boolean get() = stageIndex >= CHICK.stageIndex
    val hasSpurs: Boolean get() = stageIndex >= SUB_ADULT.stageIndex
    val hasHackles: Boolean get() = stageIndex >= SUB_ADULT.stageIndex
    val hasSickleFeathers: Boolean get() = stageIndex >= SUB_ADULT.stageIndex
    val hasFullPlumage: Boolean get() = stageIndex >= ADULT.stageIndex

    /**
     * Progress within this stage: 0.0 (just entered) → 1.0 (about to transition)
     */
    fun progressInStage(ageDays: Int): Float {
        val min = minDays
        val max = maxDays ?: (minDays + 365)
        return ((ageDays - min).toFloat() / (max - min)).coerceIn(0f, 1f)
    }

    companion object {
        fun fromAgeDays(days: Int, isMale: Boolean = true): BiologicalStage {
            // For egg stage, caller must explicitly say isEgg=true
            return when {
                days < 7 -> HATCHLING
                days < 42 -> CHICK
                days < 112 -> GROWER
                days < 240 -> SUB_ADULT
                days < 365 -> ADULT
                days < 730 -> MATURE_ADULT
                else -> SENIOR
            }
        }
    }
}
