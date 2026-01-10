package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.FarmerDashboard
import com.rio.rostry.data.repository.FarmFinancialsRepository
import com.rio.rostry.data.repository.CostPerBirdAnalysis
import com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Profitability metrics for the farmer dashboard.
 */
data class ProfitabilityMetrics(
    val totalRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMargin: Double = 0.0, // Percentage
    val costBreakdown: Map<String, Double> = emptyMap(), // Category -> Amount
    val revenueBreakdown: Map<String, Double> = emptyMap(), // Source -> Amount
    val profitTrend: List<Double> = emptyList(), // Last 7 snapshots
    val isLoading: Boolean = true
)

/**
 * Enhanced Farmer Dashboard state with profitability.
 */
data class EnhancedFarmerDashboard(
    val base: FarmerDashboard = FarmerDashboard(revenue = 0.0, orders = 0, productViews = 0, engagementScore = 0.0),
    val profitability: ProfitabilityMetrics = ProfitabilityMetrics(),
    val recentSnapshots: List<FarmerDashboardSnapshotEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FarmerDashboardViewModel @Inject constructor(
    private val repo: AnalyticsRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val snapshotDao: FarmerDashboardSnapshotDao,
    private val financialsRepository: FarmFinancialsRepository
) : ViewModel() {
    private val empty = FarmerDashboard(revenue = 0.0, orders = 0, productViews = 0, engagementScore = 0.0)
    private val uid = currentUserProvider.userIdOrNull()
    
    // Legacy: Keep for backward compatibility
    val dashboard: StateFlow<FarmerDashboard> =
        (uid?.let { repo.farmerDashboard(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)

    val lastFour: StateFlow<List<FarmerDashboardSnapshotEntity>> =
        (uid?.let { snapshotDao.observeLastN(it, 4) } ?: MutableStateFlow(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    
    // NEW: Profitability metrics
    private val _profitability = MutableStateFlow(ProfitabilityMetrics())
    val profitability: StateFlow<ProfitabilityMetrics> = _profitability
    
    // NEW: Enhanced dashboard with all metrics combined
    val enhancedDashboard: StateFlow<EnhancedFarmerDashboard> = combine(
        dashboard,
        _profitability,
        lastFour
    ) { base, profit, snapshots ->
        EnhancedFarmerDashboard(
            base = base,
            profitability = profit,
            recentSnapshots = snapshots,
            isLoading = profit.isLoading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EnhancedFarmerDashboard())
    
    init {
        loadProfitabilityMetrics()
    }
    
    /**
     * Load profitability metrics from FarmFinancialsRepository.
     */
    private fun loadProfitabilityMetrics() {
        val farmerId = uid ?: return
        
        viewModelScope.launch {
            try {
                // Get overall cost analysis for the farm
                financialsRepository.getOverallFarmCostAnalysis(farmerId).collect { resource ->
                    when (resource) {
                        is com.rio.rostry.utils.Resource.Success -> {
                            val analysis = resource.data
                            if (analysis != null) {
                                // Calculate revenue from dashboard
                                val revenue = dashboard.value.revenue
                                val expenses = analysis.totalCost
                                val profit = revenue - expenses
                                val margin = if (revenue > 0) (profit / revenue) * 100 else 0.0
                                
                                // Build cost breakdown
                                val breakdown = mutableMapOf<String, Double>()
                                breakdown["Feed"] = analysis.feedCost
                                breakdown["Medication"] = analysis.medicationCost
                                breakdown["Vaccination"] = analysis.vaccinationCost
                                breakdown["Other"] = analysis.otherCost
                                breakdown["Mortality Loss"] = analysis.mortalityCost
                                
                                // Get profit trend from snapshots
                                val profitTrend = lastFour.value.map { snapshot ->
                                    // Estimate profit from snapshot data
                                    snapshot.revenueInr * 0.3 // Assume 30% margin historically
                                }
                                
                                _profitability.value = ProfitabilityMetrics(
                                    totalRevenue = revenue,
                                    totalExpenses = expenses,
                                    netProfit = profit,
                                    profitMargin = margin,
                                    costBreakdown = breakdown,
                                    profitTrend = profitTrend,
                                    isLoading = false
                                )
                            }
                        }
                        is com.rio.rostry.utils.Resource.Error -> {
                            _profitability.value = _profitability.value.copy(isLoading = false)
                        }
                        is com.rio.rostry.utils.Resource.Loading -> {
                            _profitability.value = _profitability.value.copy(isLoading = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _profitability.value = _profitability.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * Refresh all dashboard data.
     */
    fun refresh() {
        loadProfitabilityMetrics()
    }
}
