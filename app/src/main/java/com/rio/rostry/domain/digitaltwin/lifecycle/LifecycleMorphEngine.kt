package com.rio.rostry.domain.digitaltwin.lifecycle

import com.rio.rostry.domain.model.*

/**
 * 🧬 LifecycleMorphEngine — Progressive Morphological Lifecycle Engine (PMLE)
 *
 * The core engine that transforms a BirdAppearance based on biological age.
 * Binds visual morphology to age, making the Digital Twin time-aware and
 * biologically accurate.
 *
 * Three modes of operation:
 * 1. AUTO_BIOLOGICAL: Morphs evolve daily based on age. Smooth interpolation.
 * 2. MANUAL_STAGE: User selects stage, morphs snap to stage defaults.
 * 3. MANUAL_FREE: User has full slider control (existing behavior).
 *
 * Usage:
 * ```kotlin
 * val engine = LifecycleMorphEngine
 * val evolved = engine.evolve(currentAppearance, ageProfile)
 * // Or with constraining user edits:
 * val constrained = engine.constrainToStage(userAppearance, ageProfile)
 * ```
 */
object LifecycleMorphEngine {

    /**
     * Evolve a BirdAppearance based on the bird's AgeProfile.
     *
     * In AUTO_BIOLOGICAL mode: computes morphology purely from age.
     * In MANUAL_STAGE mode: uses stage defaults but respects user overrides within stage limits.
     * In MANUAL_FREE mode: returns appearance unchanged (no constraints).
     *
     * @param base The current/base BirdAppearance
     * @param ageProfile The bird's current age profile
     * @return Evolved BirdAppearance with age-appropriate morphology
     */
    fun evolve(base: BirdAppearance, ageProfile: AgeProfile): BirdAppearance {
        if (ageProfile.stage == BiologicalStage.EGG) {
            // Egg stage: no bird appearance changes needed, egg is rendered separately
            return base
        }

        return when (ageProfile.growthMode) {
            GrowthMode.AUTO_BIOLOGICAL -> evolveAutomatic(base, ageProfile)
            GrowthMode.MANUAL_STAGE -> evolveManualStage(base, ageProfile)
            GrowthMode.MANUAL_FREE -> base // No constraints in free mode
        }
    }

    /**
     * Get the morph constraints for the current age.
     * Uses smooth interpolation between stages for continuous evolution.
     */
    fun getConstraints(ageProfile: AgeProfile): StageMorphConstraints {
        val currentStage = ageProfile.stage
        val currentConstraints = MorphConstraints.forStage(currentStage, ageProfile.isMale)

        // Find next stage for interpolation
        val nextStage = BiologicalStage.entries
            .filter { it.stageIndex > currentStage.stageIndex }
            .minByOrNull { it.stageIndex }
            ?: return currentConstraints  // Senior has no next stage

        val nextConstraints = MorphConstraints.forStage(nextStage, ageProfile.isMale)
        val progress = currentStage.progressInStage(ageProfile.ageInDays)

        // Smooth ease-in-out interpolation
        val smoothProgress = smoothStep(progress)

        return MorphConstraints.interpolate(currentConstraints, nextConstraints, smoothProgress)
    }

    /**
     * Constrain user-edited appearance values to the valid range for the current stage.
     * Used when the user edits sliders — prevents unrealistic values.
     */
    fun constrainToStage(appearance: BirdAppearance, ageProfile: AgeProfile): BirdAppearance {
        if (ageProfile.growthMode == GrowthMode.MANUAL_FREE) return appearance

        val constraints = getConstraints(ageProfile)
        return appearance.copy(
            bodyWidth = constraints.bodyWidth.clamp(appearance.bodyWidth),
            bodyRoundness = constraints.bodyRoundness.clamp(appearance.bodyRoundness),
            legLength = constraints.legLength.clamp(appearance.legLength),
            legThickness = constraints.legThickness.clamp(appearance.legThickness),
            tailLength = constraints.tailLength.clamp(appearance.tailLength),
            tailAngle = constraints.tailAngle.clamp(appearance.tailAngle),
            tailSpread = constraints.tailSpread.clamp(appearance.tailSpread),
            combSize = constraints.combSize.clamp(appearance.combSize),
            beakScale = constraints.beakScale.clamp(appearance.beakScale),
            beakCurvature = constraints.beakCurvature.clamp(appearance.beakCurvature),
            // Enum constraints
            comb = if (appearance.comb in constraints.allowedCombs) appearance.comb
                   else constraints.allowedCombs.first(),
            tail = if (appearance.tail in constraints.allowedTails) appearance.tail
                   else constraints.allowedTails.first(),
            nails = if (appearance.nails in constraints.allowedNails) appearance.nails
                    else constraints.allowedNails.first(),
            wattle = if (appearance.wattle in constraints.allowedWattles) appearance.wattle
                     else constraints.allowedWattles.first(),
            stance = if (appearance.stance in constraints.allowedStances) appearance.stance
                     else constraints.allowedStances.first(),
            sheen = if (appearance.sheen in constraints.allowedSheens) appearance.sheen
                    else constraints.allowedSheens.first(),
            neck = if (appearance.neck in constraints.allowedNecks) appearance.neck
                   else constraints.allowedNecks.first(),
            breast = if (appearance.breast in constraints.allowedBreasts) appearance.breast
                     else constraints.allowedBreasts.first()
        )
    }

