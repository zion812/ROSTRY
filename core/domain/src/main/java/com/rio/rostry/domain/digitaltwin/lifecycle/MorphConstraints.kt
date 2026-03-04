package com.rio.rostry.domain.digitaltwin.lifecycle

import com.rio.rostry.domain.model.*

/**
 * 🧬 MorphConstraints — Stage-Based Min/Max Ranges for Every Morph Parameter
 *
 * Prevents unrealistic builds by constraining slider values based on
 * biological stage. A Chick cannot have full hackles or large spurs.
 *
 * Each MorphTarget has:
 * - min: Minimum slider value allowed at this stage
 * - max: Maximum slider value allowed at this stage
 * - defaultValue: Auto-biological default for this stage
 *
 * Usage:
 * ```kotlin
 * val constraints = MorphConstraints.forStage(BiologicalStage.CHICK, isMale = true)
 * val clampedLegLength = constraints.legLength.clamp(userInput)
 * ```
 */
data class MorphRange(
    val min: Float,
    val max: Float,
    val defaultValue: Float
) {
    /** Clamp a user value to the allowed range */
    fun clamp(value: Float): Float = value.coerceIn(min, max)

    /** Interpolate between min and max using a 0-1 factor */
    fun interpolate(factor: Float): Float = min + (max - min) * factor.coerceIn(0f, 1f)
}

/**
 * Complete set of morph constraints for a specific biological stage.
 */
data class StageMorphConstraints(
    val stage: BiologicalStage,
    val isMale: Boolean,

    // === V3 MORPH TARGET RANGES ===
    val bodyWidth: MorphRange,
    val bodyRoundness: MorphRange,
    val legLength: MorphRange,
    val legThickness: MorphRange,
    val tailLength: MorphRange,
    val tailAngle: MorphRange,
    val tailSpread: MorphRange,
    val combSize: MorphRange,
    val beakScale: MorphRange,
    val beakCurvature: MorphRange,

    // === ADDITIONAL MORPH TARGETS (new for PMLE) ===
    val neckLength: MorphRange,
    val neckThickness: MorphRange,
    val chestDepth: MorphRange,
    val hackleLength: MorphRange,
    val spurSize: MorphRange,
    val boneThickness: MorphRange,
    val featherDensity: MorphRange,

    // === ENUM CONSTRAINTS (what styles are valid at this stage) ===
    val allowedCombs: List<CombStyle>,
    val allowedTails: List<TailStyle>,
    val allowedNails: List<NailStyle>,
    val allowedWattles: List<WattleStyle>,
    val allowedStances: List<Stance>,
    val allowedSheens: List<Sheen>,
    val allowedNecks: List<NeckStyle>,
    val allowedBreasts: List<BreastShape>,

    // === SUGGESTED DEFAULTS FOR AUTO MODE ===
    val defaultBodySize: BodySize,
    val defaultStance: Stance,
    val defaultSheen: Sheen,
    val defaultFeatherTexture: String   // "Fluff", "Soft", "Developing", "Hard", "Peak", "Dulling"
)

/**
 * Factory for stage-specific morph constraints.
 */
object MorphConstraints {

    /**
     * Get the full constraint set for a given biological stage.
     */
    fun forStage(stage: BiologicalStage, isMale: Boolean = true): StageMorphConstraints {
        return when (stage) {
            BiologicalStage.EGG -> eggConstraints(isMale)
            BiologicalStage.HATCHLING -> hatchlingConstraints(isMale)
            BiologicalStage.CHICK -> chickConstraints(isMale)
            BiologicalStage.GROWER -> growerConstraints(isMale)
            BiologicalStage.SUB_ADULT -> subAdultConstraints(isMale)
            BiologicalStage.ADULT -> adultConstraints(isMale)
            BiologicalStage.MATURE_ADULT -> matureAdultConstraints(isMale)
            BiologicalStage.SENIOR -> seniorConstraints(isMale)
        }
    }

