package com.rio.rostry.ui.gamification

import com.rio.rostry.data.database.entity.AchievementEntity
import com.rio.rostry.data.database.entity.UserProgressEntity

data class AchievementWithProgress(
    val achievement: AchievementEntity,
    val progress: UserProgressEntity?
) {
    val isUnlocked: Boolean get() = progress?.unlockedAt != null
    val progressPercent: Float get() = progress?.let {
        if (it.target > 0) it.progress.toFloat() / it.target else 0f
    } ?: 0f
    val pointsEarned: Int get() = if (isUnlocked) achievement.points else 0
}
