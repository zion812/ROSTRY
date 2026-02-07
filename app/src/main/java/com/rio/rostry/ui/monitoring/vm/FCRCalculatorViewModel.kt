package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.repository.FCRAnalysis
import com.rio.rostry.data.repository.FarmFinancialsRepository
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.data.repository.FarmActivityLogRepository
import com.rio.rostry.data.repository.FCRRating
import com.rio.rostry.data.repository.reference.BreedStandardRepository
import com.rio.rostry.domain.service.FeedConversionService
import com.rio.rostry.domain.service.HistoricalFCRPoint
import com.rio.rostry.domain.service.SmartInsight
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for FCR (Feed Conversion Ratio) Calculator screen.
 * Enhanced with historical trends and breed benchmarking.
 */
@HiltViewModel
class FCRCalculatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val financialsRepository: FarmFinancialsRepository,
    private val growthRepository: GrowthRepository,
    private val activityLogRepository: FarmActivityLogRepository,
    private val breedStandardRepository: BreedStandardRepository,
    private val feedConversionService: FeedConversionService
) : ViewModel() {
    
    private val assetId: String = savedStateHandle.get<String>("assetId") ?: ""
    
    // FCR Analysis result
    private val _fcrAnalysis = MutableStateFlow<FCRAnalysis?>(null)
    val fcrAnalysis = _fcrAnalysis.asStateFlow()
    
    // Selected period for calculation
    private val _selectedPeriod = MutableStateFlow(30) // Default 30 days
    val selectedPeriod = _selectedPeriod.asStateFlow()
    
    // Historical Data
    private val _historicalFCR = MutableStateFlow<List<HistoricalFCRPoint>>(emptyList())
    val historicalFCR = _historicalFCR.asStateFlow()
    
    // Benchmarking
    private val _availableBreeds = MutableStateFlow<List<String>>(emptyList())
    val availableBreeds = _availableBreeds.asStateFlow()
    
    private val _selectedBreed = MutableStateFlow("Cobb 500") // Default
    val selectedBreed = _selectedBreed.asStateFlow()
    
    private val _benchmarkData = MutableStateFlow<List<HistoricalFCRPoint>>(emptyList())
    val benchmarkData = _benchmarkData.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    // Available period options
    val periodOptions = listOf(7, 14, 30, 90)
    
    init {
        loadBreeds()
        if (assetId.isNotBlank()) {
            calculateFCR()
            loadHistoricalData()
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
    
    private fun loadBreeds() {
        val breeds = breedStandardRepository.getAvailableBreeds()
        _availableBreeds.value = breeds
        if (breeds.isNotEmpty()) {
             // Try to inherit from asset if possible (not implemented yet), else default
        }
    }
    
    fun selectBreed(breed: String) {
        _selectedBreed.value = breed
        updateBenchmarkData()
    }
    
    private fun loadHistoricalData() {
        if (assetId.isBlank()) return
        
        viewModelScope.launch {
            // 1. Fetch Growth Records
            val growthFlow = growthRepository.observe(assetId)
            
            // 2. Fetch Feed Logs (approximate by looking at all feed logs for this asset/product)
            // Note: FarmActivityLogRepository.observeForProduct returns Flow
            val feedFlow = activityLogRepository.observeForProduct(assetId)
            
            combine(growthFlow, feedFlow) { growth, feed ->
                val feedLogs = feed.filter { it.activityType == "FEED" }
                val points = feedConversionService.calculateHistoricalFCR(growth, feedLogs)
                _historicalFCR.value = points
                
                // Update benchmark to match relevant weeks
                updateBenchmarkData()
            }.collect()
        }
    }
    
    private fun updateBenchmarkData() {
        val breed = _selectedBreed.value
        val history = _historicalFCR.value
        val standard = breedStandardRepository.getStandard(breed)
        
        if (standard != null && history.isNotEmpty()) {
            val benchmarks = history.map { point ->
                val weeklyStd = standard.weeklyStandards.find { it.week == point.week }
                if (weeklyStd != null) {
                    HistoricalFCRPoint(
                        week = point.week,
                        fcr = weeklyStd.targetFCR,
                        weightKg = weeklyStd.targetWeightGrams / 1000f,
                        cumulativeFeedKg = weeklyStd.cumulativeFeedGrams / 1000f
                    )
                } else {
                    // Extrapolate or skip
                    HistoricalFCRPoint(point.week, 0f, 0f, 0f)
                }
            }
            _benchmarkData.value = benchmarks
        } else {
            _benchmarkData.value = emptyList()
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
     * Get actionable insights using the shared service logic
     */
    fun getInsights(): List<SmartInsight> {
        val analysis = _fcrAnalysis.value ?: return emptyList()
        
        // Convert FCRAnalysis (Repository DTO) to FeedConversionResult (Service Domain) if needed
        // Or mainly use the service to generate insights if we had raw data.
        // Since we have analysis, we can map it manually or adapt.
        
        // For now, let's just map the analysis to what we can.
        // Ideally we should move 'generateSmartInsights' logic to work with FCRAnalysis too.
        // Re-implementing simplified mapping here for UI consistency:
        
        // But wait! We have feedConversionService available!
        // We can reconstruct the input for it? No, FCRAnalysis is aggregated.
        
        // Let's create SmartInsights manually here using the same types
        val insights = mutableListOf<SmartInsight>()
        
        if (analysis.rating == FCRRating.EXCELLENT) {
             insights.add(SmartInsight(
                 com.rio.rostry.domain.service.InsightType.FEED_EFFICIENCY,
                 "Outstanding Performance! 🏆",
                 "${String.format("%.2f", analysis.fcr)} FCR",
                 "Your FCR is ${String.format("%.0f", analysis.percentageVsBenchmark)}% better than average.",
                 priority = com.rio.rostry.domain.service.InsightPriority.HIGH
             ))
        } else if (analysis.rating == FCRRating.POOR) {
             insights.add(SmartInsight(
                 com.rio.rostry.domain.service.InsightType.FEED_EFFICIENCY,
                 "Action Needed ⚠️",
                 "${String.format("%.2f", analysis.fcr)} FCR",
                 "Review feed quality and health status.",
                 priority = com.rio.rostry.domain.service.InsightPriority.CRITICAL,
                 actionLabel = "View Tips"
             ))
        }
        
        // Add deviation alert if current FCR is far from benchmark
        val currentWeek = _historicalFCR.value.maxByOrNull { it.week }?.week
        val benchmark = _benchmarkData.value.find { it.week == currentWeek }
        if (currentWeek != null && benchmark != null && benchmark.fcr > 0) {
            val diff = analysis.fcr - benchmark.fcr
            if (diff > 0.2) {
                 insights.add(SmartInsight(
                     com.rio.rostry.domain.service.InsightType.GROWTH_TREND,
                     "Below Breed Standard",
                     "+${String.format("%.2f", diff)} vs ${selectedBreed.value}",
                     "Performance is lagging behind the breed standard.",
                     priority = com.rio.rostry.domain.service.InsightPriority.HIGH
                 ))
            }
        }
        
        return insights
    }
}
