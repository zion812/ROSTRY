package com.rio.rostry.ui.auth

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthEvent
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.normalizeToE164India
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val phoneInput: String = "",
        val e164: String? = null,
        val otp: String = "",
        val verificationId: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val resendCooldownSec: Int = 0
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _navigation = MutableSharedFlow<NavAction>(extraBufferCapacity = 8)
    val navigation = _navigation.asSharedFlow()

    sealed class NavAction {
        data class ToOtp(val verificationId: String): NavAction()
        data class ToHome(val userType: UserType): NavAction()
    }

    private var cooldownJob: kotlinx.coroutines.Job? = null

    init {
        // Restore verificationId/phone from SavedStateHandle if present
        val restoredVerificationId: String? = savedStateHandle.get<String>("verificationId")
        val restoredPhone: String? = savedStateHandle.get<String>("phoneE164")
        _uiState.value = _uiState.value.copy(
            verificationId = restoredVerificationId,
            e164 = restoredPhone
        )
        viewModelScope.launch {
            authRepository.events.collectLatest { event ->
                when (event) {
                    is AuthEvent.CodeSent -> {
                        savedStateHandle["verificationId"] = event.verificationId
                        _uiState.value = _uiState.value.copy(verificationId = event.verificationId, isLoading = false)
                        startResendCooldown()
                        _navigation.tryEmit(NavAction.ToOtp(event.verificationId))
                    }
                    is AuthEvent.AutoVerified -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        postAuthBootstrapAndNavigate()
                    }
                    is AuthEvent.VerificationFailed -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = event.message)
                    }
                }
            }
        }
    }

    fun onPhoneChanged(input: String) {
        val e164 = normalizeToE164India(input)
        _uiState.value = _uiState.value.copy(phoneInput = input, e164 = e164)
        savedStateHandle["phoneE164"] = e164
    }

    fun onOtpChanged(code: String) {
        _uiState.value = _uiState.value.copy(otp = code)
    }

    fun startVerification(activity: Activity) {
        val normalized = _uiState.value.e164
        if (normalized == null) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid Indian number")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.startPhoneVerification(activity, normalized)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                else -> Unit
            }
        }
    }

    fun verifyOtpAndSignIn() {
        val verificationId = _uiState.value.verificationId ?: return
        val code = _uiState.value.otp
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid 6-digit OTP")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.verifyOtp(verificationId, code)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    postAuthBootstrapAndNavigate()
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                else -> Unit
            }
        }
    }

    fun resendOtp(activity: Activity) {
        if (_uiState.value.resendCooldownSec > 0) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.resendVerificationCode(activity)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                else -> _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun startResendCooldown(seconds: Int = 60) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining >= 0) {
                _uiState.value = _uiState.value.copy(resendCooldownSec = remaining)
                kotlinx.coroutines.delay(1000)
                remaining--
            }
        }
    }

    private fun postAuthBootstrapAndNavigate() {
        viewModelScope.launch {
            // Ensure user profile exists and cached
            userRepository.getCurrentUser().collectLatest { resource ->
                if (resource is Resource.Success) {
                    // Mark session start with role if available
                    val user = resource.data
                    if (user != null) {
                        sessionManager.markAuthenticated(System.currentTimeMillis(), user.userType)
                    }
                    val role = user?.userType ?: UserType.GENERAL
                    _navigation.tryEmit(NavAction.ToHome(role))
                } else if (resource is Resource.Error) {
                    _uiState.value = _uiState.value.copy(error = resource.message)
                }
            }
        }
    }
}
