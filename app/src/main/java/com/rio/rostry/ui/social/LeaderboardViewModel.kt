package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.LeaderboardDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.LeaderboardEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class for leaderboard entries with resolved display names.
 */
data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val score: Long,
    val rank: Int,
    val avatarUrl: String? = null
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardDao: LeaderboardDao,
    private val userDao: UserDao
) : ViewModel() {
    private val period = MutableStateFlow("week") // "week" | "month" | "all"
    
    // Map of userId to display name
    private val _userNames = MutableStateFlow<Map<String, String>>(emptyMap())
    
    // Raw leaderboard data
    private val rawTop: StateFlow<List<LeaderboardEntity>> = period
        .flatMapLatest { key ->
            val periodKey = when (key) {
                "week" -> "week"
                "month" -> "month"
                else -> "all"
            }
            leaderboardDao.leaderboard(periodKey)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
    
    // Combined leaderboard with display names
    val top: StateFlow<List<LeaderboardEntry>> = combine(rawTop, _userNames) { entries, names ->
        entries.map { entity ->
            LeaderboardEntry(
                userId = entity.userId,
                displayName = names[entity.userId] ?: formatUserId(entity.userId),
                score = entity.score,
                rank = entity.rank
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
    
    init {
        // Observe raw leaderboard and fetch user names
        viewModelScope.launch {
            rawTop.collect { entries ->
                if (entries.isNotEmpty()) {
                    fetchUserNames(entries.map { it.userId })
                }
            }
        }
    }
    
    /**
     * Fetch display names for the given user IDs.
     */
    private suspend fun fetchUserNames(userIds: List<String>) {
        val users = userDao.getUsersByIds(userIds)
        val nameMap = users.associate { user ->
            user.userId to (user.fullName?.takeIf { it.isNotBlank() } 
                ?: formatUserId(user.userId))
        }
        _userNames.value = _userNames.value + nameMap
    }
    
    /**
     * Format user ID for display when name is not available.
     * e.g., "usr_abc123def456" -> "user_abc1...f456"
     */
    private fun formatUserId(userId: String): String {
        return if (userId.length > 12) {
            "${userId.take(8)}...${userId.takeLast(4)}"
        } else {
            userId
        }
    }

    fun setPeriod(key: String) {
        period.value = key
    }
}
