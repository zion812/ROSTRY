package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.rio.rostry.data.database.entity.*

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<AchievementEntity>)

    @Query("SELECT * FROM achievements_def")
    fun all(): Flow<List<AchievementEntity>>
}

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun forUser(userId: String): Flow<List<UserProgressEntity>>
}

@Dao
interface BadgeDefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<com.rio.rostry.data.database.entity.GamificationBadgeEntity>)

    @Query("SELECT * FROM badges_def")
    fun all(): Flow<List<com.rio.rostry.data.database.entity.GamificationBadgeEntity>>
}

@Dao
interface LeaderboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<LeaderboardEntity>)

    @Query("SELECT * FROM leaderboard WHERE periodKey = :periodKey ORDER BY rank ASC")
    fun leaderboard(periodKey: String): Flow<List<LeaderboardEntity>>
}

@Dao
interface RewardDefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<RewardEntity>)

    @Query("SELECT * FROM rewards_def")
    fun all(): Flow<List<RewardEntity>>
}
