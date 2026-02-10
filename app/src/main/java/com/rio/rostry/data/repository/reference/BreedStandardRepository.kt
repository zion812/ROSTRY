package com.rio.rostry.data.repository.reference

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for breed-specific performance standards.
 * Provides benchmark data for growth (weight) and Feed Conversion Ratio (FCR).
 *
 * Currently supports:
 * - Cobb 500 (Broiler)
 * - Ross 308 (Broiler)
 * - Kuroiler (Dual purpose)
 * - Sasso (Dual purpose)
 */
@Singleton
class BreedStandardRepository @Inject constructor() {

    // Cache of standards — Commercial + Indian Native breeds
    private val standards = mapOf(
        // Commercial breeds
        "Cobb 500" to createCobb500Standard(),
        "Ross 308" to createRoss308Standard(),
        "Kuroiler" to createKuroilerStandard(),
        "Sasso" to createSassoStandard(),
        // Indian Native breeds (core ROSTRY market)
        "Aseel" to createAseelStandard(),
        "Kadaknath" to createKadaknathStandard(),
        "Natu Kodi" to createNatuKodiStandard(),
        "Giriraja" to createGrirajaStandard(),
        "Vanaraja" to createVanarajaStandard()
    )

    /**
     * Get list of all available breed names for benchmarking.
     */
    fun getAvailableBreeds(): List<String> {
        return standards.keys.sorted()
    }

    /**
     * Get the standard performance metris for a specific breed.
     */
    fun getStandard(breedName: String): BreedStandard? {
        return standards[breedName]
    }

    /**
     * Get expectations for a specific week.
     */
    fun getWeeklyExpectation(breedName: String, week: Int): WeeklyStandard? {
        return standards[breedName]?.weeklyStandards?.find { it.week == week }
    }

    // ============ Data Definitions ============

