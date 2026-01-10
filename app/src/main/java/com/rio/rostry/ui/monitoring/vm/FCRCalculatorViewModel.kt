package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.FCRAnalysis
import com.rio.rostry.data.repository.FCRRating
import com.rio.rostry.data.repository.FarmFinancialsRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for FCR (Feed Conversion Ratio) Calculator screen.
 */
@HiltViewModel
class FCRCalculatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val financialsRepository: FarmFinancialsRepository
) : ViewModel() {
    
    private val assetId: String = savedStateHandle.get<String>("assetId") ?: ""
    
    // FCR Analysis result
    private val _fcrAnalysis = MutableStateFlow<FCRAnalysis?>(null)
    val fcrAnalysis = _fcrAnalysis.asStateFlow()
    
    // Selected period for calculation
    private val _selectedPeriod = MutableStateFlow(30) // Default 30 days
    val selectedPeriod = _selectedPeriod.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    // Available period options
    val periodOptions = listOf(7, 14, 30, 90)
    
    init {
        if (assetId.isNotBlank()) {
            calculateFCR()
        }
    }
    
    /**
     * Calculate FCR for the selected period.
     */
    fun calculateFCR(periodDays: Int = _selectedPeriod.value) {
        if (assetId.isBlank()) {
            _error.value = "Invalid asset ID"
            return
        }
        
        _selectedPeriod.value = periodDays
        
        viewModelScope.launch {
            financialsRepository.calculateFCR(assetId, periodDays)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _isLoading.value = true
                            _error.value = null
                        }
                        is Resource.Success -> {
                            _isLoading.value = false
                            _fcrAnalysis.value = result.data
                            _error.value = null
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _error.value = result.message
                            _fcrAnalysis.value = null
                        }
                    }
                }
        }
    }
    
    /**
     * Update period and recalculate.
     */
    fun selectPeriod(days: Int) {
        if (days in periodOptions && days != _selectedPeriod.value) {
            calculateFCR(days)
        }
    }
    
    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Get actionable insights based on FCR analysis.
     */
    fun getInsights(): List<FCRInsight> {
        val analysis = _fcrAnalysis.value ?: return emptyList()
        
        val insights = mutableListOf<FCRInsight>()
        
        when (analysis.rating) {
            FCRRating.EXCELLENT -> {
                insights.add(
                    FCRInsight(
                        title = "Outstanding Performance! 🏆",
                        description = "Your FCR of ${String.format("%.2f", analysis.fcr)} is ${String.format("%.0f", analysis.percentageVsBenchmark)}% better than industry average.",
                        type = InsightType.POSITIVE
                    )
                )
            }
            FCRRating.GOOD -> {
                insights.add(
                    FCRInsight(
                        title = "Good Performance 👍",
                        description = "Your FCR is meeting industry benchmarks. Small improvements in feed quality could push you to excellent.",
                        type = InsightType.POSITIVE
                    )
                )
            }
            FCRRating.AVERAGE -> {
                insights.add(
                    FCRInsight(
                        title = "Room for Improvement 📈",
                        description = "Consider reviewing feed quality and feeding schedule to improve FCR.",
                        type = InsightType.WARNING
                    )
                )
            }
            FCRRating.POOR -> {
                insights.add(
                    FCRInsight(
                        title = "Action Needed ⚠️",
                        description = "High FCR indicates inefficiency. Review feed quality, health status, and environmental conditions.",
                        type = InsightType.ALERT
                    )
                )
            }
        }
        
        // Feed consumption insight
        if (analysis.totalFeedKg > 0) {
            val dailyFeed = analysis.totalFeedKg / _selectedPeriod.value
            insights.add(
                FCRInsight(
                    title = "Daily Feed Average",
                    description = "Average daily consumption: ${String.format("%.2f", dailyFeed)} kg",
                    type = InsightType.INFO
                )
            )
        }
        
        return insights
    }
}

/**
 * Data class for FCR insights.
 */
data class FCRInsight(
    val title: String,
    val description: String,
    val type: InsightType
)

enum class InsightType {
    POSITIVE,
    WARNING,
    ALERT,
    INFO
}
