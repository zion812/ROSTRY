package com.rio.rostry.feature.login.ui
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.account.repository.AuthRepository
import com.rio.rostry.core.common.session.SessionManager
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.core.common.analytics.AuthAnalyticsTracker
import com.rio.rostry.core.common.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle,
    private val authAnalytics: AuthAnalyticsTracker,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigation = MutableSharedFlow<NavAction>(extraBufferCapacity = 8)
    val navigation = _navigation.asSharedFlow()

    sealed class NavAction {
        object ToHome : NavAction()
        object ToUserSetup : NavAction()
    }

    fun handleFirebaseUIResult(response: IdpResponse?, resultCode: Int) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            val user = firebaseAuth.currentUser
            val provider = response?.providerType ?: "unknown"
            
            authAnalytics.trackAuthComplete(provider, false)
            
            viewModelScope.launch {
                val roleStr = savedStateHandle.get<String>("authRole")
                val role = roleStr?.let { UserType.valueOf(it) } ?: UserType.GENERAL
                
                // For now, simplify logic: just mark as authenticated with selected role
                sessionManager.markAuthenticated(System.currentTimeMillis(), role)
                
                if (role == UserType.GENERAL) {
                    _navigation.emit(NavAction.ToUserSetup)
                } else {
                    _navigation.emit(NavAction.ToHome)
                }
            }
        } else {
            // Handle cancellation or error silently for now as LoginScreen handles UI
            val error = response?.error?.errorCode
            if (error != null) {
                authAnalytics.trackAuthCancel("error_$error")
            } else {
                authAnalytics.trackAuthCancel("user_cancel")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
