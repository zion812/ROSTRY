package com.rio.rostry.ui.social
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val score: Long,
    val rank: Int,
    val avatarUrl: String? = null
)
