package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "posts", indices = [Index("authorId"), Index("createdAt"), Index("type")])
data class PostEntity(
    @PrimaryKey val postId: String,
    val authorId: String,
    val type: String, // TEXT, IMAGE, VIDEO, PRODUCT_SHOWCASE, BREEDING_ACHIEVEMENT, DISCUSSION
    val text: String?,
    val mediaUrl: String?,
    val thumbnailUrl: String?,
    val productId: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "comments", indices = [Index("postId"), Index("authorId"), Index("createdAt")])
data class CommentEntity(
    @PrimaryKey val commentId: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val createdAt: Long,
)

@Entity(tableName = "likes", indices = [Index(value=["postId","userId"], unique = true)])
data class LikeEntity(
    @PrimaryKey val likeId: String,
    val postId: String,
    val userId: String,
    val createdAt: Long,
)

@Entity(tableName = "follows", indices = [Index(value=["followerId","followedId"], unique = true)])
data class FollowEntity(
    @PrimaryKey val followId: String,
    val followerId: String,
    val followedId: String,
    val createdAt: Long,
)

@Entity(tableName = "groups", indices = [Index("ownerId"), Index("name")])
data class GroupEntity(
    @PrimaryKey val groupId: String,
    val name: String,
    val description: String?,
    val ownerId: String,
    val category: String?, // breed, region, etc
    val createdAt: Long,
)

@Entity(tableName = "group_members", indices = [Index(value=["groupId","userId"], unique = true)])
data class GroupMemberEntity(
    @PrimaryKey val membershipId: String,
    val groupId: String,
    val userId: String,
    val role: String, // MEMBER, ADMIN, MOD
    val joinedAt: Long,
)

@Entity(tableName = "events", indices = [Index("groupId"), Index("startTime")])
data class EventEntity(
    @PrimaryKey val eventId: String,
    val groupId: String?,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Long,
    val endTime: Long?,
)

@Entity(tableName = "expert_bookings", indices = [Index("expertId"), Index("userId"), Index("startTime")])
data class ExpertBookingEntity(
    @PrimaryKey val bookingId: String,
    val expertId: String,
    val userId: String,
    val topic: String?,
    val startTime: Long,
    val endTime: Long,
    val status: String, // REQUESTED, CONFIRMED, COMPLETED, CANCELLED
)

@Entity(tableName = "moderation_reports", indices = [Index("targetType"), Index("targetId"), Index("status")])
data class ModerationReportEntity(
    @PrimaryKey val reportId: String,
    val targetType: String, // POST, COMMENT, USER
    val targetId: String,
    val reporterId: String,
    val reason: String,
    val status: String, // OPEN, UNDER_REVIEW, RESOLVED, REJECTED
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "badges", indices = [Index("userId"), Index("awardedAt")])
data class BadgeEntity(
    @PrimaryKey val badgeId: String,
    val userId: String,
    val name: String,
    val description: String?,
    val awardedAt: Long,
)

@Entity(tableName = "reputation", indices = [Index(value=["userId"], unique = true)])
data class ReputationEntity(
    @PrimaryKey val repId: String,
    val userId: String,
    val score: Int,
    val updatedAt: Long,
)
