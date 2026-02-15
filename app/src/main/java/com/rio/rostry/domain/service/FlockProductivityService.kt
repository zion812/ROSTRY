package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Flock-wide productivity analytics and leaderboard rankings.
 * Aggregates per-bird data into breed benchmarks, leaderboards,
 * and productivity summaries for the Enthusiast dashboard.
 */
@Singleton
class FlockProductivityService @Inject constructor(
    private val productDao: ProductDao,
    private val breedingROIService: BreedingROIService,
    private val breedingValueService: BreedingValueService,
    private val showRecordDao: ShowRecordDao,
    private val traitRecordDao: BirdTraitRecordDao
) {

    data class FlockSummary(
        val totalBirds: Int,
        val totalMales: Int,
        val totalFemales: Int,
        val avgBvi: Float,
        val totalOffspring: Int,
        val totalRevenue: Double,
        val totalCost: Double,
        val netProfit: Double,
        val overallROI: Double,
        val avgCostPerChick: Double,
        val birdLeaderboard: List<LeaderboardEntry>,
        val breedBreakdown: List<BreedSummary>,
        val topPerformers: List<PerformerBadge>
    )

    data class LeaderboardEntry(
        val rank: Int,
        val bird: ProductEntity,
        val bvi: Float,
        val offspringCount: Int,
        val roi: Double,
        val showWins: Int,
        val productivityScore: Float,  // 0–100 composite
        val badge: String              // "🏆 Champion", "⭐ Star", "📈 Rising", etc.
    )

    data class BreedSummary(
        val breed: String,
        val count: Int,
        val avgBvi: Float,
        val avgROI: Double,
        val totalOffspring: Int,
        val avgCostPerChick: Double,
        val topBird: String? // Name of best bird in this breed
    )

    data class PerformerBadge(
        val bird: ProductEntity,
        val category: String,   // "Top Producer", "Show Champion", "Best ROI", "Most Consistent"
        val value: String       // Display value
    )

    /**
     * Generate a comprehensive flock productivity summary for a user
     */
    suspend fun analyzeFlockProductivity(userId: String): FlockSummary {
        val allBirds = productDao.getProductsBySellerSuspend(userId)
            .filter { it.category.lowercase() in listOf("bird", "rooster", "hen", "chick", "poultry", "") }

        if (allBirds.isEmpty()) {
            return FlockSummary(
                totalBirds = 0, totalMales = 0, totalFemales = 0,
                avgBvi = 0f, totalOffspring = 0,
                totalRevenue = 0.0, totalCost = 0.0, netProfit = 0.0,
                overallROI = 0.0, avgCostPerChick = 0.0,
                birdLeaderboard = emptyList(),
                breedBreakdown = emptyList(),
                topPerformers = emptyList()
            )
        }

        val males = allBirds.count { it.gender?.lowercase() == "male" }
        val females = allBirds.count { it.gender?.lowercase() == "female" }

        // Analyze each bird
        val roiResults = allBirds.mapNotNull { bird ->
            try { breedingROIService.analyzeBirdROI(bird.productId) } catch (_: Exception) { null }
        }

        val totalRevenue = roiResults.sumOf { it.totalRevenue }
        val totalCost = roiResults.sumOf { it.totalCost }
        val netProfit = totalRevenue - totalCost
        val overallROI = if (totalCost > 0) (netProfit / totalCost) * 100 else 0.0
        val totalOffspring = roiResults.sumOf { it.offspringCount }
        val avgCostPerChick = roiResults.filter { it.offspringCount > 0 }
            .map { it.costPerChick }.average().takeIf { !it.isNaN() } ?: 0.0

        // BVI for all birds
        val bviMap = allBirds.associate { bird ->
            bird.productId to (try { breedingValueService.calculateBVI(bird.productId).bvi } catch (_: Exception) { 0f })
        }
        val avgBvi = bviMap.values.average().toFloat()

        // Build leaderboard (sorted by productivity score)
        val leaderboardUnsorted = roiResults.map { roi ->
            val bvi = bviMap[roi.bird.productId] ?: 0f
            val productivityScore = calculateProductivityScore(bvi, roi)
            val badge = when {
                productivityScore >= 85f -> "🏆 Champion"
                productivityScore >= 70f -> "⭐ Star"
                productivityScore >= 55f -> "📈 Rising"
                productivityScore >= 40f -> "📊 Steady"
                else -> "🌱 Developing"
            }
            LeaderboardEntry(
                rank = 0, // Will be assigned after sorting
                bird = roi.bird,
                bvi = bvi,
                offspringCount = roi.offspringCount,
                roi = roi.roiPercent,
                showWins = roi.showWins,
                productivityScore = productivityScore,
                badge = badge
            )
        }.sortedByDescending { it.productivityScore }

        val leaderboard = leaderboardUnsorted.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }

        // Breed breakdown
        val breedGroups = roiResults.groupBy { it.bird.breed ?: "Unknown" }
        val breedBreakdown = breedGroups.map { (breed, birds) ->
            val bviAvg = birds.mapNotNull { bviMap[it.bird.productId] }.average().toFloat()
            val roiAvg = birds.map { it.roiPercent }.average()
            val offspringTotal = birds.sumOf { it.offspringCount }
            val avgCPC = birds.filter { it.offspringCount > 0 }.map { it.costPerChick }
                .average().takeIf { !it.isNaN() } ?: 0.0
            val topBird = birds.maxByOrNull { bviMap[it.bird.productId] ?: 0f }

            BreedSummary(
                breed = breed,
                count = birds.size,
                avgBvi = bviAvg,
                avgROI = roiAvg,
                totalOffspring = offspringTotal,
                avgCostPerChick = avgCPC,
                topBird = topBird?.bird?.name
            )
        }.sortedByDescending { it.avgBvi }

        // Top performers (badges)
        val topPerformers = mutableListOf<PerformerBadge>()

        // Most offspring
        roiResults.maxByOrNull { it.offspringCount }?.let { top ->
            if (top.offspringCount > 0) {
                topPerformers.add(PerformerBadge(top.bird, "Top Producer", "${top.offspringCount} chicks"))
            }
        }

        // Most show wins
        roiResults.maxByOrNull { it.showWins }?.let { top ->
            if (top.showWins > 0) {
                topPerformers.add(PerformerBadge(top.bird, "Show Champion", "${top.showWins} wins"))
            }
        }

        // Best ROI
        roiResults.filter { it.offspringCount > 0 }.maxByOrNull { it.roiPercent }?.let { top ->
            topPerformers.add(PerformerBadge(top.bird, "Best ROI", "${String.format("%.0f", top.roiPercent)}%"))
        }

        // Highest BVI
        val highestBvi = bviMap.maxByOrNull { it.value }
        highestBvi?.let { (id, bvi) ->
            val bird = allBirds.find { it.productId == id }
            if (bird != null && bvi > 0) {
                topPerformers.add(PerformerBadge(bird, "Elite Genetics", "${(bvi * 100).toInt()} BVI"))
            }
        }

        return FlockSummary(
            totalBirds = allBirds.size,
            totalMales = males,
            totalFemales = females,
            avgBvi = avgBvi,
            totalOffspring = totalOffspring,
            totalRevenue = totalRevenue,
            totalCost = totalCost,
            netProfit = netProfit,
            overallROI = overallROI,
            avgCostPerChick = avgCostPerChick,
            birdLeaderboard = leaderboard,
            breedBreakdown = breedBreakdown,
            topPerformers = topPerformers
        )
    }

    /**
     * Composite productivity score (0–100) weighing BVI, ROI, offspring, and show record.
     */
    private fun calculateProductivityScore(bvi: Float, roi: BreedingROIService.BirdROI): Float {
        // Weights: BVI 35%, ROI 25%, Offspring production 25%, Show performance 15%
        val bviScore = (bvi * 100).coerceIn(0f, 100f) * 0.35f
        val roiScore = (roi.roiPercent.coerceIn(-100.0, 200.0).toFloat() + 100f) / 3f * 0.25f // Normalize -100..200 → 0..100
        val offspringScore = (roi.offspringCount.coerceAtMost(20).toFloat() / 20f * 100f) * 0.25f
        val showScore = if (roi.showTotal > 0) {
            (roi.showWins.toFloat() / roi.showTotal * 100f).coerceIn(0f, 100f) * 0.15f
        } else 0f

        return (bviScore + roiScore + offspringScore + showScore).coerceIn(0f, 100f)
    }
}
