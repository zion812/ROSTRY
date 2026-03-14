package com.rio.rostry.feature.admin.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rio.rostry.domain.commerce.repository.OrderManagementRepository

@HiltViewModel
class CommerceAnalyticsViewModel @Inject constructor(
    private val repository: OrderManagementRepository
) : ViewModel() {

    data class TopProduct(val name: String, val sales: Int, val revenue: Double)
    data class TopSeller(val name: String, val orders: Int, val revenue: Double)

    data class UiState(
        val isLoading: Boolean = true,
        val totalOrders: Int = 0,
        val ordersThisWeek: Int = 0,
        val ordersThisMonth: Int = 0,
        val totalRevenue: Double = 0.0,
        val revenueThisWeek: Double = 0.0,
        val revenueThisMonth: Double = 0.0,
        val avgOrderValue: Double = 0.0,
        val completedOrders: Int = 0,
        val pendingOrders: Int = 0,
        val disputedOrders: Int = 0,
        val topProducts: List<TopProduct> = emptyList(),
        val topSellers: List<TopSeller> = emptyList(),
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
            
            try {
                // Fetch stats from new domain layer
                val statsResult = repository.getOrderStats()
                
                if (statsResult is com.rio.rostry.core.model.Result.Success<*>) {
                    val stats = statsResult.data as? Map<String, Any> ?: emptyMap()

                    _state.update { it.copy(
                        isLoading = false,
                        totalOrders = (stats["totalOrders"] as? Number)?.toInt() ?: 0,
                        ordersThisWeek = (stats["ordersThisWeek"] as? Number)?.toInt() ?: 0,
                        ordersThisMonth = (stats["ordersThisMonth"] as? Number)?.toInt() ?: 0,
                        totalRevenue = (stats["totalRevenue"] as? Number)?.toDouble() ?: 0.0,
                        revenueThisWeek = (stats["revenueThisWeek"] as? Number)?.toDouble() ?: 0.0,
                        revenueThisMonth = (stats["revenueThisMonth"] as? Number)?.toDouble() ?: 0.0,
                        avgOrderValue = (stats["avgOrderValue"] as? Number)?.toDouble() ?: 0.0,
                        completedOrders = (stats["completedOrders"] as? Number)?.toInt() ?: 0,
                        pendingOrders = (stats["pendingOrders"] as? Number)?.toInt() ?: 0,
                        disputedOrders = 0, // Not yet implemented in repository
                        topProducts = emptyList(), // Not supported by current repo
                        topSellers = emptyList() // Not supported by current repo
                    ) }
                } else if (statsResult is com.rio.rostry.core.model.Result.Error) {
                    _state.update { it.copy(isLoading = false, error = statsResult.exception.message) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
