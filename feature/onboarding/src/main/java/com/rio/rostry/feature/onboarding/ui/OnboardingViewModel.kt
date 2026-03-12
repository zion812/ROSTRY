package com.rio.rostry.feature.onboarding.ui
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.rio.rostry.domain.model.UserType

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    // TODO: SessionManager and AnalyticsRepository need to be extracted to core/domain interfaces
) : ViewModel() {

    data class Questionnaire(
        val userType: UserType? = null,
        val goals: List<String> = emptyList(),
        val experienceLevel: String? = null,
        val interests: List<String> = emptyList()
    )

    data class UiState(
        val step: Int = 0,
        val totalSteps: Int = 5,
        val questionnaire: Questionnaire = Questionnaire(),
        val canSkip: Boolean = true,
        val completed: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun start(role: UserType?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(questionnaire = _state.value.questionnaire.copy(userType = role))
        }
    }

    fun updateGoals(goals: List<String>) {
        _state.value = _state.value.copy(questionnaire = _state.value.questionnaire.copy(goals = goals))
    }

    fun updateExperience(level: String) {
        _state.value = _state.value.copy(questionnaire = _state.value.questionnaire.copy(experienceLevel = level))
    }

    fun updateInterests(interests: List<String>) {
        _state.value = _state.value.copy(questionnaire = _state.value.questionnaire.copy(interests = interests))
    }

    fun nextStep() {
        val s = _state.value
        if (s.step + 1 < s.totalSteps) {
            _state.value = s.copy(step = s.step + 1)
        } else {
            complete()
        }
    }

    fun prevStep() {
        val s = _state.value
        if (s.step > 0) _state.value = s.copy(step = s.step - 1)
    }

    fun skip() {
        if (_state.value.canSkip) nextStep()
    }

    fun complete() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(completed = true)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(error = t.message)
            }
        }
    }
}
