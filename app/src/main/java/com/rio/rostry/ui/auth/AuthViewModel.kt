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
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.analytics.AuthAnalyticsTracker
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
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
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle,
    private val featureToggles: FeatureToggles,
    private val authAnalytics: AuthAnalyticsTracker,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val isNewUser: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _navigation = MutableSharedFlow<NavAction>(extraBufferCapacity = 8)
    val navigation = _navigation.asSharedFlow()

    sealed class NavAction {
        data class ToHome(val userType: UserType): NavAction()
        object ToUserSetup : NavAction()
        object ToOtpPlaceholder : NavAction() // Kept for minimal compatibility if needed
    }

    init {
        viewModelScope.launch {
            authRepository.events.collectLatest { event ->
                when (event) {
                    is AuthEvent.GoogleSignInSuccess -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        authAnalytics.trackAuthComplete("google", false)
                        postAuthBootstrapAndNavigate()
                    }
                    is AuthEvent.GoogleSignInFailed -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = event.message)
                        authAnalytics.trackAuthCancel("google")
                    }
                }
            }
        }
    }

    fun handleFirebaseUIResult(response: IdpResponse?, resultCode: Int) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            val user = firebaseAuth.currentUser
            val meta = user?.metadata
            val isNew = meta?.creationTimestamp == meta?.lastSignInTimestamp
            val provider = response?.providerType ?: "unknown"
            
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

    private fun postAuthBootstrapAndNavigate() {
        viewModelScope.launch {
            Log.d("AuthViewModel", "postAuthBootstrapAndNavigate started")
            
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                Log.w("AuthViewModel", "No UID after auth, navigating to setup")
                _navigation.tryEmit(NavAction.ToUserSetup)
                return@launch
            }
            
            try {
                Log.d("AuthViewModel", "Attempting refreshCurrentUser...")
                withTimeoutOrNull(3000) {
                    userRepository.refreshCurrentUser(uid)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "refreshCurrentUser failure", e)
            }

            Log.d("AuthViewModel", "Fetching user profile...")
            val resource = withTimeoutOrNull(3000) { userRepository.getCurrentUser().first() }
            
            when (resource) {
                is Resource.Success -> {
                    val user = resource.data
                    if (user != null) {
                        Log.d("AuthViewModel", "User found, role=${user.role}, navigating...")
                        
                        val isGuest = sessionManager.isGuestSession().first()
                        if (isGuest) {
                            Log.d("AuthViewModel", "Upgrading GUEST session...")
                            val guestStartedAt = sessionManager.getGuestSessionStartedAt()
                            val now = System.currentTimeMillis()
                            sessionManager.upgradeGuestToAuthenticated(user.role, now)
                            if (guestStartedAt != null) {
                                val duration = (now - guestStartedAt) / 1000
                                flowAnalyticsTracker.trackGuestModeUpgraded(user.role.name, duration)
                            }
                        } else {
                            Log.d("AuthViewModel", "Marking authenticated session...")
                            sessionManager.markAuthenticated(System.currentTimeMillis(), user.role)
                        }

                        if (user.role == UserType.GENERAL) {
                            _navigation.tryEmit(NavAction.ToUserSetup)
                        } else {
                            _navigation.tryEmit(NavAction.ToHome(user.role))
                        }
                    } else {
                        Log.d("AuthViewModel", "No user data, navigating to UserSetup")
                        _navigation.tryEmit(NavAction.ToUserSetup)
                    }
                }
                else -> {
                    Log.w("AuthViewModel", "User fetch failed or timed out, navigating to setup")
                    _navigation.tryEmit(NavAction.ToUserSetup)
                }
            }
        }
    }

    fun setFromGuest(fromGuest: Boolean) {
        savedStateHandle["fromGuest"] = fromGuest
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

val AuthViewModel.UiState.needsPhoneVerificationBanner: Boolean
    get() = false // Phone verification no longer required
