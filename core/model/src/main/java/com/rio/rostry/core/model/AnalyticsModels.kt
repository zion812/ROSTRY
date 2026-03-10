package com.rio.rostry.core.model

data class DailyGoal(
    val goalId: String,
    val type: String, // TASKS, DAILY_LOGS, VACCINATIONS
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val progress: Float, // 0.0 to 1.0
    val priority: String, // HIGH, MEDIUM, LOW
    val deepLink: String,
    val iconName: String,
    val completedAt: Long? = null
)

data class ActionableInsight(
    val id: String,
    val title: String,
    val description: String,
    val deepLink: String,
    val priority: String // HIGH, MEDIUM, LOW
)
