package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements_def")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val name: String,
    val description: String?,
    val points: Int,
    val category: String?,
    val icon: String?
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val achievementId: String,
    val progress: Int,
    val target: Int,
    val unlockedAt: Long?,
    val updatedAt: Long
)

@Entity(tableName = "badges_def")
data class GamificationBadgeEntity(
    @PrimaryKey val badgeId: String,
    val name: String,
    val description: String?,
    val icon: String?
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey val id: String,
    val periodKey: String,
    val userId: String,
    val score: Long,
    val rank: Int
)

@Entity(tableName = "rewards_def")
data class RewardEntity(
    @PrimaryKey val rewardId: String,
    val name: String,
    val description: String?,
    val pointsRequired: Int
)
