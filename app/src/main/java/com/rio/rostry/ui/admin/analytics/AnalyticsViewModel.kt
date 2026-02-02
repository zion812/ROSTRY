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
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        // Overview Stats
        val totalUsers: Int = 0,
        val newUsersThisWeek: Int = 0,
        val activeUsersToday: Int = 0,
        val totalOrders: Int = 0,
        val totalRevenue: Double = 0.0,
        val avgOrderValue: Double = 0.0,
        val pendingVerifications: Int = 0,
        val activeDisputes: Int = 0,
        // Growth metrics
        val userGrowthPercent: Double = 0.0,
        val revenueGrowthPercent: Double = 0.0,
        val orderGrowthPercent: Double = 0.0,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            delay(500) // Simulate loading
            
            // Mock data - in production would come from analytics repositories
            _state.update { it.copy(
                isLoading = false,
                totalUsers = 1254,
                newUsersThisWeek = 47,
                activeUsersToday = 312,
                totalOrders = 856,
                totalRevenue = 4850000.0,
                avgOrderValue = 5667.0,
                pendingVerifications = 12,
                activeDisputes = 3,
                userGrowthPercent = 8.5,
                revenueGrowthPercent = 12.3,
                orderGrowthPercent = 15.2
            ) }
        }
    }

    fun refresh() {
        loadAnalytics()
    }
}