    /**
     * Generate a BirdAppearance snapshot at a specific age.
     * Great for "preview at age X" feature.
     *
     * @param base Base appearance (colors, breed traits)
     * @param ageDays Target age in days
     * @param isMale Gender
     * @return Appearance as it would look at that age
     */
    fun previewAtAge(base: BirdAppearance, ageDays: Int, isMale: Boolean = true): BirdAppearance {
        val ageProfile = AgeProfile.fromDays(ageDays, isMale, GrowthMode.AUTO_BIOLOGICAL)
        return evolveAutomatic(base, ageProfile)
    }

    /**
     * Generate a complete growth timeline: appearance snapshots at key ages.
     * Useful for growth visualization UI.
     */
    fun generateGrowthTimeline(
        base: BirdAppearance,
        isMale: Boolean = true,
        maxDays: Int = 730
    ): List<GrowthSnapshot> {
        val keyDays = listOf(
            0,    // Hatchling
            3,    // Early hatchling
            7,    // End hatchling
            14,   // 2 week chick
            28,   // 4 week chick
            42,   // 6 week (grower start)
            63,   // 9 weeks
            84,   // 12 weeks
            112,  // 16 weeks (sub-adult)
            150,  // ~5 months
            180,  // 6 months
            210,  // 7 months
            240,  // 8 months (adult)
            300,  // 10 months
            365,  // 12 months (mature)
            455,  // 15 months
            548,  // 18 months
            730   // 2 years (senior)
        ).filter { it <= maxDays }

        return keyDays.map { day ->
            val ageProfile = AgeProfile.fromDays(day, isMale, GrowthMode.AUTO_BIOLOGICAL)
            val appearance = evolveAutomatic(base, ageProfile)
            GrowthSnapshot(
                ageDays = day,
                stage = ageProfile.stage,
                maturityIndex = ageProfile.maturityIndex,
                appearance = appearance,
                morphSummary = generateMorphSummary(ageProfile)
            )
        }
    }

    /**
     * Get what changed between two ages (for event log / notifications).
     */
    fun getTransitionChanges(fromDays: Int, toDays: Int, isMale: Boolean = true): List<MorphChange> {
        val fromStage = BiologicalStage.fromAgeDays(fromDays, isMale)
        val toStage = BiologicalStage.fromAgeDays(toDays, isMale)

        if (fromStage == toStage) return emptyList()

        val changes = mutableListOf<MorphChange>()
        val toConstraints = MorphConstraints.forStage(toStage, isMale)
        val fromConstraints = MorphConstraints.forStage(fromStage, isMale)

        // Report significant changes
        if (toConstraints.hackleLength.defaultValue > fromConstraints.hackleLength.defaultValue + 0.1f) {
            changes.add(MorphChange("Hackle Feathers", "Growing", "Hackle feathers becoming visible"))
        }
        if (toConstraints.spurSize.defaultValue > fromConstraints.spurSize.defaultValue + 0.1f && isMale) {
            changes.add(MorphChange("Spurs", "Developing", "Spurs are starting to grow"))
        }
        if (toConstraints.chestDepth.defaultValue > fromConstraints.chestDepth.defaultValue + 0.1f) {
            changes.add(MorphChange("Chest", "Deepening", "Chest muscles thickening"))
        }
        if (toConstraints.featherDensity.defaultValue > fromConstraints.featherDensity.defaultValue + 0.1f) {
            changes.add(MorphChange("Plumage", "Filling In", "Feather coverage increasing"))
        }
        if (toStage.stageIndex >= BiologicalStage.ADULT.stageIndex &&
            fromStage.stageIndex < BiologicalStage.ADULT.stageIndex) {
            changes.add(MorphChange("Feather Quality", "Hardening", "Feathers transitioning to hard/tight"))
        }
        if (toStage == BiologicalStage.SENIOR) {
            changes.add(MorphChange("Sheen", "Slight Dulling", "Feather sheen beginning to reduce"))
        }

        return changes
    }

