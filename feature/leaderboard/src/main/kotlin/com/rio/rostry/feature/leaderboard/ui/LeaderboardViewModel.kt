package com.rio.rostry.ui.social
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel : ViewModel() {
    private val period = MutableStateFlow("week") // "week" | "month" | "all"

    private val sample = listOf(
        LeaderboardEntry(userId = "user_01", displayName = "Farmer Rio", score = 1540, rank = 1),
        LeaderboardEntry(userId = "user_02", displayName = "Asha Farms", score = 1320, rank = 2),
        LeaderboardEntry(userId = "user_03", displayName = "Poultry Hub", score = 1180, rank = 3)
    )

    val top: StateFlow<List<LeaderboardEntry>> = period
        .map { key ->
            when (key) {
                "week" -> sample
                "month" -> sample.mapIndexed { index, e -> e.copy(score = e.score + 250 - (index * 50L)) }
                else -> sample.mapIndexed { index, e -> e.copy(score = e.score + 600 - (index * 100L)) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, sample)

    fun setPeriod(key: String) {
        period.value = key
    }
}
