package com.rio.rostry.ui.onboarding

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class OnboardingUiState(
        val fullName: String = "",
        val selectedUserType: UserType? = null,
        val address: String = "",
        val farmLocationLat: Double? = null,
        val farmLocationLng: Double? = null,
        val kycDocumentUri: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val validationErrors: Map<String, String> = emptyMap()
    )

    data class ProfileDraft(
        val fullName: String,
        val userType: UserType,
        val address: String?,
        val farmLocationLat: Double?,
        val farmLocationLng: Double?,
        val kycDocumentUri: String?
    )

    sealed class Nav {
        data class ToHome(val role: UserType) : Nav()
    }

    private val _ui = MutableStateFlow(OnboardingUiState())
    val ui: StateFlow<OnboardingUiState> = _ui

    private val _nav = MutableSharedFlow<Nav>(extraBufferCapacity = 4)
    val nav = _nav.asSharedFlow()

    private var currentUser: UserEntity? = null

    init {
        // Preload current user to get userId/phone/email if present
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { res ->
                if (res is Resource.Success) {
                    currentUser = res.data
                    // Preselect role if available
                    val preRole = res.data?.userType
                    _ui.value = _ui.value.copy(selectedUserType = preRole)
                    // Prefill name/address if present
                    _ui.value = _ui.value.copy(
                        fullName = res.data?.fullName.orEmpty(),
                        address = res.data?.address.orEmpty(),
                        farmLocationLat = res.data?.farmLocationLat,
                        farmLocationLng = res.data?.farmLocationLng
                    )
                }
            }
        }
    }

    fun selectUserType(type: UserType) {
        Log.d("RostryOnboarding", "selectUserType: $type")
        _ui.value = _ui.value.copy(selectedUserType = type, validationErrors = emptyMap())
    }

    fun updateFullName(name: String) {
        _ui.value = _ui.value.copy(fullName = name)
    }

    fun updateAddress(addr: String) {
        _ui.value = _ui.value.copy(address = addr)
    }

    fun setFarmLocation(lat: Double, lng: Double) {
        _ui.value = _ui.value.copy(farmLocationLat = lat, farmLocationLng = lng)
    }

    fun setKycDocument(uri: String?) {
        _ui.value = _ui.value.copy(kycDocumentUri = uri)
    }

    fun setValidationError(key: String, message: String?) {
        val current = _ui.value.validationErrors.toMutableMap()
        if (message.isNullOrBlank()) current.remove(key) else current[key] = message
        _ui.value = _ui.value.copy(validationErrors = current)
    }

    private fun validateName(): String? = if (_ui.value.fullName.trim().isEmpty()) "Name is required" else null

    private fun validateAddress(required: Boolean): String? {
        if (!required) return null
        return if (_ui.value.address.trim().isEmpty()) "Address is required" else null
    }

    private fun validateRoleSpecificFields(role: UserType?): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        when (role) {
            UserType.FARMER -> {
                if (_ui.value.farmLocationLat == null || _ui.value.farmLocationLng == null) {
                    errors["farmLocation"] = "Farm location is required"
                }
                if (_ui.value.address.isBlank()) {
                    errors["address"] = "Address is required for farmers"
                }
            }
            UserType.ENTHUSIAST -> {
                if (_ui.value.address.isBlank()) {
                    errors["address"] = "Address is required for enthusiasts"
                }
            }
            UserType.GENERAL, null -> {
                // No extra requirements
            }
        }
        return errors
    }

    private fun buildDraft(): ProfileDraft? {
        val role = _ui.value.selectedUserType ?: return null
        return ProfileDraft(
            fullName = _ui.value.fullName.trim(),
            userType = role,
            address = _ui.value.address.trim().ifEmpty { null },
            farmLocationLat = _ui.value.farmLocationLat,
            farmLocationLng = _ui.value.farmLocationLng,
            kycDocumentUri = _ui.value.kycDocumentUri
        )
    }

    fun updateProfile() {
        completeOnboarding()
    }

    fun completeOnboarding() {
        val role = _ui.value.selectedUserType
        val nameError = validateName()
        val addressError = validateAddress(
            required = role == UserType.FARMER || role == UserType.ENTHUSIAST
        )
        val roleErrors = validateRoleSpecificFields(role)
        val allErrors = mutableMapOf<String, String>().apply {
            nameError?.let { this["fullName"] = it }
            addressError?.let { this["address"] = it }
            putAll(roleErrors)
        }
        if (allErrors.isNotEmpty() || role == null) {
            Log.d("RostryOnboarding", "validation failed: role=$role errors=$allErrors")
            _ui.value = _ui.value.copy(validationErrors = allErrors, error = if (role == null) "Please select a role" else null)
            return
        }
        val draft = buildDraft() ?: return
        val base = currentUser
        if (base == null) {
            Log.w("RostryOnboarding", "currentUser is null; cannot complete onboarding")
            _ui.value = _ui.value.copy(error = "User not loaded; please try again")
            return
        }
        val entity = base.copy(
            fullName = draft.fullName,
            userType = draft.userType,
            address = draft.address,
            farmLocationLat = draft.farmLocationLat,
            farmLocationLng = draft.farmLocationLng,
            updatedAt = System.currentTimeMillis()
        )
        Log.d("RostryOnboarding", "submitting profile update for userId=${entity.userId} role=${entity.userType}")
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            when (val res = userRepository.updateUserProfile(entity)) {
                is Resource.Success -> {
                    // Preserve current auth mode (DEMO vs FIREBASE) and demo user id if any
                    val mode = sessionManager.authMode().first()
                    val demoId = sessionManager.currentDemoUserId().first()
                    Log.d("RostryOnboarding", "profile update success; marking session mode=$mode demoId=$demoId; navigating home")
                    sessionManager.markAuthenticated(
                        System.currentTimeMillis(),
                        entity.userType,
                        mode = mode,
                        demoUserId = demoId
                    )
                    _ui.value = _ui.value.copy(isLoading = false)
                    _nav.tryEmit(Nav.ToHome(entity.userType))
                }
                is Resource.Error -> {
                    Log.e("RostryOnboarding", "profile update error: ${res.message}")
                    _ui.value = _ui.value.copy(isLoading = false, error = res.message)
                }
                else -> Unit
            }
        }
    }
}
