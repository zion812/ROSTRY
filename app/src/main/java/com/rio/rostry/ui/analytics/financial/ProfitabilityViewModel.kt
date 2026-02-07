package com.rio.rostry.ui.analytics.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.analytics.BreedROI
import com.rio.rostry.data.repository.analytics.MonthlyProfit
import com.rio.rostry.data.repository.analytics.ProfitabilityRepository
import com.rio.rostry.data.repository.analytics.ProfitabilitySummary
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProfitabilityViewModel @Inject constructor(
    private val repository: ProfitabilityRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val summary: ProfitabilitySummary? = null,
        val trends: List<MonthlyProfit> = emptyList(),
        val expenseBreakdown: Map<String, Double> = emptyMap(),
        val breedRoiList: List<BreedROI> = emptyList(),
        val error: String? = null,
        val timeRangeLabel: String = "Last 6 Months"
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Default range: Last 6 months for summary?? 
            // Usually summary is "All Time" or "This Year". 
            // Let's do "This Year" or "Last 6 Months" to match trends?
            // Let's align with Trends (6 months).
            
            val calendar = Calendar.getInstance()
            val end = calendar.timeInMillis
            calendar.add(Calendar.MONTH, -6)
            val start = calendar.timeInMillis

            // Fetch Summary
            val summaryRes = repository.getProfitabilitySummary(start, end)
            
            // Fetch Trends
            val trendsRes = repository.getMonthlyTrends(6)
            
            // Fetch Breakdown
            val breakdownRes = repository.getExpenseBreakdown(start, end)
            
            // Fetch Breed ROI
            val breedRes = repository.getProfitableBreeds(start, end)

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    summary = summaryRes.data,
                    trends = trendsRes.data ?: emptyList(),
                    expenseBreakdown = breakdownRes.data ?: emptyMap(),
                    breedRoiList = breedRes.data ?: emptyList(),
                    error = summaryRes.message ?: trendsRes.message // Simple error handling
                ) 
            }
        }
    }
    
    fun refresh() {
        loadData()
    }
}
