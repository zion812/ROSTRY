package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.ErrorLogEntity

/**
 * DAO for error log persistence.
 */
@Dao
interface ErrorLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(errorLog: ErrorLogEntity)

    @Query("SELECT * FROM error_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 100): List<ErrorLogEntity>

    @Query("SELECT * FROM error_logs WHERE error_category = :category ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByCategory(category: String, limit: Int = 50): List<ErrorLogEntity>

    @Query("SELECT * FROM error_logs WHERE timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getSince(since: Long): List<ErrorLogEntity>

    @Query("DELETE FROM error_logs WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT COUNT(*) FROM error_logs")
    suspend fun count(): Int
}
