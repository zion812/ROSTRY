package com.rio.rostry.marketplace.intelligence

import android.util.Log
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data classes for recommendations
 */
data class RecommendationRequest(
    val userId: String,
    val productId: String? = null,
    val limit: Int = 5
)

data class RecommendationResult(
    val products: List<ProductEntity>,
    val strategy: RecommendationStrategy
)

enum class RecommendationStrategy {
    PERSONALIZED,          // Based on user history
    RELATED,               // Based on current product
    FREQUENTLY_BOUGHT,     // Co-occurrence analysis
    POPULAR,               // Fallback to popular items
    DEFAULT                // System defaults
}

/**
 * Recommendation Engine for marketplace products.
 * 
 * Provides personalized product recommendations based on:
 * - User browsing and purchase history
 * - Product similarities (breed, category, price range)
 * - Co-occurrence analysis (frequently bought together)
 * - Popular products fallback when insufficient data
 * 
 * Performance target: < 500ms response time
 */
@Singleton
class RecommendationEngine @Inject constructor(
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) {
    companion object {
        private const val TAG = "RecommendationEngine"
        private const val BROWSING_HISTORY_DAYS = 30L
        private const val MIN_RECOMMENDATIONS = 5
    }

    /**
     * Get personalized recommendations for a user.
     */
    suspend fun getRecommendations(request: RecommendationRequest): RecommendationResult {
        val startTime = System.currentTimeMillis()

        return try {
            // 1. Try personalized recommendations based on purchase history
            val personalized = getPersonalizedRecommendations(request.userId, request.limit)
            if (personalized.size >= MIN_RECOMMENDATIONS) {
                logTiming("Personalized", startTime)
                return RecommendationResult(personalized, RecommendationStrategy.PERSONALIZED)
            }

            // 2. Try related products if viewing a specific product
            if (request.productId != null) {
                val related = getRelatedProducts(request.productId, request.limit)
                if (related.size >= MIN_RECOMMENDATIONS) {
                    logTiming("Related", startTime)
                    return RecommendationResult(related, RecommendationStrategy.RELATED)
                }
            }

            // 3. Try frequently bought together
            val frequentlyBought = getFrequentlyBoughtTogether(request.userId, request.limit)
            if (frequentlyBought.size >= MIN_RECOMMENDATIONS) {
                logTiming("FrequentlyBought", startTime)
                return RecommendationResult(frequentlyBought, RecommendationStrategy.FREQUENTLY_BOUGHT)
            }

            // 4. Fallback to popular products
            val popular = getPopularProducts(request.limit)
            logTiming("Popular", startTime)
            RecommendationResult(popular, RecommendationStrategy.POPULAR)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recommendations for user ${request.userId}", e)
            // Final fallback: return empty with DEFAULT strategy
            RecommendationResult(emptyList(), RecommendationStrategy.DEFAULT)
        }
    }

    /**
     * Get personalized recommendations based on purchase history.
     * Analyzes breeds, categories, and price ranges from past orders.
     */
    private suspend fun getPersonalizedRecommendations(userId: String, limit: Int): List<ProductEntity> {
        // Get user's purchase history to determine preferences
        val userOrders = orderDao.getOrdersByBuyerIdSuspend(userId)
        if (userOrders.isEmpty()) return emptyList()

        // Extract product preferences from orders
        val purchasedProductIds = userOrders.map { it.productId }.toSet()
        val allProducts = productDao.getAllProductsSnapshot()

        // Get breeds and categories from purchased products
        val purchasedProducts = allProducts.filter { it.productId in purchasedProductIds }
        val preferredBreeds = purchasedProducts.mapNotNull { it.breed }.toSet()
        val preferredCategories = purchasedProducts.mapNotNull { it.category }.toSet()
        val avgPrice = purchasedProducts.mapNotNull { it.price }.average().let { 
            if (it.isNaN()) 0.0 else it
        }

        // Score and rank all products
        return allProducts
            .filter { it.productId !in purchasedProductIds } // Don't recommend already purchased
            .filter { it.sellerId != userId } // Don't recommend own products
            .filter { !it.isDeleted }
            .map { product ->
                var score = 0.0
                if (product.breed in preferredBreeds) score += 30.0
                if (product.category in preferredCategories) score += 20.0
                product.price?.let { price ->
                    if (avgPrice > 0) {
                        val priceDiff = kotlin.math.abs(price - avgPrice) / avgPrice
                        if (priceDiff < 0.3) score += 15.0  // Within 30% of avg price
                        else if (priceDiff < 0.5) score += 5.0
                    }
                }
                product to score
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    /**
     * Get related products based on a specific product's attributes.
     */
    private suspend fun getRelatedProducts(productId: String, limit: Int): List<ProductEntity> {
        val product = productDao.findById(productId) ?: return emptyList()
        val allProducts = productDao.getAllProductsSnapshot()

        return allProducts
            .filter { it.productId != productId }
            .filter { !it.isDeleted }
            .map { candidate ->
                var score = 0.0
                if (candidate.breed == product.breed) score += 40.0
                if (candidate.category == product.category) score += 25.0
                product.price?.let { refPrice ->
                    candidate.price?.let { candPrice ->
                        if (refPrice > 0) {
                            val priceDiff = kotlin.math.abs(candPrice - refPrice) / refPrice
                            if (priceDiff < 0.2) score += 20.0
                            else if (priceDiff < 0.5) score += 10.0
                        }
                    }
                }
                if (candidate.sellerId == product.sellerId) score += 5.0 // Same seller bonus
                candidate to score
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    /**
     * Get frequently bought together products based on order co-occurrence.
     */
    private suspend fun getFrequentlyBoughtTogether(userId: String, limit: Int): List<ProductEntity> {
        val userOrders = orderDao.getOrdersByBuyerIdSuspend(userId)
        if (userOrders.isEmpty()) return emptyList()

        // Find products frequently ordered by buyers who bought similar products
        val purchasedProductIds = userOrders.map { it.productId }.toSet()

        // Get all order values (productId → value) for co-occurrence
        val allOrderValues = orderDao.getAllOrderValues()
        val coOccurrenceMap = mutableMapOf<String, Int>()

        // Simple co-occurrence: find other products bought by buyers who bought similar items
        for (productId in purchasedProductIds) {
            val ordersForProduct = orderDao.getOrdersForProduct(productId)
            for (order in ordersForProduct) {
                val otherOrders = orderDao.getOrdersByBuyerIdSuspend(order.buyerId ?: "")
                for (otherOrder in otherOrders) {
                    if (otherOrder.productId !in purchasedProductIds) {
                        coOccurrenceMap[otherOrder.productId] = 
                            (coOccurrenceMap[otherOrder.productId] ?: 0) + 1
                    }
                }
            }
        }

        // Rank by co-occurrence count
        val topProductIds = coOccurrenceMap.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }

        return topProductIds.mapNotNull { productDao.findById(it) }
    }

    /**
     * Fallback: Get popular products ordered by recent activity.
     */
    private suspend fun getPopularProducts(limit: Int): List<ProductEntity> {
        return productDao.getAllProductsSnapshot()
            .filter { !it.isDeleted }
            .sortedByDescending { it.updatedAt }
            .take(limit)
    }

    private fun logTiming(strategy: String, startTime: Long) {
        val elapsed = System.currentTimeMillis() - startTime
        Log.d(TAG, "$strategy recommendations generated in ${elapsed}ms")
        if (elapsed > 500) {
            Log.w(TAG, "Recommendation took ${elapsed}ms - exceeds 500ms target")
        }
    }
}
