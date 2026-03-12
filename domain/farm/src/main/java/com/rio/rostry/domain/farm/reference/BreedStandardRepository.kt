package com.rio.rostry.domain.farm.reference

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for breed-specific performance standards.
 * Provides benchmark data for growth (weight) and Feed Conversion Ratio (FCR).
 *
 * This is a pure in-memory data class — no DAO dependencies, no Hilt binding needed.
 * Moved from app/data/repository/reference/ to domain:farm as part of modularization.
 */
@Singleton
class BreedStandardRepository @Inject constructor() {

    private val standards = mapOf(
        "Cobb 500" to createCobb500Standard(),
        "Ross 308" to createRoss308Standard(),
        "Kuroiler" to createKuroilerStandard(),
        "Sasso" to createSassoStandard(),
        "Aseel" to createAseelStandard(),
        "Kadaknath" to createKadaknathStandard(),
        "Natu Kodi" to createNatuKodiStandard(),
        "Giriraja" to createGrirajaStandard(),
        "Vanaraja" to createVanarajaStandard()
    )

    fun getAvailableBreeds(): List<String> = standards.keys.sorted()
    fun getStandard(breedName: String): BreedStandard? = standards[breedName]
    fun getWeeklyExpectation(breedName: String, week: Int): WeeklyStandard? =
        standards[breedName]?.weeklyStandards?.find { it.week == week }

    private fun createCobb500Standard() = BreedStandard("Cobb 500", BreedType.BROILER, "Efficient feed conversion, excellent growth rate.", listOf(
        WeeklyStandard(0, 42, 0, 0.0f), WeeklyStandard(1, 185, 167, 0.90f), WeeklyStandard(2, 465, 523, 1.12f),
        WeeklyStandard(3, 918, 1139, 1.24f), WeeklyStandard(4, 1526, 2058, 1.35f), WeeklyStandard(5, 2237, 3288, 1.47f),
        WeeklyStandard(6, 2998, 4789, 1.60f), WeeklyStandard(7, 3749, 6499, 1.73f), WeeklyStandard(8, 4453, 8352, 1.88f)
    ))
    private fun createRoss308Standard() = BreedStandard("Ross 308", BreedType.BROILER, "Robust performance, good for various environments.", listOf(
        WeeklyStandard(0, 42, 0, 0.0f), WeeklyStandard(1, 189, 170, 0.90f), WeeklyStandard(2, 480, 540, 1.13f),
        WeeklyStandard(3, 959, 1190, 1.24f), WeeklyStandard(4, 1599, 2150, 1.34f), WeeklyStandard(5, 2334, 3420, 1.47f),
        WeeklyStandard(6, 3110, 4960, 1.59f), WeeklyStandard(7, 3876, 6690, 1.73f)
    ))
    private fun createKuroilerStandard() = BreedStandard("Kuroiler", BreedType.DUAL_PURPOSE, "Hardy dual-purpose breed, slower growth but better resilience.", listOf(
        WeeklyStandard(1, 100, 120, 1.2f), WeeklyStandard(2, 250, 400, 1.6f), WeeklyStandard(3, 450, 850, 1.9f),
        WeeklyStandard(4, 700, 1500, 2.1f), WeeklyStandard(5, 1000, 2400, 2.4f), WeeklyStandard(8, 2000, 6000, 3.0f)
    ))
    private fun createSassoStandard() = BreedStandard("Sasso", BreedType.DUAL_PURPOSE, "Premium meat quality, slower growth.", listOf(
        WeeklyStandard(1, 110, 130, 1.18f), WeeklyStandard(2, 280, 420, 1.5f), WeeklyStandard(3, 520, 950, 1.82f),
        WeeklyStandard(4, 850, 1800, 2.1f), WeeklyStandard(9, 2200, 6500, 2.95f)
    ))
    private fun createAseelStandard() = BreedStandard("Aseel", BreedType.INDIGENOUS, "Premium native game bird. Slow growth, muscular build, high adoption value.", listOf(
        WeeklyStandard(1, 50, 70, 1.4f), WeeklyStandard(4, 200, 500, 2.5f), WeeklyStandard(8, 500, 1500, 3.0f),
        WeeklyStandard(12, 900, 3200, 3.6f), WeeklyStandard(20, 1800, 7500, 4.2f), WeeklyStandard(30, 2800, 14000, 5.0f)
    ))
    private fun createKadaknathStandard() = BreedStandard("Kadaknath", BreedType.INDIGENOUS, "Premium black meat breed from Madhya Pradesh. Medicinal value, low fat content.", listOf(
        WeeklyStandard(1, 40, 60, 1.5f), WeeklyStandard(4, 180, 450, 2.5f), WeeklyStandard(8, 450, 1400, 3.1f),
        WeeklyStandard(12, 750, 2800, 3.7f), WeeklyStandard(20, 1200, 5500, 4.6f)
    ))
    private fun createNatuKodiStandard() = BreedStandard("Natu Kodi", BreedType.INDIGENOUS, "South Indian country chicken. Hardy, free-range, rich taste.", listOf(
        WeeklyStandard(1, 45, 65, 1.4f), WeeklyStandard(4, 190, 480, 2.5f), WeeklyStandard(8, 480, 1450, 3.0f),
        WeeklyStandard(12, 800, 2900, 3.6f), WeeklyStandard(20, 1500, 6000, 4.0f)
    ))
    private fun createGrirajaStandard() = BreedStandard("Giriraja", BreedType.DUAL_PURPOSE, "Government-developed dual-purpose for backyard farming.", listOf(
        WeeklyStandard(1, 80, 100, 1.25f), WeeklyStandard(4, 350, 700, 2.0f), WeeklyStandard(8, 900, 2200, 2.4f),
        WeeklyStandard(12, 1500, 4200, 2.8f), WeeklyStandard(20, 2500, 8500, 3.4f)
    ))
    private fun createVanarajaStandard() = BreedStandard("Vanaraja", BreedType.DUAL_PURPOSE, "Backyard breed developed by PDP. Multicolored, hardy.", listOf(
        WeeklyStandard(1, 75, 95, 1.27f), WeeklyStandard(4, 320, 650, 2.0f), WeeklyStandard(8, 850, 2100, 2.5f),
        WeeklyStandard(12, 1400, 4000, 2.9f), WeeklyStandard(20, 2300, 8000, 3.5f)
    ))
}

data class BreedStandard(val breedName: String, val type: BreedType, val description: String, val weeklyStandards: List<WeeklyStandard>)
data class WeeklyStandard(val week: Int, val targetWeightGrams: Int, val cumulativeFeedGrams: Int, val targetFCR: Float)
enum class BreedType { BROILER, LAYER, DUAL_PURPOSE, INDIGENOUS }
