package com.rio.rostry.ui.upgrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleUpgradeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val rbacGuard: RbacGuard,
    private val flowAnalyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    enum class WizardStep {
        CURRENT_ROLE,
        BENEFITS,
        PREREQUISITES,
        CONFIRMATION
    }

    sealed class UiEvent {
        data class NavigateToProfileEdit(val field: String) : UiEvent()
        data class NavigateToVerification(val upgradeType: UpgradeType) : UiEvent()
        data class ShowUpgradeSuggestion(val role: UserType, val missingPrerequisites: List<String>) : UiEvent()
    }

    data class UiState(
        val currentStep: WizardStep = WizardStep.CURRENT_ROLE,
        val currentRole: UserType? = null,
        val targetRole: UserType? = null,
        val upgradeType: UpgradeType? = null,
        val user: UserEntity? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val validationErrors: Map<String, String> = emptyMap(),
        val canProceed: Boolean = false,
        val isUpgrading: Boolean = false,
        val eligibleUpgrades: List<UserType> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        _uiState.value = _uiState.value.copy(
                            user = user,
                            currentRole = user?.role,
                            isLoading = false
                        )
                        checkEligibleUpgrades()
                        updateCanProceed()
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = resource.message,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun setTargetRole(role: UserType) {
        val currentRole = _uiState.value.currentRole
        val upgradeType = when {
            currentRole == UserType.GENERAL && role == UserType.FARMER -> UpgradeType.GENERAL_TO_FARMER
            currentRole == UserType.FARMER && role == UserType.ENTHUSIAST -> UpgradeType.FARMER_TO_ENTHUSIAST
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            targetRole = role, 
            upgradeType = upgradeType,
            error = null
        )
        
        viewModelScope.launch {
            val errors = validatePrerequisitesInternal()
            _uiState.value = _uiState.value.copy(validationErrors = errors)
            updateCanProceed()
            flowAnalyticsTracker.trackRoleUpgradePrerequisiteCheck(role.name, errors.isEmpty(), errors.keys)
        }
    }

    fun nextStep() {
        val currentState = _uiState.value
        if (!currentState.canProceed) return

        val nextStep = when (currentState.currentStep) {
            WizardStep.CURRENT_ROLE -> WizardStep.BENEFITS
            WizardStep.BENEFITS -> WizardStep.PREREQUISITES
            WizardStep.PREREQUISITES -> WizardStep.CONFIRMATION
            WizardStep.CONFIRMATION -> return
        }

        _uiState.value = currentState.copy(
            currentStep = nextStep,
            validationErrors = if (nextStep == WizardStep.PREREQUISITES) currentState.validationErrors else emptyMap(),
            error = null
        )
        
        if (nextStep == WizardStep.PREREQUISITES) {
            viewModelScope.launch {
                val errors = validatePrerequisitesInternal()
                _uiState.value = _uiState.value.copy(validationErrors = errors)
                updateCanProceed()
            }
        } else {
            updateCanProceed()
        }
    }

    fun previousStep() {
        val currentState = _uiState.value
        val previousStep = when (currentState.currentStep) {
            WizardStep.CURRENT_ROLE -> return
            WizardStep.BENEFITS -> WizardStep.CURRENT_ROLE
            WizardStep.PREREQUISITES -> WizardStep.BENEFITS
            WizardStep.CONFIRMATION -> WizardStep.PREREQUISITES
        }

        _uiState.value = currentState.copy(
            currentStep = previousStep,
            validationErrors = emptyMap(),
            error = null
        )
        updateCanProceed()
    }

    private suspend fun validatePrerequisitesInternal(): Map<String, String> {
        val state = _uiState.value
        val user = state.user ?: return mapOf("user" to "User not found")
        val targetRole = state.targetRole ?: return mapOf("targetRole" to "Target role not set")
        val upgradeType = state.upgradeType

        val errors = mutableMapOf<String, String>()

        if (state.currentRole == targetRole) {
            errors["sameRole"] = "You are already on the selected role"
        }

        if (user.fullName.isNullOrBlank()) {
            errors["fullName"] = "Complete your full name"
        }
        if (user.email.isNullOrBlank()) {
            errors["email"] = "Add your email address"
        }
        if (user.phoneNumber.isNullOrBlank()) {
            errors["phoneNumber"] = "Verify your phone number"
        }

        // For upgrades that require verification, we don't block here unless they are ALREADY rejected or something?
        // Actually, the "Prerequisites" step should probably check if they have basic profile info.
        // The "Verification" step comes AFTER Confirmation.
        
        // If they are already pending, maybe warn them?
        if (user.verificationStatus == VerificationStatus.PENDING) {
             // errors["pending"] = "You have a pending verification request."
             // Or maybe we allow them to continue to check status?
        }

        return errors
    }

    private fun checkEligibleUpgrades() {
        val user = _uiState.value.user ?: return
        val currentRole = user.role
        val eligible = mutableListOf<UserType>()

        // Simple logic: Can always try to upgrade to next level if not there yet
        val next = currentRole.nextLevel()
        if (next != null) {
            eligible.add(next)
        }
        
        _uiState.value = _uiState.value.copy(eligibleUpgrades = eligible)
    }

    fun fixPrerequisite(field: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.NavigateToProfileEdit(field))
            flowAnalyticsTracker.trackRoleUpgradeFixAction(field)
        }
    }

    fun performUpgrade() {
        val currentState = _uiState.value
        val upgradeType = currentState.upgradeType ?: return
        
        viewModelScope.launch {
            // Check RBAC
            if (!rbacGuard.canRequestUpgrade(upgradeType)) {
                 _uiState.value = currentState.copy(error = "Complete farmer verification before upgrading to Enthusiast")
                 return@launch
            }
            
            // Navigate to verification flow
            _uiEvent.emit(UiEvent.NavigateToVerification(upgradeType))
        }
    }

    private fun updateCanProceed() {
        val state = _uiState.value
        val canProceed = when (state.currentStep) {
            WizardStep.CURRENT_ROLE -> state.targetRole != null && state.targetRole != state.currentRole
            WizardStep.BENEFITS -> true
            WizardStep.PREREQUISITES -> state.validationErrors.isEmpty()
            WizardStep.CONFIRMATION -> true
        }
        _uiState.value = state.copy(canProceed = canProceed)
    }
}
