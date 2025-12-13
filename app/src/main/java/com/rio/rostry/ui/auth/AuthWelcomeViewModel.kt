package com.rio.rostry.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthWelcomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedRole = MutableStateFlow<UserType?>(null)
    val selectedRole: StateFlow<UserType?> = _selectedRole.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // Restore selected role from SavedStateHandle if any
        val savedRole = savedStateHandle.get<String>("selectedRole")?.let { UserType.valueOf(it) }
        _selectedRole.value = savedRole
    }

    fun selectRole(role: UserType) {
        _selectedRole.value = role
        savedStateHandle["selectedRole"] = role.name
    }

    fun startGuestMode(role: UserType) {
        viewModelScope.launch {
            _isLoading.value = true
            sessionManager.markGuestSession(role, System.currentTimeMillis())
            flowAnalyticsTracker.trackGuestModeStarted(role.name)
            _navigationEvent.emit(NavigationEvent.ToGuestHome(role))
            _isLoading.value = false
        }
    }

    fun startAuthentication(role: UserType) {
        viewModelScope.launch {
            _isLoading.value = true
            savedStateHandle["authRole"] = role.name
            flowAnalyticsTracker.trackOnboardingRoleSelected(role.name)
            _navigationEvent.emit(NavigationEvent.ToAuth(role))
            _isLoading.value = false
        }
    }

    sealed class NavigationEvent {
        data class ToGuestHome(val role: UserType) : NavigationEvent()
        data class ToAuth(val role: UserType) : NavigationEvent()
    }
}