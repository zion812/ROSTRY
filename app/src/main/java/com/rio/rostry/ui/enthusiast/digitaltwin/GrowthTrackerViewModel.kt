package com.rio.rostry.ui.enthusiast.digitaltwin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BirdEventDao
import com.rio.rostry.data.database.dao.DigitalTwinDao
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import com.rio.rostry.domain.digitaltwin.DigitalTwinService
import com.rio.rostry.domain.digitaltwin.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeightRecord(
    val ageDays: Int,
    val weightGrams: Int,
    val date: Long,
    val ratingLabel: String,
    val ratingEmoji: String,
    val deviationPercent: Int
)

data class GrowthTrackerState(
    val twin: DigitalTwinEntity? = null,
    val ageProfile: AgeProfile? = null,

    // Growth curve data
    val idealCurve: List<WeightExpectation> = emptyList(),
    val actualWeights: List<WeightRecord> = emptyList(),

    // Current status
    val currentEvaluation: WeightEvaluation? = null,
    val prediction: WeightPrediction? = null,

    // Growth timeline (visual evolution snapshots)
    val growthTimeline: List<GrowthSnapshot> = emptyList(),

    // Input state
    val weightInputGrams: String = "",
    val isWeightDialogOpen: Boolean = false,
    val isSavingWeight: Boolean = false,
    val savedMessage: String? = null,

    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class GrowthTrackerViewModel @Inject constructor(
    private val twinDao: DigitalTwinDao,
    private val eventDao: BirdEventDao,
    private val twinService: DigitalTwinService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthTrackerState())
    val uiState: StateFlow<GrowthTrackerState> = _uiState.asStateFlow()

    private val birdId: String = savedStateHandle.get<String>("birdId") ?: ""

    init {
        if (birdId.isNotBlank()) {
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val twin = twinDao.getByBirdId(birdId)
                if (twin == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Bird not found") }
                    return@launch
                }

                val ageDays = twin.ageDays ?: 0
                val isMale = twin.gender?.equals("MALE", true) ?: true
                val ageProfile = AgeProfile.fromDays(ageDays, isMale)

                // Generate ideal growth curve
                val maxDays = (ageDays * 1.5).toInt().coerceAtLeast(365)
                val idealCurve = GrowthCurve.generateCurve(isMale, maxDays, stepDays = 7)

                // Get actual weight recordings
                val weightEvents = eventDao.getRecentEvents(birdId, 1000)
                    .filter { it.eventType == BirdEventEntity.TYPE_WEIGHT_RECORDED }
                    .sortedBy { it.eventDate }

                val actualWeights = weightEvents.mapNotNull { event ->
                    val grams = event.numericValue?.toInt() ?: return@mapNotNull null
                    val eventAgeDays = event.ageDaysAtEvent ?: 0
                    val eval = GrowthCurve.evaluateWeight(eventAgeDays, grams, isMale)
                    WeightRecord(
                        ageDays = eventAgeDays,
                        weightGrams = grams,
                        date = event.eventDate,
                        ratingLabel = eval.rating.displayName,
                        ratingEmoji = eval.rating.emoji,
                        deviationPercent = eval.deviationPercent
                    )
                }

                // Current evaluation
                val currentWeight = twin.weightKg?.let { (it * 1000).toInt() }
                val currentEval = currentWeight?.let {
                    GrowthCurve.evaluateWeight(ageDays, it, isMale)
                }

                // Prediction (if we have >= 2 data points)
                val prediction = if (actualWeights.size >= 2) {
                    val recent = actualWeights.takeLast(2)
                    val targetDays = ageDays + 30 // Predict 30 days ahead
                    GrowthCurve.predictFutureWeight(
                        currentAgeDays = recent[1].ageDays,
                        currentWeightGrams = recent[1].weightGrams,
                        previousWeightGrams = recent[0].weightGrams,
                        previousAgeDays = recent[0].ageDays,
                        targetAgeDays = targetDays,
                        isMale = isMale
                    )
                } else null

                // Growth timeline (visual evolution)
                val timeline = twinService.getGrowthTimeline(birdId) ?: emptyList()

                _uiState.update { it.copy(
                    twin = twin,
                    ageProfile = ageProfile,
                    idealCurve = idealCurve,
                    actualWeights = actualWeights,
                    currentEvaluation = currentEval,
                    prediction = prediction,
                    growthTimeline = timeline,
                    isLoading = false,
                    error = null
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun openWeightDialog() {
        _uiState.update { it.copy(isWeightDialogOpen = true, weightInputGrams = "", savedMessage = null) }
    }

    fun closeWeightDialog() {
        _uiState.update { it.copy(isWeightDialogOpen = false) }
    }

    fun setWeightInput(value: String) {
        _uiState.update { it.copy(weightInputGrams = value) }
    }

    fun saveWeight() {
        val grams = _uiState.value.weightInputGrams.toIntOrNull() ?: return
        if (grams <= 0 || grams > 10000) return

        val twin = _uiState.value.twin ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingWeight = true) }

            twinService.recordWeight(birdId, twin.ownerId, grams)

            _uiState.update { it.copy(
                isSavingWeight = false,
                isWeightDialogOpen = false,
                savedMessage = "Weight recorded: ${grams}g ✅"
            ) }

            // Refresh data
            loadData()
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(savedMessage = null) }
    }
}
