package com.rio.rostry.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.upgrade.RoleUpgradeManager
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.RolePreferenceDataSource
import com.rio.rostry.session.SessionManager
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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val rolePreferences: RolePreferenceDataSource,
    private val startDestinationProvider: RoleStartDestinationProvider,
    private val currentUserProvider: CurrentUserProvider,
    private val roleUpgradeManager: RoleUpgradeManager
) : ViewModel() {

    data class SessionUiState(
        val isAuthenticated: Boolean = false,
        val role: UserType? = null,
        val navConfig: RoleNavigationConfig? = null,
        val isLoading: Boolean = true,
        val error: String? = null,
        val availableUpgrade: UserType? = null,
        val authMode: SessionManager.AuthMode = SessionManager.AuthMode.FIREBASE,
        val pendingDeepLink: String? = null,
        val sessionExpiryWarning: String? = null,
        val user: com.rio.rostry.data.database.entity.UserEntity? = null,
        val isAdmin: Boolean = false
    )

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState

    private var sessionJob: Job? = null
    private var userCollectionJob: Job? = null
    private var expiryCheckJob: Job? = null
    
    // Track recent role changes to prevent synchronizeRole from overriding an in-progress upgrade
    @Volatile
    private var lastRoleSyncTime: Long = 0L
    private val roleSyncDebounceMs = 3000L // Don't re-sync role within 3 seconds of last sync

    init {
        observeSession()
        observePersistedRole()
    }

    private fun observeSession() {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                sessionManager.authMode(),
                authRepository.isAuthenticated
            ) { mode, firebaseAuthed ->
                val isAuthed = when (mode) {
                    SessionManager.AuthMode.FIREBASE -> firebaseAuthed
                    SessionManager.AuthMode.GUEST -> true // Guests are considered authenticated in a limited capacity
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
            combine(
                rolePreferences.role,
                sessionManager.getGuestRole(),
                sessionManager.authMode()
            ) { prefRole, guestRole, authMode ->
                val role = if (authMode == SessionManager.AuthMode.GUEST) guestRole else prefRole
                role
            }.collectLatest { role ->
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

    fun refresh() {
        synchronizeRole(_uiState.value.authMode)
    }

    fun refreshUserProfile() {
        viewModelScope.launch {
            val uid = currentUserProvider.userIdOrNull() ?: return@launch
            when (val result = userRepository.refreshCurrentUser(uid)) {
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {
                    synchronizeRole(_uiState.value.authMode)
                }
            }
        }
    }

    private fun synchronizeRole(mode: SessionManager.AuthMode) {
        userCollectionJob?.cancel()
        userCollectionJob = viewModelScope.launch {
            if (mode == SessionManager.AuthMode.GUEST) {
                val role = sessionManager.getGuestRole().firstOrNull() ?: UserType.GENERAL
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
                return@launch
            }
            // Debounce user flow to prevent rapid updates during upgrade
            userRepository.getCurrentUser()
                .debounce(500L) // Wait 500ms for changes to stabilize
                .distinctUntilChangedBy { (it as? Resource.Success)?.data?.userType } // Only react to actual role changes
                .collectLatest { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Timber.e("Sync role failed: ${resource.message}")
                        // Only show error screen if we don't have a usable role yet.
                        // If we have a role (from cache/DataStore), fallback to it (stale-while-revalidate).
                        if (_uiState.value.role == null) {
                            _uiState.value = _uiState.value.copy(isLoading = false, error = resource.message)
                        } else {
                            // We have cached data, so just stop loading. The user can continue offline/stale.
                            _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        val role = resource.data?.role
                            ?: rolePreferences.role.value
                            ?: UserType.GENERAL
                        
                        // CRITICAL: Skip role sync if an upgrade is in progress
                        // This prevents premature navigation during role transitions
                        if (sessionManager.upgradeInProgress.value) {
                            Timber.d("SessionVM: Upgrade in progress, skipping role sync")
                            return@collectLatest
                        }
                        
                        // Check if we recently synced (within debounce window)
                        // This prevents SessionViewModel from overriding an in-progress upgrade
                        val now = System.currentTimeMillis()
                        val timeSinceLastSync = now - lastRoleSyncTime
                        if (timeSinceLastSync < roleSyncDebounceMs && _uiState.value.role == role) {
                            Timber.d("SessionVM: Skipping role sync, recently synced (${timeSinceLastSync}ms ago)")
                            return@collectLatest
                        }
                        lastRoleSyncTime = now
                        
                        sessionManager.markAuthenticated(now, role)
                        rolePreferences.persist(role)
                        val navConfig = startDestinationProvider.configFor(role)
                        val isAdmin = resource.data?.email == "rowdyzion@gmail.com" || 
                                      resource.data?.phoneNumber == "+918106312656" || 
                                      resource.data?.phoneNumber == "8106312656" ||
                                      role == UserType.ADMIN
                        
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = true,
                            role = role,
                            navConfig = navConfig,
                            availableUpgrade = role.nextLevel(),
                            isLoading = false,
                            error = null,
                            authMode = mode,
                            user = resource.data,
                            isAdmin = isAdmin
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

    /**
     * Switches the user's role.
     * 
     * For guest mode: lightweight switch without validation or audit logging.
     * For authenticated mode: route through RoleUpgradeManager for consistency.
     * 
     * NOTE: This is reserved for guest-mode role switching and administrative overrides.
     * Interactive user upgrades should use the upgrade wizard flow.
     */
    fun switchRole(role: UserType) {
        viewModelScope.launch {
            val uid = currentUserProvider.userIdOrNull()
            val navConfig = startDestinationProvider.configFor(role)
            val authMode = _uiState.value.authMode

            // For guest mode: lightweight switch without validation
            if (authMode == SessionManager.AuthMode.GUEST) {
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
                return@launch
            }

            // For authenticated mode: use RoleUpgradeManager for audit logging and analytics
            if (uid != null && authMode == SessionManager.AuthMode.FIREBASE) {
                when (val result = roleUpgradeManager.upgradeRole(uid, role, skipValidation = true)) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message ?: "Failed to update role")
                    }
                    is Resource.Success -> {
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

    fun signOut() {
        expiryCheckJob?.cancel()
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = SessionUiState(
                isAuthenticated = false,
                role = null,
                navConfig = null,
                isLoading = false,
                error = null,
                authMode = SessionManager.AuthMode.FIREBASE,
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
