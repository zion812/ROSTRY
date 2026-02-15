package com.rio.rostry.ui.enthusiast.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.service.BreedingROIService
import com.rio.rostry.domain.service.FlockProductivityService
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlockAnalyticsViewModel @Inject constructor(
    private val flockProductivityService: FlockProductivityService,
    private val breedingROIService: BreedingROIService,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val summary: FlockProductivityService.FlockSummary? = null,
        val selectedBirdROI: BreedingROIService.BirdROI? = null,
        val roiLoading: Boolean = false,
        val activeTab: Int = 0,  // 0 = Overview, 1 = Leaderboard, 2 = Breeds
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = currentUserProvider.userIdOrNull() ?: return@launch
                val summary = flockProductivityService.analyzeFlockProductivity(userId)
                _uiState.update { it.copy(isLoading = false, summary = summary) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load analytics") }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(activeTab = index) }
    }

    fun viewBirdROI(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(roiLoading = true) }
            try {
                val roi = breedingROIService.analyzeBirdROI(productId)
                _uiState.update { it.copy(roiLoading = false, selectedBirdROI = roi) }
            } catch (e: Exception) {
                _uiState.update { it.copy(roiLoading = false) }
            }
        }
    }

    fun clearBirdROI() {
        _uiState.update { it.copy(selectedBirdROI = null) }
    }

    fun refresh() {
        loadAnalytics()
    }
}
