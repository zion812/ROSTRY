package com.rio.rostry.ui.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.demo.DemoUserProfile
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.RolePreferenceDataSource
import com.rio.rostry.session.SessionManager
import com.rio.rostry.session.MockAuthManager
import com.rio.rostry.ui.navigation.RoleNavigationConfig
import com.rio.rostry.ui.navigation.RoleStartDestinationProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val rolePreferences: RolePreferenceDataSource,
    private val startDestinationProvider: RoleStartDestinationProvider,
    private val currentUserProvider: CurrentUserProvider,
    private val mockAuthManager: MockAuthManager
) : ViewModel() {

    data class SessionUiState(
        val isAuthenticated: Boolean = false,
        val role: UserType? = null,
        val navConfig: RoleNavigationConfig? = null,
        val isLoading: Boolean = true,
        val error: String? = null,
        val availableUpgrade: UserType? = null,
        val demoProfiles: List<DemoUserProfile> = emptyList(),
        val currentDemoProfile: DemoUserProfile? = null,
        val authMode: SessionManager.AuthMode = SessionManager.AuthMode.FIREBASE
    )

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState

    // When true, AuthFlow should prefer real phone authentication UI and hide demo UI.
    private val _preferRealAuth = MutableStateFlow(false)
    val preferRealAuth: StateFlow<Boolean> = _preferRealAuth

    private var sessionJob: Job? = null
    private var userCollectionJob: Job? = null

    init {
        viewModelScope.launch {
            mockAuthManager.ensureSeeded()
            _uiState.value = _uiState.value.copy(
                demoProfiles = mockAuthManager.allProfiles(),
                currentDemoProfile = mockAuthManager.currentProfile.value
            )
        }
        observeSession()
        observePersistedRole()
        observeDemoProfile()
    }

    private fun observeSession() {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                sessionManager.authMode(),
                authRepository.isAuthenticated,
                mockAuthManager.isAuthenticated,
                preferRealAuth
            ) { mode, firebaseAuthed, demoAuthed, preferReal ->
                // If user has chosen to switch to real, immediately suppress demo auth
                if (preferReal) {
                    SessionManager.AuthMode.FIREBASE to false
                } else {
                    val isAuthed = when (mode) {
                        SessionManager.AuthMode.FIREBASE -> firebaseAuthed
                        SessionManager.AuthMode.DEMO -> demoAuthed
                    }
                    mode to isAuthed
                }
            }.collectLatest { (mode, authenticated) ->
                Log.d("RostrySession", "observeSession: mode=$mode authenticated=$authenticated preferReal=${preferRealAuth.value}")
                if (!authenticated) {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        isLoading = false,
                        authMode = mode,
                        role = null,
                        navConfig = null,
                        availableUpgrade = null,
                        error = null
                    )
                    Log.d("RostrySession", "observeSession: unauthenticated -> show AuthFlow")
                    return@collectLatest
                }
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    isLoading = true,
                    authMode = mode,
                    error = null
                )
                Log.d("RostrySession", "observeSession: authenticated -> synchronizeRole(mode=$mode)")
                synchronizeRole(mode)
            }
        }
    }

    private fun observePersistedRole() {
        viewModelScope.launch {
            rolePreferences.role.collectLatest { role ->
                if (role == null) return@collectLatest
                val navConfig = startDestinationProvider.configFor(role)
                _uiState.value = _uiState.value.copy(
                    role = role,
                    navConfig = navConfig,
                    availableUpgrade = role.nextLevel(),
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    private fun observeDemoProfile() {
        viewModelScope.launch {
            mockAuthManager.currentProfile.collectLatest { profile ->
                _uiState.value = _uiState.value.copy(
                    currentDemoProfile = profile,
                    demoProfiles = mockAuthManager.allProfiles()
                )
            }
        }
    }

    fun refresh() {
        Log.d("RostrySession", "refresh() called; authMode=${_uiState.value.authMode}")
        synchronizeRole(_uiState.value.authMode)
    }

    fun preferRealSignIn() {
        _preferRealAuth.value = true
    }

    fun clearPreferRealSignIn() {
        _preferRealAuth.value = false
    }

    private fun synchronizeRole(mode: SessionManager.AuthMode) {
        Log.d("RostrySession", "synchronizeRole: start mode=$mode")
        userCollectionJob?.cancel()
        userCollectionJob = viewModelScope.launch {
            // Fast-path for demo: use active demo profile without waiting for repository
            if (mode == SessionManager.AuthMode.DEMO) {
                val demoProfile = mockAuthManager.currentProfile.value
                if (demoProfile != null) {
                    val role = demoProfile.role
                    sessionManager.markAuthenticated(
                        System.currentTimeMillis(),
                        role,
                        mode = SessionManager.AuthMode.DEMO,
                        demoUserId = demoProfile.id
                    )
                    rolePreferences.persist(role)
                    val navConfig = startDestinationProvider.configFor(role)
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        role = role,
                        navConfig = navConfig,
                        availableUpgrade = role.nextLevel(),
                        isLoading = false,
                        error = null,
                        authMode = mode
                    )
                    Log.d("RostrySession", "synchronizeRole: demo fast-path -> role=$role start=${navConfig.startDestination}")
                    return@launch
                }
            }
            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = resource.message)
                        Log.e("RostrySession", "synchronizeRole: error=${resource.message}")
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        Log.d("RostrySession", "synchronizeRole: loading...")
                    }
                    is Resource.Success -> {
                        val demoProfile = mockAuthManager.currentProfile.value
                        val role = demoProfile?.role
                            ?: resource.data?.userType
                            ?: rolePreferences.role.value
                            ?: UserType.GENERAL
                        if (mode == SessionManager.AuthMode.DEMO && demoProfile != null) {
                            sessionManager.markAuthenticated(
                                System.currentTimeMillis(),
                                role,
                                mode = SessionManager.AuthMode.DEMO,
                                demoUserId = demoProfile.id
                            )
                        } else {
                            sessionManager.markAuthenticated(System.currentTimeMillis(), role)
                        }
                        rolePreferences.persist(role)
                        val navConfig = startDestinationProvider.configFor(role)
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = true,
                            role = role,
                            navConfig = navConfig,
                            availableUpgrade = role.nextLevel(),
                            isLoading = false,
                            error = null,
                            authMode = mode
                        )
                        Log.d("RostrySession", "synchronizeRole: success -> role=$role start=${navConfig.startDestination}")
                    }
                }
            }
        }
    }

    fun switchRole(role: UserType) {
        viewModelScope.launch {
            val mode = _uiState.value.authMode
            if (mode == SessionManager.AuthMode.DEMO) {
                val target = mockAuthManager.profilesByRole(role).firstOrNull()
                if (target != null) {
                    when (mockAuthManager.activateProfile(target.id)) {
                        is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Failed to switch demo profile")
                        else -> Unit
                    }
                } else {
                    _uiState.value = _uiState.value.copy(error = "No demo profile for role $role")
                }
                return@launch
            }

            val uid = currentUserProvider.userIdOrNull()
            val navConfig = startDestinationProvider.configFor(role)
            sessionManager.markAuthenticated(System.currentTimeMillis(), role)
            rolePreferences.persist(role)
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                role = role,
                navConfig = navConfig,
                availableUpgrade = role.nextLevel(),
                error = null,
                isLoading = false
            )

            if (uid != null) {
                when (val result = userRepository.updateUserType(uid, role)) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message ?: "Failed to update role")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun upgradeRole() {
        val next = _uiState.value.availableUpgrade ?: return
        switchRole(next)
    }

    fun activateDemoProfile(profileId: String) {
        viewModelScope.launch {
            when (mockAuthManager.activateProfile(profileId)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Unable to activate demo profile")
                else -> Unit
            }
        }
    }

    fun signInDemo(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = mockAuthManager.authenticate(username, password)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                else -> Unit
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            mockAuthManager.signOut()
            authRepository.signOut()
            _uiState.value = SessionUiState(
                demoProfiles = mockAuthManager.allProfiles(),
                authMode = SessionManager.AuthMode.FIREBASE,
                isLoading = false,
                isAuthenticated = false,
                navConfig = null,
                role = null,
                error = null
            )
        }
    }
}
