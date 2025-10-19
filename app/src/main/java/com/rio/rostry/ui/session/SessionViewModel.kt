package com.rio.rostry.ui.session

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
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
        val authMode: SessionManager.AuthMode = SessionManager.AuthMode.FIREBASE,
        val pendingDeepLink: String? = null,
        val sessionExpiryWarning: String? = null
    )

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState

    private var sessionJob: Job? = null
    private var userCollectionJob: Job? = null
    private var expiryCheckJob: Job? = null

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
                mockAuthManager.isAuthenticated
            ) { mode, firebaseAuthed, demoAuthed ->
                val isAuthed = when (mode) {
                    SessionManager.AuthMode.FIREBASE -> firebaseAuthed
                    SessionManager.AuthMode.DEMO -> demoAuthed
                }
                mode to isAuthed
            }.collectLatest { (mode, authenticated) ->
                if (!authenticated) {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        isLoading = false,
                        authMode = mode,
                        error = null,
                        sessionExpiryWarning = null
                    )
                    expiryCheckJob?.cancel()
                    return@collectLatest
                }
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    isLoading = true,
                    authMode = mode,
                    error = null
                )
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
        synchronizeRole(_uiState.value.authMode)
    }

    private fun synchronizeRole(mode: SessionManager.AuthMode) {
        userCollectionJob?.cancel()
        userCollectionJob = viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = resource.message)
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
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
                        scheduleSessionExpiryCheck()
                    }
                }
            }
        }
    }

    fun scheduleSessionExpiryCheck() {
        expiryCheckJob?.cancel()
        expiryCheckJob = viewModelScope.launch {
            val periodicFlow = flow {
                emit(Unit)
                while (true) {
                    delay(3600000L) // Check every hour
                    emit(Unit)
                }
            }
            combine(periodicFlow, sessionManager.lastAuthAt(), sessionManager.sessionRole()) { _, last, role ->
                if (last == null || role == null) return@combine null
                val timeoutDays = when (role) {
                    UserType.GENERAL -> 30L
                    UserType.FARMER, UserType.ENTHUSIAST -> 7L
                }
                val timeoutMillis = timeoutDays * 24 * 60 * 60 * 1000
                val now = System.currentTimeMillis()
                val elapsed = now - last
                val timeLeft = timeoutMillis - elapsed
                timeLeft
            }.collect { timeLeft ->
                if (timeLeft == null) {
                    _uiState.value = _uiState.value.copy(sessionExpiryWarning = null)
                    return@collect
                }
                if (timeLeft <= 0) {
                    // Session expired, sign out
                    signOut()
                } else if (timeLeft <= 86400000L) { // 1 day in ms
                    val daysLeft = timeLeft / (24 * 60 * 60 * 1000)
                    _uiState.value = _uiState.value.copy(sessionExpiryWarning = "Your session will expire in $daysLeft days. Please re-authenticate soon.")
                } else {
                    _uiState.value = _uiState.value.copy(sessionExpiryWarning = null)
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
        expiryCheckJob?.cancel()
        viewModelScope.launch {
            mockAuthManager.signOut()
            authRepository.signOut()
            _uiState.value = SessionUiState(
                isAuthenticated = false,
                role = null,
                navConfig = null,
                isLoading = false,
                error = null,
                demoProfiles = mockAuthManager.allProfiles(),
                currentDemoProfile = null,
                authMode = SessionManager.AuthMode.DEMO,
                sessionExpiryWarning = null
            )
        }
    }

    fun setPendingDeepLink(route: String) {
        _uiState.value = _uiState.value.copy(pendingDeepLink = route)
    }

    fun clearPendingDeepLink() {
        _uiState.value = _uiState.value.copy(pendingDeepLink = null)
    }

    fun consumePendingDeepLink(): String? {
        val link = _uiState.value.pendingDeepLink
        if (link != null) {
            clearPendingDeepLink()
        }
        return link
    }
}