package com.rio.rostry.ui.admin.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val userRepository: com.rio.rostry.data.repository.UserRepository,
    private val orderRepository: com.rio.rostry.data.repository.OrderManagementRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val totalUsers: Int = 0,
        val newUsersThisWeek: Int = 0,
        val activeUsersToday: Int = 0,
        val totalOrders: Int = 0,
        val totalRevenue: Double = 0.0,
        val avgOrderValue: Double = 0.0,
        val pendingVerifications: Int = 0,
        val activeDisputes: Int = 0,
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
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val now = System.currentTimeMillis()
                val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000L)
                val oneDayAgo = now - (24 * 60 * 60 * 1000L)

                // Parallel fetch
                val deferredUserCount = async { userRepository.countAllUsers() }
                val deferredNewUsers = async { userRepository.getNewUsersCount(oneWeekAgo) }
                val deferredActiveUsers = async { userRepository.getActiveUsersCount(oneDayAgo) }
                val deferredPendingVerifications = async { userRepository.getPendingVerificationCount() }
                val deferredCommerceStats = async { orderRepository.getCommerceStats() }

                val totalUsers = deferredUserCount.await()
                val newUsers = deferredNewUsers.await()
                val activeUsers = deferredActiveUsers.await()
                val pendingVerifications = deferredPendingVerifications.await()
                val commerceStats = deferredCommerceStats.await()
                
                // Calculate pseudo-growth (mocking previous period for now unless we do double queries)
                // For MVP, we'll just show 0 or simple ratio if we had history.
                // Assuming newUsers is the growth number for now.
                val userGrowth = if (totalUsers > 0) (newUsers.toDouble() / totalUsers) * 100 else 0.0
                
                // Commerce Growth (Revenue This Week vs Last Week - approximated)
                val orderGrowth = if (commerceStats.totalOrders > 0) (commerceStats.ordersThisWeek.toDouble() / commerceStats.totalOrders) * 100 else 0.0
                val revenueGrowth = if (commerceStats.totalRevenue > 0) (commerceStats.revenueThisWeek / commerceStats.totalRevenue) * 100 else 0.0

                _state.update { it.copy(
                    isLoading = false,
                    totalUsers = totalUsers,
                    newUsersThisWeek = newUsers,
                    activeUsersToday = activeUsers,
                    totalOrders = commerceStats.totalOrders,
                    totalRevenue = commerceStats.totalRevenue,
                    avgOrderValue = commerceStats.avgOrderValue,
                    pendingVerifications = pendingVerifications,
                    activeDisputes = 0, // TODO: Add DisputeRepository count
                    userGrowthPercent = userGrowth,
                    revenueGrowthPercent = revenueGrowth,
                    orderGrowthPercent = orderGrowth
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        loadAnalytics()
    }
}
