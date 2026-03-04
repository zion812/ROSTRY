package com.rio.rostry.domain.model

/**
 * Weight benchmarks for NatuKodi (Desi/Country Chicken).
 * Based on AP State Hatchery reference data.
 * 
 * Growth Chart: 0-20 weeks with expected weight ranges
 * Mature Birds: Gender-specific weight ranges for adults
 */
object PoultryWeightBenchmarks {
    
    /**
     * Weight benchmark for a specific age range
     */
    data class WeightBenchmark(
        val weekNumber: Int,
        val minDays: Int,
        val maxDays: Int,
        val minWeightGrams: Int,
        val maxWeightGrams: Int
    ) {
        val midpointWeight: Int get() = (minWeightGrams + maxWeightGrams) / 2
    }
    
    /**
     * Weight validation result - non-blocking warnings
     */
    sealed class WeightValidationResult {
        data object Valid : WeightValidationResult()
        data class BelowExpected(
            val actualWeight: Int,
            val expectedRange: IntRange,
            val message: String
        ) : WeightValidationResult()
        data class AboveExpected(
            val actualWeight: Int,
            val expectedRange: IntRange,
            val message: String
        ) : WeightValidationResult()
        data object UnknownAge : WeightValidationResult()
    }
    
    // NatuKodi Growth Chart (0-20 weeks)
    val growthChart: List<WeightBenchmark> = listOf(
        WeightBenchmark(0, 0, 7, 30, 60),
        WeightBenchmark(1, 8, 14, 60, 80),
        WeightBenchmark(2, 15, 21, 100, 140),
        WeightBenchmark(3, 22, 28, 180, 230),
        WeightBenchmark(4, 29, 35, 260, 320),
        WeightBenchmark(5, 36, 42, 350, 420),
        WeightBenchmark(6, 43, 49, 430, 520),
        WeightBenchmark(7, 50, 56, 500, 600),
        WeightBenchmark(8, 57, 63, 600, 700),
        WeightBenchmark(9, 64, 70, 650, 800),
        WeightBenchmark(10, 71, 77, 700, 850),
        WeightBenchmark(11, 78, 84, 750, 900),
        WeightBenchmark(12, 85, 91, 800, 950),
        WeightBenchmark(14, 99, 105, 850, 1050),
        WeightBenchmark(16, 113, 119, 900, 1150),
        WeightBenchmark(18, 127, 133, 950, 1250),
        WeightBenchmark(20, 141, 147, 1000, 1400)
    )
    
    // Mature bird weight ranges (grams)
    val maleAdultRange = 2500..3500  // Rooster: 2.5 - 3.5 kg
    val femaleAdultRange = 1500..2000  // Hen: 1.5 - 2.0 kg
    
    // Threshold for considering a bird as mature (days)
    const val MATURE_AGE_THRESHOLD_DAYS = 150  // ~5 months
    
    /**
     * Get expected weight range for a given age in days.
     * Returns null if age is beyond growth chart range.
     */
    fun getExpectedRangeForAge(ageInDays: Int): IntRange? {
        if (ageInDays < 0) return null
        
        // Find matching week in growth chart
        val benchmark = growthChart.find { ageInDays in it.minDays..it.maxDays }
        if (benchmark != null) {
            return benchmark.minWeightGrams..benchmark.maxWeightGrams
        }
        
        // Interpolate for gaps (e.g., week 13, 15, 17, 19)
        if (ageInDays in 92..98) {
            // Between week 12 and 14
            return 825..1000
        }
        if (ageInDays in 106..112) {
            // Between week 14 and 16
            return 875..1100
        }
        if (ageInDays in 120..126) {
            // Between week 16 and 18
            return 925..1200
        }
        if (ageInDays in 134..140) {
            // Between week 18 and 20
            return 975..1325
        }
        
        // Beyond 20 weeks - return mature range (unsexed)
        if (ageInDays > 147) {
            return 1000..3500  // Wide range as gender-specific would be better
        }
        
        return null
    }
    
