package com.rio.rostry.presentation.auth.linking

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.usecase.LinkPhoneUseCase
import com.rio.rostry.utils.normalizeToE164
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for phone linking screen.
 * Used when user signs in with Google/Email and needs to add phone.
 * 
 * Responsibilities:
 * - Phone number input
 * - Start phone linking
 * - Verify OTP and link
 * - Managing UI state
 * 
 * Does NOT handle:
 * - Initial authentication (see PhoneAuthViewModel)
 * - Navigation (see AuthCoordinator)
 */
@HiltViewModel
class PhoneLinkingViewModel @Inject constructor(
    private val linkPhoneUseCase: LinkPhoneUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PhoneLinkingUiState())
    val uiState: StateFlow<PhoneLinkingUiState> = _uiState.asStateFlow()
    
    private var cooldownJob: Job? = null
    
    /**
     * Update phone number input
     */
    fun onPhoneChanged(input: String) {
        val e164 = normalizeToE164(input)
        _uiState.value = _uiState.value.copy(
            phoneInput = input,
            phoneE164 = e164,
            error = null
        )
    }
    
    /**
     * Update OTP input
     */
    fun onOtpChanged(otp: String) {
        val filtered = otp.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(
            otp = filtered,
            error = null
        )
        
        // Auto-verify when 6 digits entered
        if (filtered.length == 6 && _uiState.value.verificationId != null) {
            verifyAndLink()
        }
    }
    
    /**
     * Start phone linking process
     */
    fun startLinking(activity: Activity) {
        val e164 = _uiState.value.phoneE164
        if (e164 == null) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a valid phone number (E.164, e.g. +1234567890)"
            )
            return
        }
        
        val phoneNumber = try {
            PhoneNumber(e164)
        } catch (e: IllegalArgumentException) {
            _uiState.value = _uiState.value.copy(
                error = "Invalid phone number format"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            Timber.d("Starting phone linking for ${phoneNumber.masked()}")
            
            when (val result = linkPhoneUseCase.startLinking(activity, phoneNumber)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        verificationId = result.data,
                        step = PhoneLinkingStep.OTP_ENTRY,
                        error = null
                    )
                    startResendCooldown()
                    Timber.d("Phone linking OTP sent")
                }
                
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error.getMessage()
                    )
                    Timber.e("Failed to start phone linking: ${result.error.getTitle()}")
                }
                
                AuthResult.Loading -> {}
            }
        }
    }
    
    /**
     * Verify OTP and complete linking
     */
    fun verifyAndLink() {
        val verificationId = _uiState.value.verificationId
        if (verificationId == null) {
            _uiState.value = _uiState.value.copy(
                error = "Verification session expired"
            )
            return
        }
        
        val otpString = _uiState.value.otp
        if (otpString.length != 6) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a valid 6-digit code"
            )
            return
        }
        
        val otpCode = try {
            OtpCode(otpString)
        } catch (e: IllegalArgumentException) {
            _uiState.value = _uiState.value.copy(
                error = "Invalid OTP format"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            Timber.d("Verifying OTP and linking phone")
            
            when (val result = linkPhoneUseCase.completeLinking(verificationId, otpCode)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLinked = true,
                        error = null
                    )
                    Timber.d("Phone linked successfully")
                }
                
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error.getMessage(),
                        otp = "" // Clear OTP on error
                    )
                    Timber.e("Failed to link phone: ${result.error.getTitle()}")
                }
                
                AuthResult.Loading -> {}
            }
        }
    }
    
    /**
     * Start resend cooldown
     */
    private fun startResendCooldown(seconds: Int = 60) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                _uiState.value = _uiState.value.copy(
                    resendCooldownSeconds = remaining
                )
                delay(1000)
                remaining--
            }
            _uiState.value = _uiState.value.copy(
                resendCooldownSeconds = 0
            )
        }
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        cooldownJob?.cancel()
    }
}

/**
 * UI state for phone linking
 */
data class PhoneLinkingUiState(
    val phoneInput: String = "",
    val phoneE164: String? = null,
    val otp: String = "",
    val verificationId: VerificationId? = null,
    val step: PhoneLinkingStep = PhoneLinkingStep.PHONE_ENTRY,
    val isLoading: Boolean = false,
    val isLinked: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val error: String? = null
)

/**
 * Steps in phone linking flow
 */
enum class PhoneLinkingStep {
    PHONE_ENTRY,
    OTP_ENTRY
}
