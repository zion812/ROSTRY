package com.rio.rostry.domain.service

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import kotlin.math.roundToInt

/**
 * AI-Powered Feed Optimizer Service
 * 
 * Calculates Feed Conversion Ratio (FCR) and provides optimization recommendations.
 * FCR = Total Feed Consumed (kg) / Weight Gained (kg)
 * 
 * Industry Standards (Broilers):
 * - Excellent: FCR < 1.6
 * - Good: FCR 1.6 - 1.8
 * - Average: FCR 1.8 - 2.0
 * - Poor: FCR > 2.0
 * 
 * Lower FCR = Better efficiency = More profit
 */
@Singleton
class FeedConversionService @Inject constructor() {
    companion object {
        // FCR Thresholds (industry standards for broilers)
        const val FCR_EXCELLENT = 1.6f
        const val FCR_GOOD = 1.8f
        const val FCR_AVERAGE = 2.0f
        const val FCR_POOR = 2.2f
    }
    
    /**
     * Calculate FCR from raw data
     * @param totalFeedKg Total feed consumed in kg
     * @param startWeightGrams Starting weight in grams
     * @param endWeightGrams Ending weight in grams
     */
    fun calculateFCR(totalFeedKg: Float, startWeightGrams: Int, endWeightGrams: Int): FeedConversionResult {
        return try {
            if (totalFeedKg <= 0) {
                return FeedConversionResult.InsufficientData("No feed data recorded")
            }
            
            val weightGainGrams = endWeightGrams - startWeightGrams
            if (weightGainGrams <= 0) {
                return FeedConversionResult.InsufficientData("No weight gain recorded")
            }
            
            val weightGainKg = weightGainGrams / 1000f
            val fcr = totalFeedKg / weightGainKg
            
            val rating = when {
                fcr <= 0 -> FCRRating.UNKNOWN
                fcr < FCR_EXCELLENT -> FCRRating.EXCELLENT
                fcr < FCR_GOOD -> FCRRating.GOOD
                fcr < FCR_AVERAGE -> FCRRating.AVERAGE
                else -> FCRRating.POOR
            }
            
            val recommendations = generateRecommendations(rating)
            val potentialSavings = calculatePotentialSavings(fcr)
            
            FeedConversionResult.Success(
                fcr = fcr,
                rating = rating,
                totalFeedKg = totalFeedKg,
                weightGainKg = weightGainKg,
                recommendations = recommendations,
                potentialSavingsPercentage = potentialSavings
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate FCR")
            FeedConversionResult.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Calculate historical FCR trends from growth and feed logs.
     * 
     * @param growthRecords List of growth records for the asset
     * @param feedLogs List of feed activity logs (impl: "FEED")
     * @param initialWeightGrams Initial weight of the bird/batch (default 40g for chicks)
     */
    fun calculateHistoricalFCR(
        growthRecords: List<GrowthRecordEntity>,
        feedLogs: List<FarmActivityLogEntity>,
        initialWeightGrams: Double = 40.0
    ): List<HistoricalFCRPoint> {
        if (growthRecords.isEmpty()) return emptyList()

        val points = mutableListOf<HistoricalFCRPoint>()
        
        // Group data by week
        val maxWeek = growthRecords.maxOfOrNull { it.week } ?: 0
        val feedByWeek = feedLogs
            .filter { it.activityType == "FEED" && it.quantity != null }
            .groupBy { 
                // Estimate week based on createdAt if week not explicit (feed logs usually timestamp based)
                // For now, assume we map timestamp to week relative to start
                // This is complex without batch start date. 
                // Taking a simplified approach: assuming logs are for the same batch/asset.
                // We need to map timestamp -> week. 
                // This Service doesn't know batch start date. 
                // passed in logs should ideally be pre-filtered/mapped or we assume sorted.
                // fallback: simple accumulation if week is not easy to derive.
                // But we need per-week points.
                // Strategy: Use growth record weeks as anchors.
                0 // Placeholder for grouping logic, solved below
            }
            
        // Correct approach: We calculate CUMULATIVE FCR at each growth record point.
        // 1. Sort growth records by date/week
        val sortedGrowth = growthRecords.sortedBy { it.week }
        val earliestLog = feedLogs.minOfOrNull { it.createdAt } ?: 0L
        val minTime = sortedGrowth.minOfOrNull { it.createdAt }?.let { kotlin.math.min(it, earliestLog) } ?: 0L

        var cumulativeFeedKg = 0.0
        
        // We iterate through weeks 1..maxWeek
        // For each week, we sum up feed consumed UP TO that week's measurement
        
        sortedGrowth.forEach { record ->
            val recordTime = record.createdAt
            val currentWeightKg = (record.weightGrams ?: 0.0) / 1000.0
            
            if (currentWeightKg > 0) {
                // Sum all feed logs created before or at this record's time
                // Optimization: In a real app, optimize this loop
                val feedSoFar = feedLogs
                    .filter { it.createdAt <= recordTime }
                    .sumOf { it.quantity ?: 0.0 } // Assumes quantity is in KG
                
                // standard FCR = Total Feed / Total Weight
                // Total Weight = Current Weight (for a single bird/batch tracking)
                // Note: If mortality occurred, this is 'Technical FCR'. 
                // 'Economic FCR' would include feed eaten by dead birds. 
                // Here we assume simple FCR.
                
                val fcr = if (currentWeightKg > 0) feedSoFar / currentWeightKg else 0.0
                
                if (fcr > 0 && fcr < 10.0) { // Filter outliers
                     points.add(HistoricalFCRPoint(
                         week = record.week,
                         fcr = fcr.toFloat(),
                         weightKg = currentWeightKg.toFloat(),
                         cumulativeFeedKg = feedSoFar.toFloat()
                     ))
                }
            }
        }
        
        return points.sortedBy { it.week }
    }

    /**
     * Analyze mortality patterns from data
     */
    fun analyzeMortality(
        totalDeaths: Int,
        totalBirds: Int,
        recentDeaths: Int,
        previousPeriodDeaths: Int
    ): MortalityAnalysis {
        val mortalityRate = if (totalBirds > 0) totalDeaths.toFloat() / totalBirds * 100 else 0f
        
        val trend = when {
            recentDeaths > previousPeriodDeaths * 1.5 -> MortalityTrend.INCREASING
            recentDeaths < previousPeriodDeaths * 0.5 -> MortalityTrend.DECREASING
            else -> MortalityTrend.STABLE
        }
        
        val alerts = mutableListOf<String>()
        if (trend == MortalityTrend.INCREASING) {
            alerts.add("⚠️ Mortality spike detected! $recentDeaths deaths recently")
        }
        if (mortalityRate > 5) {
            alerts.add("🔴 High mortality rate: ${String.format("%.1f", mortalityRate)}%")
        }
        
        return MortalityAnalysis(
            totalDeaths = totalDeaths,
            mortalityRate = mortalityRate,
            trend = trend,
            recentDeaths = recentDeaths,
            alerts = alerts
        )
    }
    
    /**
     * Generate smart insights based on FCR and mortality data
     */
    fun generateSmartInsights(
        fcrResult: FeedConversionResult?,
        mortalityAnalysis: MortalityAnalysis?
    ): List<SmartInsight> {
        val insights = mutableListOf<SmartInsight>()
        
        // FCR Insight
        (fcrResult as? FeedConversionResult.Success)?.let { result ->
            insights.add(SmartInsight(
                type = InsightType.FEED_EFFICIENCY,
                title = "Feed Efficiency",
                value = String.format("%.2f", result.fcr),
                description = when (result.rating) {
                    FCRRating.EXCELLENT -> "Excellent! Your feed conversion is industry-leading"
                    FCRRating.GOOD -> "Good efficiency. Minor optimizations possible"
                    FCRRating.AVERAGE -> "Room for improvement. Check feed quality"
                    FCRRating.POOR -> "High feed waste detected. Review feeding practices"
                    FCRRating.UNKNOWN -> "Not enough data"
                },
                actionLabel = if (result.rating == FCRRating.POOR) "View Recommendations" else null,
                priority = if (result.rating == FCRRating.POOR) InsightPriority.HIGH else InsightPriority.MEDIUM
            ))
            
            // Savings insight
            if (result.potentialSavingsPercentage > 5) {
                insights.add(SmartInsight(
                    type = InsightType.COST_SAVINGS,
                    title = "Potential Savings",
                    value = "${result.potentialSavingsPercentage.roundToInt()}%",
                    description = "Optimizing feed could save ${result.potentialSavingsPercentage.roundToInt()}% on feed costs",
                    priority = InsightPriority.HIGH
                ))
            }
        }
        
        // Mortality Insight
        mortalityAnalysis?.let { analysis ->
            if (analysis.trend == MortalityTrend.INCREASING) {
                insights.add(SmartInsight(
                    type = InsightType.MORTALITY_ALERT,
                    title = "Mortality Alert",
                    value = "${analysis.recentDeaths} deaths",
                    description = "Unusual mortality pattern detected recently",
                    actionLabel = "View Details",
                    priority = InsightPriority.CRITICAL
                ))
            }
        }
        
        return insights.sortedByDescending { it.priority.ordinal }
    }
    
    private fun generateRecommendations(rating: FCRRating): List<String> {
        return when (rating) {
            FCRRating.POOR -> listOf(
                "🔍 Check feed quality - moisture, mold, or spoilage",
                "⏰ Review feeding schedule - avoid overfeeding",
                "🌡️ Ensure proper temperature (25-30°C for broilers)",
                "💧 Verify water availability - dehydration affects FCR"
            )
            FCRRating.AVERAGE -> listOf(
                "📊 Consider higher protein feed for better conversion",
                "🏥 Review vaccination schedule for health optimization"
            )
            FCRRating.GOOD -> listOf(
                "✅ Maintain current practices",
                "📈 Consider gradual protein increase for marginal gains"
            )
            FCRRating.EXCELLENT -> listOf(
                "🏆 Excellent performance! Document your practices"
            )
            FCRRating.UNKNOWN -> emptyList()
        }
    }
    
    private fun calculatePotentialSavings(currentFCR: Float): Float {
        if (currentFCR <= FCR_EXCELLENT) return 0f
        return ((currentFCR - FCR_EXCELLENT) / currentFCR * 100)
    }
}

// ============ Data Classes ============

sealed class FeedConversionResult {
    data class Success(
        val fcr: Float,
        val rating: FCRRating,
        val totalFeedKg: Float,
        val weightGainKg: Float,
        val recommendations: List<String>,
        val potentialSavingsPercentage: Float = 0f
    ) : FeedConversionResult()
    
    data class InsufficientData(val reason: String) : FeedConversionResult()
    data class Error(val message: String) : FeedConversionResult()
}

enum class FCRRating { EXCELLENT, GOOD, AVERAGE, POOR, UNKNOWN }

data class MortalityAnalysis(
    val totalDeaths: Int,
    val mortalityRate: Float,
    val trend: MortalityTrend,
    val topCause: String? = null,
    val recentDeaths: Int = 0,
    val alerts: List<String> = emptyList()
)

enum class MortalityTrend { INCREASING, STABLE, DECREASING }

data class SmartInsight(
    val type: InsightType,
    val title: String,
    val value: String,
    val description: String,
    val actionLabel: String? = null,
    val priority: InsightPriority = InsightPriority.MEDIUM
)

enum class InsightType { FEED_EFFICIENCY, COST_SAVINGS, MORTALITY_ALERT, GROWTH_TREND, PRICE_OPPORTUNITY }
enum class InsightPriority { LOW, MEDIUM, HIGH, CRITICAL }

data class HistoricalFCRPoint(
    val week: Int,
    val fcr: Float,
    val weightKg: Float,
    val cumulativeFeedKg: Float
)
