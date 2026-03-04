package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.TransferAnalyticsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for transfer analytics operations.
 */
@Dao
interface TransferAnalyticsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analytics: TransferAnalyticsEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(analytics: List<TransferAnalyticsEntity>)
    
    @Query("SELECT * FROM transfer_analytics WHERE id = :id")
    suspend fun getById(id: String): TransferAnalyticsEntity?
    
    @Query("SELECT * FROM transfer_analytics WHERE transferId = :transferId")
    suspend fun getByTransferId(transferId: String): TransferAnalyticsEntity?
    
    /**
     * Get all transfers by sender within a time period.
     */
    @Query("""
        SELECT * FROM transfer_analytics 
        WHERE senderId = :userId 
        AND initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
        ORDER BY initiatedAt DESC
    """)
    suspend fun getBySender(userId: String, startTime: Long, endTime: Long): List<TransferAnalyticsEntity>
    
    /**
     * Get all transfers by recipient within a time period.
     */
    @Query("""
        SELECT * FROM transfer_analytics 
        WHERE recipientId = :userId 
        AND initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
        ORDER BY initiatedAt DESC
    """)
    suspend fun getByRecipient(userId: String, startTime: Long, endTime: Long): List<TransferAnalyticsEntity>
    
    /**
     * Get all transfers for a user (as sender or recipient) within a time period.
     */
    @Query("""
        SELECT * FROM transfer_analytics 
        WHERE (senderId = :userId OR recipientId = :userId)
        AND initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
        ORDER BY initiatedAt DESC
    """)
    suspend fun getByUser(userId: String, startTime: Long, endTime: Long): List<TransferAnalyticsEntity>
    
    /**
     * Get all transfers for a product.
     */
    @Query("""
        SELECT * FROM transfer_analytics 
        WHERE productId = :productId
        ORDER BY initiatedAt DESC
    """)
    suspend fun getByProduct(productId: String): List<TransferAnalyticsEntity>
    
    /**
     * Calculate average transfer completion time for a user.
     */
    @Query("""
        SELECT AVG(durationSeconds) FROM transfer_analytics 
        WHERE (senderId = :userId OR recipientId = :userId)
        AND completedAt IS NOT NULL
        AND initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
    """)
    suspend fun getAverageCompletionTime(userId: String, startTime: Long, endTime: Long): Double?
    
    /**
     * Get most frequently transferred products.
     */
    @Query("""
        SELECT productId, COUNT(*) as transferCount 
        FROM transfer_analytics 
        WHERE initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
        GROUP BY productId 
        ORDER BY transferCount DESC 
        LIMIT :limit
    """)
    suspend fun getMostTransferredProducts(startTime: Long, endTime: Long, limit: Int = 10): List<ProductTransferCount>
    
    /**
     * Get users with highest transfer activity.
     */
    @Query("""
        SELECT senderId as userId, COUNT(*) as transferCount 
        FROM transfer_analytics 
        WHERE initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
        GROUP BY senderId 
        ORDER BY transferCount DESC 
        LIMIT :limit
    """)
    suspend fun getMostActiveTransferUsers(startTime: Long, endTime: Long, limit: Int = 10): List<UserTransferCount>
    
    /**
     * Get transfer count by user within a time period.
     */
    @Query("""
        SELECT COUNT(*) FROM transfer_analytics 
        WHERE (senderId = :userId OR recipientId = :userId)
        AND initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
    """)
    suspend fun getTransferCount(userId: String, startTime: Long, endTime: Long): Int
    
    /**
     * Get transfers with conflicts.
     */
    @Query("""
        SELECT * FROM transfer_analytics 
        WHERE hadConflicts = 1
        AND initiatedAt >= :startTime 
        AND initiatedAt <= :endTime
        ORDER BY initiatedAt DESC
    """)
    suspend fun getTransfersWithConflicts(startTime: Long, endTime: Long): List<TransferAnalyticsEntity>
    
    /**
     * Observe transfer analytics for a user.
     */
    @Query("""
        SELECT * FROM transfer_analytics 
        WHERE (senderId = :userId OR recipientId = :userId)
        ORDER BY initiatedAt DESC
        LIMIT :limit
    """)
    fun observeUserTransfers(userId: String, limit: Int = 50): Flow<List<TransferAnalyticsEntity>>
    
    @Query("DELETE FROM transfer_analytics WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM transfer_analytics")
    suspend fun deleteAll()
}

/**
 * Result class for product transfer count queries.
 */
data class ProductTransferCount(
    val productId: String,
    val transferCount: Int
)

/**
 * Result class for user transfer count queries.
 */
data class UserTransferCount(
    val userId: String,
    val transferCount: Int
)
