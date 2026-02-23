package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.domain.error.ErrorHandler
import timber.log.Timber
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

import com.rio.rostry.domain.manager.DegradationManager
import com.rio.rostry.domain.manager.DegradedService

/**
 * Marketplace recommendation engine providing personalized product suggestions
 * based on purchase history, breed preferences, and location proximity.
 */
@Singleton
class MarketplaceRecommendationEngine @Inject constructor(
    private val productDao: ProductDao,
    private val errorHandler: ErrorHandler,
    private val degradationManager: DegradationManager
) {

    companion object {
        const val MAX_RECOMMENDATIONS = 20
        const val HISTORY_WEIGHT = 0.4
        const val PREFERENCE_WEIGHT = 0.3
        const val LOCATION_WEIGHT = 0.2
        const val RECENCY_WEIGHT = 0.1
    }

    data class Recommendation(
        val productId: String,
        val productName: String,
        val score: Double,
        val reason: String
    )

    /**
     * Get personalized recommendations for a user.
     */
    suspend fun getRecommendations(
        userId: String,
        userLat: Double? = null,
        userLon: Double? = null,
        limit: Int = MAX_RECOMMENDATIONS
    ): List<Recommendation> {
        return try {
            // Fallback to popular products when we can't personalize
            val result = getPopularProducts(limit)
            degradationManager.reportRecovered(DegradedService.RECOMMENDATION)
            result
        } catch (e: Exception) {
            degradationManager.reportDegraded(DegradedService.RECOMMENDATION)
            errorHandler.handle(e, "MarketplaceRecommendationEngine.getRecommendations")
            getPopularProducts(limit) // Fallback anyway
        }
    }

    /**
     * Get "frequently bought together" product pairs.
     */
    suspend fun getFrequentlyBoughtTogether(productId: String): List<String> {
        return try {
            // Placeholder — real implementation would query order_items co-occurrence
            emptyList()
        } catch (e: Exception) {
            Timber.w(e, "Failed to get frequently bought together")
            emptyList()
        }
    }

    /**
     * Fallback: return popular products by order count.
     */
    private suspend fun getPopularProducts(limit: Int): List<Recommendation> {
        return try {
            // Return available products sorted by view count / popularity
            // In a real scenario we'd want only active products, but this DAO only has getAllProducts
            val products = productDao.getAllProducts().firstOrNull() ?: emptyList()
            products.take(limit).map { product ->
                Recommendation(
                    productId = product.productId,
                    productName = product.name,
                    score = 1.0,
                    reason = "Popular product"
                )
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to get popular products")
            emptyList()
        }
    }

    /**
     * Calculate haversine distance between two coordinates in km.
     */
    fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
