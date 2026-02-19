package com.rio.rostry.domain.digitaltwin

/**
 * 🧬 Aseel Digital Twin — 7-Stage Lifecycle State Machine
 *
 * Designed specifically for Aseel (Game Bird) lifecycle which differs fundamentally
 * from commercial poultry (Layer/Broiler). Aseel birds live 8-12 years with distinct
 * maturity phases that unlock different capabilities.
 *
 * State Machine:
 * ```
 * EGG (0-21 days)
 *   ↓
 * CHICK (1-45 days)
 *   ↓
 * GROWER (45-180 days)
 *   ↓  [Morphology traits unlocked]
 * PRE_ADULT (6-9 months)
 *   ↓  [Performance metrics unlocked]
 * ADULT_FIGHTER (9-24 months)
 *   ↓  [Breeding eligibility granted]
 * BREEDER_PRIME (2-4 years)
 *   ↓  [Peak genetic value]
 * SENIOR (4+ years)
 *       [Decline factors active]
 * ```
 *
 * Gender-specific paths:
 * - Males: ADULT_FIGHTER → BREEDER_PRIME (if proven) → SENIOR
 * - Females: PRE_ADULT → BREEDER_PRIME (earlier eligibility) → SENIOR
 */
enum class AseelLifecycleStage(
    val displayName: String,
    val teluguName: String,
    val minDays: Int,
    val maxDays: Int?,      // null = no upper bound
    val minWeeks: Int,
    val maxWeeks: Int?,
    val ordinal_index: Int  // For sorting/comparison
) {
    /** Incubation period. No physical traits measurable. */
    EGG(
        displayName = "Egg",
        teluguName = "గుడ్డు",
        minDays = 0,
        maxDays = 21,
        minWeeks = 0,
        maxWeeks = 3,
        ordinal_index = 0
    ),

    /** Fuzzy, round, no comb. Cannot measure morphology. Basic health tracking only. */
    CHICK(
        displayName = "Chick",
        teluguName = "పిల్ల",
        minDays = 1,
        maxDays = 45,
        minWeeks = 0,
        maxWeeks = 6,
        ordinal_index = 1
    ),

    /** Lanky, developing feathers. MORPHOLOGY TRAITS UNLOCK HERE. Weight tracking starts. */
    GROWER(
        displayName = "Grower",
        teluguName = "పెరుగుదల",
        minDays = 45,
        maxDays = 180,
        minWeeks = 6,
        maxWeeks = 26,
        ordinal_index = 2
    ),

    /** Frame filling, hackles starting. PERFORMANCE METRICS UNLOCK. Structure assessment begins. */
    PRE_ADULT(
        displayName = "Pre-Adult",
        teluguName = "ముందు-పెద్ద",
        minDays = 180,
        maxDays = 270,
        minWeeks = 26,
        maxWeeks = 39,
        ordinal_index = 3
    ),

    /** Prime structure, full plumage. BREEDING ELIGIBILITY GRANTED. Peak aggression/stamina. */
    ADULT_FIGHTER(
        displayName = "Adult Fighter",
        teluguName = "యుద్ధకాలు",
        minDays = 270,
        maxDays = 730,
        minWeeks = 39,
        maxWeeks = 104,
        ordinal_index = 4
    ),

    /** Proven breeder. Peak genetic value. Offspring quality assessment available. */
    BREEDER_PRIME(
        displayName = "Breeder Prime",
        teluguName = "ప్రధాన పెంపకదారు",
        minDays = 730,
        maxDays = 1460,
        minWeeks = 104,
        maxWeeks = 208,
        ordinal_index = 5
    ),

    /** 4+ years. Decline factors active. Reduced stamina, but high market value as proven sire/dam. */
    SENIOR(
        displayName = "Senior",
        teluguName = "సీనియర్",
        minDays = 1460,
        maxDays = null,
        minWeeks = 208,
        maxWeeks = null,
        ordinal_index = 6
    );

    // ==================== CAPABILITY GATES ====================

    /** Can morphology traits (body weight, shank length, bone density) be recorded? */
    val canMeasureMorphology: Boolean
        get() = ordinal_index >= GROWER.ordinal_index

    /** Can performance traits (aggression, stamina, endurance) be assessed? */
    val canMeasurePerformance: Boolean
        get() = ordinal_index >= PRE_ADULT.ordinal_index

    /** Is this bird eligible for breeding? */
    val isBreedingEligible: Boolean
        get() = ordinal_index >= ADULT_FIGHTER.ordinal_index

    /** Are decline factors active? */
    val hasDeclineFactors: Boolean
        get() = ordinal_index >= SENIOR.ordinal_index

    /** Can this bird be shown in competitions? */
    val isShowEligible: Boolean
        get() = ordinal_index >= PRE_ADULT.ordinal_index && ordinal_index <= BREEDER_PRIME.ordinal_index

    /** Is full valuation scoring meaningful? */
    val canBeValuated: Boolean
        get() = ordinal_index >= GROWER.ordinal_index

    companion object {
        /**
         * Determine lifecycle stage from age in days.
         *
         * @param ageDays Age in days from birth/hatch
         * @param isMale Gender for stage-specific rules (e.g., females reach Breeder earlier)
         * @param isEgg True if this record represents an unhatched egg
         */
        fun fromAgeDays(ageDays: Int, isMale: Boolean = true, isEgg: Boolean = false): AseelLifecycleStage {
            if (isEgg && ageDays <= 21) return EGG

            return when {
                ageDays < 45 -> CHICK
                ageDays < 180 -> GROWER
                ageDays < 270 -> PRE_ADULT
                // Females can enter Breeder Prime earlier (at ~18 months)
                !isMale && ageDays >= 540 && ageDays < 1460 -> BREEDER_PRIME
                ageDays < 730 -> ADULT_FIGHTER
                ageDays < 1460 -> BREEDER_PRIME
                else -> SENIOR
            }
        }

        /**
         * Determine stage from age in weeks (convenience).
         */
        fun fromAgeWeeks(ageWeeks: Int, isMale: Boolean = true): AseelLifecycleStage {
            return fromAgeDays(ageWeeks * 7, isMale)
        }

        /**
         * Get the next stage after current (for transition predictions).
         * Returns null if at SENIOR (terminal stage).
         */
        fun nextStage(current: AseelLifecycleStage): AseelLifecycleStage? {
            return entries.find { it.ordinal_index == current.ordinal_index + 1 }
        }

        /**
         * Calculate days until next stage transition.
         */
        fun daysUntilNextTransition(currentAgeDays: Int, isMale: Boolean = true): Int? {
            val currentStage = fromAgeDays(currentAgeDays, isMale)
            val nextStage = nextStage(currentStage) ?: return null
            return (nextStage.minDays - currentAgeDays).coerceAtLeast(0)
        }
    }
}
