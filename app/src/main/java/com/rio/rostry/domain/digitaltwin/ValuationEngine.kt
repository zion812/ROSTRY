package com.rio.rostry.domain.digitaltwin

import com.rio.rostry.data.database.dao.BirdEventDao
import com.rio.rostry.data.database.dao.DigitalTwinDao
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🧬 ValuationEngine — Multi-Factor Scoring & Market Valuation
 *
 * Computes composite scores for each Digital Twin bird using the formula:
 *
 *   ValuationScore = (Morphology × 0.4) + (Genetics × 0.3) + (Performance × 0.2) + (Health × 0.1)
 *
 * Each component score is 0-100 and is computed from trait records, event history,
 * and breed standard comparisons.
 *
 * Market Multipliers:
 * - Champion certification: ×1.5
 * - Verified lineage (3+ generations): ×1.3
 * - Show record (wins): ×1.2
 * - Proven breeder: ×1.2
 * - Active injury: ×0.7
 * - Senior decline: ×0.85
 *
 * Usage:
 * ```kotlin
 * val engine = ValuationEngine(twinDao, eventDao)
 * val scored = engine.computeFullValuation(twin)
 * ```
 */
@Singleton
class ValuationEngine @Inject constructor(
    private val twinDao: DigitalTwinDao,
    private val eventDao: BirdEventDao
) {

    /**
     * Compute full valuation for a Digital Twin.
     * Updates all component scores and the composite valuation score.
     *
     * @param twin The bird to valuate
     * @return Updated entity with all scores populated
     */
    suspend fun computeFullValuation(twin: DigitalTwinEntity): DigitalTwinEntity {
        val morphScore = computeMorphologyScore(twin)
        val genScore = computeGeneticsScore(twin)
        val perfScore = computePerformanceScore(twin)
        val healthScore = computeHealthScore(twin)

        // Base composite valuation
        val baseValuation = (
            (morphScore * WEIGHT_MORPHOLOGY) +
            (genScore * WEIGHT_GENETICS) +
            (perfScore * WEIGHT_PERFORMANCE) +
            (healthScore * WEIGHT_HEALTH)
        ).toInt()

        // Apply market multipliers
        val multiplier = computeMarketMultiplier(twin)
        val finalValuation = (baseValuation * multiplier).toInt().coerceIn(0, 100)

        // Estimate market value in INR
        val estimatedValue = estimateMarketValueInr(finalValuation, twin)

        return twin.copy(
            morphologyScore = morphScore,
            geneticsScore = genScore,
            performanceScore = perfScore,
            healthScore = healthScore,
            valuationScore = finalValuation,
            estimatedValueInr = estimatedValue,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Quick re-score just the valuation (when component scores are already set).
     */
    fun quickReValuate(twin: DigitalTwinEntity): DigitalTwinEntity {
        val valuation = twin.computeValuationScore()
        val multiplier = computeMarketMultiplier(twin)
        val finalValuation = (valuation * multiplier).toInt().coerceIn(0, 100)
        return twin.copy(
            valuationScore = finalValuation,
            estimatedValueInr = estimateMarketValueInr(finalValuation, twin)
        )
    }

    /**
     * Process a new event and update relevant scores.
     * Call this when a BirdEvent is logged to keep scores current.
     */
    suspend fun processEventImpact(twin: DigitalTwinEntity, event: BirdEventEntity): DigitalTwinEntity {
        var updated = twin

        when (event.eventType) {
            BirdEventEntity.TYPE_WEIGHT_RECORDED -> {
                // Update weight and reassess morphology
                event.numericValue?.let { weightGrams ->
                    updated = updated.copy(weightKg = weightGrams / 1000.0)
                }
                updated = updated.copy(morphologyScore = computeMorphologyScore(updated))
            }

            BirdEventEntity.TYPE_FIGHT_WIN -> {
                updated = updated.copy(
                    totalFights = twin.totalFights + 1,
                    fightWins = twin.fightWins + 1,
                    performanceScore = computePerformanceScore(updated.copy(
                        totalFights = twin.totalFights + 1,
                        fightWins = twin.fightWins + 1
                    ))
                )
            }

            BirdEventEntity.TYPE_FIGHT_LOSS, BirdEventEntity.TYPE_FIGHT_DRAW -> {
                updated = updated.copy(
                    totalFights = twin.totalFights + 1,
                    performanceScore = computePerformanceScore(updated.copy(
                        totalFights = twin.totalFights + 1
                    ))
                )
            }

            BirdEventEntity.TYPE_INJURY -> {
                updated = updated.copy(
                    injuryCount = twin.injuryCount + 1,
                    currentHealthStatus = "INJURED",
                    healthScore = computeHealthScore(updated.copy(
                        injuryCount = twin.injuryCount + 1,
                        currentHealthStatus = "INJURED"
                    ))
                )
            }

            BirdEventEntity.TYPE_RECOVERY -> {
                updated = updated.copy(
                    currentHealthStatus = "HEALTHY",
                    healthScore = computeHealthScore(updated.copy(currentHealthStatus = "HEALTHY"))
                )
            }

            BirdEventEntity.TYPE_VACCINATION -> {
                updated = updated.copy(
                    vaccinationCount = twin.vaccinationCount + 1,
                    healthScore = computeHealthScore(updated.copy(
                        vaccinationCount = twin.vaccinationCount + 1
                    ))
                )
            }

            BirdEventEntity.TYPE_BREEDING_SUCCESS -> {
                updated = updated.copy(
                    totalBreedingAttempts = twin.totalBreedingAttempts + 1,
                    successfulBreedings = twin.successfulBreedings + 1,
                    breedingStatus = "PROVEN"
                )
            }

            BirdEventEntity.TYPE_BREEDING_FAILURE -> {
                updated = updated.copy(
                    totalBreedingAttempts = twin.totalBreedingAttempts + 1
                )
            }

            BirdEventEntity.TYPE_SHOW_RESULT -> {
                val placement = event.numericValue?.toInt()
                updated = updated.copy(
                    totalShows = twin.totalShows + 1,
                    showWins = if (placement != null && placement <= 3) twin.showWins + 1 else twin.showWins,
                    bestPlacement = minOf(twin.bestPlacement ?: Int.MAX_VALUE, placement ?: Int.MAX_VALUE).let {
                        if (it == Int.MAX_VALUE) null else it
                    }
                )
            }
        }

        // Re-compute valuation after any update
        val finalValuation = updated.computeValuationScore()
        val multiplier = computeMarketMultiplier(updated)
        val finalScore = (finalValuation * multiplier).toInt().coerceIn(0, 100)

        return updated.copy(
            valuationScore = finalScore,
            estimatedValueInr = estimateMarketValueInr(finalScore, updated),
            updatedAt = System.currentTimeMillis()
        )
    }

    // ==================== COMPONENT SCORE CALCULATORS ====================

    /**
     * Morphology Score (0-100)
     * Based on: weight, height, bone density, body type assessment, breed standard comparison.
     * NOW INCLUDES: Manual grading inputs if available.
     */
    private fun computeMorphologyScore(twin: DigitalTwinEntity): Int {
        // 1. Check for Manual Grading first (High precision)
        val manualGrades = try {
            if (twin.metadataJson.contains("manualGrades")) {
                val json = org.json.JSONObject(twin.metadataJson)
                if (json.has("manualGrades")) {
                    parseManualGrades(json.getJSONObject("manualGrades"))
                } else null
            } else null
        } catch (e: Exception) {
            null
        }

        if (manualGrades != null) {
            return computeScoreFromGrades(manualGrades, twin)
        }

        // 2. Fallback to Auto-Calculation (Low precision) if no manual grading
        var score = 50 // Base score (average)

        // Weight contribution (0-25 pts)
        twin.weightKg?.let { weight ->
            val stage = AseelLifecycleStage.entries.find { it.name == twin.lifecycleStage }
            if (stage != null) {
                val isMale = twin.gender?.uppercase() != "FEMALE"
                val idealWeight = getIdealWeight(stage, isMale)
                if (idealWeight > 0) {
                    val ratio = weight / idealWeight
                    // Within 10% of ideal = full marks
                    score += when {
                        ratio in 0.9..1.1 -> 25
                        ratio in 0.8..1.2 -> 20
                        ratio in 0.7..1.3 -> 15
                        ratio in 0.6..1.4 -> 10
                        else -> 5
                    }
                }
            }
        }

        // Bone density (0-15 pts)
        twin.boneDensityScore?.let {
            score += (it * 0.15).toInt()
        }

        // Height (0-10 pts) — basic check
        twin.heightCm?.let { height ->
            // Aseel roosters typically 55-75 cm
            val isMale = twin.gender?.uppercase() != "FEMALE"
            val idealRange = if (isMale) 55.0..75.0 else 45.0..60.0
            score += if (height in idealRange) 10 else 5
        }

        return score.coerceIn(0, 100)
    }

    /**
     * Compute morphology score using detailed manual grades.
     * This replaces the AI analysis path.
     */
    private fun computeScoreFromGrades(grades: ManualMorphologyGrades, twin: DigitalTwinEntity): Int {
        var totalScore = 0.0
        
        // Head & Face (Max 20)
        totalScore += when (grades.beakType) {
            "PARROT" -> 10.0 // Preferred
            "MEDIUM" -> 7.0
            else -> 4.0
        }
        totalScore += when (grades.eyeColor) {
            "PEARL", "WHITE" -> 10.0 // Preferred
            "YELLOW" -> 7.0
            else -> 4.0
        }
        
        // Structure (Max 30)
        totalScore += grades.bodyStructureScore * 0.3 // 0-10 input -> max 30 pts

        // Plumage & Condition (Max 20)
        totalScore += grades.plumageQualityScore * 2.0 // 0-10 input -> max 20 pts

        // Legs & Stance (Max 20)
        totalScore += when (grades.legColor) {
            "WHITE", "IVORY" -> 10.0
            "YELLOW" -> 8.0
            else -> 5.0
        }
        totalScore += grades.stanceScore // 0-10 input

        // Tail (Max 10)
        totalScore += when (grades.tailCarry) {
            "LOW", "DROOPING" -> 10.0
            "LEVEL" -> 7.0
            "HIGH", "SQUIRREL" -> 2.0 // Fault
            else -> 5.0
        }

        // Bonus/Penalty implementation from checklist
        if (grades.hasWryTail) totalScore -= 10
        if (grades.hasSplitWing) totalScore -= 5
        if (grades.hasCrookedToes) totalScore -= 5

        return totalScore.toInt().coerceIn(0, 100)
    }

    private fun parseManualGrades(json: org.json.JSONObject): ManualMorphologyGrades {
        return ManualMorphologyGrades(
            beakType = json.optString("beakType", "STRAIGHT"),
            eyeColor = json.optString("eyeColor", "RED"),
            legColor = json.optString("legColor", "YELLOW"),
            tailCarry = json.optString("tailCarry", "HIGH"),
            bodyStructureScore = json.optInt("bodyStructureScore", 5),
            plumageQualityScore = json.optInt("plumageQualityScore", 5),
            stanceScore = json.optInt("stanceScore", 5),
            hasWryTail = json.optBoolean("hasWryTail", false),
            hasSplitWing = json.optBoolean("hasSplitWing", false),
            hasCrookedToes = json.optBoolean("hasCrookedToes", false)
        )
    }

    /**
     * Genetics Score (0-100)
     * Based on: lineage depth, inbreeding coefficient, genetic purity, proven offspring
     */
    private fun computeGeneticsScore(twin: DigitalTwinEntity): Int {
        var score = 40 // Base score

        // Lineage depth (0-20 pts)
        score += when {
            twin.generationDepth >= 5 -> 20  // 5+ generations documented
            twin.generationDepth >= 3 -> 15  // 3+ generations
            twin.generationDepth >= 1 -> 10  // At least parents known
            else -> 0
        }

        // Inbreeding coefficient (0-20 pts, INVERSE — lower COI = higher score)
        twin.inbreedingCoefficient?.let { coi ->
            score += when {
                coi < 0.03 -> 20     // Very low inbreeding
                coi < 0.06 -> 15     // Low
                coi < 0.12 -> 10     // Moderate
                coi < 0.25 -> 5      // High
                else -> 0            // Very high — penalty
            }
        } ?: run {
            score += 10 // Unknown = neutral
        }

        // Genetic purity (0-10 pts)
        twin.geneticPurityScore?.let {
            score += (it * 0.1).toInt()
        }

        // Proven offspring quality (0-10 pts)
        score += when {
            twin.totalOffspring >= 10 -> 10
            twin.totalOffspring >= 5 -> 7
            twin.totalOffspring >= 1 -> 5
            else -> 0
        }

        return score.coerceIn(0, 100)
    }

    /**
     * Performance Score (0-100)
     * Based on: fight record, show record, aggression, endurance, intelligence
     */
    private fun computePerformanceScore(twin: DigitalTwinEntity): Int {
        var score = 30 // Base score

        // Fight record (0-30 pts)
        if (twin.totalFights > 0) {
            val winRate = twin.fightWins.toDouble() / twin.totalFights
            score += (winRate * 30).toInt()
        }

        // Show record (0-15 pts)
        if (twin.totalShows > 0) {
            val showWinRate = twin.showWins.toDouble() / twin.totalShows
            score += (showWinRate * 15).toInt()
        }

        // Aggression index (0-10 pts)
        twin.aggressionIndex?.let {
            score += (it * 0.1).toInt()
        }

        // Endurance score (0-10 pts)
        twin.enduranceScore?.let {
            score += (it * 0.1).toInt()
        }

        // Intelligence score (0-5 pts)
        twin.intelligenceScore?.let {
            score += (it * 0.05).toInt()
        }

        return score.coerceIn(0, 100)
    }

    /**
     * Health Score (0-100)
     * Based on: current health, injury history, vaccination compliance, stamina
     */
    private fun computeHealthScore(twin: DigitalTwinEntity): Int {
        var score = 60 // Base score (healthy)

        // Current status impact
        score += when (twin.currentHealthStatus) {
            "HEALTHY", "OK" -> 20
            "RECOVERING" -> 10
            "INJURED" -> -10
            "SICK" -> -20
            else -> 0
        }

        // Vaccination compliance (0-10 pts)
        score += when {
            twin.vaccinationCount >= 5 -> 10   // Fully vaccinated
            twin.vaccinationCount >= 3 -> 7
            twin.vaccinationCount >= 1 -> 4
            else -> 0
        }

        // Injury history penalty (0 to -20)
        score -= when {
            twin.injuryCount >= 5 -> 20
            twin.injuryCount >= 3 -> 10
            twin.injuryCount >= 1 -> 5
            else -> 0
        }

        // Stamina (0-10 pts)
        twin.staminaScore?.let {
            score += (it * 0.1).toInt()
        }

        return score.coerceIn(0, 100)
    }

    // ==================== MARKET ====================

    /**
     * Compute market multiplier based on special attributes.
     */
    private fun computeMarketMultiplier(twin: DigitalTwinEntity): Double {
        var multiplier = 1.0

        // Certification bonus
        multiplier *= when (twin.certificationLevel) {
            "CHAMPION" -> 1.5
            "VERIFIED" -> 1.3
            "REGISTERED" -> 1.1
            else -> 1.0
        }

        // Proven breeder bonus
        if (twin.breedingStatus == "PROVEN" && twin.successfulBreedings >= 3) {
            multiplier *= 1.2
        }

        // Show record bonus
        if (twin.showWins >= 3) {
            multiplier *= 1.15
        }

        // Active injury penalty
        if (twin.currentHealthStatus in listOf("INJURED", "SICK")) {
            multiplier *= 0.7
        }

        // Senior decline
        val stage = AseelLifecycleStage.entries.find { it.name == twin.lifecycleStage }
        if (stage?.hasDeclineFactors == true) {
            multiplier *= 0.85
        }

        return multiplier
    }

    /**
     * Estimate market value in INR based on valuation score.
     * Rough mapping for Aseel premium market (Andhra/Telangana):
     * - 90-100: ₹50,000 - ₹2,00,000+ (Champion grade)
     * - 70-89:  ₹15,000 - ₹50,000 (Premium grade)
     * - 50-69:  ₹5,000 - ₹15,000 (Standard grade)
     * - 30-49:  ₹2,000 - ₹5,000 (Utility grade)
     * - 0-29:   ₹500 - ₹2,000 (Basic)
     */
    private fun estimateMarketValueInr(score: Int, twin: DigitalTwinEntity): Double {
        val isMale = twin.gender?.uppercase() != "FEMALE"
        val genderFactor = if (isMale) 1.0 else 0.6 // Males valued higher in Aseel market

        val baseValue = when {
            score >= 90 -> 100000.0
            score >= 70 -> 30000.0
            score >= 50 -> 10000.0
            score >= 30 -> 3000.0
            else -> 1000.0
        }

        // Scale within range based on exact score
        val rangeMultiplier = 1.0 + (score % 20) * 0.05

        return (baseValue * rangeMultiplier * genderFactor)
    }

    /**
     * Get ideal weight for stage (same as lifecycle engine, but accessible here).
     */
    private fun getIdealWeight(stage: AseelLifecycleStage, isMale: Boolean): Double {
        return when (stage) {
            AseelLifecycleStage.EGG -> 0.055
            AseelLifecycleStage.CHICK -> if (isMale) 0.2 else 0.15
            AseelLifecycleStage.GROWER -> if (isMale) 1.5 else 1.2
            AseelLifecycleStage.PRE_ADULT -> if (isMale) 2.5 else 2.0
            AseelLifecycleStage.ADULT_FIGHTER -> if (isMale) 3.5 else 2.5
            AseelLifecycleStage.BREEDER_PRIME -> if (isMale) 4.0 else 2.8
            AseelLifecycleStage.SENIOR -> if (isMale) 3.8 else 2.6
        }
    }

    companion object {
        // Valuation weights — MUST sum to 1.0
        const val WEIGHT_MORPHOLOGY = 0.4
        const val WEIGHT_GENETICS = 0.3
        const val WEIGHT_PERFORMANCE = 0.2
        const val WEIGHT_HEALTH = 0.1
    }
}

/**
 * Data class for manual morphology grading inputs.
 * Substitutes AI visual analysis with expert human input.
 */
data class ManualMorphologyGrades(
    val beakType: String,       // PARROT, MEDIUM, STRAIGHT
    val eyeColor: String,       // PEARL, WHITE, YELLOW, RED
    val legColor: String,       // WHITE, IVORY, YELLOW, DARK
    val tailCarry: String,      // LOW, DROOPING, LEVEL, HIGH
    val bodyStructureScore: Int,// 1-10
    val plumageQualityScore: Int,// 1-10
    val stanceScore: Int,       // 1-10
    val hasWryTail: Boolean,
    val hasSplitWing: Boolean,
    val hasCrookedToes: Boolean
)
