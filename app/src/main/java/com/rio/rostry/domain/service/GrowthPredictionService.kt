package com.rio.rostry.domain.service

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Growth Prediction Service - Predicts bird growth trajectory and market readiness
 * 
 * Features:
 * - Breed-specific growth benchmarks
 * - Growth trajectory prediction
 * - Anomaly detection
 */
@Singleton
class GrowthPredictionService @Inject constructor() {
    companion object {
        // Standard breed growth curves (grams per week)
        val BREED_BENCHMARKS = mapOf(
            "Broiler" to BreedBenchmark(
                weeklyTargets = listOf(150, 400, 750, 1100, 1500, 1900, 2300, 2700),
                optimalSlaughterWeight = 2500,
                maxAge = 8
            ),
            "Kadaknath" to BreedBenchmark(
                weeklyTargets = listOf(80, 180, 300, 450, 600, 750, 900, 1050, 1200),
                optimalSlaughterWeight = 1200,
                maxAge = 12
            ),
            "Aseel" to BreedBenchmark(
                weeklyTargets = listOf(100, 220, 380, 550, 720, 900, 1100, 1300, 1500, 1700),
                optimalSlaughterWeight = 2000,
                maxAge = 16
            ),
            "Desi" to BreedBenchmark(
                weeklyTargets = listOf(70, 150, 280, 420, 570, 720, 870, 1020, 1170, 1320),
                optimalSlaughterWeight = 1500,
                maxAge = 14
            ),
            "Layer" to BreedBenchmark(
                weeklyTargets = listOf(60, 140, 250, 400, 550, 700, 850, 1000, 1150, 1300),
                optimalSlaughterWeight = 1800,
                maxAge = 20
            )
        )
        
        val DEFAULT_BENCHMARK = BreedBenchmark(
            weeklyTargets = listOf(100, 250, 450, 700, 1000, 1300, 1600, 1900),
            optimalSlaughterWeight = 2000,
            maxAge = 10
        )
    }
    
    /**
     * Predict growth trajectory from weight history
     * @param weights List of weight measurements in grams (chronological order)
     * @param breed Breed name for benchmark comparison
     */
    fun predictGrowthTrajectory(weights: List<Int>, breed: String = "Broiler"): GrowthPrediction {
        if (weights.size < 2) {
            return GrowthPrediction(
                status = GrowthStatus.INSUFFICIENT_DATA,
                message = "Need at least 2 weight records for prediction"
            )
        }
        
        val benchmark = BREED_BENCHMARKS[breed] ?: DEFAULT_BENCHMARK
        
        val currentWeight = weights.last()
        val currentWeek = weights.size
        
        // Calculate growth rate (grams per week average)
        val weeklyGrowthRate = (weights.last() - weights.first()) / (weights.size - 1).toFloat()
        
        // Compare to benchmark
        val expectedWeight = benchmark.weeklyTargets.getOrElse(currentWeek - 1) { benchmark.weeklyTargets.last() }
        val performanceRatio = currentWeight.toFloat() / expectedWeight
        
        val status = when {
            performanceRatio >= 1.1 -> GrowthStatus.ABOVE_AVERAGE
            performanceRatio >= 0.95 -> GrowthStatus.ON_TRACK
            performanceRatio >= 0.8 -> GrowthStatus.BELOW_AVERAGE
            else -> GrowthStatus.NEEDS_ATTENTION
        }
        
        // Predict weeks to optimal weight
        val weeksToOptimal = if (weeklyGrowthRate > 0) {
            ((benchmark.optimalSlaughterWeight - currentWeight) / weeklyGrowthRate).toInt()
        } else -1
        
        // Calculate projected weights for next 6 weeks
        val projectedWeights = (1..6).map { weeksAhead ->
            currentWeight + (weeklyGrowthRate * weeksAhead).toInt()
        }
        
        // Detect anomalies
        val anomalies = detectGrowthAnomalies(weights)
        
        val recommendations = generateGrowthRecommendations(status, performanceRatio)
        val message = generateStatusMessage(status, performanceRatio, weeksToOptimal)
        
        return GrowthPrediction(
            currentWeight = currentWeight,
            currentWeek = currentWeek,
            weeklyGrowthRate = weeklyGrowthRate,
            expectedWeight = expectedWeight,
            performanceRatio = performanceRatio,
            status = status,
            weeksToOptimalWeight = weeksToOptimal,
            optimalWeight = benchmark.optimalSlaughterWeight,
            projectedWeights = projectedWeights,
            anomalies = anomalies,
            recommendations = recommendations,
            message = message
        )
    }
    
    /**
     * Get benchmark for a breed
     */
    fun getBenchmark(breed: String): BreedBenchmark {
        return BREED_BENCHMARKS[breed] ?: DEFAULT_BENCHMARK
    }
    
    /**
     * Compare multiple batches
     */
    fun compareBatches(batches: List<BatchData>): List<BatchPerformance> {
        return batches.mapNotNull { batch ->
            val prediction = predictGrowthTrajectory(batch.weights, batch.breed)
            if (prediction.status != GrowthStatus.INSUFFICIENT_DATA) {
                BatchPerformance(
                    productId = batch.productId,
                    name = batch.name,
                    breed = batch.breed,
                    currentWeight = prediction.currentWeight,
                    performanceRatio = prediction.performanceRatio,
                    status = prediction.status,
                    weeklyGrowthRate = prediction.weeklyGrowthRate
                )
            } else null
        }.sortedByDescending { it.performanceRatio }
    }
    
