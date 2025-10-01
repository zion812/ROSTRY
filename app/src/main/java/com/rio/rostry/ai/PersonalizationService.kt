package com.rio.rostry.ai

import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.session.SessionManager
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.PostEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class PersonalizationService @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val sessionManager: SessionManager,
    private val recommendationEngine: RecommendationEngine
) {
    // In-memory cache for personalization results with TTL
    private val mutex = Mutex()
    private val productRankingCache = mutableMapOf<String, Pair<List<ProductEntity>, Long>>()
    private val postRankingCache = mutableMapOf<String, Pair<List<PostEntity>, Long>>()
    private val cacheTTL = 5 * 60 * 1000L // 5 minutes

    suspend fun personalizeFor(userId: String) {
        // Trigger recommendation computation to refresh caches
        recommendationEngine.recommendationsFor(userId)
    }

    /**
     * Rank products based on user preferences and behavior
     */
    suspend fun rankProductsForUser(userId: String, products: List<ProductEntity>): List<ProductEntity> {
        // Check cache
        mutex.withLock {
            val cached = productRankingCache[userId]
            if (cached != null && System.currentTimeMillis() - cached.second < cacheTTL) {
                return cached.first
            }
        }

        // Score products based on personalization factors
        val scored = products.map { product ->
            var score = 0.0

            // User preference: preferred breeds (mock - would use analytics)
            if (product.breed != null) {
                score += 10.0
            }

            // Price range preferences (mid-range preferred)
            if (product.price in 1000.0..10000.0) {
                score += 15.0
            }

            // Verified sellers boost
            if (!product.sellerId.isNullOrBlank()) {
                score += 10.0
            }

            // Traceable products boost
            if (product.familyTreeId != null) {
                score += 8.0
            }

            // Fresh listings
            val age = System.currentTimeMillis() - product.createdAt
            if (age < 7 * 24 * 60 * 60 * 1000) { // Less than 7 days
                score += 5.0
            }

            // Location proximity (would use real location data)
            score += (product.productId.hashCode().toDouble() % 10)

            product to score
        }

        val ranked = scored.sortedByDescending { it.second }.map { it.first }

        // Update cache
        mutex.withLock {
            productRankingCache[userId] = ranked to System.currentTimeMillis()
        }

        return ranked
    }

    /**
     * Rank posts for personalized social feed
     */
    suspend fun rankPostsForUser(userId: String, posts: List<PostEntity>): List<PostEntity> {
        // Check cache
        mutex.withLock {
            val cached = postRankingCache[userId]
            if (cached != null && System.currentTimeMillis() - cached.second < cacheTTL) {
                return cached.first
            }
        }

        // Score posts based on user engagement patterns
        val scored = posts.map { post ->
            var score = 0.0

            // Recent posts boost
            val age = System.currentTimeMillis() - post.createdAt
            if (age < 24 * 60 * 60 * 1000) { // Less than 24 hours
                score += 20.0
            }

            // Posts with media get boost
            if (!post.mediaUrl.isNullOrBlank()) {
                score += 15.0
            }

            // Posts with text content
            if (!post.text.isNullOrBlank() && post.text!!.length > 50) {
                score += 10.0
            }

            // Video content boost
            if (post.type.equals("video", ignoreCase = true)) {
                score += 12.0
            }

            // Randomization for discovery
            score += (post.postId.hashCode().toDouble() % 20)

            post to score
        }

        val ranked = scored.sortedByDescending { it.second }.map { it.first }

        // Update cache
        mutex.withLock {
            postRankingCache[userId] = ranked to System.currentTimeMillis()
        }

        return ranked
    }

    /**
     * Determine if user should be notified based on activity patterns
     */
    suspend fun shouldNotifyUser(userId: String, notificationType: String): Boolean {
        // Simple heuristic: allow notifications during active hours
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        
        return when (notificationType) {
            "PRODUCT_UPDATE" -> hour in 9..21 // 9 AM to 9 PM
            "SOCIAL_ACTIVITY" -> hour in 8..22 // 8 AM to 10 PM
            "PRICE_DROP" -> hour in 10..20 // 10 AM to 8 PM
            "BREEDING_REMINDER" -> hour in 7..19 // 7 AM to 7 PM
            else -> hour in 9..21
        }
    }

    /**
     * Clear cache for a specific user
     */
    suspend fun clearCache(userId: String) {
        mutex.withLock {
            productRankingCache.remove(userId)
            postRankingCache.remove(userId)
        }
    }
}
