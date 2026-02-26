package com.rio.rostry.domain.breeding

import android.util.Log
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Comprehensive breeding evaluation result
 */
data class BreedingEvaluation(
    val compatibility: BreedingCompatibilityCalculator.CompatibilityResult,
    val phenotypePredictions: List<PhenotypePrediction>,
    val geneticIssues: List<GeneticIssue>,
    val inbreedingWarning: InbreedingWarning?,
    val alternativePairings: List<AlternativePairing>,
    val evaluationTimeMs: Long
)

data class PhenotypePrediction(
    val traitName: String,
    val possibleOutcomes: List<PhenotypeOutcome>
)

data class PhenotypeOutcome(
    val value: String,
    val probability: Int // percentage
)

data class GeneticIssue(
    val severity: IssueSeverity,
    val description: String,
    val recommendation: String
)

enum class IssueSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class InbreedingWarning(
    val coiPercent: Double,
    val level: InbreedingLevel,
    val description: String
)

enum class InbreedingLevel {
    NONE, LOW, MODERATE, HIGH, CRITICAL
}

data class AlternativePairing(
    val candidateId: String,
    val candidateName: String,
    val candidateBreed: String?,
    val compatibilityScore: Int,
    val reason: String
)

/**
 * Enhanced Breeding Compatibility System.
 *
 * Extends the base BreedingCompatibilityCalculator with:
 * - Phenotype prediction using Punnett squares
 * - Genetic issue identification
 * - Inbreeding detection with severity levels
 * - Alternative pairing suggestions ranked by compatibility
 * - Performance target: < 2 seconds per evaluation
 *
 * Requirements: 24.1-24.8
 */
