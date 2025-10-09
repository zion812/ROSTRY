package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: DailyLogEntity)

    @Query("SELECT * FROM daily_logs WHERE productId = :productId ORDER BY logDate DESC")
    fun observeForProduct(productId: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end ORDER BY logDate DESC")
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE farmerId = :farmerId AND logDate = :date")
    suspend fun getByFarmerAndDate(farmerId: String, date: Long): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE productId = :productId AND logDate = :date LIMIT 1")
    suspend fun getByProductAndDate(productId: String, date: Long): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE dirty = 1")
    suspend fun getDirty(): List<DailyLogEntity>

    @Query("UPDATE daily_logs SET dirty = 0, syncedAt = :syncedAt WHERE logId IN (:logIds)")
    suspend fun clearDirty(logIds: List<String>, syncedAt: Long)

    @Query("SELECT COUNT(*) FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end")
    fun observeCountForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<Int>

    @Query("DELETE FROM daily_logs WHERE logId = :logId")
    suspend fun delete(logId: String)

    // Cursor-based incremental sync
    @Query("SELECT * FROM daily_logs WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int): List<DailyLogEntity>
}
