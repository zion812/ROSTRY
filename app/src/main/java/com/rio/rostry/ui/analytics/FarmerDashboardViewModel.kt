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
    val isLoading: Boolean = true,
    val dateRange: DateRange = DateRange.ALL_TIME
)

@HiltViewModel
class FarmerDashboardViewModel @Inject constructor(
    private val repo: AnalyticsRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val snapshotDao: FarmerDashboardSnapshotDao,
    private val financialsRepository: FarmFinancialsRepository,
    private val analyticsDao: com.rio.rostry.data.database.dao.AnalyticsDao
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
    
    // Date Range Filter
    private val _dateRange = MutableStateFlow(DateRange.ALL_TIME)
    val dateRange: StateFlow<DateRange> = _dateRange
    
    // NEW: Profitability metrics
    private val _profitability = MutableStateFlow(ProfitabilityMetrics())
    val profitability: StateFlow<ProfitabilityMetrics> = _profitability
    
    // NEW: Enhanced dashboard with all metrics combined
    val enhancedDashboard: StateFlow<EnhancedFarmerDashboard> = combine(
        dashboard,
        _profitability,
        lastFour,
        _dateRange
    ) { base, profit, snapshots, range ->
        EnhancedFarmerDashboard(
            base = base,
            profitability = profit,
            recentSnapshots = snapshots,
            isLoading = profit.isLoading,
            dateRange = range // Assuming we add this to data class or just UI uses the flow
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EnhancedFarmerDashboard())
    
    init {
        // Initial load
        loadProfitabilityMetrics(DateRange.ALL_TIME)
        
        // Re-load when filter changes
        viewModelScope.launch {
            _dateRange.collect { range ->
                loadProfitabilityMetrics(range)
            }
        }
    }
    
    fun setDateRange(range: DateRange) {
        _dateRange.value = range
    }
    
    /**
     * Load profitability metrics from FarmFinancialsRepository.
     */
    private fun loadProfitabilityMetrics(range: DateRange = _dateRange.value) {
        val farmerId = uid ?: return
        
        viewModelScope.launch {
            try {
                _profitability.value = _profitability.value.copy(isLoading = true)
                
                // Calculate time range
                val now = java.time.LocalDate.now()
                val (startMs, endMs) = when (range) {
                    DateRange.LAST_7_DAYS -> {
                         val start = now.minusDays(7)
                         Pair(
                             start.atStartOfDay(java.time.ZoneId.systemDefault()).toEpochSecond() * 1000,
                             System.currentTimeMillis()
                         )
                    }
                    DateRange.LAST_30_DAYS -> {
                        val start = now.minusDays(30)
                        Pair(
                             start.atStartOfDay(java.time.ZoneId.systemDefault()).toEpochSecond() * 1000,
                             System.currentTimeMillis()
                         )
                    }
                    DateRange.THIS_MONTH -> {
                        val start = now.withDayOfMonth(1)
                        Pair(
                             start.atStartOfDay(java.time.ZoneId.systemDefault()).toEpochSecond() * 1000,
                             System.currentTimeMillis()
                         )
                    }
                    DateRange.ALL_TIME -> Pair(0L, System.currentTimeMillis())
                }
                
                // 1. Get filtered Revenue
                val revenue = if (range == DateRange.ALL_TIME) {
                    dashboard.value.revenue // Use cached all-time if available, or fetch
                } else {
                    val fromDate = when(range) {
                        DateRange.LAST_7_DAYS -> now.minusDays(7)
                        DateRange.LAST_30_DAYS -> now.minusDays(30)
                        DateRange.THIS_MONTH -> now.withDayOfMonth(1)
                        else -> now.minusYears(10)
                    }
                    val logs = analyticsDao.listRange(farmerId, fromDate.toString(), now.toString())
                    logs.sumOf { it.salesRevenue }
                }
                
                // 2. Get filtered Expenses
                val rangePair = if (range == DateRange.ALL_TIME) null else Pair(startMs, endMs)
                
                financialsRepository.getOverallFarmCostAnalysis(farmerId, rangePair).collect { resource ->
                    when (resource) {
                        is com.rio.rostry.utils.Resource.Success -> {
                            val analysis = resource.data
                            if (analysis != null) {
                                val expenses = analysis.totalCost
                                val profit = revenue - expenses
                                val margin = if (revenue > 0) (profit / revenue) * 100 else 0.0
                                
                                // Build cost breakdown
                                val breakdown = mutableMapOf<String, Double>()
                                breakdown["Feed"] = analysis.feedCost
                                breakdown["Medical"] = analysis.medicationCost + analysis.vaccinationCost
                                breakdown["Other"] = analysis.otherCost
                                breakdown["Loss"] = analysis.mortalityCost
                                
                                _profitability.value = ProfitabilityMetrics(
                                    totalRevenue = revenue,
                                    totalExpenses = expenses,
                                    netProfit = profit,
                                    profitMargin = margin,
                                    costBreakdown = breakdown,
                                    // Trend is tricky with filtering, maybe keep it last 7 days regardless or snapshot based?
                                    // For now, keep snapshots showing "Trends" generally.
                                    profitTrend = lastFour.value.map { it.revenueInr * 0.3 }, 
                                    isLoading = false
                                )
                            }
                        }
                        is com.rio.rostry.utils.Resource.Error -> {
                            _profitability.value = _profitability.value.copy(isLoading = false)
                        }
                        is com.rio.rostry.utils.Resource.Loading -> {
                            // Keep loading
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

enum class DateRange {
    LAST_7_DAYS,
    LAST_30_DAYS,
    THIS_MONTH,
    ALL_TIME
}
