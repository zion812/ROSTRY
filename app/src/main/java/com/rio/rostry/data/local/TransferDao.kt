package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Transfer
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers WHERE isDeleted = 0")
    fun getAllTransfers(): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE id = :id AND isDeleted = 0")
    suspend fun getTransferById(id: String): Transfer?

    @Query("SELECT * FROM transfers WHERE (fromUserId = :userId OR toUserId = :userId) AND isDeleted = 0")
    fun getTransfersByUserId(userId: String): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE status = :status AND isDeleted = 0")
    fun getTransfersByStatus(status: String): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE type = :type AND isDeleted = 0")
    fun getTransfersByType(type: String): Flow<List<Transfer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: Transfer)

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Query("UPDATE transfers SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteTransfer(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM transfers WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    suspend fun purgeDeletedTransfers(beforeTimestamp: Long)
}