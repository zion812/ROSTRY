package com.rio.rostry.domain.monitoring.service

/**
 * Domain interface for the price trend analysis service.
 *
 * Provides seasonal price analysis, "best time to sell" predictions,
 * price comparisons against market averages, and sales-data-driven insights.
 */
interface PriceTrendService {

    /** Compare a price against the market average. */
    fun comparePriceWithMarket(yourPrice: Float, marketAverage: Float): Map<String, Any>

    /** Predict the best upcoming time to sell based on seasonal patterns. */
    fun predictBestSellingTime(): Map<String, Any>

    /** Get current seasonal context (peak / normal / low). */
    fun getSeasonalContext(): Map<String, Any>

    /** Analyze price trend from a historical price list. */
    fun analyzeTrend(priceHistory: List<Float>): String

    /** Get the market average price for a specific breed from sales data. */
    fun getBreedMarketAverage(soldProducts: List<Map<String, Any>>, breed: String): Float

    /** Get a suggested price range based on sales data. */
    fun getSuggestedPriceRange(soldProducts: List<Map<String, Any>>, breed: String): Map<String, Any>
}