    /**
     * Interpolate constraints between two stages for smooth transitions.
     * @param progress 0.0 = fully 'from' constraints, 1.0 = fully 'to' constraints
     */
    fun interpolate(from: StageMorphConstraints, to: StageMorphConstraints, progress: Float): StageMorphConstraints {
        val p = progress.coerceIn(0f, 1f)
        return to.copy(
            bodyWidth = lerp(from.bodyWidth, to.bodyWidth, p),
            bodyRoundness = lerp(from.bodyRoundness, to.bodyRoundness, p),
            legLength = lerp(from.legLength, to.legLength, p),
            legThickness = lerp(from.legThickness, to.legThickness, p),
            tailLength = lerp(from.tailLength, to.tailLength, p),
            tailAngle = lerp(from.tailAngle, to.tailAngle, p),
            tailSpread = lerp(from.tailSpread, to.tailSpread, p),
            combSize = lerp(from.combSize, to.combSize, p),
            beakScale = lerp(from.beakScale, to.beakScale, p),
            beakCurvature = lerp(from.beakCurvature, to.beakCurvature, p),
            neckLength = lerp(from.neckLength, to.neckLength, p),
            neckThickness = lerp(from.neckThickness, to.neckThickness, p),
            chestDepth = lerp(from.chestDepth, to.chestDepth, p),
            hackleLength = lerp(from.hackleLength, to.hackleLength, p),
            spurSize = lerp(from.spurSize, to.spurSize, p),
            boneThickness = lerp(from.boneThickness, to.boneThickness, p),
            featherDensity = lerp(from.featherDensity, to.featherDensity, p)
        )
    }

    private fun lerp(a: MorphRange, b: MorphRange, t: Float): MorphRange {
        return MorphRange(
            min = a.min + (b.min - a.min) * t,
            max = a.max + (b.max - a.max) * t,
            defaultValue = a.defaultValue + (b.defaultValue - a.defaultValue) * t
        )
    }

    // ==================== STAGE CONSTRAINT DEFINITIONS ====================

