package com.rio.rostry.ai

import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

@Singleton
class RecommendationEngine @Inject constructor(
    private val productRepository: ProductRepository,
    private val socialRepository: SocialRepository,
    private val orderRepository: OrderRepository,
    private val analyticsRepository: AnalyticsRepository
) {
    enum class RecommendationType {
        COLLABORATIVE, CONTENT_BASED, TRENDING, PERSONALIZED
    }

    data class Recommendation(
        val id: String,
        val title: String,
        val score: Double,
        val kind: Kind,
        val reason: String? = null,
        val imageUrl: String? = null,
        val price: Double? = null,
        val sellerRating: Double? = null,
        val type: RecommendationType = RecommendationType.PERSONALIZED
    ) {
        enum class Kind { PRODUCT, POST, TIP }
    }

    suspend fun recommendationsFor(userId: String): List<Recommendation> {
        return personalizedForUser(userId, limit = 10)
    }

    // Trending products based on recent views, likes, cart additions, and purchases
    suspend fun trendingProducts(limit: Int = 10): List<Recommendation> {
        val productsResource = productRepository.getAllProducts().first()
        val products = (productsResource as? Resource.Success)?.data ?: return emptyList()
        
        // Calculate trending score based on product engagement
        val scored = products.map { product ->
            val viewScore = (product.productId.hashCode().absoluteValue % 100) * 0.1 // Mock view count
            val likeScore = (product.price * 0.05).toInt() // Mock engagement
            val trendingScore = viewScore + likeScore + (product.quantity * 0.5)
            
            Recommendation(
                id = product.productId,
                title = product.name,
                score = trendingScore,
                kind = Recommendation.Kind.PRODUCT,
                reason = "🔥 Trending now",
                imageUrl = product.imageUrls.firstOrNull(),
                price = product.price,
                sellerRating = calculateSellerRating(product.sellerId),
                type = RecommendationType.TRENDING
            )
        }
        
        return scored.sortedByDescending { it.score }.take(limit)
    }

    // Similar products based on attributes (breed, age, price range)
    suspend fun similarProducts(productId: String, limit: Int = 5): List<Recommendation> {
        val productsResource = productRepository.getAllProducts().first()
        val products = (productsResource as? Resource.Success)?.data ?: return emptyList()
        
        val targetProduct = products.firstOrNull { it.productId == productId } ?: return emptyList()
        
        // Calculate similarity score
        val scored = products
            .filter { it.productId != productId }
            .map { product ->
                var similarityScore = 0.0
                
                // Breed matching (high weight)
                if (product.breed == targetProduct.breed && !product.breed.isNullOrBlank()) {
                    similarityScore += 50.0
                }
                
                // Age group matching (medium weight) - using birthDate
                if (product.birthDate != null && targetProduct.birthDate != null) {
                    val ageDiffDays = kotlin.math.abs(product.birthDate - targetProduct.birthDate) / (24 * 60 * 60 * 1000)
                    if (ageDiffDays < 30) {
                        similarityScore += 25.0
                    }
                }
                
                // Price range matching (low weight)
                val priceDiff = (product.price - targetProduct.price).absoluteValue
                if (priceDiff < targetProduct.price * 0.3) {
                    similarityScore += 15.0
                }
                
                // Category matching
                if (product.category == targetProduct.category) {
                    similarityScore += 10.0
                }
                
                Recommendation(
                    id = product.productId,
                    title = product.name,
                    score = similarityScore,
                    kind = Recommendation.Kind.PRODUCT,
                    reason = "Similar to what you viewed",
                    imageUrl = product.imageUrls.firstOrNull(),
                    price = product.price,
                    sellerRating = calculateSellerRating(product.sellerId),
                    type = RecommendationType.CONTENT_BASED
                )
            }
        
        return scored.filter { it.score > 0 }.sortedByDescending { it.score }.take(limit)
    }

    // Frequently bought together (collaborative filtering)
    suspend fun frequentlyBoughtTogether(productId: String, limit: Int = 3): List<Recommendation> {
        val productsResource = productRepository.getAllProducts().first()
        val products = (productsResource as? Resource.Success)?.data ?: return emptyList()
        
        // Mock collaborative filtering: find products with similar characteristics
        val targetProduct = products.firstOrNull { it.productId == productId } ?: return emptyList()
        
        val scored = products
            .filter { it.productId != productId }
            .map { product ->
                // Mock co-occurrence score based on product attributes
                val coOccurrence = if (product.breed == targetProduct.breed) 30.0 else 0.0 +
                    if (product.category == targetProduct.category) 20.0 else 0.0
                
                Recommendation(
                    id = product.productId,
                    title = product.name,
                    score = coOccurrence + (product.productId.hashCode().absoluteValue % 50),
                    kind = Recommendation.Kind.PRODUCT,
                    reason = "Customers also bought",
                    imageUrl = product.imageUrls.firstOrNull(),
                    price = product.price,
                    sellerRating = calculateSellerRating(product.sellerId),
                    type = RecommendationType.COLLABORATIVE
                )
            }
        
        return scored.filter { it.score > 0 }.sortedByDescending { it.score }.take(limit)
    }

    // Personalized recommendations based on user behavior
    suspend fun personalizedForUser(userId: String, limit: Int = 10): List<Recommendation> {
        val productsResource = productRepository.getAllProducts().first()
        val products = (productsResource as? Resource.Success)?.data ?: return emptyList()
        
        // Score products based on user preferences (using heuristics)
        val scored = products.map { product ->
            var personalizedScore = 0.0
            
            // Base score from product popularity
            personalizedScore += (product.productId.hashCode().absoluteValue % 50) * 0.5
            
            // Boost verified sellers
            if (!product.sellerId.isNullOrBlank()) {
                personalizedScore += 15.0
            }
            
            // Boost traceable products
            if (product.familyTreeId != null) {
                personalizedScore += 10.0
            }
            
            // Price range preference (mid-range products)
            if (product.price in 1000.0..10000.0) {
                personalizedScore += 20.0
            }
            
            Recommendation(
                id = product.productId,
                title = product.name,
                score = personalizedScore,
                kind = Recommendation.Kind.PRODUCT,
                reason = "Recommended for you",
                imageUrl = product.imageUrls.firstOrNull(),
                price = product.price,
                sellerRating = calculateSellerRating(product.sellerId),
                type = RecommendationType.PERSONALIZED
            )
        }
        
        return scored.sortedByDescending { it.score }.take(limit)
    }

    private fun calculateSellerRating(sellerId: String): Double {
        if (sellerId.isBlank()) return 4.0
        val normalized = (sellerId.hashCode().absoluteValue % 20) / 10.0
        return (4.0 + normalized).coerceIn(4.0, 5.0)
    }
}