    // ==================== PRIVATE IMPLEMENTATION ====================

    /**
     * AUTO mode: Compute appearance purely from age, preserving user's
     * color choices but overriding all morph values.
     */
    private fun evolveAutomatic(base: BirdAppearance, ageProfile: AgeProfile): BirdAppearance {
        val constraints = getConstraints(ageProfile)
        val stage = ageProfile.stage
        val progress = stage.progressInStage(ageProfile.ageInDays)

        return base.copy(
            // === MORPH TARGETS: Set to stage defaults ===
            bodyWidth = constraints.bodyWidth.defaultValue,
            bodyRoundness = constraints.bodyRoundness.defaultValue,
            legLength = constraints.legLength.defaultValue,
            legThickness = constraints.legThickness.defaultValue,
            tailLength = constraints.tailLength.defaultValue,
            tailAngle = constraints.tailAngle.defaultValue,
            tailSpread = constraints.tailSpread.defaultValue,
            combSize = constraints.combSize.defaultValue,
            beakScale = constraints.beakScale.defaultValue,
            beakCurvature = constraints.beakCurvature.defaultValue,

            // === ENUM SELECTIONS: Set to stage-appropriate defaults ===
            bodySize = constraints.defaultBodySize,
            stance = constraints.defaultStance,
            sheen = constraints.defaultSheen,

            // Comb: Aseel typically has pea comb, but only visible from sub-adult
            comb = when {
                stage.stageIndex <= BiologicalStage.CHICK.stageIndex -> CombStyle.NONE
                stage.stageIndex == BiologicalStage.GROWER.stageIndex ->
                    if (progress > 0.5f) base.comb.takeIf { it in constraints.allowedCombs } ?: CombStyle.PEA
                    else CombStyle.NONE
                else -> base.comb.takeIf { it in constraints.allowedCombs } ?: CombStyle.PEA
            },

            // Tail: Progressively from none → short → sickle
            tail = when {
                stage.stageIndex <= BiologicalStage.HATCHLING.stageIndex -> TailStyle.NONE
                stage.stageIndex == BiologicalStage.CHICK.stageIndex -> TailStyle.SHORT
                stage.stageIndex == BiologicalStage.GROWER.stageIndex ->
                    if (ageProfile.isMale && progress > 0.6f) TailStyle.SICKLE else TailStyle.SHORT
                stage.stageIndex >= BiologicalStage.SUB_ADULT.stageIndex ->
                    if (ageProfile.isMale) base.tail.takeIf { it in constraints.allowedTails } ?: TailStyle.SICKLE
                    else TailStyle.SHORT
                else -> base.tail
            },

            // Nails/Spurs: Only for males from sub-adult
            nails = when {
                stage.stageIndex < BiologicalStage.CHICK.stageIndex -> NailStyle.NONE
                stage.stageIndex < BiologicalStage.SUB_ADULT.stageIndex -> NailStyle.SHORT
                ageProfile.isMale && stage.stageIndex >= BiologicalStage.ADULT.stageIndex ->
                    base.nails.takeIf { it in constraints.allowedNails } ?: NailStyle.LONG_SPUR
                ageProfile.isMale -> NailStyle.SHORT
                else -> NailStyle.SHORT
            },

            // Wattle: Grows from grower stage
            wattle = when {
                stage.stageIndex <= BiologicalStage.CHICK.stageIndex -> WattleStyle.NONE
                stage.stageIndex == BiologicalStage.GROWER.stageIndex -> WattleStyle.SMALL
                stage.stageIndex == BiologicalStage.SUB_ADULT.stageIndex ->
                    if (ageProfile.isMale) WattleStyle.MEDIUM else WattleStyle.SMALL
                else -> base.wattle.takeIf { it in constraints.allowedWattles } ?: WattleStyle.MEDIUM
            },

            // Neck: Progressively thickens
            neck = when {
                stage.stageIndex <= BiologicalStage.CHICK.stageIndex -> NeckStyle.SHORT
                stage.stageIndex == BiologicalStage.GROWER.stageIndex -> NeckStyle.MEDIUM
                stage.stageIndex >= BiologicalStage.MATURE_ADULT.stageIndex && ageProfile.isMale ->
                    NeckStyle.MUSCULAR
                else -> base.neck.takeIf { it in constraints.allowedNecks } ?: NeckStyle.MEDIUM
            },

            // Breast: Deepens with maturity
            breast = when {
                stage.stageIndex <= BiologicalStage.GROWER.stageIndex -> BreastShape.FLAT
                stage.stageIndex == BiologicalStage.SUB_ADULT.stageIndex -> BreastShape.ROUND
                ageProfile.isMale && stage.stageIndex >= BiologicalStage.ADULT.stageIndex ->
                    base.breast.takeIf { it in constraints.allowedBreasts } ?: BreastShape.DEEP
                else -> BreastShape.ROUND
            },

            // Back: Hackle development for males
            back = when {
                stage.stageIndex < BiologicalStage.SUB_ADULT.stageIndex -> BackStyle.SMOOTH
                ageProfile.isMale && stage.stageIndex >= BiologicalStage.ADULT.stageIndex -> BackStyle.HACKLE
                ageProfile.isMale -> BackStyle.SADDLE
                else -> BackStyle.SMOOTH
            },

            // Legs: Clean → Spurred for males
            legs = when {
                stage.stageIndex < BiologicalStage.SUB_ADULT.stageIndex -> LegStyle.CLEAN
                ageProfile.isMale && stage.stageIndex >= BiologicalStage.ADULT.stageIndex -> LegStyle.SPURRED
                else -> base.legs
            },

            // Wings: Tight for game birds from sub-adult
            wings = when {
                stage.stageIndex < BiologicalStage.GROWER.stageIndex -> WingStyle.FOLDED
                stage.stageIndex >= BiologicalStage.SUB_ADULT.stageIndex -> WingStyle.TIGHT
                else -> WingStyle.FOLDED
            }
        )
    }

