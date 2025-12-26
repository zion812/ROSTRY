package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.DailyBirdLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyBirdLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyBirdLogEntity)

    @Query("SELECT * FROM daily_bird_logs WHERE birdId = :birdId ORDER BY date DESC")
    fun getLogsForBird(birdId: String): Flow<List<DailyBirdLogEntity>>

    @Query("SELECT * FROM daily_bird_logs WHERE birdId = :birdId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsForBirdInRange(birdId: String, startDate: Long, endDate: Long): Flow<List<DailyBirdLogEntity>>

    @Query("DELETE FROM daily_bird_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)
}