    // 🥚 EGG — No bird morphology, just egg shell
    private fun eggConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.EGG, isMale = isMale,
        bodyWidth = MorphRange(0f, 0f, 0f),
        bodyRoundness = MorphRange(0f, 0f, 0f),
        legLength = MorphRange(0f, 0f, 0f),
        legThickness = MorphRange(0f, 0f, 0f),
        tailLength = MorphRange(0f, 0f, 0f),
        tailAngle = MorphRange(0f, 0f, 0f),
        tailSpread = MorphRange(0f, 0f, 0f),
        combSize = MorphRange(0f, 0f, 0f),
        beakScale = MorphRange(0f, 0f, 0f),
        beakCurvature = MorphRange(0f, 0f, 0f),
        neckLength = MorphRange(0f, 0f, 0f),
        neckThickness = MorphRange(0f, 0f, 0f),
        chestDepth = MorphRange(0f, 0f, 0f),
        hackleLength = MorphRange(0f, 0f, 0f),
        spurSize = MorphRange(0f, 0f, 0f),
        boneThickness = MorphRange(0f, 0f, 0f),
        featherDensity = MorphRange(0f, 0f, 0f),
        allowedCombs = listOf(CombStyle.NONE),
        allowedTails = listOf(TailStyle.NONE),
        allowedNails = listOf(NailStyle.NONE),
        allowedWattles = listOf(WattleStyle.NONE),
        allowedStances = listOf(Stance.NORMAL),
        allowedSheens = listOf(Sheen.MATTE),
        allowedNecks = listOf(NeckStyle.SHORT),
        allowedBreasts = listOf(BreastShape.ROUND),
        defaultBodySize = BodySize.TINY,
        defaultStance = Stance.NORMAL,
        defaultSheen = Sheen.MATTE,
        defaultFeatherTexture = "None"
    )

    // 🐣 HATCHLING (0-7 days) — Oversized head, puffy down, no comb
    private fun hatchlingConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.HATCHLING, isMale = isMale,
        bodyWidth = MorphRange(0.3f, 0.5f, 0.4f),
        bodyRoundness = MorphRange(0.7f, 1.0f, 0.9f),     // Very round
        legLength = MorphRange(0.1f, 0.25f, 0.2f),         // Very short
        legThickness = MorphRange(0.2f, 0.4f, 0.3f),
        tailLength = MorphRange(0.0f, 0.05f, 0.0f),        // No tail
        tailAngle = MorphRange(0.3f, 0.5f, 0.4f),
        tailSpread = MorphRange(0.0f, 0.1f, 0.0f),
        combSize = MorphRange(0.0f, 0.02f, 0.0f),          // No comb
        beakScale = MorphRange(0.2f, 0.35f, 0.25f),        // Tiny beak
        beakCurvature = MorphRange(0.3f, 0.5f, 0.4f),
        neckLength = MorphRange(0.1f, 0.3f, 0.2f),         // Very short neck
        neckThickness = MorphRange(0.2f, 0.4f, 0.3f),
        chestDepth = MorphRange(0.1f, 0.2f, 0.15f),
        hackleLength = MorphRange(0.0f, 0.0f, 0.0f),       // No hackles
        spurSize = MorphRange(0.0f, 0.0f, 0.0f),           // No spurs
        boneThickness = MorphRange(0.1f, 0.2f, 0.15f),
        featherDensity = MorphRange(0.0f, 0.1f, 0.05f),    // Down only
        allowedCombs = listOf(CombStyle.NONE),
        allowedTails = listOf(TailStyle.NONE, TailStyle.SHORT),
        allowedNails = listOf(NailStyle.NONE, NailStyle.SHORT),
        allowedWattles = listOf(WattleStyle.NONE),
        allowedStances = listOf(Stance.NORMAL, Stance.LOW),
        allowedSheens = listOf(Sheen.MATTE),
        allowedNecks = listOf(NeckStyle.SHORT),
        allowedBreasts = listOf(BreastShape.ROUND),
        defaultBodySize = BodySize.TINY,
        defaultStance = Stance.LOW,
        defaultSheen = Sheen.MATTE,
        defaultFeatherTexture = "Fluff"
    )

    // 🐥 CHICK (1-6 weeks) — Legs growing, first feathers, tail stub
    private fun chickConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.CHICK, isMale = isMale,
        bodyWidth = MorphRange(0.3f, 0.5f, 0.35f),
        bodyRoundness = MorphRange(0.4f, 0.7f, 0.6f),
        legLength = MorphRange(0.25f, 0.45f, 0.4f),         // Growing faster
        legThickness = MorphRange(0.25f, 0.4f, 0.3f),
        tailLength = MorphRange(0.05f, 0.25f, 0.15f),       // Tail stub
        tailAngle = MorphRange(0.3f, 0.5f, 0.4f),
        tailSpread = MorphRange(0.05f, 0.2f, 0.1f),
        combSize = MorphRange(0.0f, 0.12f, 0.05f),          // Tiny bump
        beakScale = MorphRange(0.25f, 0.4f, 0.3f),
        beakCurvature = MorphRange(0.3f, 0.5f, 0.4f),
        neckLength = MorphRange(0.2f, 0.4f, 0.3f),
        neckThickness = MorphRange(0.2f, 0.4f, 0.3f),
        chestDepth = MorphRange(0.1f, 0.25f, 0.15f),
        hackleLength = MorphRange(0.0f, 0.05f, 0.0f),       // No hackles
        spurSize = MorphRange(0.0f, 0.0f, 0.0f),
        boneThickness = MorphRange(0.15f, 0.3f, 0.2f),
        featherDensity = MorphRange(0.1f, 0.35f, 0.2f),     // Partial feathering
        allowedCombs = listOf(CombStyle.NONE, CombStyle.PEA),
        allowedTails = listOf(TailStyle.NONE, TailStyle.SHORT),
        allowedNails = listOf(NailStyle.NONE, NailStyle.SHORT),
        allowedWattles = listOf(WattleStyle.NONE, WattleStyle.SMALL),
        allowedStances = listOf(Stance.NORMAL, Stance.LOW),
        allowedSheens = listOf(Sheen.MATTE),
        allowedNecks = listOf(NeckStyle.SHORT, NeckStyle.MEDIUM),
        allowedBreasts = listOf(BreastShape.ROUND, BreastShape.FLAT),
        defaultBodySize = BodySize.BANTAM,
        defaultStance = Stance.NORMAL,
        defaultSheen = Sheen.MATTE,
        defaultFeatherTexture = "Soft"
    )

    // 🐤 GROWER (6-16 weeks) — Awkward phase, lanky legs, patchy feathers
    private fun growerConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.GROWER, isMale = isMale,
        bodyWidth = MorphRange(0.3f, 0.5f, 0.35f),
        bodyRoundness = MorphRange(0.2f, 0.5f, 0.35f),       // Getting leaner
        legLength = MorphRange(0.45f, 0.7f, 0.6f),           // Long awkward legs
        legThickness = MorphRange(0.3f, 0.5f, 0.35f),        // Still thin
        tailLength = MorphRange(0.2f, 0.45f, 0.35f),
        tailAngle = MorphRange(0.3f, 0.6f, 0.45f),
        tailSpread = MorphRange(0.1f, 0.3f, 0.2f),
        combSize = MorphRange(0.05f, 0.25f, 0.12f),
        beakScale = MorphRange(0.3f, 0.5f, 0.4f),
        beakCurvature = MorphRange(0.3f, 0.55f, 0.45f),
        neckLength = MorphRange(0.35f, 0.6f, 0.5f),          // Neck lengthening
        neckThickness = MorphRange(0.25f, 0.45f, 0.3f),      // Still thin
        chestDepth = MorphRange(0.15f, 0.35f, 0.25f),        // Thin chest
        hackleLength = MorphRange(0.0f, 0.15f, 0.05f),       // Barely visible
        spurSize = MorphRange(0.0f, 0.05f, 0.0f),            // No spurs yet
        boneThickness = MorphRange(0.2f, 0.4f, 0.3f),
        featherDensity = MorphRange(0.25f, 0.55f, 0.4f),     // Patchy development
        allowedCombs = listOf(CombStyle.NONE, CombStyle.PEA, CombStyle.SINGLE, CombStyle.WALNUT),
        allowedTails = listOf(TailStyle.SHORT, TailStyle.SICKLE),
        allowedNails = listOf(NailStyle.SHORT),
        allowedWattles = listOf(WattleStyle.NONE, WattleStyle.SMALL),
        allowedStances = listOf(Stance.NORMAL, Stance.UPRIGHT),
        allowedSheens = listOf(Sheen.MATTE, Sheen.SATIN),
        allowedNecks = listOf(NeckStyle.SHORT, NeckStyle.MEDIUM, NeckStyle.LONG),
        allowedBreasts = listOf(BreastShape.FLAT, BreastShape.ROUND),
        defaultBodySize = BodySize.SMALL,
        defaultStance = Stance.NORMAL,
        defaultSheen = Sheen.MATTE,
        defaultFeatherTexture = "Developing"
    )

    // 🐔 SUB-ADULT (4-8 months) — Sexual dimorphism, hackles/sickles forming
    private fun subAdultConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.SUB_ADULT, isMale = isMale,
        bodyWidth = MorphRange(0.35f, 0.6f, if (isMale) 0.45f else 0.4f),
        bodyRoundness = MorphRange(0.25f, 0.5f, 0.35f),
        legLength = MorphRange(0.5f, 0.75f, 0.65f),
        legThickness = MorphRange(0.35f, 0.6f, if (isMale) 0.5f else 0.4f),
        tailLength = MorphRange(0.3f, 0.65f, if (isMale) 0.55f else 0.4f),
        tailAngle = MorphRange(0.35f, 0.65f, 0.5f),
        tailSpread = MorphRange(0.2f, 0.4f, 0.3f),
        combSize = MorphRange(0.1f, 0.4f, if (isMale) 0.3f else 0.15f),
        beakScale = MorphRange(0.35f, 0.55f, 0.45f),
        beakCurvature = MorphRange(0.35f, 0.6f, 0.5f),
        neckLength = MorphRange(0.45f, 0.7f, 0.6f),
        neckThickness = MorphRange(0.35f, 0.55f, if (isMale) 0.5f else 0.4f),
        chestDepth = MorphRange(0.3f, 0.55f, if (isMale) 0.45f else 0.35f),
        hackleLength = MorphRange(0.1f, 0.65f, if (isMale) 0.5f else 0.1f),    // Males developing hackles
        spurSize = MorphRange(0.0f, 0.3f, if (isMale) 0.15f else 0.0f),         // Males: small spurs
        boneThickness = MorphRange(0.3f, 0.55f, 0.4f),
        featherDensity = MorphRange(0.45f, 0.75f, 0.6f),
        allowedCombs = CombStyle.entries.toList(),
        allowedTails = TailStyle.entries.toList(),
        allowedNails = NailStyle.entries.toList(),
        allowedWattles = WattleStyle.entries.toList(),
        allowedStances = Stance.entries.toList(),
        allowedSheens = listOf(Sheen.MATTE, Sheen.SATIN, Sheen.GLOSSY),
        allowedNecks = NeckStyle.entries.toList(),
        allowedBreasts = BreastShape.entries.toList(),
        defaultBodySize = BodySize.MEDIUM,
        defaultStance = if (isMale) Stance.UPRIGHT else Stance.NORMAL,
        defaultSheen = Sheen.SATIN,
        defaultFeatherTexture = "Medium"
    )

    // 🐓 ADULT (8-12 months) — Full structure, hard feather, spurs visible
    private fun adultConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.ADULT, isMale = isMale,
        bodyWidth = MorphRange(0.4f, 0.7f, if (isMale) 0.55f else 0.45f),
        bodyRoundness = MorphRange(0.25f, 0.5f, 0.35f),
        legLength = MorphRange(0.55f, 0.8f, 0.7f),
        legThickness = MorphRange(0.4f, 0.7f, if (isMale) 0.6f else 0.45f),
        tailLength = MorphRange(0.4f, 0.8f, if (isMale) 0.7f else 0.45f),
        tailAngle = MorphRange(0.4f, 0.7f, 0.55f),
        tailSpread = MorphRange(0.25f, 0.55f, 0.4f),
        combSize = MorphRange(0.2f, 0.6f, if (isMale) 0.5f else 0.25f),
        beakScale = MorphRange(0.4f, 0.6f, 0.5f),
        beakCurvature = MorphRange(0.4f, 0.65f, 0.55f),
        neckLength = MorphRange(0.5f, 0.8f, 0.65f),
        neckThickness = MorphRange(0.4f, 0.7f, if (isMale) 0.6f else 0.45f),
        chestDepth = MorphRange(0.45f, 0.75f, if (isMale) 0.65f else 0.5f),
        hackleLength = MorphRange(0.3f, 0.85f, if (isMale) 0.75f else 0.15f),
        spurSize = MorphRange(0.1f, 0.6f, if (isMale) 0.45f else 0.0f),
        boneThickness = MorphRange(0.4f, 0.7f, 0.55f),
        featherDensity = MorphRange(0.6f, 0.85f, 0.75f),
        allowedCombs = CombStyle.entries.toList(),
        allowedTails = TailStyle.entries.toList(),
        allowedNails = NailStyle.entries.toList(),
        allowedWattles = WattleStyle.entries.toList(),
        allowedStances = Stance.entries.toList(),
        allowedSheens = Sheen.entries.toList(),
        allowedNecks = NeckStyle.entries.toList(),
        allowedBreasts = BreastShape.entries.toList(),
        defaultBodySize = if (isMale) BodySize.LARGE else BodySize.MEDIUM,
        defaultStance = if (isMale) Stance.GAME_READY else Stance.NORMAL,
        defaultSheen = Sheen.GLOSSY,
        defaultFeatherTexture = "Hard"
    )

    // 🏆 MATURE ADULT (12+ months) — Maximum everything. Peak form.
    private fun matureAdultConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.MATURE_ADULT, isMale = isMale,
        bodyWidth = MorphRange(0.5f, 0.8f, if (isMale) 0.65f else 0.5f),
        bodyRoundness = MorphRange(0.2f, 0.5f, 0.3f),        // Lean and muscular
        legLength = MorphRange(0.6f, 0.85f, 0.75f),
        legThickness = MorphRange(0.5f, 0.8f, if (isMale) 0.7f else 0.5f),
        tailLength = MorphRange(0.5f, 0.9f, if (isMale) 0.8f else 0.5f),
        tailAngle = MorphRange(0.45f, 0.75f, 0.6f),
        tailSpread = MorphRange(0.3f, 0.6f, 0.45f),
        combSize = MorphRange(0.3f, 0.7f, if (isMale) 0.55f else 0.3f),
        beakScale = MorphRange(0.45f, 0.65f, 0.55f),
        beakCurvature = MorphRange(0.45f, 0.7f, 0.6f),
        neckLength = MorphRange(0.55f, 0.85f, 0.7f),
        neckThickness = MorphRange(0.55f, 0.85f, if (isMale) 0.75f else 0.55f), // Thick muscular neck
        chestDepth = MorphRange(0.55f, 0.85f, if (isMale) 0.8f else 0.6f),      // Deep chest
        hackleLength = MorphRange(0.5f, 1.0f, if (isMale) 0.9f else 0.2f),      // Full hackles
        spurSize = MorphRange(0.3f, 0.85f, if (isMale) 0.75f else 0.0f),        // Prominent spurs
        boneThickness = MorphRange(0.55f, 0.85f, 0.75f),                         // Maximum bone
        featherDensity = MorphRange(0.75f, 1.0f, 0.9f),                          // Full plumage
        allowedCombs = CombStyle.entries.toList(),
        allowedTails = TailStyle.entries.toList(),
        allowedNails = NailStyle.entries.toList(),
        allowedWattles = WattleStyle.entries.toList(),
        allowedStances = Stance.entries.toList(),
        allowedSheens = Sheen.entries.toList(),
        allowedNecks = NeckStyle.entries.toList(),
        allowedBreasts = BreastShape.entries.toList(),
        defaultBodySize = if (isMale) BodySize.LARGE else BodySize.MEDIUM,
        defaultStance = if (isMale) Stance.GAME_READY else Stance.NORMAL,
        defaultSheen = if (isMale) Sheen.IRIDESCENT else Sheen.GLOSSY,
        defaultFeatherTexture = "Peak"
    )

    // 🦅 SENIOR (2+ years) — Slight dulling, very thick spurs, heavy neck
    private fun seniorConstraints(isMale: Boolean) = StageMorphConstraints(
        stage = BiologicalStage.SENIOR, isMale = isMale,
        bodyWidth = MorphRange(0.5f, 0.8f, if (isMale) 0.65f else 0.5f),
        bodyRoundness = MorphRange(0.25f, 0.55f, 0.4f),      // Slight sag
        legLength = MorphRange(0.6f, 0.85f, 0.72f),
        legThickness = MorphRange(0.55f, 0.85f, if (isMale) 0.75f else 0.55f),
        tailLength = MorphRange(0.45f, 0.85f, if (isMale) 0.72f else 0.45f),  // Slightly less flamboyant
        tailAngle = MorphRange(0.4f, 0.7f, 0.55f),
        tailSpread = MorphRange(0.25f, 0.55f, 0.4f),
        combSize = MorphRange(0.25f, 0.65f, if (isMale) 0.5f else 0.3f),
        beakScale = MorphRange(0.45f, 0.65f, 0.55f),
        beakCurvature = MorphRange(0.45f, 0.7f, 0.6f),
        neckLength = MorphRange(0.55f, 0.85f, 0.7f),
        neckThickness = MorphRange(0.6f, 0.9f, if (isMale) 0.8f else 0.6f),  // Even thicker
        chestDepth = MorphRange(0.5f, 0.8f, if (isMale) 0.7f else 0.55f),    // Slight sag
        hackleLength = MorphRange(0.4f, 0.9f, if (isMale) 0.8f else 0.15f),
        spurSize = MorphRange(0.5f, 1.0f, if (isMale) 0.85f else 0.0f),       // Very thick spurs
        boneThickness = MorphRange(0.6f, 0.9f, 0.8f),
        featherDensity = MorphRange(0.6f, 0.9f, 0.75f),                        // Slight dulling
        allowedCombs = CombStyle.entries.toList(),
        allowedTails = TailStyle.entries.toList(),
        allowedNails = NailStyle.entries.toList(),
        allowedWattles = WattleStyle.entries.toList(),
        allowedStances = Stance.entries.toList(),
        allowedSheens = listOf(Sheen.MATTE, Sheen.SATIN, Sheen.GLOSSY, Sheen.METALLIC),  // No iridescent
        allowedNecks = NeckStyle.entries.toList(),
        allowedBreasts = BreastShape.entries.toList(),
        defaultBodySize = if (isMale) BodySize.LARGE else BodySize.MEDIUM,
        defaultStance = Stance.NORMAL,
        defaultSheen = Sheen.GLOSSY,      // Slight dulling from peak
        defaultFeatherTexture = "Dulling"
    )
}
