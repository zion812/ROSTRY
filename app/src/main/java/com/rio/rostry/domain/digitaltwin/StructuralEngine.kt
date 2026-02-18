package com.rio.rostry.domain.digitaltwin

import com.rio.rostry.domain.model.BirdAppearance

/**
 * The Brain of the Digital Twin System.
 * Calculates ASI (Aseel Structural Index) and validates authenticity.
 */
object StructuralEngine {

    // Defined Weights based on "Power Frame" standard
    private const val WEIGHT_NECK = 0.15f
    private const val WEIGHT_LEG = 0.15f
    private const val WEIGHT_BONE = 0.15f
    private const val WEIGHT_CHEST = 0.15f
    private const val WEIGHT_FEATHER = 0.10f
    private const val WEIGHT_POSTURE = 0.10f
    private const val WEIGHT_TAIL = 0.10f
    private const val WEIGHT_WIDTH = 0.10f

    /**
     * Calculates the Aseel Structural Index (0-100).
     * A score below 60 is considered "Non-Aseel / Mixed".
     * A score above 90 is "Show Quality Structure".
     */
    fun calculateASI(profile: StructureProfile): AseelStructuralIndex {
        var score = 0f
        val warnings = mutableListOf<String>()

        // 1. Neck Length (Aseel needs long neck, > 0.6)
        val neckScore = calculateIdeallyHigh(profile.neckLength, 0.6f)
        score += neckScore * WEIGHT_NECK * 100
        if (profile.neckLength < 0.4f) warnings.add("Neck too short for Aseel standard")

        // 2. Leg Length (Aseel needs height, > 0.6)
        val legScore = calculateIdeallyHigh(profile.legLength, 0.6f)
        score += legScore * WEIGHT_LEG * 100
        if (profile.legLength < 0.4f) warnings.add("Legs too short (Creeper trait)")

        // 3. Bone Thickness (Heavy bone preferred, > 0.7)
        val boneScore = calculateIdeallyHigh(profile.boneThickness, 0.7f)
        score += boneScore * WEIGHT_BONE * 100
        if (profile.boneThickness < 0.4f) warnings.add("Bone structure too fine")

        // 4. Chest Depth (Deep keel, > 0.6)
        val chestScore = calculateIdeallyHigh(profile.chestDepth, 0.6f)
        score += chestScore * WEIGHT_CHEST * 100

        // 5. Feather Tightness (Must be tight/hard, > 0.7)
        val featherScore = calculateIdeallyHigh(profile.featherTightness, 0.7f)
        score += featherScore * WEIGHT_FEATHER * 100
        if (profile.featherTightness < 0.5f) warnings.add("Feathering too loose/fluffy")

        // 6. Posture (Upright/Game Ready, > 0.7)
        val postureScore = calculateIdeallyHigh(profile.postureAngle, 0.7f)
        score += postureScore * WEIGHT_POSTURE * 100
        if (profile.postureAngle < 0.5f) warnings.add("Posture too horizontal")

        // 7. Tail Carriage (Low/Medium, < 0.5 is better for some, < 0.8 for others)
        // Aseel generally carry tail lower than Leghorn (squirrel tail is bad)
        val tailScore = calculateIdeallyLow(profile.tailCarriage, 0.6f) 
        score += tailScore * WEIGHT_TAIL * 100
        if (profile.tailCarriage > 0.8f) warnings.add("Squirrel tail fault")

        // 8. Body Width (Broad shoulders, > 0.6)
        val widthScore = calculateIdeallyHigh(profile.bodyWidth, 0.6f)
        score += widthScore * WEIGHT_WIDTH * 100

        // Clamp final score
        val finalScore = score.coerceIn(0f, 100f).toInt()

        return AseelStructuralIndex(
            score = finalScore,
            validationWarnings = warnings
        )
    }

    // Helper: Score is higher if value approaches or exceeds target
    private fun calculateIdeallyHigh(value: Float, target: Float): Float {
        // Linear ramp: 0.0 -> 0 score, Target -> 1.0 score
        // Exceeding target keeps it at 1.0
        return (value / target).coerceIn(0f, 1.0f)
    }

    // Helper: Score is higher if value is lower than target (penalty for excess)
    private fun calculateIdeallyLow(value: Float, threshold: Float): Float {
        return if (value <= threshold) 1.0f
        else 1.0f - ((value - threshold) / (1.0f - threshold))
    }
}
