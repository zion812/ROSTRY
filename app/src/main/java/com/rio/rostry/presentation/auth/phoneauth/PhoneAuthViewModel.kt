package com.rio.rostry.presentation.auth.phoneauth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.usecase.StartPhoneVerificationUseCase
import com.rio.rostry.utils.formatToE164
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for phone authentication screen.
 * 
 * Responsibilities:
 * - Phone number input validation
 * - Initiating phone verification
 * - Managing UI state
 * 
 * Does NOT handle:
 * - OTP verification (see OtpVerificationViewModel)
 * - Phone linking (see PhoneLinkingViewModel)
 * - Navigation (see AuthCoordinator)
 */
@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val startPhoneVerificationUseCase: StartPhoneVerificationUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PhoneAuthUiState())
    val uiState: StateFlow<PhoneAuthUiState> = _uiState.asStateFlow()
    
    /**
     * Update phone number input.
     * Input should be digits only (max 10). We prepend +91 and format to E.164.
     */
    fun onPhoneChanged(input: String) {
        // Strip non-digits and limit to 10
        val digits = input.filter { it.isDigit() }.take(10)
        // Build E.164 using +91 country code
        val e164 = if (digits.isNotEmpty()) formatToE164("+91", digits) else null
        _uiState.value = _uiState.value.copy(
            phoneInput = digits,
            phoneE164 = e164,
            error = null // Clear error on new input
        )
    }
    
    /**
     * Send OTP to phone number
     */
    fun sendOtp(activity: Activity) {
        val e164 = _uiState.value.phoneE164
        if (e164 == null) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a valid phone number (E.164, e.g. +1234567890)"
            )
            return
        }
        
        // Create PhoneNumber value class (validates format)
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
            
            Timber.d("Sending OTP to ${phoneNumber.masked()}")
            
            when (val result = startPhoneVerificationUseCase(activity, phoneNumber)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        verificationId = result.data,
                        error = null
                    )
                    Timber.d("OTP sent successfully")
                }
                
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error.getMessage()
                    )
                    Timber.e("Failed to send OTP: ${result.error.getTitle()}")
                }
                
                AuthResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for phone authentication screen
 */
data class PhoneAuthUiState(
    val phoneInput: String = "",
    val phoneE164: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val verificationId: VerificationId? = null
)
