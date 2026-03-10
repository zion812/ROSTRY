package com.rio.rostry.ui.gamification

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AchievementsViewModel : ViewModel() {

    private val _achievementsWithProgress = MutableStateFlow(
        listOf(
            AchievementWithProgress(
                achievement = AchievementItem(
                    achievementId = "first_steps",
                    name = "First Steps",
                    description = "Complete your first setup action",
                    points = 50,
                    category = "onboarding"
                ),
                progress = UserProgressItem(
                    achievementId = "first_steps",
                    progress = 1,
                    target = 1,
                    unlockedAt = System.currentTimeMillis()
                )
            ),
            AchievementWithProgress(
                achievement = AchievementItem(
                    achievementId = "growth_tracker",
                    name = "Growth Tracker",
                    description = "Record 10 growth entries",
                    points = 100,
                    category = "monitoring"
                ),
                progress = UserProgressItem(
                    achievementId = "growth_tracker",
                    progress = 4,
                    target = 10,
                    unlockedAt = null
                )
            )
        )
    )

    val achievementsWithProgress: StateFlow<List<AchievementWithProgress>> =
        _achievementsWithProgress.asStateFlow()

    private val _totalPoints = MutableStateFlow(
        _achievementsWithProgress.value.sumOf { it.pointsEarned }
    )
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()
}
