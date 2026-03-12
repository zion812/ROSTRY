package com.rio.rostry.ui.gamification
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

data class AchievementItem(
    val achievementId: String,
    val name: String,
    val description: String?,
    val points: Int,
    val category: String?
)

data class UserProgressItem(
    val achievementId: String,
    val progress: Int,
    val target: Int,
    val unlockedAt: Long?
)

data class AchievementWithProgress(
    val achievement: AchievementItem,
    val progress: UserProgressItem?
) {
    val isUnlocked: Boolean get() = progress?.unlockedAt != null
    val progressPercent: Float get() = progress?.let {
        if (it.target > 0) it.progress.toFloat() / it.target else 0f
    } ?: 0f
    val pointsEarned: Int get() = if (isUnlocked) achievement.points else 0
}
