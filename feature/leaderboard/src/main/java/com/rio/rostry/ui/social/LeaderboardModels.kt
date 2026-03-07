package com.rio.rostry.ui.social

data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val score: Long,
    val rank: Int,
    val avatarUrl: String? = null
)
