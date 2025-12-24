package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Review entity for product and seller reviews.
 * Supports both product reviews and seller ratings.
 */
@Entity(
    tableName = "reviews",
    indices = [
        Index("productId"),
        Index("sellerId"),
        Index("reviewerId"),
        Index("createdAt")
    ]
)
data class ReviewEntity(
    @PrimaryKey val reviewId: String,
    val productId: String?,          // Product being reviewed (optional)
    val sellerId: String,            // Seller being reviewed (required)
    val orderId: String?,            // Order this review is for (optional)
    val reviewerId: String,          // User who wrote the review
    val rating: Int,                 // 1-5 stars
    val title: String?,              // Review title (optional)
    val content: String?,            // Review text (optional)
    val isVerifiedPurchase: Boolean, // True if reviewer bought from seller
    val helpfulCount: Int = 0,       // Number of users who found this helpful
    val responseFromSeller: String?, // Seller's response (optional)
    val responseAt: Long?,           // When seller responded
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val dirty: Boolean = true        // For sync
)

/**
 * Entity to track which users found a review helpful.
 */
@Entity(
    tableName = "review_helpful",
    primaryKeys = ["reviewId", "userId"],
    indices = [Index("reviewId")]
)
data class ReviewHelpfulEntity(
    val reviewId: String,
    val userId: String,
    val createdAt: Long
)

/**
 * Aggregated rating statistics for a seller or product.
 */
@Entity(
    tableName = "rating_stats",
    indices = [Index("sellerId"), Index("productId")]
)
data class RatingStatsEntity(
    @PrimaryKey val statsId: String,
    val sellerId: String?,
    val productId: String?,
    val averageRating: Double,       // Average 1-5 rating
    val totalReviews: Int,           // Total number of reviews
    val rating5Count: Int,           // Number of 5-star reviews
    val rating4Count: Int,           // Number of 4-star reviews
    val rating3Count: Int,           // Number of 3-star reviews
    val rating2Count: Int,           // Number of 2-star reviews
    val rating1Count: Int,           // Number of 1-star reviews
    val verifiedPurchaseCount: Int,  // Reviews from verified purchases
    val lastUpdated: Long
)
