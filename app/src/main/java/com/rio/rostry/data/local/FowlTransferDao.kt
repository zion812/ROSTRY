package com.rio.rostry.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rio.rostry.data.models.FowlTransfer

@Dao
interface FowlTransferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: FowlTransfer)
}
