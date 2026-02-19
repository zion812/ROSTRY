package com.rio.rostry.ui.enthusiast.digitalfarm.grading

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.digitaltwin.DigitalTwinService
import com.rio.rostry.domain.digitaltwin.ManualMorphologyGrades
import com.rio.rostry.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GradingUiState(
    val isLoading: Boolean = true,
    val birdId: String = "",
    val birdName: String = "",
    val breed: String = "Aseel",
    val grades: ManualMorphologyGrades = ManualMorphologyGrades(
        beakType = "STRAIGHT",
        eyeColor = "YELLOW",
        legColor = "YELLOW",
        tailCarry = "LEVEL",
        bodyStructureScore = 5,
        plumageQualityScore = 5,
        stanceScore = 5,
        hasWryTail = false,
        hasSplitWing = false,
        hasCrookedToes = false
    ),
    val isSaving: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MorphologyGradingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val digitalTwinService: DigitalTwinService
) : ViewModel() {

    private val _uiState = MutableStateFlow(GradingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBird()
    }

    private fun loadBird() {
        val birdId = savedStateHandle.get<String>("birdId") ?: return
        
        viewModelScope.launch {
            val twin = digitalTwinService.getTwin(birdId) ?: return@launch
            
            // Try to parse existing grades
            var existingGrades = _uiState.value.grades
            try {
                if (twin.metadataJson.isNotEmpty()) {
                    val json = org.json.JSONObject(twin.metadataJson)
                    if (json.has("manualGrades")) {
                        val g = json.getJSONObject("manualGrades")
                        existingGrades = ManualMorphologyGrades(
                            beakType = g.optString("beakType", "STRAIGHT"),
                            eyeColor = g.optString("eyeColor", "YELLOW"),
                            legColor = g.optString("legColor", "YELLOW"),
                            tailCarry = g.optString("tailCarry", "LEVEL"),
                            bodyStructureScore = g.optInt("bodyStructureScore", 5),
                            plumageQualityScore = g.optInt("plumageQualityScore", 5),
                            stanceScore = g.optInt("stanceScore", 5),
                            hasWryTail = g.optBoolean("hasWryTail", false),
                            hasSplitWing = g.optBoolean("hasSplitWing", false),
                            hasCrookedToes = g.optBoolean("hasCrookedToes", false)
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignore parse errors, start fresh
            }

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    birdId = birdId,
                    birdName = twin.birdName ?: "Unknown",
                    breed = twin.baseBreed,
                    grades = existingGrades
                ) 
            }
        }
    }

    fun updateGrades(newGrades: ManualMorphologyGrades) {
        _uiState.update { it.copy(grades = newGrades) }
    }
    
    fun updateBeak(type: String) {
        _uiState.update { it.copy(grades = it.grades.copy(beakType = type)) }
    }

    fun updateEye(color: String) {
        _uiState.update { it.copy(grades = it.grades.copy(eyeColor = color)) }
    }

    fun updateLegColor(color: String) {
        _uiState.update { it.copy(grades = it.grades.copy(legColor = color)) }
    }
    
    fun updateTailCarry(carry: String) {
        _uiState.update { it.copy(grades = it.grades.copy(tailCarry = carry)) }
    }
    
    fun updateBodyScore(score: Float) {
        _uiState.update { it.copy(grades = it.grades.copy(bodyStructureScore = score.toInt())) }
    }
    
    fun updatePlumageScore(score: Float) {
        _uiState.update { it.copy(grades = it.grades.copy(plumageQualityScore = score.toInt())) }
    }
    
    fun updateStanceScore(score: Float) {
        _uiState.update { it.copy(grades = it.grades.copy(stanceScore = score.toInt())) }
    }
    
    fun toggleWryTail(checked: Boolean) {
        _uiState.update { it.copy(grades = it.grades.copy(hasWryTail = checked)) }
    }
    
    fun toggleSplitWing(checked: Boolean) {
        _uiState.update { it.copy(grades = it.grades.copy(hasSplitWing = checked)) }
    }
    
    fun toggleCrookedToes(checked: Boolean) {
        _uiState.update { it.copy(grades = it.grades.copy(hasCrookedToes = checked)) }
    }

    fun submitGrading() {
        val birdId = _uiState.value.birdId
        if (birdId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                digitalTwinService.submitManualGrading(birdId, _uiState.value.grades)
                _uiState.update { it.copy(isSaving = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Unknown error occurred") }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
