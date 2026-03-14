package com.rio.rostry.domain.recommendation

import com.rio.rostry.core.model.Product
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.commerce.repository.OrderRepository
import com.rio.rostry.domain.commerce.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine for generating product recommendations based on user behavior and preferences.
 * 
 * Implements collaborative filtering and content-based recommendation algorithms.
 */
@Singleton
class RecommendationEngine @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {
    /**
     * Get recommendations for a user based on their viewing history.
     */
    suspend fun getRecommendations(
        userId: String,
        viewedProducts: List<Product>,
        limit: Int = 10
    ): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Get user's purchase history for collaborative filtering
            val purchaseHistory = orderRepository.getUserPurchaseHistory(userId)
                .getOrNull() ?: emptyList()
            
            // Get co-occurrence based recommendations
            val coOccurrenceRecommendations = purchaseHistory
                .flatMap { getFrequentlyBoughtTogether(it.productId, 5).getOrNull() ?: emptyList() }
                .distinctBy { it.id }
                .filter { product -> viewedProducts.none { it.id == product.id } }
                .take(limit)
            
            // If no co-occurrence data, fall back to content-based
            val recommendations = if (coOccurrenceRecommendations.isEmpty()) {
                viewedProducts
                    .flatMap { it.breed?.let { breed -> listOf(breed) } ?: emptyList() }
                    .distinct()
                    .take(limit)
            } else {
                coOccurrenceRecommendations
            }
            
            Result.Success(recommendations)
        } catch (e: Exception) {
            Timber.e(e, "Error getting recommendations for user $userId")
            Result.Error(e)
        }
    }

    /**
     * Get frequently bought together products based on order co-occurrence.
     * Implements collaborative filtering by analyzing order history.
     */
    suspend fun getFrequentlyBoughtTogether(
        productId: String,
        limit: Int = 5
    ): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Get orders containing this product
            val ordersWithProduct = orderRepository.getOrdersContainingProduct(productId)
                .getOrNull() ?: emptyList()
            
            // Extract co-occurring product IDs from same orders
            val coOccurringProductIds = ordersWithProduct
                .flatMap { order -> order.items.map { it.productId } }
                .filter { it != productId }
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key }
            
            // Fetch product details
            val products = coOccurringProductIds
                .mapNotNull { productRepository.getProductById(it).getOrNull() }
            
            Result.Success(products)
        } catch (e: Exception) {
            Timber.e(e, "Error getting frequently bought together for product $productId")
            Result.Error(e)
        }
    }

    /**
     * Get personalized recommendations based on user behavior.
     * Tracks user interactions and builds preference profile.
     */
    suspend fun getPersonalizedRecommendations(
        userId: String,
        limit: Int = 10
    ): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Get user's view history
            val viewedProductIds = getUserViewHistory(userId)
            
            // Get user's purchase history
            val purchasedProductIds = orderRepository.getUserPurchaseHistory(userId)
                .getOrNull()?.map { it.productId } ?: emptyList()
            
            // Get similar products to viewed items
            val viewedProducts = viewedProductIds
                .mapNotNull { productRepository.getProductById(it).getOrNull() }
            
            val similarToViewed = viewedProducts
                .flatMap { product ->
                    getSimilarProducts(product, emptyList(), 3).getOrNull() ?: emptyList()
                }
                .filter { it.id !in viewedProductIds && it.id !in purchasedProductIds }
                .distinctBy { it.id }
                .take(limit)
            
            // Get frequently bought together from purchases
            val frequentlyBought = purchasedProductIds
                .flatMap { getFrequentlyBoughtTogether(it, 3).getOrNull() ?: emptyList() }
                .filter { it.id !in viewedProductIds && it.id !in purchasedProductIds }
                .distinctBy { it.id }
                .take(limit)
            
            // Combine and rank recommendations
            val recommendations = (similarToViewed + frequentlyBought)
                .distinctBy { it.id }
                .take(limit)
            
            Result.Success(recommendations)
        } catch (e: Exception) {
            Timber.e(e, "Error getting personalized recommendations for user $userId")
            Result.Error(e)
        }
    }

    /**
     * Get similar products to a given product.
     * Implements content-based filtering by comparing attributes.
     */
    suspend fun getSimilarProducts(
        product: Product,
        allProducts: List<Product>,
        limit: Int = 5
    ): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // If allProducts not provided, fetch from repository
            val products = if (allProducts.isEmpty()) {
                productRepository.getAllProducts()
                    .getOrNull() ?: emptyList()
            } else {
                allProducts
            }
            
            val similar = products
                .filter { it.id != product.id }
                .filter { 
                    it.breed == product.breed || 
                    it.category == product.category ||
                    it.priceRange == product.priceRange
                }
                .sortedByDescending { similarityScore(product, it) }
                .take(limit)
            
            Result.Success(similar)
        } catch (e: Exception) {
            Timber.e(e, "Error getting similar products for ${product.id}")
            Result.Error(e)
        }
    }

    /**
     * Calculate similarity score between two products.
     */
    private fun similarityScore(source: Product, target: Product): Int {
        var score = 0
        
        // Same breed: high score
        if (source.breed == target.breed) score += 10
        
        // Same category: medium score
        if (source.category == target.category) score += 5
        
        // Same price range: low score
        if (source.priceRange == target.priceRange) score += 2
        
        return score
    }

    /**
     * Get user's view history (placeholder - would integrate with analytics).
     */
    private suspend fun getUserViewHistory(userId: String): List<String> {
        // In production, this would query user behavior tracking
        return emptyList()
    }
}
