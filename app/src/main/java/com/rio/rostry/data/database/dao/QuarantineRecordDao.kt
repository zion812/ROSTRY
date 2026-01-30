package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuarantineRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: QuarantineRecordEntity)

    @Update
    suspend fun update(record: QuarantineRecordEntity)

    @Query("SELECT * FROM quarantine_records WHERE productId = :productId ORDER BY startedAt DESC")
    fun observeForProduct(productId: String): Flow<List<QuarantineRecordEntity>>

    @Query("SELECT * FROM quarantine_records WHERE status = :status ORDER BY startedAt DESC")
    fun observeByStatus(status: String): Flow<List<QuarantineRecordEntity>>

    @Query("SELECT * FROM quarantine_records WHERE farmerId = :farmerId")
    fun getRecordsForFarmer(farmerId: String): Flow<List<QuarantineRecordEntity>>

    @Query("SELECT * FROM quarantine_records WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    fun observeActiveForFarmer(farmerId: String): Flow<List<QuarantineRecordEntity>>

    @Query("SELECT * FROM quarantine_records WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun getAllActiveForFarmer(farmerId: String): List<QuarantineRecordEntity>

    @Query("SELECT COUNT(*) FROM quarantine_records WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun countActiveForFarmer(farmerId: String): Int

    @Query("SELECT * FROM quarantine_records WHERE farmerId = :farmerId AND updatedAt < :threshold")
    suspend fun getUpdatesOverdueForFarmer(farmerId: String, threshold: Long): List<QuarantineRecordEntity>

    @Query("SELECT * FROM quarantine_records WHERE farmerId = :farmerId AND updatedAt < :threshold")
    fun observeUpdatesOverdueForFarmer(farmerId: String, threshold: Long): Flow<List<QuarantineRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: QuarantineRecordEntity)

    @Query("SELECT * FROM quarantine_records WHERE dirty = 1")
    suspend fun getDirty(): List<QuarantineRecordEntity>

    @Query("UPDATE quarantine_records SET dirty = 0, syncedAt = :syncedAt WHERE quarantineId IN (:ids)")
    suspend fun clearDirty(ids: List<String>, syncedAt: Long)
}
