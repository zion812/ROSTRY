package com.rio.rostry.domain.service

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Price Trend Service - Analyzes historical prices and predicts optimal selling times
 * 
 * Features:
 * - Seasonal pattern detection based on Indian poultry market
 * - "Best time to sell" suggestions
 * - Price comparison utilities
 */
@Singleton
class PriceTrendService @Inject constructor() {
    companion object {
        // Seasonal multipliers (based on Indian poultry market patterns)
        private val SEASONAL_MULTIPLIERS = mapOf(
            1 to 1.05f,   // January - slight increase post-holidays
            2 to 1.0f,    // February - stable
            3 to 0.95f,   // March - slight decrease
            4 to 0.90f,   // April - summer decrease
            5 to 0.85f,   // May - low season (heat)
            6 to 0.88f,   // June - monsoon start
            7 to 0.92f,   // July - stabilizing
            8 to 0.95f,   // August - festival prep
            9 to 1.05f,   // September - Navratri/festivals
            10 to 1.15f,  // October - Diwali peak
            11 to 1.20f,  // November - wedding season peak
            12 to 1.10f   // December - holiday demand
        )
    }
    
    /**
     * Compare your price with market average
     */
    fun comparePriceWithMarket(yourPrice: Float, marketAverage: Float): PriceComparison {
        if (marketAverage <= 0) {
            return PriceComparison(
                yourPrice = yourPrice,
                marketAverage = 0f,
                percentageDifference = 0f,
                recommendation = "No market data available"
            )
        }
        
        val difference = ((yourPrice - marketAverage) / marketAverage * 100)
        
        val recommendation = when {
            difference > 20 -> "⚠️ Price is ${kotlin.math.abs(difference).toInt()}% above market. Consider lowering for faster sale."
            difference > 10 -> "Your price is slightly above market average. Good for premium quality."
            difference < -20 -> "💰 Price is ${kotlin.math.abs(difference).toInt()}% below market. You could increase it!"
            difference < -10 -> "Competitive price. Should attract buyers quickly."
            else -> "✅ Fair market price. Well positioned."
        }
        
        return PriceComparison(
            yourPrice = yourPrice,
            marketAverage = marketAverage,
            percentageDifference = difference,
            recommendation = recommendation
        )
    }
    
    /**
     * Predict best time to sell based on seasonal patterns
     */
    fun predictBestSellingTime(): BestTimeToSell {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        
        // Find the next peak month
        val futureMonths = (currentMonth..12).toList() + (1 until currentMonth).toList()
        
        var bestMonth = currentMonth
        var bestMultiplier = SEASONAL_MULTIPLIERS[currentMonth] ?: 1f
        
        for ((index, month) in futureMonths.withIndex()) {
            val multiplier = SEASONAL_MULTIPLIERS[month] ?: 1f
            if (multiplier > bestMultiplier) {
                bestMonth = month
                bestMultiplier = multiplier
                // Only look ahead 3 months max for practical advice
                if (index < 3) break
            }
        }
        
        val monthName = SimpleDateFormat("MMMM", Locale.ENGLISH).format(
            Calendar.getInstance().apply { set(Calendar.MONTH, bestMonth - 1) }.time
        )
        
        val weeksUntil = calculateWeeksUntilMonth(bestMonth, currentMonth)
        
        return if (bestMonth == currentMonth) {
            BestTimeToSell(
                recommendation = "Now is a good time to sell!",
                timing = "This month",
                expectedPriceIncrease = 0f,
                weeksAway = 0
            )
        } else {
            BestTimeToSell(
                recommendation = "Consider waiting for $monthName",
                timing = monthName,
                expectedPriceIncrease = ((bestMultiplier - (SEASONAL_MULTIPLIERS[currentMonth] ?: 1f)) * 100),
                weeksAway = weeksUntil
            )
        }
    }
    
    /**
     * Get current seasonal context
     */
    fun getSeasonalContext(): SeasonalContext {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val multiplier = SEASONAL_MULTIPLIERS[month] ?: 1f
        
        val seasonType = when {
            multiplier >= 1.10f -> SeasonType.PEAK
            multiplier >= 0.95f -> SeasonType.NORMAL
            else -> SeasonType.LOW
        }
        
        val opportunity = when (month) {
            9 -> "🎉 Navratri coming! Expect 5-10% price increase"
            10 -> "🪔 Diwali peak! Best selling window of the year"
            11 -> "💒 Wedding season! High demand continues"
            7, 8 -> "📅 Festival season approaching in ${10 - month} months"
            else -> null
        }
        
        return SeasonalContext(
            currentMonth = month,
            multiplier = multiplier,
            seasonType = seasonType,
            upcomingOpportunity = opportunity
        )
    }
    
    /**
     * Analyze price trend from historical data
     */
    fun analyzeTrend(priceHistory: List<Float>): PriceTrend {
        if (priceHistory.size < 4) return PriceTrend.STABLE
        
        val recent = priceHistory.takeLast(2).average()
        val previous = priceHistory.dropLast(2).takeLast(2).average()
        
        if (previous == 0.0) return PriceTrend.STABLE
        
        val changePercent = ((recent - previous) / previous * 100)
        
        return when {
            changePercent > 10 -> PriceTrend.RISING_FAST
            changePercent > 3 -> PriceTrend.RISING
            changePercent < -10 -> PriceTrend.FALLING_FAST
            changePercent < -3 -> PriceTrend.FALLING
            else -> PriceTrend.STABLE
        }
    }
    
    private fun calculateWeeksUntilMonth(targetMonth: Int, currentMonth: Int): Int {
        val monthsAway = if (targetMonth > currentMonth) {
            targetMonth - currentMonth
        } else {
            12 - currentMonth + targetMonth
        }
        return monthsAway * 4
    }
}

// ============ Data Classes ============

data class PriceComparison(
    val yourPrice: Float,
    val marketAverage: Float,
    val percentageDifference: Float,
    val recommendation: String
)

data class BestTimeToSell(
    val recommendation: String,
    val timing: String,
    val expectedPriceIncrease: Float,
    val weeksAway: Int
)

data class SeasonalContext(
    val currentMonth: Int,
    val multiplier: Float,
    val seasonType: SeasonType,
    val upcomingOpportunity: String?
)

enum class SeasonType { PEAK, NORMAL, LOW }

enum class PriceTrend {
    RISING_FAST,
    RISING,
    STABLE,
    FALLING,
    FALLING_FAST
}
