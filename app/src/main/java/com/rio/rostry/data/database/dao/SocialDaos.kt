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
}

@Dao
interface CommentsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun streamByPost(postId: String): Flow<List<CommentEntity>>
}

@Dao
interface LikesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(like: LikeEntity)

    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun unlike(postId: String, userId: String)

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    fun count(postId: String): Flow<Int>
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
}
