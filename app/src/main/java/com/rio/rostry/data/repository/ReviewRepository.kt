package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ReviewDao
import com.rio.rostry.data.database.entity.RatingStatsEntity
import com.rio.rostry.data.database.entity.ReviewEntity
import com.rio.rostry.data.database.entity.ReviewHelpfulEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface ReviewRepository {
    // Create/Update
    suspend fun submitReview(
        productId: String?,
        sellerId: String,
        orderId: String?,
        reviewerId: String,
        rating: Int,
        title: String?,
        content: String?,
        isVerifiedPurchase: Boolean
    ): Resource<ReviewEntity>
    
    suspend fun updateReview(
        reviewId: String,
        rating: Int,
        title: String?,
        content: String?
    ): Resource<Unit>
    
    suspend fun deleteReview(reviewId: String): Resource<Unit>
    
    // Read
    fun getProductReviews(productId: String): Flow<List<ReviewEntity>>
    fun getSellerReviews(sellerId: String): Flow<List<ReviewEntity>>
    fun getUserReviews(userId: String): Flow<List<ReviewEntity>>
    suspend fun getReviewByOrder(orderId: String): ReviewEntity?
    suspend fun canUserReview(userId: String, orderId: String): Boolean
    
    // Statistics
    fun getSellerStats(sellerId: String): Flow<RatingStatsEntity?>
    fun getProductStats(productId: String): Flow<RatingStatsEntity?>
    fun countProductReviews(productId: String): Flow<Int>
    fun countSellerReviews(sellerId: String): Flow<Int>
    fun averageProductRating(productId: String): Flow<Double?>
    fun averageSellerRating(sellerId: String): Flow<Double?>
    
    // Helpful votes
    suspend fun markHelpful(reviewId: String, userId: String): Resource<Unit>
    suspend fun unmarkHelpful(reviewId: String, userId: String): Resource<Unit>
    
    // Seller response
    suspend fun addSellerResponse(reviewId: String, sellerId: String, response: String): Resource<Unit>
}

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val orderDao: com.rio.rostry.data.database.dao.OrderDao
) : ReviewRepository {

    override suspend fun submitReview(
        productId: String?,
        sellerId: String,
        orderId: String?,
        reviewerId: String,
        rating: Int,
        title: String?,
        content: String?,
        isVerifiedPurchase: Boolean
    ): Resource<ReviewEntity> {
        return try {
            require(rating in 1..5) { "Rating must be between 1 and 5" }
            require(reviewerId != sellerId) { "You cannot review yourself" }
            
            // Check if already reviewed this order
            if (orderId != null) {
                val existing = reviewDao.getReviewByOrder(orderId)
                if (existing != null) {
                    return Resource.Error("You have already reviewed this order")
                }
            }
            
            val now = System.currentTimeMillis()
            val review = ReviewEntity(
                reviewId = UUID.randomUUID().toString(),
                productId = productId,
                sellerId = sellerId,
                orderId = orderId,
                reviewerId = reviewerId,
                rating = rating,
                title = title,
                content = content,
                isVerifiedPurchase = isVerifiedPurchase,
                helpfulCount = 0,
                responseFromSeller = null,
                responseAt = null,
                createdAt = now,
                updatedAt = now,
                isDeleted = false,
                dirty = true
            )
            
            reviewDao.upsert(review)
            updateRatingStats(sellerId, productId)
            
            Resource.Success(review)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit review")
        }
    }

    override suspend fun updateReview(
        reviewId: String,
        rating: Int,
        title: String?,
        content: String?
    ): Resource<Unit> {
        return try {
            require(rating in 1..5) { "Rating must be between 1 and 5" }
            
            val existing = reviewDao.findById(reviewId) ?: return Resource.Error("Review not found")
            val now = System.currentTimeMillis()
            
            val updated = existing.copy(
                rating = rating,
                title = title,
                content = content,
                updatedAt = now,
                dirty = true
            )
            
            reviewDao.upsert(updated)
            updateRatingStats(existing.sellerId, existing.productId)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update review")
        }
    }

    override suspend fun deleteReview(reviewId: String): Resource<Unit> {
        return try {
            val existing = reviewDao.findById(reviewId) ?: return Resource.Error("Review not found")
            reviewDao.softDelete(reviewId, System.currentTimeMillis())
            updateRatingStats(existing.sellerId, existing.productId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete review")
        }
    }

    override fun getProductReviews(productId: String): Flow<List<ReviewEntity>> =
        reviewDao.getProductReviews(productId)

    override fun getSellerReviews(sellerId: String): Flow<List<ReviewEntity>> =
        reviewDao.getSellerReviews(sellerId)

    override fun getUserReviews(userId: String): Flow<List<ReviewEntity>> =
        reviewDao.getUserReviews(userId)

    override suspend fun getReviewByOrder(orderId: String): ReviewEntity? =
        reviewDao.getReviewByOrder(orderId)

    override suspend fun canUserReview(userId: String, orderId: String): Boolean {
        // Check if user is the buyer of this order and order is delivered
        val order = orderDao.findById(orderId) ?: return false
        return order.buyerId == userId && 
               order.status == "DELIVERED" && 
               reviewDao.getReviewByOrder(orderId) == null
    }

    override fun getSellerStats(sellerId: String): Flow<RatingStatsEntity?> =
        reviewDao.getSellerStats(sellerId)

    override fun getProductStats(productId: String): Flow<RatingStatsEntity?> =
        reviewDao.getProductStats(productId)

    override fun countProductReviews(productId: String): Flow<Int> =
        reviewDao.countProductReviews(productId)

    override fun countSellerReviews(sellerId: String): Flow<Int> =
        reviewDao.countSellerReviews(sellerId)

    override fun averageProductRating(productId: String): Flow<Double?> =
        reviewDao.averageProductRating(productId)

    override fun averageSellerRating(sellerId: String): Flow<Double?> =
        reviewDao.averageSellerRating(sellerId)

    override suspend fun markHelpful(reviewId: String, userId: String): Resource<Unit> {
        return try {
            val review = reviewDao.findById(reviewId) ?: return Resource.Error("Review not found")
            if (review.reviewerId == userId) {
                return Resource.Error("You cannot mark your own review as helpful")
            }
            
            if (reviewDao.hasMarkedHelpful(reviewId, userId)) {
                return Resource.Success(Unit) // Already marked
            }
            
            val helpful = ReviewHelpfulEntity(
                reviewId = reviewId,
                userId = userId,
                createdAt = System.currentTimeMillis()
            )
            reviewDao.upsertHelpful(helpful)
            reviewDao.incrementHelpful(reviewId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark helpful")
        }
    }

    override suspend fun unmarkHelpful(reviewId: String, userId: String): Resource<Unit> {
        return try {
            if (!reviewDao.hasMarkedHelpful(reviewId, userId)) {
                return Resource.Success(Unit) // Not marked
            }
            
            reviewDao.removeHelpful(reviewId, userId)
            reviewDao.decrementHelpful(reviewId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unmark helpful")
        }
    }

    override suspend fun addSellerResponse(
        reviewId: String,
        sellerId: String,
        response: String
    ): Resource<Unit> {
        return try {
            val review = reviewDao.findById(reviewId) ?: return Resource.Error("Review not found")
            
            if (review.sellerId != sellerId) {
                return Resource.Error("Only the seller can respond to this review")
            }
            
            val now = System.currentTimeMillis()
            reviewDao.addSellerResponse(reviewId, response, now, now)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add response")
        }
    }

    /**
     * Recalculates and updates the rating statistics for a seller/product.
     */
    private suspend fun updateRatingStats(sellerId: String, productId: String?) {
        try {
            // Get all reviews for this seller
            val reviews = reviewDao.getSellerReviews(sellerId).first()
            
            if (reviews.isEmpty()) {
                // No reviews, clear stats
                return
            }
            
            val ratingCounts = IntArray(5) { 0 }
            var verifiedCount = 0
            var totalRating = 0
            
            for (review in reviews) {
                if (review.rating in 1..5) {
                    ratingCounts[review.rating - 1]++
                    totalRating += review.rating
                }
                if (review.isVerifiedPurchase) verifiedCount++
            }
            
            val stats = RatingStatsEntity(
                statsId = "seller-$sellerId",
                sellerId = sellerId,
                productId = null,
                averageRating = totalRating.toDouble() / reviews.size,
                totalReviews = reviews.size,
                rating5Count = ratingCounts[4],
                rating4Count = ratingCounts[3],
                rating3Count = ratingCounts[2],
                rating2Count = ratingCounts[1],
                rating1Count = ratingCounts[0],
                verifiedPurchaseCount = verifiedCount,
                lastUpdated = System.currentTimeMillis()
            )
            
            reviewDao.upsertStats(stats)
            
            // Also update product stats if productId is provided
            if (productId != null) {
                val productReviews = reviewDao.getProductReviews(productId).first()
                if (productReviews.isNotEmpty()) {
                    val productRatingCounts = IntArray(5) { 0 }
                    var productVerifiedCount = 0
                    var productTotalRating = 0
                    
                    for (review in productReviews) {
                        if (review.rating in 1..5) {
                            productRatingCounts[review.rating - 1]++
                            productTotalRating += review.rating
                        }
                        if (review.isVerifiedPurchase) productVerifiedCount++
                    }
                    
                    val productStats = RatingStatsEntity(
                        statsId = "product-$productId",
                        sellerId = null,
                        productId = productId,
                        averageRating = productTotalRating.toDouble() / productReviews.size,
                        totalReviews = productReviews.size,
                        rating5Count = productRatingCounts[4],
                        rating4Count = productRatingCounts[3],
                        rating3Count = productRatingCounts[2],
                        rating2Count = productRatingCounts[1],
                        rating1Count = productRatingCounts[0],
                        verifiedPurchaseCount = productVerifiedCount,
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    reviewDao.upsertStats(productStats)
                }
            }
        } catch (e: Exception) {
            // Log but don't fail the review operation
            timber.log.Timber.e(e, "Failed to update rating stats")
        }
    }
}
