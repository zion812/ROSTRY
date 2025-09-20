package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Transfer
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers")
    fun getAllTransfers(): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE id = :id")
    suspend fun getTransferById(id: String): Transfer?

    @Query("SELECT * FROM transfers WHERE fromUserId = :userId OR toUserId = :userId")
    fun getTransfersByUserId(userId: String): Flow<List<Transfer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: Transfer)

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Delete
    suspend fun deleteTransfer(transfer: Transfer)
}