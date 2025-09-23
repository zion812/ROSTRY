package com.rio.rostry.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(post: PostEntity)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun paging(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun pagingByAuthor(authorId: String): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM posts WHERE postId = :postId LIMIT 1")
    suspend fun getById(postId: String): PostEntity?

    // Ranked by engagement (likes count) then recency
    @Query(
        "SELECT * FROM posts ORDER BY (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.postId) DESC, createdAt DESC"
    )
    fun pagingRanked(): PagingSource<Int, PostEntity>
}

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDaily(entity: com.rio.rostry.data.database.entity.AnalyticsDailyEntity)

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId AND dateKey BETWEEN :from AND :to ORDER BY dateKey ASC")
    fun streamRange(userId: String, from: String, to: String): Flow<List<com.rio.rostry.data.database.entity.AnalyticsDailyEntity>>

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId ORDER BY dateKey DESC LIMIT :limit")
    fun recent(userId: String, limit: Int = 30): Flow<List<com.rio.rostry.data.database.entity.AnalyticsDailyEntity>>

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId AND dateKey BETWEEN :from AND :to ORDER BY dateKey ASC")
    suspend fun listRange(userId: String, from: String, to: String): List<com.rio.rostry.data.database.entity.AnalyticsDailyEntity>
}

@Dao
interface ReportsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: com.rio.rostry.data.database.entity.ReportEntity)

    @Query("SELECT * FROM reports WHERE userId = :userId ORDER BY createdAt DESC")
    fun streamReports(userId: String): Flow<List<com.rio.rostry.data.database.entity.ReportEntity>>
}

@Dao
interface EventRsvpsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rsvp: com.rio.rostry.data.database.entity.EventRsvpEntity)

    @Query("SELECT * FROM event_rsvps WHERE eventId = :eventId")
    fun streamForEvent(eventId: String): Flow<List<com.rio.rostry.data.database.entity.EventRsvpEntity>>

    @Query("SELECT * FROM event_rsvps WHERE userId = :userId")
    fun streamForUser(userId: String): Flow<List<com.rio.rostry.data.database.entity.EventRsvpEntity>>
}

@Dao
interface CommentsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun streamByPost(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments WHERE authorId = :userId")
    suspend fun countByUser(userId: String): Int
}

@Dao
interface LikesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(like: LikeEntity)

    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun unlike(postId: String, userId: String)

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    fun count(postId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM likes WHERE userId = :userId")
    suspend fun countByUser(userId: String): Int
}

@Dao
interface FollowsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followedId = :followedId")
    suspend fun unfollow(followerId: String, followedId: String)

    @Query("SELECT COUNT(*) FROM follows WHERE followedId = :userId")
    fun followersCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :userId")
    fun followingCount(userId: String): Flow<Int>
}

@Dao
interface GroupsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(group: GroupEntity)

    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun streamAll(): Flow<List<GroupEntity>>
}

@Dao
interface GroupMembersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(member: GroupMemberEntity)

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND userId = :userId")
    suspend fun leave(groupId: String, userId: String)

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    fun streamMembers(groupId: String): Flow<List<GroupMemberEntity>>
}

@Dao
interface EventsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: EventEntity)

    @Query("SELECT * FROM events WHERE startTime >= :now ORDER BY startTime ASC")
    fun streamUpcoming(now: Long): Flow<List<EventEntity>>
}

@Dao
interface ExpertBookingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(booking: ExpertBookingEntity)

    @Query("SELECT * FROM expert_bookings WHERE userId = :userId ORDER BY startTime DESC")
    fun streamUserBookings(userId: String): Flow<List<ExpertBookingEntity>>
}

@Dao
interface ModerationReportsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: ModerationReportEntity)

    @Query("SELECT * FROM moderation_reports WHERE status = :status ORDER BY createdAt DESC")
    fun streamByStatus(status: String): Flow<List<ModerationReportEntity>>

    @Query("UPDATE moderation_reports SET status = :status, updatedAt = :updatedAt WHERE reportId = :reportId")
    suspend fun updateStatus(reportId: String, status: String, updatedAt: Long)
}

@Dao
interface BadgesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(badge: BadgeEntity)

    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY awardedAt DESC")
    fun streamUserBadges(userId: String): Flow<List<BadgeEntity>>
}

@Dao
interface ReputationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rep: ReputationEntity)

    @Query("SELECT * FROM reputation ORDER BY score DESC LIMIT :limit")
    fun top(limit: Int = 50): Flow<List<ReputationEntity>>

    @Query("SELECT * FROM reputation WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): ReputationEntity?
}

@Dao
interface RateLimitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(limit: com.rio.rostry.data.database.entity.RateLimitEntity)

    @Query("SELECT * FROM rate_limits WHERE userId = :userId AND action = :action LIMIT 1")
    suspend fun get(userId: String, action: String): com.rio.rostry.data.database.entity.RateLimitEntity?
}
