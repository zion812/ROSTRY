package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.TransferRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferRecordDao {
    @Query("SELECT * FROM transfer_records WHERE poultryId = :poultryId ORDER BY transferDate DESC")
    fun getTransferRecordsByPoultryId(poultryId: String): Flow<List<TransferRecord>>

    @Query("SELECT * FROM transfer_records WHERE id = :id")
    suspend fun getTransferRecordById(id: String): TransferRecord?

    @Query("SELECT * FROM transfer_records WHERE qrCode = :qrCode")
    suspend fun getTransferRecordByQRCode(qrCode: String): TransferRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransferRecord(transferRecord: TransferRecord)

    @Update
    suspend fun updateTransferRecord(transferRecord: TransferRecord)

    @Delete
    suspend fun deleteTransferRecord(transferRecord: TransferRecord)
}