@Singleton
class EnhancedBreedingSystem @Inject constructor(
    private val compatibilityCalculator: BreedingCompatibilityCalculator,
    private val geneticEngine: GeneticEngine,
    private val productDao: ProductDao,
    private val productRepository: ProductRepository
) {
    companion object {
        private const val TAG = "EnhancedBreeding"
        private const val MAX_ALTERNATIVES = 5
        private const val PERFORMANCE_TARGET_MS = 2000L
    }

    /**
     * Perform a comprehensive breeding evaluation.
     * Performance target: < 2 seconds
     */
    suspend fun evaluate(male: ProductEntity, female: ProductEntity): BreedingEvaluation {
        val startTime = System.currentTimeMillis()

        // 1. Calculate base compatibility
        val compatibility = compatibilityCalculator.calculateCompatibility(male, female)

        // 2. Predict phenotypes
        val phenotypePredictions = predictPhenotypes(male, female)

        // 3. Identify genetic issues
        val geneticIssues = identifyGeneticIssues(male, female, compatibility)

        // 4. Generate inbreeding warning
        val inbreedingWarning = generateInbreedingWarning(compatibility.coiPercent)

        // 5. Suggest alternative pairings
        val alternatives = suggestAlternatives(male, female, compatibility.score)

        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed > PERFORMANCE_TARGET_MS) {
            Log.w(TAG, "Breeding evaluation took ${elapsed}ms - exceeds 2s target")
        } else {
            Log.d(TAG, "Breeding evaluation completed in ${elapsed}ms")
        }

        return BreedingEvaluation(
            compatibility = compatibility,
            phenotypePredictions = phenotypePredictions,
            geneticIssues = geneticIssues,
            inbreedingWarning = inbreedingWarning,
            alternativePairings = alternatives,
            evaluationTimeMs = elapsed
        )
    }

    /**
     * Predict phenotype outcomes for offspring using Punnett square analysis.
     */
    private fun predictPhenotypes(male: ProductEntity, female: ProductEntity): List<PhenotypePrediction> {
        val predictions = mutableListOf<PhenotypePrediction>()

        // Comb type prediction
        val maleComb = geneticEngine.inferGenotype(GeneticEngine.TraitType.COMB, extractCombType(male))
        val femaleComb = geneticEngine.inferGenotype(GeneticEngine.TraitType.COMB, extractCombType(female))
        val combPrediction = geneticEngine.simulateCross(maleComb, femaleComb)
        if (combPrediction.probabilities.isNotEmpty()) {
            predictions.add(
                PhenotypePrediction(
                    traitName = "Comb Type",
                    possibleOutcomes = combPrediction.probabilities.map {
                        PhenotypeOutcome(it.phenotypeDescription, it.percentage)
                    }
                )
            )
        }

        // Plumage color prediction
        val maleColor = geneticEngine.inferGenotype(GeneticEngine.TraitType.PLUMAGE_COLOR, male.color)
        val femaleColor = geneticEngine.inferGenotype(GeneticEngine.TraitType.PLUMAGE_COLOR, female.color)
        val colorPrediction = geneticEngine.simulateCross(maleColor, femaleColor)
        if (colorPrediction.probabilities.isNotEmpty()) {
            predictions.add(
                PhenotypePrediction(
                    traitName = "Plumage Color",
                    possibleOutcomes = colorPrediction.probabilities.map {
                        PhenotypeOutcome(it.phenotypeDescription, it.percentage)
                    }
                )
            )
        }

        // Shank color prediction
        val maleShank = geneticEngine.inferGenotype(GeneticEngine.TraitType.SHANK_COLOR, extractShankColor(male))
        val femaleShank = geneticEngine.inferGenotype(GeneticEngine.TraitType.SHANK_COLOR, extractShankColor(female))
        val shankPrediction = geneticEngine.simulateCross(maleShank, femaleShank)
        if (shankPrediction.probabilities.isNotEmpty()) {
            predictions.add(
                PhenotypePrediction(
                    traitName = "Shank Color",
                    possibleOutcomes = shankPrediction.probabilities.map {
                        PhenotypeOutcome(it.phenotypeDescription, it.percentage)
                    }
                )
            )
        }

        return predictions
    }

    /**
     * Identify potential genetic issues with a pairing.
     */
    private fun identifyGeneticIssues(
        male: ProductEntity,
        female: ProductEntity,
        compatibility: BreedingCompatibilityCalculator.CompatibilityResult
    ): List<GeneticIssue> {
        val issues = mutableListOf<GeneticIssue>()

        // Check health status
        if (male.healthStatus?.equals("Sick", ignoreCase = true) == true) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.CRITICAL,
                    description = "Male parent is currently marked as sick",
                    recommendation = "Wait until the male has fully recovered before breeding"
                )
            )
        }
        if (female.healthStatus?.equals("Sick", ignoreCase = true) == true) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.CRITICAL,
                    description = "Female parent is currently marked as sick",
                    recommendation = "Wait until the female has fully recovered before breeding"
                )
            )
        }

        // Check inbreeding
        if (compatibility.coiPercent > 25.0) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.CRITICAL,
                    description = "Very high inbreeding coefficient (${String.format("%.1f", compatibility.coiPercent)}%)",
                    recommendation = "Select an unrelated mate to maintain genetic diversity"
                )
            )
        } else if (compatibility.coiPercent > 12.5) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.HIGH,
                    description = "High inbreeding coefficient (${String.format("%.1f", compatibility.coiPercent)}%)",
                    recommendation = "Consider alternative pairings with lower relatedness"
                )
            )
        } else if (compatibility.coiPercent > 6.25) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.MEDIUM,
                    description = "Moderate inbreeding coefficient (${String.format("%.1f", compatibility.coiPercent)}%)",
                    recommendation = "Monitor offspring for reduced vigor"
                )
            )
        }

        // Check same-parent crossing
        if (male.parentMaleId != null && male.parentMaleId == female.parentMaleId) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.HIGH,
                    description = "Both parents share the same sire",
                    recommendation = "Avoid mating half-siblings"
                )
            )
        }
        if (male.parentFemaleId != null && male.parentFemaleId == female.parentFemaleId) {
            issues.add(
                GeneticIssue(
                    severity = IssueSeverity.HIGH,
                    description = "Both parents share the same dam",
                    recommendation = "Avoid mating half-siblings"
                )
            )
        }

        return issues
    }

    /**
     * Generate an inbreeding warning based on COI percentage.
     */
    private fun generateInbreedingWarning(coiPercent: Double): InbreedingWarning? {
        return when {
            coiPercent > 25.0 -> InbreedingWarning(
                coiPercent = coiPercent,
                level = InbreedingLevel.CRITICAL,
                description = "Critical inbreeding level. Full siblings or parent-offspring crossing detected."
            )
            coiPercent > 12.5 -> InbreedingWarning(
                coiPercent = coiPercent,
                level = InbreedingLevel.HIGH,
                description = "High inbreeding. Half-sibling or closer relationship detected."
            )
            coiPercent > 6.25 -> InbreedingWarning(
                coiPercent = coiPercent,
                level = InbreedingLevel.MODERATE,
                description = "Moderate inbreeding. First cousin or similar relationship detected."
            )
            coiPercent > 1.0 -> InbreedingWarning(
                coiPercent = coiPercent,
                level = InbreedingLevel.LOW,
                description = "Low inbreeding. Distant relatives."
            )
            else -> null // No warning needed
        }
    }

    /**
     * Suggest alternative pairings with better compatibility scores.
     */
    private suspend fun suggestAlternatives(
        male: ProductEntity,
        female: ProductEntity,
        currentScore: Int
    ): List<AlternativePairing> {
        val alternatives = mutableListOf<AlternativePairing>()

        try {
            // Get potential partner candidates (opposite gender, same owner, not deleted)
            val ownerId = male.sellerId
            val femaleCandidates = productDao.getPotentialParents(ownerId, male.productId, "Female")
                .filter { it.productId != female.productId }
                .filter { it.healthStatus?.equals("Sick", ignoreCase = true) != true }

            // Evaluate top candidates
            for (candidate in femaleCandidates.take(MAX_ALTERNATIVES * 2)) {
                try {
                    val result = compatibilityCalculator.calculateCompatibility(male, candidate)
                    if (result.score > currentScore) {
                        alternatives.add(
                            AlternativePairing(
                                candidateId = candidate.productId,
                                candidateName = candidate.name,
                                candidateBreed = candidate.breed,
                                compatibilityScore = result.score,
                                reason = result.verdict
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error evaluating alternative pairing with ${candidate.productId}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding alternative pairings", e)
        }

        return alternatives
            .sortedByDescending { it.compatibilityScore }
            .take(MAX_ALTERNATIVES)
    }

    // ─── Helper Functions ───────────────────────────────────────────────

    /**
     * Extract comb type from product entity (looks in description/condition fields).
     */
    private fun extractCombType(product: ProductEntity): String? {
        // Try to extract from condition or description
        val combTypes = listOf("Rose", "Pea", "Walnut", "Single", "Buttercup", "V-Shaped")
        for (type in combTypes) {
            if (product.condition?.contains(type, ignoreCase = true) == true ||
                product.description.contains(type, ignoreCase = true)) {
                return type
            }
        }
        return null
    }

    /**
     * Extract shank color from product entity.
     */
    private fun extractShankColor(product: ProductEntity): String? {
        val shankColors = listOf("White", "Pink", "Yellow", "Green", "Blue", "Slate")
        for (color in shankColors) {
            if (product.description.contains("shank.*$color".toRegex(RegexOption.IGNORE_CASE)) ||
                product.description.contains("$color.*shank".toRegex(RegexOption.IGNORE_CASE))) {
                return color
            }
        }
        return null
    }
}
