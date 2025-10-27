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

    // Optimized filter helpers (optional params via CASE conditions)
    @Query(
        "SELECT * FROM transfers " +
        "WHERE (fromUserId = :userId OR toUserId = :userId) " +
        "AND (:start IS NULL OR initiatedAt >= :start) " +
        "AND (:end IS NULL OR initiatedAt <= :end) " +
        "ORDER BY initiatedAt DESC"
    )
    fun getUserTransfersBetween(userId: String, start: Long?, end: Long?): Flow<List<TransferEntity>>

    @Query(
        "SELECT * FROM transfers " +
        "WHERE (fromUserId = :userId OR toUserId = :userId) " +
        "AND type = :type " +
        "AND (:start IS NULL OR initiatedAt >= :start) " +
        "AND (:end IS NULL OR initiatedAt <= :end) " +
        "ORDER BY initiatedAt DESC"
    )
    fun getUserTransfersByTypeBetween(userId: String, type: String, start: Long?, end: Long?): Flow<List<TransferEntity>>

    @Query(
        "SELECT * FROM transfers " +
        "WHERE (fromUserId = :userId OR toUserId = :userId) " +
        "AND status = :status " +
        "AND (:start IS NULL OR initiatedAt >= :start) " +
        "AND (:end IS NULL OR initiatedAt <= :end) " +
        "ORDER BY initiatedAt DESC"
    )
    fun getUserTransfersByStatusBetween(userId: String, status: String, start: Long?, end: Long?): Flow<List<TransferEntity>>

    // Paging: history excludes PENDING
    @androidx.room.Query(
        "SELECT * FROM transfers " +
        "WHERE (fromUserId = :userId OR toUserId = :userId) " +
        "AND status != 'PENDING' " +
        "AND (:type IS NULL OR type = :type) " +
        "AND (:status IS NULL OR status = :status) " +
        "AND (:start IS NULL OR initiatedAt >= :start) " +
        "AND (:end IS NULL OR initiatedAt <= :end) " +
        "ORDER BY initiatedAt DESC"
    )
    fun pagingHistory(
        userId: String,
        type: String?,
        status: String?,
        start: Long?,
        end: Long?
    ): androidx.paging.PagingSource<Int, TransferEntity>

    @Query("SELECT * FROM transfers WHERE dirty = 1 ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getDirty(limit: Int = 500): List<TransferEntity>

    @Query("UPDATE transfers SET dirty = 0, syncedAt = :syncedAt WHERE transferId IN (:transferIds)")
    suspend fun clearDirty(transferIds: List<String>, syncedAt: Long)

    @Query("SELECT * FROM transfers WHERE (fromUserId = :userId OR toUserId = :userId) AND dirty = 1")
    fun observeDirtyByUser(userId: String): Flow<List<TransferEntity>>

    @Query("SELECT COUNT(*) FROM transfers WHERE (fromUserId = :userId OR toUserId = :userId) AND status = 'PENDING'")
    fun observePendingCountForFarmer(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM transfers WHERE toUserId = :userId AND status = 'PENDING'")
    fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int>

    @Query("SELECT * FROM transfers WHERE (fromUserId = :userId OR toUserId = :userId) AND initiatedAt >= :since ORDER BY initiatedAt DESC")
    fun observeRecentTransfersForFarmer(userId: String, since: Long): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE (fromUserId = :userId OR toUserId = :userId) AND status = :status ORDER BY initiatedAt DESC")
    fun observeTransfersByStatusForFarmer(userId: String, status: String): Flow<List<TransferEntity>>
}