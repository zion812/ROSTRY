package com.rio.rostry.ui.upgrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.RolePreferenceStorage
import com.rio.rostry.utils.Resource
import com.rio.rostry.notifications.VerificationNotificationService
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RoleUpgradeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val rbacGuard: RbacGuard,
    private val currentUserProvider: CurrentUserProvider,
    private val rolePreferenceStorage: RolePreferenceStorage,
    private val auditLogDao: AuditLogDao,
    private val verificationNotificationService: VerificationNotificationService,
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
        object NavigateToVerification : UiEvent()
        data class ShowUpgradeSuggestion(val role: UserType, val missingPrerequisites: List<String>) : UiEvent()
    }

    data class UiState(
        val currentStep: WizardStep = WizardStep.CURRENT_ROLE,
        val currentRole: UserType? = null,
        val targetRole: UserType? = null,
        val user: UserEntity? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val validationErrors: Map<String, String> = emptyMap(),
        val canProceed: Boolean = false,
        val isUpgrading: Boolean = false,
        val eligibleUpgrades: List<UserType> = emptyList(),
        val upgradeSuggestions: Map<UserType, List<String>> = emptyMap()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private var cachedValidations: MutableMap<UserType, Map<String, String>> = mutableMapOf()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        _uiState.value = _uiState.value.copy(
                            user = user,
                            currentRole = user?.userType,
                            isLoading = false
                        )
                        // Auto-check for eligible upgrades on profile load
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
        _uiState.value = _uiState.value.copy(targetRole = role, error = null)
        // Validate immediately on target selection (same-role guard and prereqs known so far)
        viewModelScope.launch {
            val errors = validatePrerequisitesInternal()
            _uiState.value = _uiState.value.copy(validationErrors = errors)
            updateCanProceed()
            // Track prerequisite check analytics
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
            WizardStep.CONFIRMATION -> return // Already at last step
        }

        _uiState.value = currentState.copy(
            currentStep = nextStep,
            // keep existing errors when entering PREREQUISITES; clear on other steps
            validationErrors = if (nextStep == WizardStep.PREREQUISITES) currentState.validationErrors else emptyMap(),
            error = null
        )
        if (nextStep == WizardStep.PREREQUISITES) {
            // Recompute validations on entering prerequisites step
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
            WizardStep.CURRENT_ROLE -> return // Already at first step
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

        // Check cache first
        cachedValidations[targetRole]?.let { return it }

        val errors = mutableMapOf<String, String>()

        // Same-role guard
        if (state.currentRole == targetRole) {
            errors["sameRole"] = "You are already on the selected role"
        }

        // Allowed transition guard: only permit next-level upgrades
        val allowedNext = state.currentRole?.nextLevel()
        if (state.currentRole != null) {
            if (allowedNext == null) {
                errors["transition"] = "You are already at the highest role"
            } else if (targetRole != allowedNext) {
                errors["transition"] = "You can only upgrade to ${allowedNext.displayName} from ${state.currentRole.displayName}"
            }
        }

        // Profile completeness with detailed messages
        if (user.fullName.isNullOrBlank()) {
            errors["fullName"] = "Complete your full name to upgrade to ${targetRole.name.lowercase()}"
        }
        if (user.email.isNullOrBlank()) {
            errors["email"] = "Add your email address to upgrade to ${targetRole.name.lowercase()}"
        }
        if (user.phoneNumber.isNullOrBlank()) {
            errors["phoneNumber"] = "Verify your phone number to upgrade to ${targetRole.name.lowercase()}"
        }

        // Verification required only for ENTHUSIAST
        if (targetRole == UserType.ENTHUSIAST) {
            val isVerified = rbacGuard.isVerified()
            if (!isVerified) {
                errors["verification"] = "Complete KYC verification to become an Enthusiast. Go to Profile → Verification."
            }
        }

        // Cache the result
        cachedValidations[targetRole] = errors
        return errors
    }

    private suspend fun checkEligibleUpgrades() {
        val user = _uiState.value.user ?: return
        val currentRole = user.userType
        val eligible = mutableListOf<UserType>()
        val suggestions = mutableMapOf<UserType, List<String>>()

        // Check for each possible upgrade
        UserType.values().filter { it != currentRole }.forEach { targetRole ->
            _uiState.value = _uiState.value.copy(targetRole = targetRole)
            val errors = validatePrerequisitesInternal()
            if (errors.isEmpty()) {
                eligible.add(targetRole)
            } else {
                suggestions[targetRole] = errors.values.toList()
            }
        }

        _uiState.value = _uiState.value.copy(
            eligibleUpgrades = eligible,
            upgradeSuggestions = suggestions,
            targetRole = null // Reset
        )

        // Emit proactive suggestions if any
        eligible.forEach { role ->
            _uiEvent.emit(UiEvent.ShowUpgradeSuggestion(role, emptyList()))
        }
        suggestions.forEach { (role, missing) ->
            _uiEvent.emit(UiEvent.ShowUpgradeSuggestion(role, missing))
        }
    }

    fun fixPrerequisite(field: String) {
        viewModelScope.launch {
            when (field) {
                "fullName", "email", "phoneNumber" -> {
                    _uiEvent.emit(UiEvent.NavigateToProfileEdit(field))
                }
                "verification" -> {
                    _uiEvent.emit(UiEvent.NavigateToVerification)
                }
            }
            // Track fix action analytics
            flowAnalyticsTracker.trackRoleUpgradeFixAction(field)
        }
    }

    fun performUpgrade() {
        val currentState = _uiState.value
        if (currentState.isUpgrading || currentState.targetRole == null || currentState.user == null) return

        _uiState.value = currentState.copy(isUpgrading = true, error = null)

        viewModelScope.launch {
            try {
                val userId = currentState.user.userId
                val newRole = currentState.targetRole

                // Track upgrade start analytics
                flowAnalyticsTracker.trackRoleUpgradeStarted(
                    currentState.currentRole?.name ?: "UNKNOWN",
                    newRole.name
                )

                // Update user type in repository
                val updateResult = userRepository.updateUserType(userId, newRole)
                if (updateResult is Resource.Error) {
                    _uiState.value = _uiState.value.copy(
                        isUpgrading = false,
                        error = updateResult.message ?: "Failed to update user type"
                    )
                    flowAnalyticsTracker.trackRoleUpgradeFailed(newRole.name, updateResult.message ?: "Unknown error")
                    return@launch
                }

                // Update role preference storage
                rolePreferenceStorage.persist(newRole)

                // Create audit log
                val auditLog = AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "ROLE_UPGRADE",
                    refId = userId,
                    action = "UPGRADE_TO_${newRole.name}",
                    actorUserId = userId,
                    detailsJson = """{"from":"${currentState.currentRole?.name}","to":"${newRole.name}"}""",
                    createdAt = System.currentTimeMillis()
                )
                auditLogDao.insert(auditLog)

                // Emit success - perhaps navigate or show success message
                _uiState.value = _uiState.value.copy(
                    isUpgrading = false,
                    currentRole = newRole,
                    error = null
                )

                // Track successful upgrade analytics
                flowAnalyticsTracker.trackRoleUpgradeCompleted(newRole.name)

                // Clear cache after upgrade
                cachedValidations.clear()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpgrading = false,
                    error = e.message ?: "Unknown error occurred"
                )
                flowAnalyticsTracker.trackRoleUpgradeFailed(currentState.targetRole?.name ?: "UNKNOWN", e.message ?: "Unknown error")
            }
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
