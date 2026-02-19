package com.rio.rostry.ui.enthusiast.digitaltwin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BirdEventDao
import com.rio.rostry.data.database.dao.DigitalTwinDao
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import com.rio.rostry.domain.digitaltwin.AseelLifecycleEngine
import com.rio.rostry.domain.digitaltwin.DigitalTwinService
import com.rio.rostry.domain.digitaltwin.WeightAnalytics
import com.rio.rostry.domain.digitaltwin.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DigitalTwinDashboardState(
    val twin: DigitalTwinEntity? = null,
    val ageProfile: AgeProfile? = null,
    val morphSummary: MorphSummary? = null,
    val transitionInfo: AseelLifecycleEngine.TransitionInfo? = null,
    val weightAnalytics: WeightAnalytics? = null,
    val growthTimeline: List<GrowthSnapshot> = emptyList(),
    val recentEvents: List<BirdEventEntity> = emptyList(),
    val stageBreakdown: Map<String, Int> = emptyMap(), // stageName -> count
    val isLoading: Boolean = true,
    val error: String? = null,

    // Capability gates
    val canMeasureMorphology: Boolean = false,
    val canMeasurePerformance: Boolean = false,
    val isBreedingEligible: Boolean = false,
    val isShowEligible: Boolean = false
)

@HiltViewModel
class DigitalTwinDashboardViewModel @Inject constructor(
    private val twinDao: DigitalTwinDao,
    private val eventDao: BirdEventDao,
    private val twinService: DigitalTwinService,
    private val lifecycleEngine: AseelLifecycleEngine,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DigitalTwinDashboardState())
    val uiState: StateFlow<DigitalTwinDashboardState> = _uiState.asStateFlow()

    private val birdId: String = savedStateHandle.get<String>("birdId") ?: ""

    init {
        if (birdId.isNotBlank()) {
            loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 1. Load twin
                val twin = twinDao.getByBirdId(birdId)
                if (twin == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Digital Twin not found") }
                    return@launch
                }

                // 2. Derive age profile
                val ageDays = twin.ageDays ?: 0
                val isMale = twin.gender?.equals("MALE", true) ?: true
                val ageProfile = AgeProfile.fromDays(ageDays, isMale)

                // 3. Morph summary
                val morphSummary = twinService.getMorphSummary(birdId)

                // 4. Next transition
                val transitionInfo = lifecycleEngine.getNextTransitionInfo(twin)

                // 5. Weight analytics
                val weightAnalytics = twinService.getWeightAnalytics(birdId)

                // 6. Recent events
                val events = eventDao.getRecentEvents(birdId, limit = 20)

                // 7. Capability gates
                val canMorphology = lifecycleEngine.canMeasureMorphology(twin)
                val canPerformance = lifecycleEngine.canMeasurePerformance(twin)
                val breeding = lifecycleEngine.isBreedingEligible(twin)
                val show = lifecycleEngine.isShowEligible(twin)

                _uiState.update { it.copy(
                    twin = twin,
                    ageProfile = ageProfile,
                    morphSummary = morphSummary,
                    transitionInfo = transitionInfo,
                    weightAnalytics = weightAnalytics,
                    recentEvents = events,
                    canMeasureMorphology = canMorphology,
                    canMeasurePerformance = canPerformance,
                    isBreedingEligible = breeding,
                    isShowEligible = show,
                    isLoading = false,
                    error = null
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadGrowthTimeline() {
        viewModelScope.launch {
            val timeline = twinService.getGrowthTimeline(birdId)
            _uiState.update { it.copy(growthTimeline = timeline ?: emptyList()) }
        }
    }

    fun recordWeight(weightGrams: Int) {
        val twin = _uiState.value.twin ?: return
        viewModelScope.launch {
            twinService.recordWeight(birdId, twin.ownerId, weightGrams)
            loadDashboard() // Refresh
        }
    }

    fun recordFightResult(won: Boolean, opponentName: String? = null) {
        val twin = _uiState.value.twin ?: return
        viewModelScope.launch {
            twinService.recordFightResult(birdId, twin.ownerId, won, opponentName)
            loadDashboard()
        }
    }

    fun recordHealthEvent(eventType: String, title: String, description: String? = null) {
        val twin = _uiState.value.twin ?: return
        viewModelScope.launch {
            twinService.recordHealthEvent(birdId, twin.ownerId, eventType, title, description)
            loadDashboard()
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
