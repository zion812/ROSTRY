package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.BreedingService
import com.rio.rostry.domain.breeding.CompatibilityResult
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Mate Recommendation Engine
 *
 * Given a focal bird, ranks all eligible mates from the user's flock by a composite
 * "Pairing Score" that balances:
 *  - Predicted Offspring Quality (40%): BVI of both parents averaged + trait complementarity
 *  - Genetic Diversity (25%): Inbreeding risk penalty via COI
 *  - Trait Complementarity (20%): Mates that compensate for each other's weak traits score higher
 *  - Practical Factors (15%): Same breed bonus, age suitability, health status
 */
@Singleton
class MateRecommendationService @Inject constructor(
    private val breedingValueService: BreedingValueService,
    private val breedingService: BreedingService,
    private val traitRecordDao: BirdTraitRecordDao,
    private val showRecordDao: ShowRecordDao,
    private val productDao: ProductDao,
    private val pedigreeRepository: PedigreeRepository
) {

    data class MateCandidate(
        val bird: ProductEntity,
        val pairingScore: Float,          // 0–100
        val offspringPotential: Float,    // 0–100
        val geneticDiversity: Float,      // 0–100 (higher = more diverse)
        val traitComplementarity: Float,  // 0–100
        val practicalScore: Float,        // 0–100
        val bvi: BreedingValueResult?,
        val compatibility: CompatibilityResult?,
        val recommendation: String,       // Human-readable
        val keyStrengths: List<String>,   // What this mate brings
        val keyRisks: List<String>        // Concerns
    )

    data class RecommendationResult(
        val focalBird: ProductEntity,
        val focalBvi: BreedingValueResult?,
        val candidates: List<MateCandidate>,
        val totalEvaluated: Int
    )

    /**
     * Find and rank all eligible mates for a given bird.
     */
    suspend fun findBestMates(focalBirdId: String, limit: Int = 10): RecommendationResult {
        val focalBird = productDao.findById(focalBirdId)
            ?: return RecommendationResult(
                focalBird = ProductEntity(productId = focalBirdId, name = "Unknown", sellerId = ""),
                focalBvi = null,
                candidates = emptyList(),
                totalEvaluated = 0
            )

        val targetGender = if (focalBird.gender?.lowercase() == "male") "female" else "male"

        // Get focal bird BVI
        val focalBvi = try { breedingValueService.calculateBVI(focalBirdId) } catch (_: Exception) { null }

        // Get focal bird traits for complementarity analysis
        val focalTraits = traitRecordDao.getByBird(focalBirdId)
        val focalTraitMap = focalTraits
            .groupBy { it.traitName }
            .mapValues { (_, records) ->
                records.maxByOrNull { it.recordedAt }?.let { latest ->
                    latest.traitValue.toFloatOrNull() ?: 0f
                } ?: 0f
            }

        // Get all potential mates (same owner, opposite gender, active)
        val allBirds = productDao.getProductsBySellerSuspend(focalBird.sellerId)
        val eligibleMates = allBirds.filter { bird ->
            bird.productId != focalBirdId &&
            bird.gender?.lowercase() == targetGender &&
            bird.healthStatus?.lowercase() != "sick" &&
            bird.healthStatus?.lowercase() != "dead"
        }

        // Score each candidate
        // Score each candidate (using loop to avoid suspension issues in mapNotNull)
        val candidateList = mutableListOf<MateCandidate>()
        for (mate in eligibleMates) {
            try {
                val candidate = scoreCandidate(focalBird, focalBvi, focalTraitMap, mate)
                candidateList.add(candidate)
            } catch (_: Exception) {
                // Skip if scoring fails
            }
        }
        
        val candidates = candidateList.sortedByDescending { it.pairingScore }.take(limit)

        return RecommendationResult(
            focalBird = focalBird,
            focalBvi = focalBvi,
            candidates = candidates,
            totalEvaluated = eligibleMates.size
        )
    }

    private suspend fun scoreCandidate(
        focal: ProductEntity,
        focalBvi: BreedingValueResult?,
        focalTraitMap: Map<String, Float>,
        mate: ProductEntity
    ): MateCandidate {
        val strengths = mutableListOf<String>()
        val risks = mutableListOf<String>()

        // 1. Offspring Potential (40%) — average of both parents' BVI
        val mateBvi = try { breedingValueService.calculateBVI(mate.productId) } catch (_: Exception) { null }
        val focalBviScore = focalBvi?.bvi ?: 0.3f
        val mateBviScore = mateBvi?.bvi ?: 0.3f
        val offspringPotential = ((focalBviScore + mateBviScore) / 2f * 100f).coerceIn(0f, 100f)

        if (mateBviScore >= 0.7f) strengths.add("High BVI (${mateBvi?.rating})")
        if (mateBvi?.showWins ?: 0 > 0) strengths.add("Show winner (${mateBvi?.showWins} wins)")

        // 2. Genetic Diversity (25%) — COI-based
        var geneticDiversity = 85f // Default = good diversity (no shared ancestors)
        val compatibility: CompatibilityResult? = try {
            val (sire, dam) = if (focal.gender?.lowercase() == "male") focal to mate else mate to focal
            var result: CompatibilityResult? = null
            breedingService.calculateCompatibility(sire, dam).collect {
                if (it is Resource.Success) result = it.data
            }
            result
        } catch (_: Exception) { null }

        compatibility?.let { compat ->
            geneticDiversity = (100f - (compat.inbreedingCoefficient * 400f).toFloat()).coerceIn(0f, 100f)
            if (compat.inbreedingCoefficient > 0.125) {
                risks.add("High inbreeding risk (${String.format("%.1f", compat.inbreedingCoefficient * 100)}%)")
            }
            if (compat.score > 80) strengths.add("Excellent genetic compatibility")
        }

        // 3. Trait Complementarity (20%) — mates that compensate weak traits
        val mateTraits = traitRecordDao.getByBird(mate.productId)
        val mateTraitMap = mateTraits
            .groupBy { it.traitName }
            .mapValues { (_, records) ->
                records.maxByOrNull { it.recordedAt }?.let { latest ->
                    latest.traitValue.toFloatOrNull() ?: 0f
                } ?: 0f
            }

        val traitComplementarity = calculateTraitComplementarity(focalTraitMap, mateTraitMap, strengths, risks)

        // 4. Practical Factors (15%)
        var practicalScore = 50f

        // Same breed bonus
        if (!focal.breed.isNullOrBlank() && focal.breed.equals(mate.breed, true)) {
            practicalScore += 25f
            strengths.add("Same breed (${focal.breed})")
        } else if (!focal.breed.isNullOrBlank() && !mate.breed.isNullOrBlank()) {
            strengths.add("Cross-breed: ${focal.breed} × ${mate.breed}")
        }

        // Age suitability — both should be mature (>20 weeks typically)
        val mateAge = mate.ageWeeks ?: 0
        if (mateAge >= 20) {
            practicalScore += 15f
        } else if (mateAge > 0) {
            practicalScore += 5f
            risks.add("Young bird (${mateAge}w)")
        }

        // Health bonus
        if (mate.healthStatus?.lowercase() == "healthy") {
            practicalScore += 10f
        }

        practicalScore = practicalScore.coerceIn(0f, 100f)

        // Composite Score
        val pairingScore = (
            offspringPotential * 0.40f +
            geneticDiversity * 0.25f +
            traitComplementarity * 0.20f +
            practicalScore * 0.15f
        ).coerceIn(0f, 100f)

        val recommendation = when {
            pairingScore >= 80 -> "Highly recommended — excellent pairing potential"
            pairingScore >= 65 -> "Good match — solid breeding candidate"
            pairingScore >= 50 -> "Acceptable match — some trade-offs"
            pairingScore >= 35 -> "Fair match — consider alternatives"
            else -> "Not recommended — significant concerns"
        }

        return MateCandidate(
            bird = mate,
            pairingScore = pairingScore,
            offspringPotential = offspringPotential,
            geneticDiversity = geneticDiversity,
            traitComplementarity = traitComplementarity,
            practicalScore = practicalScore,
            bvi = mateBvi,
            compatibility = compatibility,
            recommendation = recommendation,
            keyStrengths = strengths.take(4),
            keyRisks = risks.take(3)
        )
    }

    /**
     * Complementarity: high scores mean mates compensate for each other's weaknesses.
     * If focal is weak on a trait (say 3/10) and mate is strong (8/10),
     * offspring may average out (5.5/10) — this is complementary.
     * If both are weak, score is low. If both are strong, that's also good.
     */
    private fun calculateTraitComplementarity(
        focalTraits: Map<String, Float>,
        mateTraits: Map<String, Float>,
        strengths: MutableList<String>,
        risks: MutableList<String>
    ): Float {
        val allTraits = focalTraits.keys + mateTraits.keys
        if (allTraits.isEmpty()) return 50f // Neutral if no data

        var totalScore = 0f
        var traitCount = 0

        for (trait in allTraits.distinct()) {
            val focalVal = focalTraits[trait]
            val mateVal = mateTraits[trait]

            if (focalVal != null && mateVal != null) {
                traitCount++
                // Best case: at least one parent is strong (>7)
                // Complementary if one is weak and other is strong
                val avg = (focalVal + mateVal) / 2f
                val maxVal = maxOf(focalVal, mateVal)

                totalScore += when {
                    avg >= 7f -> 100f     // Both strong
                    maxVal >= 7f -> 85f   // One strong (complementary)
                    avg >= 5f -> 65f      // Both moderate
                    maxVal >= 5f -> 50f   // One moderate
                    else -> 25f           // Both weak
                }

                if (focalVal < 4f && mateVal >= 7f) {
                    val name = trait.replace("_", " ").replaceFirstChar { it.uppercase() }
                    strengths.add("Compensates weak $name")
                }
                if (focalVal < 4f && mateVal < 4f) {
                    val name = trait.replace("_", " ").replaceFirstChar { it.uppercase() }
                    risks.add("Both weak: $name")
                }
            } else if (focalVal != null || mateVal != null) {
                traitCount++
                totalScore += 50f // Partial data
            }
        }

        return if (traitCount > 0) (totalScore / traitCount).coerceIn(0f, 100f) else 50f
    }
}
