package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.LeaderboardDao
import com.rio.rostry.data.database.entity.LeaderboardEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardDao: LeaderboardDao,
) : ViewModel() {
    private val period = MutableStateFlow("week") // "week" | "month" | "all"

    val top: StateFlow<List<LeaderboardEntity>> = period
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

    fun setPeriod(key: String) {
        period.value = key
    }
}
