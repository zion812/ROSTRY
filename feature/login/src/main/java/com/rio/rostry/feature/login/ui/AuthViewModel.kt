package com.rio.rostry.feature.login.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.core.common.analytics.AuthAnalyticsTracker
import com.rio.rostry.core.common.analytics.FlowAnalyticsTracker
import com.rio.rostry.core.common.session.SessionManager
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.account.repository.AuthRepository
import com.rio.rostry.domain.model.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication flows.
 * 
 * Supports:
 * - Google Sign-In
 * - Email/Password Sign-Up & Sign-In
 * - Password Reset
 * - Guest Mode (preview without login)
 * - Email Verification
 * - Special admin privileges for specific emails
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle,
    private val authAnalytics: AuthAnalyticsTracker,
    private val flowAnalyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    /**
     * Special admin email addresses that get automatic ADMIN role.
     * These are hardcoded for security-critical access.
     */
    companion object {
        val ADMIN_EMAILS = setOf(
            "rowdyzion@gmail.com",
            "gallus9blood@gmail.com",
            "zionjuvvanapudi@gmail.com"
        )
    }

    /**
     * Check if an email is a special admin email.
     */
    fun isAdminEmail(email: String?): Boolean {
        return email?.lowercase() in ADMIN_EMAILS
    }

    /**
     * Determine the role for a user based on their email and selected role.
     * Special admin emails always get ADMIN role.
     */
    fun determineUserRole(userEmail: String?, selectedRole: UserType): UserType {
        return if (isAdminEmail(userEmail)) {
            UserType.ADMIN
        } else {
            selectedRole
        }
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>(extraBufferCapacity = 8)
    val navigation = _navigation.asSharedFlow()

    sealed class NavAction {
        object ToHome : NavAction()
        object ToUserSetup : NavAction()
        object ToEmailVerification : NavAction()
        object ToPasswordReset : NavAction()
    }

    // ==================== GOOGLE SIGN-IN ====================

    fun signInWithGoogle(idToken: String, selectedRole: UserType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.signInWithGoogle(idToken)) {
                is Result.Success -> {
                    authAnalytics.trackAuthComplete("google", false)
                    handleSuccessfulAuth(result.data, selectedRole)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Google sign-in failed"
                    )
                    authAnalytics.trackAuthError("google", result.exception.message)
                }
            }
        }
    }

    /**
     * Sign in with Google and get user email for admin detection.
     * This is called after Firebase UI returns the result.
     */
    fun handleGoogleSignInResult(
        userEmail: String?,
        selectedRole: UserType,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val finalRole = determineUserRole(userEmail, selectedRole)
            
            if (finalRole == UserType.ADMIN) {
                sessionManager.markAuthenticated(System.currentTimeMillis(), UserType.ADMIN)
                authAnalytics.trackAuthComplete("google_admin", false)
            } else {
                sessionManager.markAuthenticated(System.currentTimeMillis(), finalRole)
                authAnalytics.trackAuthComplete("google", false)
            }

            if (finalRole == UserType.GENERAL) {
                _navigation.emit(NavAction.ToUserSetup)
            } else {
                _navigation.emit(NavAction.ToHome)
            }
            onComplete()
        }
    }

    // ==================== EMAIL/PASSWORD SIGN-UP ====================

    fun signUpWithEmail(email: String, password: String, displayName: String?, selectedRole: UserType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.signUpWithEmail(email, password, displayName)) {
                is Result.Success -> {
                    authAnalytics.trackAuthComplete("email_signup", false)
                    
                    // Check for admin email - if admin, skip verification requirement
                    val finalRole = determineUserRole(email, selectedRole)
                    
                    if (finalRole == UserType.ADMIN) {
                        // Admin users get immediate access
                        sessionManager.markAuthenticated(System.currentTimeMillis(), UserType.ADMIN)
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _navigation.emit(NavAction.ToHome)
                    } else {
                        // Email verification required for regular users
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            requiresEmailVerification = true,
                            pendingUser = result.data
                        )
                        _navigation.emit(NavAction.ToEmailVerification)
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Sign up failed"
                    )
                    authAnalytics.trackAuthError("email_signup", result.exception.message)
                }
            }
        }
    }

    // ==================== EMAIL/PASSWORD SIGN-IN ====================

    fun signInWithEmail(email: String, password: String, selectedRole: UserType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.signInWithEmail(email, password)) {
                is Result.Success -> {
                    authAnalytics.trackAuthComplete("email_signin", false)
                    handleSuccessfulAuth(result.data, selectedRole)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Sign in failed"
                    )
                    authAnalytics.trackAuthError("email_signin", result.exception.message)
                }
            }
        }
    }

    // ==================== PASSWORD RESET ====================

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        passwordResetSent = true
                    )
                    _navigation.emit(NavAction.ToPasswordReset)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to send reset email"
                    )
                }
            }
        }
    }

    // ==================== EMAIL VERIFICATION ====================

    fun resendVerificationEmail() {
        viewModelScope.launch {
            when (val result = authRepository.sendEmailVerification()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        verificationEmailSent = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message ?: "Failed to send verification email"
                    )
                }
            }
        }
    }

    fun checkEmailVerification() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = authRepository.isEmailVerified()) {
                is Result.Success -> {
                    if (result.data) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            emailVerified = true
                        )
                        // Proceed with auth flow
                        _uiState.value.pendingUser?.let { user ->
                            val roleStr = savedStateHandle.get<String>("authRole")
                            val role = roleStr?.let { UserType.valueOf(it) } ?: UserType.GENERAL
                            handleSuccessfulAuth(user, role)
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            emailVerified = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to check verification"
                    )
                }
            }
        }
    }

    // ==================== GUEST MODE ====================

    fun continueAsGuest(selectedRole: UserType) {
        viewModelScope.launch {
            authAnalytics.trackAuthComplete("guest", true)
            sessionManager.markGuestSession(selectedRole, System.currentTimeMillis())
            _navigation.emit(NavAction.ToHome)
        }
    }

    // ==================== HELPER METHODS ====================

    private suspend fun handleSuccessfulAuth(user: com.rio.rostry.core.model.User, selectedRole: UserType) {
        val roleStr = savedStateHandle.get<String>("authRole")
        val initialRole = roleStr?.let { UserType.valueOf(it) } ?: selectedRole
        
        // Check for special admin emails - they get ADMIN role automatically
        val finalRole = determineUserRole(user.email, initialRole)

        // Check if guest session exists and upgrade it
        val wasGuest = sessionManager.isGuestSession()
        if (wasGuest) {
            sessionManager.upgradeGuestToAuthenticated(finalRole, System.currentTimeMillis())
        } else {
            sessionManager.markAuthenticated(System.currentTimeMillis(), finalRole)
        }

        // Track admin elevation for analytics
        if (finalRole == UserType.ADMIN && initialRole != UserType.ADMIN) {
            authAnalytics.trackAuthComplete("admin_elevation", false)
        }

        if (finalRole == UserType.GENERAL) {
            _navigation.emit(NavAction.ToUserSetup)
        } else {
            _navigation.emit(NavAction.ToHome)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

/**
 * UI State for authentication screens
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val requiresEmailVerification: Boolean = false,
    val emailVerified: Boolean = false,
    val verificationEmailSent: Boolean = false,
    val passwordResetSent: Boolean = false,
    val pendingUser: com.rio.rostry.core.model.User? = null
)