package com.rio.rostry.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthEvent
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.security.SecurityManager
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.normalizeToE164
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.analytics.AuthAnalyticsTracker
import com.rio.rostry.utils.network.FeatureToggles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle,
    private val featureToggles: FeatureToggles,
    private val authAnalytics: AuthAnalyticsTracker,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    data class UiState(
        val phoneInput: String = "",
        val e164: String? = null,
        val otp: String = "",
        val verificationId: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val resendCooldownSec: Int = 0,
        val needsPhoneLink: Boolean = false,
        val authProvider: String? = null,
        val isNewUser: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _navigation = MutableSharedFlow<NavAction>(extraBufferCapacity = 8)
    val navigation = _navigation.asSharedFlow()

    sealed class NavAction {
        data class ToOtp(val verificationId: String): NavAction()
        data class ToHome(val userType: UserType): NavAction()
        object ToUserSetup : NavAction()
    }

    private var cooldownJob: kotlinx.coroutines.Job? = null

    private var lastStartVerificationAt: Long? = null
    private var lastVerifyOtpAt: Long? = null

    init {
        viewModelScope.launch {
            authRepository.currentVerificationId.collectLatest { vid ->
                _uiState.value = _uiState.value.copy(verificationId = vid)
            }
        }
        viewModelScope.launch {
            authRepository.currentPhoneE164.collectLatest { phone ->
                _uiState.value = _uiState.value.copy(e164 = phone)
            }
        }
        viewModelScope.launch {
            authRepository.events.collectLatest { event ->
                when (event) {
                    is AuthEvent.CodeSent -> {
                        _uiState.value = _uiState.value.copy(verificationId = event.verificationId, isLoading = false)
                        startResendCooldown()
                        if (!_uiState.value.needsPhoneLink) {
                            _navigation.tryEmit(NavAction.ToOtp(event.verificationId))
                        }
                    }
                    is AuthEvent.AutoVerified -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        postAuthBootstrapAndNavigate()
                    }
                    is AuthEvent.VerificationFailed -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = event.message)
                    }
                    is AuthEvent.PhoneLinkSuccess -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, needsPhoneLink = false)
                        val uid = firebaseAuth.currentUser?.uid
                        if (uid != null) {
                            viewModelScope.launch { userRepository.refreshPhoneNumber(uid) }
                        }
                        authAnalytics.trackPhoneVerifySuccess(_uiState.value.authProvider ?: "unknown")
                        postAuthBootstrapAndNavigate()
                    }
                    is AuthEvent.PhoneLinkFailed -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = event.message)
                        authAnalytics.trackPhoneVerifyFail(_uiState.value.authProvider ?: "unknown", event.message)
                    }
                }
            }
        }
    }

    fun onPhoneChanged(input: String) {
        val e164 = normalizeToE164(input)
        _uiState.value = _uiState.value.copy(phoneInput = input, e164 = e164)
        savedStateHandle["phoneE164"] = e164
    }

    fun onOtpChanged(code: String) {
        _uiState.value = _uiState.value.copy(otp = code)
    }

    fun startVerification(activity: Activity) {
        val now = System.currentTimeMillis()
        val lastStartAt = lastStartVerificationAt
        if (lastStartAt != null && now - lastStartAt < 60000) {
            val remaining = ((60000 - (now - lastStartAt)) / 1000).toInt()
            _uiState.value = _uiState.value.copy(error = "Please wait ${remaining}s before requesting another OTP")
            return
        }
        val normalized = _uiState.value.e164
        if (normalized == null) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid phone number (E.164, e.g. +1234567890)")
            return
        }
        val maskedPhone = normalized.replaceRange(0, normalized.length - 4, "*".repeat(normalized.length - 4))
        SecurityManager.audit("AUTH_START_VERIFICATION", mapOf("phone" to maskedPhone, "cooldown" to 60))
        lastStartVerificationAt = now
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.startPhoneVerification(activity, normalized)) {
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                    lastStartVerificationAt = null // reset on error
                }
                else -> Unit
            }
        }
    }

    fun verifyOtpAndSignIn() {
        val now = System.currentTimeMillis()
        val lastVerifyAt = lastVerifyOtpAt
        if (lastVerifyAt != null && now - lastVerifyAt < 60000) {
            val remaining = ((60000 - (now - lastVerifyAt)) / 1000).toInt()
            _uiState.value = _uiState.value.copy(error = "Please wait ${remaining}s before verifying another OTP")
            return
        }
        val verificationId = authRepository.currentVerificationId.value
        if (verificationId == null) {
            _uiState.value = _uiState.value.copy(error = "Verification session expired")
            return
        }
        val code = _uiState.value.otp
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid 6-digit OTP")
            return
        }
        lastVerifyOtpAt = now
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.verifyOtp(verificationId, code)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    postAuthBootstrapAndNavigate()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                    lastVerifyOtpAt = null // reset on error
                }
                else -> Unit
            }
        }
    }

    fun resendOtp(activity: Activity) {
        if (_uiState.value.resendCooldownSec > 0) return
        SecurityManager.audit("AUTH_RESEND_OTP", mapOf("attempt" to true, "cooldown" to _uiState.value.resendCooldownSec))
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.resendVerificationCode(activity)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, otp = "")
                }
            }
        }
    }

    fun handleFirebaseUIResult(response: IdpResponse?, resultCode: Int) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            val user = firebaseAuth.currentUser
            val meta = user?.metadata
            val isNew = meta?.creationTimestamp == meta?.lastSignInTimestamp
            val hasPhone = user?.phoneNumber != null
            val provider = response?.providerType ?: "unknown"
            val requirePhone = featureToggles.isPhoneVerificationRequired()
            if (isNew == true && !hasPhone && requirePhone) {
                authAnalytics.trackPhoneVerifyStart(true)
                _uiState.value = _uiState.value.copy(needsPhoneLink = true, authProvider = provider, isNewUser = true)
                return
            }
            authAnalytics.trackAuthComplete(provider, isNew == true)
            postAuthBootstrapAndNavigate()
        } else {
            if (response == null) {
                authAnalytics.trackAuthCancel("unknown")
                _uiState.value = _uiState.value.copy(error = null)
                return
            }
            when (response.error?.errorCode) {
                ErrorCodes.NO_NETWORK -> _uiState.value = _uiState.value.copy(error = "No internet connection")
                ErrorCodes.UNKNOWN_ERROR -> _uiState.value = _uiState.value.copy(error = "Unknown error during sign-in")
                else -> _uiState.value = _uiState.value.copy(error = response.error?.message)
            }
        }
    }

    fun startPhoneLinking(activity: Activity) {
        val normalized = _uiState.value.e164
        if (normalized == null) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid Indian number")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.linkPhoneToCurrentUser(activity, normalized)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                else -> Unit
            }
        }
    }

    fun verifyOtpAndLinkPhone() {
        val verificationId = authRepository.currentVerificationId.value
        if (verificationId == null) {
            _uiState.value = _uiState.value.copy(error = "Verification session expired")
            return
        }
        val code = _uiState.value.otp
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid 6-digit OTP")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = authRepository.verifyOtpAndLink(verificationId, code)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                else -> Unit
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
            // Try to hydrate user profile quickly; don't block navigation forever
            val resource = withTimeoutOrNull(5000) { userRepository.getCurrentUser().first() }
            when (resource) {
                is Resource.Success -> {
                    val user = resource.data
                    if (user != null) {
                        sessionManager.markAuthenticated(System.currentTimeMillis(), user.userType)
                        if (user.userType == UserType.GENERAL) {
                            _navigation.tryEmit(NavAction.ToUserSetup)
                        } else {
                            _navigation.tryEmit(NavAction.ToHome(user.userType))
                        }
                    } else {
                        // No profile yet; go to setup to complete essentials
                        _navigation.tryEmit(NavAction.ToUserSetup)
                    }
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = resource.message)
                    _navigation.tryEmit(NavAction.ToUserSetup)
                }
                is Resource.Loading -> {
                    // If still loading after timeout window, proceed to setup
                    _navigation.tryEmit(NavAction.ToUserSetup)
                }
                null -> {
                    // Timed out waiting; proceed to setup to avoid spinner lock
                    _navigation.tryEmit(NavAction.ToUserSetup)
                }
            }
        }
    }

    fun cancelPhoneLinking() {
        viewModelScope.launch {
            // Log cancel and sign out to return to entry screen
            authAnalytics.trackAuthCancel(_uiState.value.authProvider ?: "unknown")
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(needsPhoneLink = false, isLoading = false, error = null)
        }
    }
}