    /**
     * Get expected weight range for a mature bird based on gender.
     * @param gender "Male", "Female", "M", "F", or similar variations
     */
    fun getExpectedRangeForGender(gender: String?): IntRange? {
        return when (gender?.lowercase()?.trim()) {
            "male", "m", "rooster", "punju", "కోడిపుంజు" -> maleAdultRange
            "female", "f", "hen", "petta", "కోడి పెట్ట" -> femaleAdultRange
            else -> null
        }
    }
    
    /**
     * Validate weight against expected benchmarks.
     * Uses age-based validation for young birds, gender-based for mature birds.
     * 
     * @param weightGrams The weight entered by user
     * @param ageInDays Bird's age in days (null if unknown)
     * @param gender Bird's gender (relevant for mature birds)
     * @return WeightValidationResult - warning if outside expected range
     */
    fun validateWeight(
        weightGrams: Int,
        ageInDays: Int?,
        gender: String?
    ): WeightValidationResult {
        if (weightGrams <= 0) {
            return WeightValidationResult.UnknownAge  // Invalid weight
        }
        
        // For mature birds, use gender-specific validation
        if (ageInDays != null && ageInDays >= MATURE_AGE_THRESHOLD_DAYS) {
            val genderRange = getExpectedRangeForGender(gender)
            if (genderRange != null) {
                return validateAgainstRange(weightGrams, genderRange, "mature ${gender?.lowercase() ?: "bird"}")
            }
            // If gender unknown for mature bird, use combined range
            val combinedRange = femaleAdultRange.first..maleAdultRange.last
            return validateAgainstRange(weightGrams, combinedRange, "mature bird")
        }
        
        // For young birds, use age-based validation
        if (ageInDays != null) {
            val ageRange = getExpectedRangeForAge(ageInDays)
            if (ageRange != null) {
                val weekNum = ageInDays / 7
                return validateAgainstRange(weightGrams, ageRange, "$weekNum-week bird")
            }
        }
        
        return WeightValidationResult.UnknownAge
    }
    
    private fun validateAgainstRange(
        weight: Int,
        expectedRange: IntRange,
        ageDescription: String
    ): WeightValidationResult {
        return when {
            weight < expectedRange.first -> WeightValidationResult.BelowExpected(
                actualWeight = weight,
                expectedRange = expectedRange,
                message = "Weight ${weight}g is below expected range (${expectedRange.first}-${expectedRange.last}g) for $ageDescription"
            )
            weight > expectedRange.last -> WeightValidationResult.AboveExpected(
                actualWeight = weight,
                expectedRange = expectedRange,
                message = "Weight ${weight}g is above expected range (${expectedRange.first}-${expectedRange.last}g) for $ageDescription"
            )
            else -> WeightValidationResult.Valid
        }
    }
    
    /**
     * Get suggested weight (midpoint) for a given age.
     */
    fun getSuggestedWeight(ageInDays: Int?, gender: String?): Int? {
        if (ageInDays == null || ageInDays < 0) return null
        
        // For mature birds with known gender
        if (ageInDays >= MATURE_AGE_THRESHOLD_DAYS) {
            val genderRange = getExpectedRangeForGender(gender)
            if (genderRange != null) {
                return (genderRange.first + genderRange.last) / 2
            }
            return 2000  // Default midpoint for unsexed mature bird
        }
        
        // For young birds
        val range = getExpectedRangeForAge(ageInDays)
        return range?.let { (it.first + it.last) / 2 }
    }
    
    /**
     * Calculate age in days from birth date millis.
     */
    fun calculateAgeInDays(birthDateMillis: Long?): Int? {
        if (birthDateMillis == null || birthDateMillis <= 0) return null
        val now = System.currentTimeMillis()
        if (birthDateMillis > now) return null  // Future date
        return ((now - birthDateMillis) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    /**
     * Get human-readable age description.
     */
    fun getAgeDescription(ageInDays: Int): String {
        return when {
            ageInDays < 7 -> "$ageInDays days"
            ageInDays < 28 -> "${ageInDays / 7} week${if (ageInDays / 7 > 1) "s" else ""}"
            ageInDays < 365 -> "${ageInDays / 30} month${if (ageInDays / 30 > 1) "s" else ""}"
            else -> "${ageInDays / 365} year${if (ageInDays / 365 > 1) "s" else ""}"
        }
    }
}