    /**
     * MANUAL_STAGE mode: Use stage defaults but allow user overrides within range.
     */
    private fun evolveManualStage(base: BirdAppearance, ageProfile: AgeProfile): BirdAppearance {
        return constrainToStage(base, ageProfile)
    }

    /**
     * Generate a human-readable summary of current morphological state.
     */
    private fun generateMorphSummary(ageProfile: AgeProfile): MorphSummary {
        val stage = ageProfile.stage
        val constraints = MorphConstraints.forStage(stage, ageProfile.isMale)

        return MorphSummary(
            stageName = stage.displayName,
            stageEmoji = stage.emoji,
            featherTexture = constraints.defaultFeatherTexture,
            keyFeatures = buildList {
                if (stage.hasHackles && ageProfile.isMale) add("Hackle feathers visible")
                if (stage.hasSpurs && ageProfile.isMale) add("Spurs developing")
                if (stage.hasSickleFeathers && ageProfile.isMale) add("Sickle tail feathers")
                if (stage.hasFullPlumage) add("Full plumage achieved")
                if (stage == BiologicalStage.HATCHLING) add("Down-covered, oversized head")
                if (stage == BiologicalStage.CHICK) add("First feathers emerging")
                if (stage == BiologicalStage.GROWER) add("Rapid leg growth, patchy feathers")
                if (stage == BiologicalStage.SENIOR) add("Peak bone thickness, slight feather dulling")
            },
            maturityPercent = (ageProfile.maturityIndex * 100).toInt().coerceIn(0, 100)
        )
    }

    /**
     * SmoothStep interpolation for natural-looking transitions.
     * Hermite interpolation f(t) = 3t² - 2t³
     */
    private fun smoothStep(t: Float): Float {
        val x = t.coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }
}

/**
 * A snapshot of the bird's appearance at a specific age.
 */
data class GrowthSnapshot(
    val ageDays: Int,
    val stage: BiologicalStage,
    val maturityIndex: Float,
    val appearance: BirdAppearance,
    val morphSummary: MorphSummary
)

/**
 * Summary of morphological state at a given age.
 */
data class MorphSummary(
    val stageName: String,
    val stageEmoji: String,
    val featherTexture: String,
    val keyFeatures: List<String>,
    val maturityPercent: Int
)

/**
 * A single morphological change that occurred during a stage transition.
 */
data class MorphChange(
    val feature: String,
    val changeType: String,
    val description: String
)
