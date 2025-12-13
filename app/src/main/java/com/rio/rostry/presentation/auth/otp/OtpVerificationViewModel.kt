package com.rio.rostry.presentation.auth.otp

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.usecase.ResendOtpUseCase
import com.rio.rostry.domain.auth.usecase.VerifyOtpUseCase
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
 * ViewModel for OTP verification screen.
 * 
 * Responsibilities:
 * - OTP input validation
 * - OTP verification
 * - Resend OTP with cooldown
 * - Managing UI state
 * 
 * Does NOT handle:
 * - Phone number input (see PhoneAuthViewModel)
 * - Navigation (see AuthCoordinator)
 */
@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val verificationIdString: String =
        savedStateHandle.get<String>("verificationId")
            ?: throw IllegalArgumentException("VerificationId is required")

    private val verificationId = VerificationId(verificationIdString)

    private val _uiState = MutableStateFlow(OtpVerificationUiState())
    val uiState: StateFlow<OtpVerificationUiState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null
    
    /**
     * Update OTP input
     */
    fun onOtpChanged(otp: String) {
        // Only allow digits, max 6
        val filtered = otp.filter { it.isDigit() }.take(6)
        
        _uiState.value = _uiState.value.copy(
            otp = filtered,
            error = null // Clear error on new input
        )
        
        // Auto-verify when 6 digits entered
        if (filtered.length == 6) {
            verifyOtp()
        }
    }
    
    /**
     * Verify entered OTP
     */
    fun verifyOtp() {
        val otpString = _uiState.value.otp
        
        // Validate OTP format
        if (otpString.length != 6 || !otpString.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a valid 6-digit code"
            )
            return
        }
        
        // Create OtpCode value class
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
                isVerifying = true,
                error = null
            )
            
            Timber.d("Verifying OTP")
            
            when (val result = verifyOtpUseCase(verificationId, otpCode)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        isVerified = true,
                        error = null
                    )
                    Timber.d("OTP verified successfully")
                }

                is AuthResult.Error -> {
                    val attempts = _uiState.value.attemptCount + 1
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        error = result.error.getMessage(),
                        attemptCount = attempts,
                        otp = "" // Clear OTP on error
                    )
                    Timber.e("OTP verification failed: ${result.error.getTitle()}")
                }

                AuthResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    /**
     * Resend OTP code
     */
    fun resendOtp(activity: Activity) {
        if (_uiState.value.resendCooldownSeconds > 0) {
            return // Still in cooldown
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isResending = true,
                error = null
            )
            
            Timber.d("Resending OTP")
            
            when (val result = resendOtpUseCase(activity)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isResending = false,
                        error = null,
                        otp = "" // Clear previous OTP
                    )
                    startResendCooldown()
                    Timber.d("OTP resent successfully")
                }
                
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isResending = false,
                        error = result.error.getMessage()
                    )
                    Timber.e("Failed to resend OTP: ${result.error.getTitle()}")
                }
                
                AuthResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    /**
     * Start resend cooldown timer
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
     * Clear error message
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
 * UI state for OTP verification screen
 */
data class OtpVerificationUiState(
    val otp: String = "",
    val isVerifying: Boolean = false,
    val isVerified: Boolean = false,
    val isResending: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val error: String? = null,
    val attemptCount: Int = 0
)
