package com.rio.rostry.data.commerce.repository

import com.rio.rostry.data.database.dao.ReviewDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.entity.RatingStatsEntity
import com.rio.rostry.data.database.entity.ReviewEntity
import com.rio.rostry.data.database.entity.ReviewHelpfulEntity
import com.rio.rostry.domain.commerce.repository.ReviewRepository
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val orderDao: OrderDao
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
    ): Result<ReviewEntity> {
        return try {
            require(rating in 1..5) { "Rating must be between 1 and 5" }
            require(reviewerId != sellerId) { "You cannot review yourself" }
            if (orderId != null) {
                val existing = reviewDao.getReviewByOrder(orderId)
                if (existing != null) return Result.Error(Exception("You have already reviewed this order"))
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
            Result.Success(review)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateReview(reviewId: String, rating: Int, title: String?, content: String?): Result<Unit> {
        return try {
            require(rating in 1..5) { "Rating must be between 1 and 5" }
            val existing = reviewDao.findById(reviewId) ?: return Result.Error(Exception("Review not found"))
            val updated = existing.copy(rating = rating, title = title, content = content, updatedAt = System.currentTimeMillis(), dirty = true)
            reviewDao.upsert(updated)
            updateRatingStats(existing.sellerId, existing.productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            val existing = reviewDao.findById(reviewId) ?: return Result.Error(Exception("Review not found"))
            reviewDao.softDelete(reviewId, System.currentTimeMillis())
            updateRatingStats(existing.sellerId, existing.productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getProductReviews(productId: String): Flow<List<ReviewEntity>> = reviewDao.getProductReviews(productId)
    override fun getSellerReviews(sellerId: String): Flow<List<ReviewEntity>> = reviewDao.getSellerReviews(sellerId)
    override fun getUserReviews(userId: String): Flow<List<ReviewEntity>> = reviewDao.getUserReviews(userId)
    override suspend fun getReviewByOrder(orderId: String): ReviewEntity? = reviewDao.getReviewByOrder(orderId)

    override suspend fun canUserReview(userId: String, orderId: String): Boolean {
        val order = orderDao.findById(orderId) ?: return false
        return order.buyerId == userId && order.status == "DELIVERED" && reviewDao.getReviewByOrder(orderId) == null
    }

    override fun getSellerStats(sellerId: String): Flow<RatingStatsEntity?> = reviewDao.getSellerStats(sellerId)
    override fun getProductStats(productId: String): Flow<RatingStatsEntity?> = reviewDao.getProductStats(productId)
    override fun countProductReviews(productId: String): Flow<Int> = reviewDao.countProductReviews(productId)
    override fun countSellerReviews(sellerId: String): Flow<Int> = reviewDao.countSellerReviews(sellerId)
    override fun averageProductRating(productId: String): Flow<Double?> = reviewDao.averageProductRating(productId)
    override fun averageSellerRating(sellerId: String): Flow<Double?> = reviewDao.averageSellerRating(sellerId)

    override suspend fun markHelpful(reviewId: String, userId: String): Result<Unit> {
        return try {
            val review = reviewDao.findById(reviewId) ?: return Result.Error(Exception("Review not found"))
            if (review.reviewerId == userId) return Result.Error(Exception("You cannot mark your own review as helpful"))
            if (reviewDao.hasMarkedHelpful(reviewId, userId)) return Result.Success(Unit)
            reviewDao.upsertHelpful(ReviewHelpfulEntity(reviewId = reviewId, userId = userId, createdAt = System.currentTimeMillis()))
            reviewDao.incrementHelpful(reviewId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unmarkHelpful(reviewId: String, userId: String): Result<Unit> {
        return try {
            if (!reviewDao.hasMarkedHelpful(reviewId, userId)) return Result.Success(Unit)
            reviewDao.removeHelpful(reviewId, userId)
            reviewDao.decrementHelpful(reviewId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addSellerResponse(reviewId: String, sellerId: String, response: String): Result<Unit> {
        return try {
            val review = reviewDao.findById(reviewId) ?: return Result.Error(Exception("Review not found"))
            if (review.sellerId != sellerId) return Result.Error(Exception("Only the seller can respond to this review"))
            val now = System.currentTimeMillis()
            reviewDao.addSellerResponse(reviewId, response, now, now)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun updateRatingStats(sellerId: String, productId: String?) {
        try {
            val reviews = reviewDao.getSellerReviews(sellerId).first()
            if (reviews.isEmpty()) return
            val ratingCounts = IntArray(5) { 0 }
            var verifiedCount = 0
            var totalRating = 0
            for (review in reviews) {
                if (review.rating in 1..5) { ratingCounts[review.rating - 1]++; totalRating += review.rating }
                if (review.isVerifiedPurchase) verifiedCount++
            }
            reviewDao.upsertStats(RatingStatsEntity(
                statsId = "seller-$sellerId", sellerId = sellerId, productId = null,
                averageRating = totalRating.toDouble() / reviews.size, totalReviews = reviews.size,
                rating5Count = ratingCounts[4], rating4Count = ratingCounts[3], rating3Count = ratingCounts[2],
                rating2Count = ratingCounts[1], rating1Count = ratingCounts[0],
                verifiedPurchaseCount = verifiedCount, lastUpdated = System.currentTimeMillis()
            ))
            if (productId != null) {
                val productReviews = reviewDao.getProductReviews(productId).first()
                if (productReviews.isNotEmpty()) {
                    val pRatingCounts = IntArray(5) { 0 }
                    var pVerified = 0; var pTotal = 0
                    for (r in productReviews) {
                        if (r.rating in 1..5) { pRatingCounts[r.rating - 1]++; pTotal += r.rating }
                        if (r.isVerifiedPurchase) pVerified++
                    }
                    reviewDao.upsertStats(RatingStatsEntity(
                        statsId = "product-$productId", sellerId = null, productId = productId,
                        averageRating = pTotal.toDouble() / productReviews.size, totalReviews = productReviews.size,
                        rating5Count = pRatingCounts[4], rating4Count = pRatingCounts[3], rating3Count = pRatingCounts[2],
                        rating2Count = pRatingCounts[1], rating1Count = pRatingCounts[0],
                        verifiedPurchaseCount = pVerified, lastUpdated = System.currentTimeMillis()
                    ))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update rating stats")
        }
    }
}
