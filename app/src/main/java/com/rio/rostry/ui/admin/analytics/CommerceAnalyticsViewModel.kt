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
class CommerceAnalyticsViewModel @Inject constructor(
    private val repository: com.rio.rostry.data.repository.OrderManagementRepository
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
                // Launch concurrent fetching
                val stats = repository.getCommerceStats()
                val topProducts = repository.getTopProducts(5)
                val topSellers = repository.getTopSellers(5)
                
                _state.update { it.copy(
                    isLoading = false,
                    totalOrders = stats.totalOrders,
                    ordersThisWeek = stats.ordersThisWeek,
                    ordersThisMonth = stats.ordersThisMonth,
                    totalRevenue = stats.totalRevenue,
                    revenueThisWeek = stats.revenueThisWeek,
                    revenueThisMonth = stats.revenueThisMonth,
                    avgOrderValue = stats.avgOrderValue,
                    completedOrders = stats.completedOrders,
                    pendingOrders = stats.pendingOrders,
                    disputedOrders = 0, // Not yet implemented in repository
                    topProducts = topProducts.map { 
                        TopProduct(it.name, it.sales, it.revenue) 
                    },
                    topSellers = topSellers.map { 
                        TopSeller(it.name, it.orders, it.revenue) 
                    }
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
