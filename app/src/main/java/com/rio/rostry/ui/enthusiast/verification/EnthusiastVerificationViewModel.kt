package com.rio.rostry.ui.enthusiast.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.EnthusiastVerificationRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnthusiastVerificationViewModel @Inject constructor(
    private val verificationRepository: EnthusiastVerificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnthusiastVerificationUiState())
    val uiState: StateFlow<EnthusiastVerificationUiState> = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EnthusiastVerificationUiState()
    )

    init {
        loadVerificationStatus()
    }

    private fun loadVerificationStatus() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUserSuspend()
            if (user != null) {
                verificationRepository.getVerificationStatus(user.userId).collect { verification ->
                    val status = verification?.status ?: user.verificationStatus
                    _uiState.update { it.copy(
                        currentStatus = status,
                        rejectionReason = verification?.rejectionReason ?: user.verificationRejectionReason
                    ) }
                }
            }
        }
    }

    fun onExperienceYearsChanged(years: Int) {
        _uiState.update { it.copy(experienceYears = years) }
    }

    fun onBirdCountChanged(count: Int) {
        _uiState.update { it.copy(birdCount = count) }
    }

    fun onSpecializationToggled(specialization: String) {
        _uiState.update { 
            val newSpecs = it.specializations.toMutableList()
            if (newSpecs.contains(specialization)) {
                newSpecs.remove(specialization)
            } else {
                newSpecs.add(specialization)
            }
            it.copy(specializations = newSpecs)
        }
    }

    fun onAchievementsChanged(achievements: String) {
        _uiState.update { it.copy(achievementsDescription = achievements) }
    }

    fun onReferenceAdded(reference: String) {
        if (reference.isBlank()) return
        _uiState.update { 
            val newRefs = it.referenceContacts.toMutableList()
            newRefs.add(reference)
            it.copy(referenceContacts = newRefs) 
        }
    }

    fun onReferenceRemoved(reference: String) {
        _uiState.update { 
            val newRefs = it.referenceContacts.toMutableList()
            newRefs.remove(reference)
            it.copy(referenceContacts = newRefs) 
        }
    }



    fun submitVerification() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = userRepository.getCurrentUserSuspend()
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not found") }
                return@launch
            }

            if (!validateForm()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val state = _uiState.value
            val result = verificationRepository.submitVerification(
                userId = user.userId,
                experienceYears = state.experienceYears,
                birdCount = state.birdCount,
                specializations = state.specializations,
                achievementsDescription = state.achievementsDescription,
                referenceContacts = state.referenceContacts,
                documentUris = emptyList(),
                profilePhotoUri = null,
                farmPhotoUris = emptyList()
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSubmitted = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Submission failed") }
            }
        }
    }

    private fun validateForm(): Boolean {
        val s = _uiState.value
        val errors = mutableListOf<String>()
        
        if (s.experienceYears < 1) errors.add("Experience years required")
        if (s.birdCount < 1) errors.add("Bird count required")
        if (s.specializations.isEmpty()) errors.add("Select at least one specialization")

        
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(error = errors.joinToString(". ")) }
            return false
        }
        return true
    }
}

data class EnthusiastVerificationUiState(
    val currentStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val experienceYears: Int = 0,
    val birdCount: Int = 0,
    val specializations: List<String> = emptyList(),
    val achievementsDescription: String = "",
    val referenceContacts: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    val rejectionReason: String? = null
)
