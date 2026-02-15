package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Breeding Value Index (BVI) Calculator — replaces mock genetics with real data.
 *
 * BVI Formula:
 *   BVI(bird) = 0.30 × OwnTraitScore
 *             + 0.25 × ShowPerformanceScore
 *             + 0.25 × OffspringQualityScore  (Progeny Testing)
 *             + 0.10 × ParentAverageBVI
 *             + 0.10 × HealthScore
 *
 * All sub-scores normalized to 0.0–1.0 range.
 */

data class BreedingValueResult(
    val bvi: Float,                          // 0.0–1.0 composite score
    val ownTraitScore: Float,                // Own phenotypic completeness & quality
    val showPerformanceScore: Float,         // Competition track record
    val offspringQualityScore: Float,        // Progeny testing (how good are their kids)
    val parentScore: Float,                  // Inherited lineage strength
    val healthScore: Float,                  // Health record quality
    val traitCount: Int,                     // Number of traits recorded
    val offspringCount: Int,                 // Number of offspring
    val showWins: Int,                       // Total show wins
    val showTotal: Int,                      // Total shows entered
    val rating: String,                      // "Elite", "Strong", "Average", "Needs Data"
    val recommendation: String               // Human-readable breeding recommendation
)

@Singleton
class BreedingValueService @Inject constructor(
    private val traitRecordDao: BirdTraitRecordDao,
    private val showRecordDao: ShowRecordDao,
    private val productDao: ProductDao,
    private val pedigreeRepository: PedigreeRepository
) {

    /**
     * Calculate full BVI for a single bird.
     */
    suspend fun calculateBVI(birdId: String): BreedingValueResult {
        val ownTrait = calculateOwnTraitScore(birdId)
        val showPerf = calculateShowScore(birdId)
        val offspring = calculateOffspringScore(birdId)
        val parent = calculateParentScore(birdId)
        val health = calculateHealthScore(birdId)

        val bvi = (0.30f * ownTrait.first +
                   0.25f * showPerf.first +
                   0.25f * offspring.first +
                   0.10f * parent +
                   0.10f * health).coerceIn(0f, 1f)

        val rating = when {
            bvi >= 0.80f -> "Elite"
            bvi >= 0.60f -> "Strong"
            bvi >= 0.40f -> "Average"
            bvi >= 0.20f -> "Developing"
            else -> "Needs Data"
        }

        val recommendation = generateRecommendation(bvi, ownTrait.second, showPerf, offspring)

        return BreedingValueResult(
            bvi = bvi,
            ownTraitScore = ownTrait.first,
            showPerformanceScore = showPerf.first,
            offspringQualityScore = offspring.first,
            parentScore = parent,
            healthScore = health,
            traitCount = ownTrait.second,
            offspringCount = offspring.second,
            showWins = showPerf.second,
            showTotal = showPerf.third,
            rating = rating,
            recommendation = recommendation
        )
    }

    /**
     * Own Trait Score: Based on how many traits are recorded and their quality.
     * Returns (score, traitCount)
     */
    private suspend fun calculateOwnTraitScore(birdId: String): Pair<Float, Int> {
        val records = traitRecordDao.getByBird(birdId)
        if (records.isEmpty()) return 0f to 0

        val distinctTraits = records.map { it.traitName }.distinct().size
        
        // Total possible traits across all categories
        val totalPossibleTraits = 20 // Approximation across all categories

        // Completeness factor (0–1): how many traits are recorded out of all possible
        val completeness = (distinctTraits.toFloat() / totalPossibleTraits).coerceIn(0f, 1f)
        
        // Quality factor: for numeric traits, compare against expected ranges
        val qualityScores = records
            .filter { it.traitUnit != null && it.traitUnit != "text" }
            .mapNotNull { record ->
                try {
                    val value = record.traitValue.toFloatOrNull() ?: return@mapNotNull null
                    // Normalize: score_1_10 traits → divide by 10
                    // Weight traits → normalize roughly to 0-1 (assume 1000-5000g range)
                    when (record.traitUnit) {
                        "score_1_10" -> (value / 10f).coerceIn(0f, 1f)
                        "grams", "g" -> (value / 5000f).coerceIn(0f, 1f)
                        "cm" -> (value / 30f).coerceIn(0f, 1f) // shank length
                        "percent", "%" -> (value / 100f).coerceIn(0f, 1f)
                        else -> 0.5f // Unknown unit → neutral
                    }
                } catch (_: Exception) { null }
            }

        val avgQuality = if (qualityScores.isNotEmpty()) qualityScores.average().toFloat() else 0.5f
        
        // Weighted: 60% completeness, 40% quality
        val score = (0.6f * completeness + 0.4f * avgQuality).coerceIn(0f, 1f)
        
        return score to distinctTraits
    }

    /**
     * Show Performance Score: Win rate and podium rate.
     * Returns (score, wins, totalShows)
     */
    private suspend fun calculateShowScore(birdId: String): Triple<Float, Int, Int> {
        val wins = showRecordDao.countWins(birdId)
        val podiums = showRecordDao.countPodiums(birdId)
        val total = showRecordDao.countTotal(birdId)

        if (total == 0) return Triple(0f, 0, 0)

        val winRate = wins.toFloat() / total
        val podiumRate = podiums.toFloat() / total

        // Score = 60% podium rate + 40% win rate + bonus for volume
        val volumeBonus = (total.toFloat() / 20f).coerceIn(0f, 0.1f) // Up to 10% bonus for 20+ shows
        val score = (0.4f * winRate + 0.6f * podiumRate + volumeBonus).coerceIn(0f, 1f)

        return Triple(score, wins, total)
    }

    /**
     * Offspring Quality Score (Progeny Testing): How good are this bird's children?
     * Returns (score, offspringCount)
     */
    private suspend fun calculateOffspringScore(birdId: String): Pair<Float, Int> {
        val offspring = productDao.getOffspring(birdId)
        if (offspring.isEmpty()) return 0f to 0

        var totalChildScore = 0f
        var scoredChildren = 0

        for (child in offspring.take(20)) { // Cap at 20 to avoid N+1 explosion
            // For each child, check if they have trait records or show records
            val childTraits = traitRecordDao.getTraitCount(child.productId)
            val childWins = showRecordDao.countWins(child.productId)
            val childTotal = showRecordDao.countTotal(child.productId)

            // Score each child: has data + show performance
            val dataScore = if (childTraits > 0) 0.3f else 0f
            val showScore = if (childTotal > 0) (childWins.toFloat() / childTotal * 0.7f) else 0f
            
            totalChildScore += (dataScore + showScore).coerceIn(0f, 1f)
            scoredChildren++
        }

        val score = if (scoredChildren > 0) (totalChildScore / scoredChildren) else 0f
        return score.coerceIn(0f, 1f) to offspring.size
    }

    /**
     * Parent Score: Average lineage strength from pedigree.
     */
    private suspend fun calculateParentScore(birdId: String): Float {
        return try {
            val result = pedigreeRepository.getFullPedigree(birdId, 2)
            val tree = result.data ?: return 0f
            
            val ancestorCount = tree.countAncestors()
            // More documented ancestors = stronger pedigree foundation
            // Max possible in 2 generations: 6 ancestors (2 parents + 4 grandparents)
            (ancestorCount.toFloat() / 6f).coerceIn(0f, 1f)
        } catch (_: Exception) { 0f }
    }

    /**
     * Health Score: Based on health events — fewer active issues = better.
     */
    private suspend fun calculateHealthScore(birdId: String): Float {
        // Currently using trait records as proxy for health data presence
        // A bird with recorded traits and no health incidents = healthy
        val traitCount = traitRecordDao.getTraitCount(birdId)
        return if (traitCount > 0) {
            // Bird is being monitored → assume healthy unless flagged
            0.8f
        } else {
            // No data → neutral assumption
            0.5f
        }
    }

    private fun generateRecommendation(
        bvi: Float,
        traitCount: Int,
        showPerf: Triple<Float, Int, Int>,
        offspring: Pair<Float, Int>
    ): String {
        return when {
            traitCount == 0 && showPerf.third == 0 && offspring.second == 0 ->
                "Record traits and enter shows to build this bird's breeding profile."
            traitCount > 0 && showPerf.third == 0 ->
                "Good trait data. Consider entering shows to validate competitive potential."
            bvi >= 0.80f ->
                "Elite breeder. Prioritize this bird for your top pairings."
            bvi >= 0.60f ->
                "Strong candidate. Pair with complementary birds to improve specific traits."
            bvi >= 0.40f ->
                "Average performer. Focus on improving weak areas through targeted pairing."
            offspring.second > 0 && offspring.first < 0.3f ->
                "Offspring underperforming. Consider pairing with a stronger mate."
            else ->
                "Continue recording data to improve breeding decisions."
        }
    }
}
