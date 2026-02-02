package com.rio.rostry.ui.admin.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAnalyticsViewModel @Inject constructor() : ViewModel() {

    data class RegionStat(val name: String, val userCount: Int)

    data class UiState(
        val isLoading: Boolean = true,
        val totalUsers: Int = 0,
        val newThisWeek: Int = 0,
        val newThisMonth: Int = 0,
        val enthusiastCount: Int = 0,
        val farmerCount: Int = 0,
        val adminCount: Int = 0,
        val activeToday: Int = 0,
        val activeWeek: Int = 0,
        val topRegions: List<RegionStat> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500)
            
            _state.update { it.copy(
                isLoading = false,
                totalUsers = 1254,
                newThisWeek = 47,
                newThisMonth = 186,
                enthusiastCount = 890,
                farmerCount = 352,
                adminCount = 12,
                activeToday = 312,
                activeWeek = 756,
                topRegions = listOf(
                    RegionStat("Karnataka", 342),
                    RegionStat("Maharashtra", 289),
                    RegionStat("Tamil Nadu", 234),
                    RegionStat("Andhra Pradesh", 198),
                    RegionStat("Kerala", 156)
                )
            ) }
        }
    }

    fun refresh() {
        loadData()
    }
}
