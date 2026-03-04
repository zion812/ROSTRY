package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.RatingStatsEntity
import com.rio.rostry.data.database.entity.ReviewEntity
import com.rio.rostry.data.database.entity.ReviewHelpfulEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    
    // ========== Insert/Update ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(review: ReviewEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHelpful(helpful: ReviewHelpfulEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: RatingStatsEntity)
    
    // ========== Queries ==========
    
    @Query("SELECT * FROM reviews WHERE reviewId = :reviewId AND isDeleted = 0")
    suspend fun findById(reviewId: String): ReviewEntity?
    
    @Query("SELECT * FROM reviews WHERE productId = :productId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getProductReviews(productId: String): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE sellerId = :sellerId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getSellerReviews(sellerId: String): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE reviewerId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getUserReviews(userId: String): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE orderId = :orderId AND isDeleted = 0 LIMIT 1")
    suspend fun getReviewByOrder(orderId: String): ReviewEntity?
    
    // ========== Statistics ==========
    
    @Query("SELECT COUNT(*) FROM reviews WHERE productId = :productId AND isDeleted = 0")
    fun countProductReviews(productId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM reviews WHERE sellerId = :sellerId AND isDeleted = 0")
    fun countSellerReviews(sellerId: String): Flow<Int>
    
    @Query("SELECT AVG(rating) FROM reviews WHERE productId = :productId AND isDeleted = 0")
    fun averageProductRating(productId: String): Flow<Double?>
    
    @Query("SELECT AVG(rating) FROM reviews WHERE sellerId = :sellerId AND isDeleted = 0")
    fun averageSellerRating(sellerId: String): Flow<Double?>
    
    @Query("SELECT * FROM rating_stats WHERE sellerId = :sellerId LIMIT 1")
    fun getSellerStats(sellerId: String): Flow<RatingStatsEntity?>
    
    @Query("SELECT * FROM rating_stats WHERE productId = :productId LIMIT 1")
    fun getProductStats(productId: String): Flow<RatingStatsEntity?>
    
    // ========== Helpful Votes ==========
    
    @Query("SELECT EXISTS(SELECT 1 FROM review_helpful WHERE reviewId = :reviewId AND userId = :userId)")
    suspend fun hasMarkedHelpful(reviewId: String, userId: String): Boolean
    
    @Query("DELETE FROM review_helpful WHERE reviewId = :reviewId AND userId = :userId")
    suspend fun removeHelpful(reviewId: String, userId: String)
    
    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE reviewId = :reviewId")
    suspend fun incrementHelpful(reviewId: String)
    
    @Query("UPDATE reviews SET helpfulCount = helpfulCount - 1 WHERE reviewId = :reviewId AND helpfulCount > 0")
    suspend fun decrementHelpful(reviewId: String)
    
    // ========== Seller Response ==========
    
    @Query("UPDATE reviews SET responseFromSeller = :response, responseAt = :responseAt, updatedAt = :updatedAt WHERE reviewId = :reviewId")
    suspend fun addSellerResponse(reviewId: String, response: String, responseAt: Long, updatedAt: Long)
    
    // ========== Deletion ==========
    
    @Query("UPDATE reviews SET isDeleted = 1, updatedAt = :deletedAt, dirty = 1 WHERE reviewId = :reviewId")
    suspend fun softDelete(reviewId: String, deletedAt: Long)
    
    // ========== Sync ==========
    
    @Query("SELECT * FROM reviews WHERE dirty = 1")
    suspend fun getDirtyReviews(): List<ReviewEntity>
    
    @Query("UPDATE reviews SET dirty = 0 WHERE reviewId = :reviewId")
    suspend fun markClean(reviewId: String)

    @Query("SELECT * FROM reviews WHERE adminFlagged = 1 ORDER BY createdAt DESC")
    fun getFlaggedReviews(): Flow<List<ReviewEntity>>
}
