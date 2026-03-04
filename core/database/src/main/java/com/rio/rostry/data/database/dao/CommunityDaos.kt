package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreadMetadataDao {
    @Upsert
    suspend fun upsert(metadata: ThreadMetadataEntity)

    @Query("SELECT * FROM thread_metadata WHERE threadId = :threadId LIMIT 1")
    suspend fun getByThreadId(threadId: String): ThreadMetadataEntity?

    @Query("SELECT * FROM thread_metadata WHERE participantIds LIKE '%' || :userId || '%' ORDER BY lastMessageAt DESC")
    fun streamUserThreads(userId: String): Flow<List<ThreadMetadataEntity>>

    @Query("SELECT * FROM thread_metadata WHERE contextType = :type ORDER BY lastMessageAt DESC")
    fun streamByContextType(type: String): Flow<List<ThreadMetadataEntity>>

    @Query("UPDATE thread_metadata SET lastMessageAt = :timestamp, updatedAt = :timestamp WHERE threadId = :threadId")
    suspend fun updateLastMessageTime(threadId: String, timestamp: Long)
}

@Dao
interface CommunityRecommendationDao {
    @Upsert
    suspend fun upsert(recommendation: CommunityRecommendationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(recommendations: List<CommunityRecommendationEntity>)

    @Query("SELECT * FROM community_recommendations WHERE userId = :userId AND type = :type AND dismissed = 0 AND expiresAt > :now ORDER BY score DESC LIMIT :limit")
    fun getRecommendations(userId: String, type: String, now: Long, limit: Int): Flow<List<CommunityRecommendationEntity>>

    @Query("UPDATE community_recommendations SET dismissed = 1 WHERE recommendationId = :id")
    suspend fun dismiss(id: String)

    @Query("DELETE FROM community_recommendations WHERE expiresAt < :now")
    suspend fun deleteExpired(now: Long)
}

@Dao
interface UserInterestDao {
    @Upsert
    suspend fun upsert(interest: UserInterestEntity)

    @Query("SELECT * FROM user_interests WHERE userId = :userId ORDER BY weight DESC")
    fun streamUserInterests(userId: String): Flow<List<UserInterestEntity>>

    @Query("SELECT * FROM user_interests WHERE userId = :userId AND category = :category ORDER BY weight DESC")
    fun streamByCategory(userId: String, category: String): Flow<List<UserInterestEntity>>

    @Query("UPDATE user_interests SET weight = :weight, updatedAt = :updatedAt WHERE userId = :userId AND category = :category AND value = :value")
    suspend fun updateWeight(userId: String, category: String, value: String, weight: Double, updatedAt: Long)
}

@Dao
interface ExpertProfileDao {
    @Upsert
    suspend fun upsert(profile: ExpertProfileEntity)

    @Query("SELECT * FROM expert_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): ExpertProfileEntity?

    @Query("SELECT * FROM expert_profiles WHERE availableForBooking = 1 ORDER BY rating DESC")
    fun streamAvailableExperts(): Flow<List<ExpertProfileEntity>>

    @Query("SELECT * FROM expert_profiles WHERE specialties LIKE '%' || :specialty || '%' ORDER BY rating DESC")
    fun streamBySpecialty(specialty: String): Flow<List<ExpertProfileEntity>>

    @Query("UPDATE expert_profiles SET rating = :rating, totalConsultations = :total, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateStats(userId: String, rating: Double, total: Int, updatedAt: Long)
}
