package com.rio.rostry.gamification

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementSystem @Inject constructor() {
    data class Achievement(val id: String, val name: String, val description: String, val points: Int)
    suspend fun achievementsFor(userId: String): List<Achievement> = emptyList()
    suspend fun recordProgress(userId: String, action: String) {}
}
