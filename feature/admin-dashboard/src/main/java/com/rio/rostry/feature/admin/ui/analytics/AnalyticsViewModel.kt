package com.rio.rostry.feature.admin.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rio.rostry.domain.commerce.repository.OrderManagementRepository

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val userRepository: com.rio.rostry.domain.account.repository.UserRepository,
    private val orderRepository: com.rio.rostry.domain.commerce.repository.OrderManagementRepository,
    private val analyticsDao: com.rio.rostry.data.database.dao.AnalyticsDao
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
                val deferredCommerceStats = async { orderRepository.getOrderStats() }

                val totalUsers = deferredUserCount.await()
                val newUsers = deferredNewUsers.await()
                val activeUsers = deferredActiveUsers.await()
                val pendingVerifications = deferredPendingVerifications.await()
                val commerceResult = deferredCommerceStats.await()

                val userGrowth = if (totalUsers > 0) (newUsers.toDouble() / totalUsers) * 100 else 0.0

                // Extract stats from Map result
                val statsMap = (commerceResult as? com.rio.rostry.core.common.Result.Success)?.data ?: emptyMap()
                val totalOrders = (statsMap["totalOrders"] as? Number)?.toInt() ?: 0
                val totalRevenue = (statsMap["totalRevenue"] as? Number)?.toDouble() ?: 0.0
                val avgOrderValue = (statsMap["avgOrderValue"] as? Number)?.toDouble() ?: 0.0

                _state.update { it.copy(
                    isLoading = false,
                    totalUsers = totalUsers,
                    newUsersThisWeek = newUsers,
                    activeUsersToday = activeUsers,
                    totalOrders = totalOrders,
                    totalRevenue = totalRevenue,
                    avgOrderValue = avgOrderValue,
                    pendingVerifications = pendingVerifications,
                    activeDisputes = 0,
                    userGrowthPercent = userGrowth,
                    revenueGrowthPercent = 0.0,
                    orderGrowthPercent = 0.0
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
