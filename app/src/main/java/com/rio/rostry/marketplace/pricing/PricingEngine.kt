package com.rio.rostry.marketplace.pricing

import com.rio.rostry.marketplace.model.AgeGroup
import com.rio.rostry.marketplace.model.ProductCategory

/**
 * Provides pricing suggestions and commission calculations.
 * Simple heuristic-based implementation for demo.
 */
object PricingEngine {
    data class Suggestion(
        val suggestedPrice: Double,
        val auctionStart: Double,
        val seasonalAdjustmentPct: Double,
        val commissionRate: Double
    )

    fun suggest(category: ProductCategory, age: AgeGroup, base: Double? = null, season: Int? = null): Suggestion {
        val commission = 0.05
        val seasonal = when (season) {
            12,1 -> 0.08 // peak demand
            6,7 -> -0.05 // monsoon slump example
            else -> 0.0
        }
        val range = when (age) {
            AgeGroup.CHICK_0_5_WEEKS -> 300.0 to 1500.0
            AgeGroup.YOUNG_5_20_WEEKS -> 800.0 to 6000.0
            AgeGroup.ADULT_20_52_WEEKS -> 1200.0 to 12000.0
            AgeGroup.BREEDER_12_MONTHS_PLUS -> 3000.0 to 30000.0
        }
        val mid = (range.first + range.second) / 2
        val catAdj = when (category) {
            is ProductCategory.Meat -> -0.15
            is ProductCategory.AdoptionNonTraceable -> 0.0
            is ProductCategory.AdoptionTraceable -> 0.25
        }
        val basePrice = base ?: mid
        val seasonalAdj = basePrice * seasonal
        val catAdjusted = basePrice * (1.0 + catAdj)
        val suggested = (catAdjusted + seasonalAdj).coerceIn(range.first, range.second)
        val auctionStart = (suggested * 0.7).coerceAtLeast(range.first * 0.8)
        return Suggestion(suggestedPrice = suggested, auctionStart = auctionStart, seasonalAdjustmentPct = seasonal, commissionRate = commission)
    }

    fun commission(amount: Double, rate: Double = 0.05): Double = amount * rate
}
