package com.rio.rostry.domain.commerce.engine

import com.rio.rostry.core.model.Recommendation

/**
 * Domain interface for recommendation services.
 */
interface RecommendationEngine {
    /**
     * Get personalized recommendations for a user.
     */
    suspend fun personalizedForUser(userId: String, limit: Int = 10): List<Recommendation>

    /**
     * Get trending products.
     */
    suspend fun trendingProducts(limit: Int = 10): List<Recommendation>

    /**
     * Get products similar to a target product.
     */
    suspend fun similarProducts(productId: String, limit: Int = 5): List<Recommendation>

    /**
     * Get products frequently bought together with a target product.
     */
    suspend fun frequentlyBoughtTogether(productId: String, limit: Int = 3): List<Recommendation>
}