    private fun createCobb500Standard(): BreedStandard {
        return BreedStandard(
            breedName = "Cobb 500",
            type = BreedType.BROILER,
            description = "Efficient feed conversion, excellent growth rate.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 0, targetWeightGrams = 42, cumulativeFeedGrams = 0, targetFCR = 0.0f),
                WeeklyStandard(week = 1, targetWeightGrams = 185, cumulativeFeedGrams = 167, targetFCR = 0.90f),
                WeeklyStandard(week = 2, targetWeightGrams = 465, cumulativeFeedGrams = 523, targetFCR = 1.12f),
                WeeklyStandard(week = 3, targetWeightGrams = 918, cumulativeFeedGrams = 1139, targetFCR = 1.24f),
                WeeklyStandard(week = 4, targetWeightGrams = 1526, cumulativeFeedGrams = 2058, targetFCR = 1.35f),
                WeeklyStandard(week = 5, targetWeightGrams = 2237, cumulativeFeedGrams = 3288, targetFCR = 1.47f),
                WeeklyStandard(week = 6, targetWeightGrams = 2998, cumulativeFeedGrams = 4789, targetFCR = 1.60f),
                WeeklyStandard(week = 7, targetWeightGrams = 3749, cumulativeFeedGrams = 6499, targetFCR = 1.73f),
                WeeklyStandard(week = 8, targetWeightGrams = 4453, cumulativeFeedGrams = 8352, targetFCR = 1.88f)
            )
        )
    }

    private fun createRoss308Standard(): BreedStandard {
        return BreedStandard(
            breedName = "Ross 308",
            type = BreedType.BROILER,
            description = "Robust performance, good for various environments.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 0, targetWeightGrams = 42, cumulativeFeedGrams = 0, targetFCR = 0.0f),
                WeeklyStandard(week = 1, targetWeightGrams = 189, cumulativeFeedGrams = 170, targetFCR = 0.90f),
                WeeklyStandard(week = 2, targetWeightGrams = 480, cumulativeFeedGrams = 540, targetFCR = 1.13f),
                WeeklyStandard(week = 3, targetWeightGrams = 959, cumulativeFeedGrams = 1190, targetFCR = 1.24f),
                WeeklyStandard(week = 4, targetWeightGrams = 1599, cumulativeFeedGrams = 2150, targetFCR = 1.34f),
                WeeklyStandard(week = 5, targetWeightGrams = 2334, cumulativeFeedGrams = 3420, targetFCR = 1.47f),
                WeeklyStandard(week = 6, targetWeightGrams = 3110, cumulativeFeedGrams = 4960, targetFCR = 1.59f),
                WeeklyStandard(week = 7, targetWeightGrams = 3876, cumulativeFeedGrams = 6690, targetFCR = 1.73f)
            )
        )
    }

    private fun createKuroilerStandard(): BreedStandard {
        // Kuroilers are slower growing dual purpose
        return BreedStandard(
            breedName = "Kuroiler",
            type = BreedType.DUAL_PURPOSE,
            description = "Hardy dual-purpose breed, slower growth but better resilience.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 100, cumulativeFeedGrams = 120, targetFCR = 1.2f),
                WeeklyStandard(week = 2, targetWeightGrams = 250, cumulativeFeedGrams = 400, targetFCR = 1.6f),
                WeeklyStandard(week = 3, targetWeightGrams = 450, cumulativeFeedGrams = 850, targetFCR = 1.9f),
                WeeklyStandard(week = 4, targetWeightGrams = 700, cumulativeFeedGrams = 1500, targetFCR = 2.1f),
                WeeklyStandard(week = 5, targetWeightGrams = 1000, cumulativeFeedGrams = 2400, targetFCR = 2.4f), // ~1kg at 5 weeks
                WeeklyStandard(week = 8, targetWeightGrams = 2000, cumulativeFeedGrams = 6000, targetFCR = 3.0f)  // ~2kg at 8 weeks
            )
        )
    }

    private fun createSassoStandard(): BreedStandard {
         return BreedStandard(
            breedName = "Sasso",
            type = BreedType.DUAL_PURPOSE,
            description = "Premium meat quality, slower growth.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 110, cumulativeFeedGrams = 130, targetFCR = 1.18f),
                WeeklyStandard(week = 2, targetWeightGrams = 280, cumulativeFeedGrams = 420, targetFCR = 1.5f),
                WeeklyStandard(week = 3, targetWeightGrams = 520, cumulativeFeedGrams = 950, targetFCR = 1.82f),
                WeeklyStandard(week = 4, targetWeightGrams = 850, cumulativeFeedGrams = 1800, targetFCR = 2.1f),
                WeeklyStandard(week = 9, targetWeightGrams = 2200, cumulativeFeedGrams = 6500, targetFCR = 2.95f)
            )
        )
    }

    // ============ Indian Native Breeds ============

    private fun createAseelStandard(): BreedStandard {
        // Aseel: Premium fighting/show breed, very slow growth, high adoption value
        return BreedStandard(
            breedName = "Aseel",
            type = BreedType.INDIGENOUS,
            description = "Premium native game bird. Slow growth, muscular build, high adoption value.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 50, cumulativeFeedGrams = 70, targetFCR = 1.4f),
                WeeklyStandard(week = 4, targetWeightGrams = 200, cumulativeFeedGrams = 500, targetFCR = 2.5f),
                WeeklyStandard(week = 8, targetWeightGrams = 500, cumulativeFeedGrams = 1500, targetFCR = 3.0f),
                WeeklyStandard(week = 12, targetWeightGrams = 900, cumulativeFeedGrams = 3200, targetFCR = 3.6f),
                WeeklyStandard(week = 20, targetWeightGrams = 1800, cumulativeFeedGrams = 7500, targetFCR = 4.2f),
                WeeklyStandard(week = 30, targetWeightGrams = 2800, cumulativeFeedGrams = 14000, targetFCR = 5.0f)
            )
        )
    }

    private fun createKadaknathStandard(): BreedStandard {
        // Kadaknath: Black meat chicken, premium price, medicinal value
        return BreedStandard(
            breedName = "Kadaknath",
            type = BreedType.INDIGENOUS,
            description = "Premium black meat breed from Madhya Pradesh. Medicinal value, low fat content.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 40, cumulativeFeedGrams = 60, targetFCR = 1.5f),
                WeeklyStandard(week = 4, targetWeightGrams = 180, cumulativeFeedGrams = 450, targetFCR = 2.5f),
                WeeklyStandard(week = 8, targetWeightGrams = 450, cumulativeFeedGrams = 1400, targetFCR = 3.1f),
                WeeklyStandard(week = 12, targetWeightGrams = 750, cumulativeFeedGrams = 2800, targetFCR = 3.7f),
                WeeklyStandard(week = 20, targetWeightGrams = 1200, cumulativeFeedGrams = 5500, targetFCR = 4.6f)
            )
        )
    }

    private fun createNatuKodiStandard(): BreedStandard {
        // Natu Kodi: Country/Desi chicken, most common backyard bird in South India
        return BreedStandard(
            breedName = "Natu Kodi",
            type = BreedType.INDIGENOUS,
            description = "South Indian country chicken. Hardy, free-range, rich taste. Most popular for meat.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 45, cumulativeFeedGrams = 65, targetFCR = 1.4f),
                WeeklyStandard(week = 4, targetWeightGrams = 190, cumulativeFeedGrams = 480, targetFCR = 2.5f),
                WeeklyStandard(week = 8, targetWeightGrams = 480, cumulativeFeedGrams = 1450, targetFCR = 3.0f),
                WeeklyStandard(week = 12, targetWeightGrams = 800, cumulativeFeedGrams = 2900, targetFCR = 3.6f),
                WeeklyStandard(week = 20, targetWeightGrams = 1500, cumulativeFeedGrams = 6000, targetFCR = 4.0f)
            )
        )
    }

    private fun createGrirajaStandard(): BreedStandard {
        // Giriraja: Government-promoted dual purpose, better than pure desi
        return BreedStandard(
            breedName = "Giriraja",
            type = BreedType.DUAL_PURPOSE,
            description = "Government-developed dual-purpose for backyard farming. Better growth than pure native.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 80, cumulativeFeedGrams = 100, targetFCR = 1.25f),
                WeeklyStandard(week = 4, targetWeightGrams = 350, cumulativeFeedGrams = 700, targetFCR = 2.0f),
                WeeklyStandard(week = 8, targetWeightGrams = 900, cumulativeFeedGrams = 2200, targetFCR = 2.4f),
                WeeklyStandard(week = 12, targetWeightGrams = 1500, cumulativeFeedGrams = 4200, targetFCR = 2.8f),
                WeeklyStandard(week = 20, targetWeightGrams = 2500, cumulativeFeedGrams = 8500, targetFCR = 3.4f)
            )
        )
    }

    private fun createVanarajaStandard(): BreedStandard {
        // Vanaraja: Developed by PDP for backyard poultry, multicolored
        return BreedStandard(
            breedName = "Vanaraja",
            type = BreedType.DUAL_PURPOSE,
            description = "Backyard breed developed by PDP. Multicolored, hardy, good for small-scale farming.",
            weeklyStandards = listOf(
                WeeklyStandard(week = 1, targetWeightGrams = 75, cumulativeFeedGrams = 95, targetFCR = 1.27f),
                WeeklyStandard(week = 4, targetWeightGrams = 320, cumulativeFeedGrams = 650, targetFCR = 2.0f),
                WeeklyStandard(week = 8, targetWeightGrams = 850, cumulativeFeedGrams = 2100, targetFCR = 2.5f),
                WeeklyStandard(week = 12, targetWeightGrams = 1400, cumulativeFeedGrams = 4000, targetFCR = 2.9f),
                WeeklyStandard(week = 20, targetWeightGrams = 2300, cumulativeFeedGrams = 8000, targetFCR = 3.5f)
            )
        )
    }
}

data class BreedStandard(
    val breedName: String,
    val type: BreedType,
    val description: String,
    val weeklyStandards: List<WeeklyStandard>
)

data class WeeklyStandard(
    val week: Int,
    val targetWeightGrams: Int,
    val cumulativeFeedGrams: Int,
    val targetFCR: Float
)

enum class BreedType {
    BROILER, LAYER, DUAL_PURPOSE, INDIGENOUS
}
