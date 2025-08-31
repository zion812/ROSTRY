package com.rio.rostry.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.models.FowlRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface FowlRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FowlRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<FowlRecord>)

    @Query("SELECT * FROM fowl_records WHERE fowlId = :fowlId AND userId = :userId ORDER BY date DESC")
    fun getFowlRecords(fowlId: String, userId: String): Flow<List<FowlRecord>>
}
