package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.AnalyticsDailyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AnalyticsDailyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDaily(entity: AnalyticsDailyEntity)

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId AND dateKey BETWEEN :from AND :to ORDER BY dateKey ASC")
    fun streamRange(userId: String, from: String, to: String): Flow<List<AnalyticsDailyEntity>>

    @Query("SELECT * FROM analytics_daily WHERE dateKey BETWEEN :from AND :to ORDER BY dateKey ASC")
    fun streamAllFarmersRange(from: String, to: String): Flow<List<AnalyticsDailyEntity>>

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId ORDER BY dateKey DESC LIMIT :limit")
    fun recent(userId: String, limit: Int = 30): Flow<List<AnalyticsDailyEntity>>

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId AND dateKey BETWEEN :from AND :to ORDER BY dateKey ASC")
    suspend fun listRange(userId: String, from: String, to: String): List<AnalyticsDailyEntity>
}
