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
class CommerceAnalyticsViewModel @Inject constructor() : ViewModel() {

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
            delay(500)
            
            _state.update { it.copy(
                isLoading = false,
                totalOrders = 856,
                ordersThisWeek = 78,
                ordersThisMonth = 312,
                totalRevenue = 4850000.0,
                revenueThisWeek = 425000.0,
                revenueThisMonth = 1850000.0,
                avgOrderValue = 5667.0,
                completedOrders = 712,
                pendingOrders = 98,
                disputedOrders = 12,
                topProducts = listOf(
                    TopProduct("Kadaknath Chicken", 156, 780000.0),
                    TopProduct("Country Eggs (30)", 234, 468000.0),
                    TopProduct("Aseel Rooster", 89, 445000.0),
                    TopProduct("Broiler Chicken (kg)", 312, 390000.0),
                    TopProduct("Duck Eggs (12)", 145, 217500.0)
                ),
                topSellers = listOf(
                    TopSeller("Sharma Farms", 89, 534000.0),
                    TopSeller("Green Valley Poultry", 76, 456000.0),
                    TopSeller("Sunrise Farms", 65, 390000.0),
                    TopSeller("Premium Poultry", 58, 348000.0)
                )
            ) }
        }
    }

    fun refresh() {
        loadData()
    }
}
