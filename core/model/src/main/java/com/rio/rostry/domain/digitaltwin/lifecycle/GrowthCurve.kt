package com.rio.rostry.domain.digitaltwin.lifecycle

/**
 * 🐓 GrowthCurve — Breed-Specific Growth Trajectory Model
 *
 * Defines expected weight, height, and morph development curves for
 * Aseel (and extensible to other breeds). Used for:
 * - Growth analytics (is bird on track?)
 * - Weight prediction
 * - Feed impact modeling
 * - Anomaly detection
 *
 * Based on real Aseel poultry science data points.
 */
object GrowthCurve {

    /**
     * Expected weight at a given age in days (grams).
     * Based on typical Aseel growth curves.
     */
    fun expectedWeight(ageDays: Int, isMale: Boolean): WeightExpectation {
        val idealGrams = if (isMale) maleWeightCurve(ageDays) else femaleWeightCurve(ageDays)
        val minGrams = (idealGrams * 0.80).toInt()  // -20% acceptable
        val maxGrams = (idealGrams * 1.15).toInt()   // +15% acceptable

        return WeightExpectation(
            ageDays = ageDays,
            idealGrams = idealGrams,
            minGrams = minGrams,
            maxGrams = maxGrams,
            isMale = isMale
        )
    }

    /**
     * Evaluate actual weight against expected.
     */
    fun evaluateWeight(ageDays: Int, actualGrams: Int, isMale: Boolean): WeightEvaluation {
        val expected = expectedWeight(ageDays, isMale)
        val ratio = actualGrams.toFloat() / expected.idealGrams

        val rating = when {
            actualGrams < expected.minGrams -> WeightRating.UNDERWEIGHT
            actualGrams > expected.maxGrams -> WeightRating.OVERWEIGHT
            ratio in 0.95f..1.05f -> WeightRating.EXCELLENT
            ratio in 0.90f..1.10f -> WeightRating.GOOD
            else -> WeightRating.FAIR
        }

        return WeightEvaluation(
            expected = expected,
            actualGrams = actualGrams,
            ratio = ratio,
            deviationPercent = ((ratio - 1f) * 100).toInt(),
            rating = rating
        )
    }

    /**
     * Generate a full growth curve for display.
     * Returns weight expectations from hatch to specified max age.
     */
    fun generateCurve(isMale: Boolean, maxDays: Int = 730, stepDays: Int = 7): List<WeightExpectation> {
        return (0..maxDays step stepDays).map { day ->
            expectedWeight(day, isMale)
        }
    }

    /**
     * Predict weight at future date based on current trajectory.
     * Uses simple linear extrapolation from last 2 data points.
     */
    fun predictFutureWeight(
        currentAgeDays: Int,
        currentWeightGrams: Int,
        previousWeightGrams: Int,
        previousAgeDays: Int,
        targetAgeDays: Int,
        isMale: Boolean
    ): WeightPrediction {
        val daysDiff = currentAgeDays - previousAgeDays
        if (daysDiff <= 0) {
            return WeightPrediction(
                targetAgeDays = targetAgeDays,
                predictedGrams = currentWeightGrams,
                confidence = 0f,
                method = "Insufficient data"
            )
        }

        val dailyGain = (currentWeightGrams - previousWeightGrams).toFloat() / daysDiff
        val daysToTarget = targetAgeDays - currentAgeDays
        val predicted = (currentWeightGrams + dailyGain * daysToTarget).toInt()

        // Compare with ideal to get confidence
        val ideal = expectedWeight(targetAgeDays, isMale)
        val deviation = kotlin.math.abs(predicted - ideal.idealGrams).toFloat() / ideal.idealGrams
        val confidence = (1f - deviation).coerceIn(0.3f, 0.95f)

        return WeightPrediction(
            targetAgeDays = targetAgeDays,
            predictedGrams = predicted.coerceAtLeast(currentWeightGrams), // Can't lose weight in prediction
            confidence = confidence,
            method = "Linear extrapolation + breed curve"
        )
    }

    // ==================== ASEEL WEIGHT CURVES ====================

    /**
     * Male Aseel weight curve (grams).
     * Data points from Aseel poultry science references.
     */
    private fun maleWeightCurve(ageDays: Int): Int {
        // Key data points (age_days -> grams)
        // Hatch: ~35g, 4w: 180g, 8w: 500g, 12w: 900g, 16w: 1400g
        // 20w: 1900g, 24w: 2400g, 32w: 3200g, 40w: 3700g, 52w: 4200g
        // 78w: 4800g, 104w: 5000g
        val dataPoints = listOf(
            0 to 35,
            7 to 55,
            14 to 90,
            21 to 130,
            28 to 180,
            42 to 320,
            56 to 500,
            70 to 700,
            84 to 900,
            98 to 1150,
            112 to 1400,
            140 to 1900,
            168 to 2400,
            196 to 2850,
            224 to 3200,
            252 to 3500,
            280 to 3700,
            308 to 3900,
            336 to 4050,
            365 to 4200,
            455 to 4600,
            548 to 4800,
            730 to 5000
        )

        return interpolateDataPoints(dataPoints, ageDays)
    }

    /**
     * Female Aseel weight curve (grams).
     * Typically 70-75% of male weight.
     */
    private fun femaleWeightCurve(ageDays: Int): Int {
        return (maleWeightCurve(ageDays) * 0.72).toInt()
    }

    /**
     * Linear interpolation between data points.
     */
    private fun interpolateDataPoints(points: List<Pair<Int, Int>>, day: Int): Int {
        if (day <= points.first().first) return points.first().second
        if (day >= points.last().first) return points.last().second

        for (i in 0 until points.size - 1) {
            val (d1, w1) = points[i]
            val (d2, w2) = points[i + 1]
            if (day in d1..d2) {
                val t = (day - d1).toFloat() / (d2 - d1)
                return (w1 + (w2 - w1) * t).toInt()
            }
        }

        return points.last().second
    }
}

data class WeightExpectation(
    val ageDays: Int,
    val idealGrams: Int,
    val minGrams: Int,
    val maxGrams: Int,
    val isMale: Boolean
)

data class WeightEvaluation(
    val expected: WeightExpectation,
    val actualGrams: Int,
    val ratio: Float,
    val deviationPercent: Int,
    val rating: WeightRating
)

enum class WeightRating(val displayName: String, val emoji: String) {
    UNDERWEIGHT("Underweight", "⚠️"),
    FAIR("Fair", "🔶"),
    GOOD("Good", "✅"),
    EXCELLENT("Excellent", "🌟"),
    OVERWEIGHT("Overweight", "⚠️")
}

data class WeightPrediction(
    val targetAgeDays: Int,
    val predictedGrams: Int,
    val confidence: Float,
    val method: String
)
