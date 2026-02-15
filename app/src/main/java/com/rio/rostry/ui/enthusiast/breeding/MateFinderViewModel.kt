package com.rio.rostry.ui.enthusiast.breeding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.BreedingService
import com.rio.rostry.domain.breeding.EnhancedTraitPrediction
import com.rio.rostry.domain.service.BreedingValueResult
import com.rio.rostry.domain.service.MateRecommendationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MateFinderViewModel @Inject constructor(
    private val mateRecommendationService: MateRecommendationService,
    private val breedingService: BreedingService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""

    data class UiState(
        val isLoading: Boolean = true,
        val focalBird: ProductEntity? = null,
        val focalBvi: BreedingValueResult? = null,
        val candidates: List<MateRecommendationService.MateCandidate> = emptyList(),
        val totalEvaluated: Int = 0,
        val selectedCandidate: MateRecommendationService.MateCandidate? = null,
        val enhancedPrediction: EnhancedTraitPrediction? = null,
        val predictionLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        if (productId.isNotBlank()) {
            loadRecommendations()
        }
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = mateRecommendationService.findBestMates(productId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        focalBird = result.focalBird,
                        focalBvi = result.focalBvi,
                        candidates = result.candidates,
                        totalEvaluated = result.totalEvaluated
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load recommendations") }
            }
        }
    }

    fun selectCandidate(candidate: MateRecommendationService.MateCandidate) {
        _uiState.update { it.copy(selectedCandidate = candidate, enhancedPrediction = null) }
        // Load enhanced prediction for this pairing
        viewModelScope.launch {
            _uiState.update { it.copy(predictionLoading = true) }
            try {
                val focal = _uiState.value.focalBird ?: return@launch
                val mate = candidate.bird
                val (sire, dam) = if (focal.gender?.lowercase() == "male") focal to mate else mate to focal
                val prediction = breedingService.predictOffspringEnhanced(sire, dam)
                _uiState.update { it.copy(predictionLoading = false, enhancedPrediction = prediction) }
            } catch (e: Exception) {
                _uiState.update { it.copy(predictionLoading = false) }
            }
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedCandidate = null, enhancedPrediction = null) }
    }

    fun refresh() {
        loadRecommendations()
    }
}
