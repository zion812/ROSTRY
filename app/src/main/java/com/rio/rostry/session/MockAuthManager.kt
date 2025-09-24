package com.rio.rostry.session

import com.rio.rostry.data.demo.DemoAccounts
import com.rio.rostry.data.demo.DemoSeeders
import com.rio.rostry.data.demo.DemoUserProfile
import com.rio.rostry.data.demo.toUserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class MockAuthManager @Inject constructor(
    private val sessionManager: SessionManager,
    private val rolePreferences: RolePreferenceDataSource,
    private val userRepository: UserRepository,
    private val demoSeeders: DemoSeeders
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentProfile = MutableStateFlow<DemoUserProfile?>(null)
    val currentProfile: StateFlow<DemoUserProfile?> = _currentProfile.asStateFlow()

    init {
        scope.launch {
            combine(
                sessionManager.authMode(),
                sessionManager.currentDemoUserId()
            ) { mode, demoId ->
                if (mode == SessionManager.AuthMode.DEMO && demoId != null) {
                    DemoAccounts.byId[demoId]
                } else {
                    null
                }
            }.collect { profile ->
                _currentProfile.value = profile
                _isAuthenticated.value = profile != null
            }
        }
    }

    suspend fun authenticate(username: String, password: String): Resource<DemoUserProfile> {
        val profile = DemoAccounts.byUsername[username]
            ?: return Resource.Error("Unknown demo user")
        if (profile.credential.password != password) {
            return Resource.Error("Incorrect password")
        }
        activateProfileInternal(profile)
        return Resource.Success(profile)
    }

    suspend fun activateProfile(profileId: String): Resource<DemoUserProfile> {
        val profile = DemoAccounts.byId[profileId]
            ?: return Resource.Error("Demo profile not found: $profileId")
        activateProfileInternal(profile)
        return Resource.Success(profile)
    }

    private suspend fun activateProfileInternal(profile: DemoUserProfile) {
        userRepository.upsertDemoUser(profile.toUserEntity())
        demoSeeders.seedProfile(profile)
        rolePreferences.persist(profile.role)
        sessionManager.markAuthenticated(
            nowMillis = System.currentTimeMillis(),
            userType = profile.role,
            mode = SessionManager.AuthMode.DEMO,
            demoUserId = profile.id
        )
        _currentProfile.value = profile
        _isAuthenticated.value = true
    }

    suspend fun signOut() {
        sessionManager.signOut()
        rolePreferences.clear()
        _currentProfile.value = null
        _isAuthenticated.value = false
    }

    fun profilesByRole(role: UserType): List<DemoUserProfile> = DemoAccounts.profilesByRole(role)

    fun allProfiles(): List<DemoUserProfile> = DemoAccounts.all

    suspend fun ensureSeeded() {
        userRepository.seedDemoUsers()
        val mode = sessionManager.authMode().first()
        if (mode == SessionManager.AuthMode.DEMO) {
            _isAuthenticated.value = sessionManager.currentDemoUserId().first() != null
        }
    }
}
