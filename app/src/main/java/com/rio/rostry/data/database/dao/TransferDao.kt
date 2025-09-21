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
}