    /**
     * Detect growth anomalies using statistical analysis
     */
    private fun detectGrowthAnomalies(weights: List<Int>): List<GrowthAnomaly> {
        if (weights.size < 3) return emptyList()
        
        val anomalies = mutableListOf<GrowthAnomaly>()
        
        // Calculate weekly changes
        val changes = weights.zipWithNext { a, b -> b - a }
        
        // Calculate mean and standard deviation
        val mean = changes.average()
        val variance = changes.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        
        // Detect outliers (> 2 standard deviations)
        changes.forEachIndexed { index, change ->
            val zScore = if (stdDev > 0) (change - mean) / stdDev else 0.0
            
            when {
                zScore < -2 -> anomalies.add(GrowthAnomaly(
                    weekNumber = index + 2,
                    type = AnomalyType.GROWTH_STALL,
                    severity = if (zScore < -3) Severity.HIGH else Severity.MEDIUM,
                    message = "Unusual growth slowdown in week ${index + 2}"
                ))
                zScore > 2 -> anomalies.add(GrowthAnomaly(
                    weekNumber = index + 2,
                    type = AnomalyType.GROWTH_SPIKE,
                    severity = Severity.LOW,
                    message = "Exceptional growth in week ${index + 2}"
                ))
                change < 0 -> anomalies.add(GrowthAnomaly(
                    weekNumber = index + 2,
                    type = AnomalyType.WEIGHT_LOSS,
                    severity = Severity.HIGH,
                    message = "Weight loss detected in week ${index + 2} - check health!"
                ))
            }
        }
        
        return anomalies
    }
    
    private fun generateGrowthRecommendations(status: GrowthStatus, ratio: Float): List<String> {
        return when (status) {
            GrowthStatus.NEEDS_ATTENTION -> listOf(
                "🔍 Check for health issues - slow growth often indicates illness",
                "🍽️ Review feed quality and quantity",
                "🌡️ Ensure proper housing temperature",
                "💧 Verify access to clean water"
            )
            GrowthStatus.BELOW_AVERAGE -> listOf(
                "📊 Consider increasing protein content in feed",
                "🏥 Schedule a health check-up"
            )
            GrowthStatus.ON_TRACK -> listOf(
                "✅ Growth on track! Maintain current practices"
            )
            GrowthStatus.ABOVE_AVERAGE -> listOf(
                "🏆 Excellent growth! Document your practices",
                "📈 Consider earlier sale for better FCR"
            )
            else -> emptyList()
        }
    }
    
    private fun generateStatusMessage(status: GrowthStatus, ratio: Float, weeksToOptimal: Int): String {
        val percentStr = "${((ratio - 1) * 100).toInt().let { if (it > 0) "+$it" else "$it" }}%"
        
        return when (status) {
            GrowthStatus.ABOVE_AVERAGE -> "Growing $percentStr above average! Ready in ~$weeksToOptimal weeks."
            GrowthStatus.ON_TRACK -> "On track for optimal weight in ~$weeksToOptimal weeks."
            GrowthStatus.BELOW_AVERAGE -> "Growing $percentStr below average. May need intervention."
            GrowthStatus.NEEDS_ATTENTION -> "Growth significantly below target. Immediate action recommended."
            else -> "Unable to determine growth status."
        }
    }
}

// ============ Data Classes ============

data class BreedBenchmark(
    val weeklyTargets: List<Int>,
    val optimalSlaughterWeight: Int,
    val maxAge: Int
)

data class GrowthPrediction(
    val currentWeight: Int = 0,
    val currentWeek: Int = 0,
    val weeklyGrowthRate: Float = 0f,
    val expectedWeight: Int = 0,
    val performanceRatio: Float = 0f,
    val status: GrowthStatus,
    val weeksToOptimalWeight: Int = -1,
    val optimalWeight: Int = 0,
    val projectedWeights: List<Int> = emptyList(),
    val anomalies: List<GrowthAnomaly> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val message: String = ""
)

enum class GrowthStatus {
    ABOVE_AVERAGE,
    ON_TRACK,
    BELOW_AVERAGE,
    NEEDS_ATTENTION,
    INSUFFICIENT_DATA,
    ERROR
}

data class GrowthAnomaly(
    val weekNumber: Int,
    val type: AnomalyType,
    val severity: Severity,
    val message: String
)

enum class AnomalyType { GROWTH_STALL, GROWTH_SPIKE, WEIGHT_LOSS }
enum class Severity { LOW, MEDIUM, HIGH }

data class BatchData(
    val productId: String,
    val name: String,
    val breed: String,
    val weights: List<Int>
)

data class BatchPerformance(
    val productId: String,
    val name: String,
    val breed: String,
    val currentWeight: Int,
    val performanceRatio: Float,
    val status: GrowthStatus,
    val weeklyGrowthRate: Float
)
