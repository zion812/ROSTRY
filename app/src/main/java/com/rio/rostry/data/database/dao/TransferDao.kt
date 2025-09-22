package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.TransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transfer: TransferEntity)

    @Update
    suspend fun updateTransfer(transfer: TransferEntity)

    @Query("SELECT * FROM transfers WHERE transferId = :transferId")
    fun getTransferById(transferId: String): Flow<TransferEntity?>

    @Query("SELECT * FROM transfers WHERE fromUserId = :userId ORDER BY initiatedAt DESC")
    fun getTransfersFromUser(userId: String): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE toUserId = :userId ORDER BY initiatedAt DESC")
    fun getTransfersToUser(userId: String): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE orderId = :orderId ORDER BY initiatedAt DESC")
    fun getTransfersByOrderId(orderId: String): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE type = :type ORDER BY initiatedAt DESC")
    fun getTransfersByType(type: String): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE status = :status ORDER BY initiatedAt DESC")
    fun getTransfersByStatus(status: String): Flow<List<TransferEntity>>

    @Query("DELETE FROM transfers WHERE transferId = :transferId")
    suspend fun deleteTransferById(transferId: String)

    @Query("DELETE FROM transfers")
    suspend fun deleteAllTransfers()

    // Incremental sync helpers
    @Query("SELECT * FROM transfers WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<TransferEntity>

    @Query("DELETE FROM transfers WHERE isDeleted = 1")
    suspend fun purgeDeleted()

    // Traceability helper
    @Query("SELECT * FROM transfers WHERE productId = :productId ORDER BY initiatedAt ASC")
    suspend fun getTransfersByProduct(productId: String): List<TransferEntity>

    // Workflow helpers
    @Query("SELECT * FROM transfers WHERE transferId = :transferId LIMIT 1")
    suspend fun getById(transferId: String): TransferEntity?

    @Query("SELECT * FROM transfers WHERE productId = :productId AND status = :status ORDER BY initiatedAt DESC")
    suspend fun getByProductAndStatus(productId: String, status: String): List<TransferEntity>

    @Query("UPDATE transfers SET status = :status, updatedAt = :updatedAt, completedAt = :completedAt WHERE transferId = :transferId")
    suspend fun updateStatusAndTimestamps(transferId: String, status: String, updatedAt: Long, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM transfers WHERE productId = :productId AND fromUserId = :fromUserId AND toUserId = :toUserId AND status = 'PENDING' AND initiatedAt > :since")
    suspend fun countRecentPending(productId: String, fromUserId: String, toUserId: String, since: Long): Int

    @Query("SELECT * FROM transfers WHERE status = 'PENDING' AND timeoutAt IS NOT NULL AND timeoutAt < :now ORDER BY timeoutAt ASC")
    suspend fun getPendingTimedOut(now: Long): List<TransferEntity>

    @Query("UPDATE transfers SET status = 'TIMEOUT', updatedAt = :updatedAt WHERE transferId IN (:ids)")
    suspend fun markTimedOut(ids: List<String>, updatedAt: Long)
}
