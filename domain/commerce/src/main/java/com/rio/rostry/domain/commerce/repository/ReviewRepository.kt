package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.data.database.entity.RatingStatsEntity
import com.rio.rostry.data.database.entity.ReviewEntity
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for review and rating operations.
 * Migrated from app module as part of Phase 1 repository migration.
 */
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
    ): Result<ReviewEntity>

    suspend fun updateReview(reviewId: String, rating: Int, title: String?, content: String?): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>

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
    suspend fun markHelpful(reviewId: String, userId: String): Result<Unit>
    suspend fun unmarkHelpful(reviewId: String, userId: String): Result<Unit>

    // Seller response
    suspend fun addSellerResponse(reviewId: String, sellerId: String, response: String): Result<Unit>
}
