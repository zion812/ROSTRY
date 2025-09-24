package com.rio.rostry.ui.session

import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.auth.AuthEvent
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.RolePreferenceDataSource
import com.rio.rostry.session.SessionManager
import com.rio.rostry.session.MockAuthManager
import com.rio.rostry.ui.navigation.BottomNavDestination
import com.rio.rostry.ui.navigation.RoleNavigationConfig
import com.rio.rostry.ui.navigation.RoleStartDestinationProvider
import com.rio.rostry.utils.Resource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var startDestinationProvider: RoleStartDestinationProvider
    private lateinit var currentUserProvider: CurrentUserProvider
    private lateinit var rolePreferences: FakeRolePreferences
    private lateinit var mockAuthManager: MockAuthManager

    private val authFlow = MutableStateFlow(false)
    private val userFlow = MutableStateFlow<Resource<UserEntity?>>(Resource.Success(null))
    private val authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    private val authModeFlow = MutableStateFlow(SessionManager.AuthMode.FIREBASE)
    private val demoUserIdFlow = MutableStateFlow<String?>(null)
    private val mockDemoAuthFlow = MutableStateFlow(false)
    private val mockDemoProfileFlow = MutableStateFlow<com.rio.rostry.data.demo.DemoUserProfile?>(null)

    private val generalConfig = RoleNavigationConfig(
        role = UserType.GENERAL,
        startDestination = "general/home",
        bottomNav = listOf(BottomNavDestination("general/home", "Home")),
        accessibleRoutes = setOf("general/home")
    )
    private val farmerConfig = RoleNavigationConfig(
        role = UserType.FARMER,
        startDestination = "farmer/home",
        bottomNav = listOf(BottomNavDestination("farmer/home", "Home")),
        accessibleRoutes = setOf("farmer/home")
    )
    private val enthusiastConfig = RoleNavigationConfig(
        role = UserType.ENTHUSIAST,
        startDestination = "enthusiast/home",
        bottomNav = listOf(BottomNavDestination("enthusiast/home", "Home")),
        accessibleRoutes = setOf("enthusiast/home")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        rolePreferences = FakeRolePreferences()

        authRepository = mockk(relaxUnitFun = true)
        every { authRepository.isAuthenticated } returns authFlow
        every { authRepository.events } returns authEvents

        userRepository = mockk(relaxUnitFun = true)
        every { userRepository.getCurrentUser() } returns userFlow
        coEvery { userRepository.updateUserType(any(), any()) } returns Resource.Success(Unit)
        coEvery { userRepository.seedDemoUsers() } just Runs

        sessionManager = mockk(relaxUnitFun = true)
        coEvery { sessionManager.markAuthenticated(any(), any(), any(), any()) } just Runs
        every { sessionManager.authMode() } returns authModeFlow
        every { sessionManager.currentDemoUserId() } returns demoUserIdFlow

        startDestinationProvider = mockk()
        every { startDestinationProvider.configFor(UserType.GENERAL) } returns generalConfig
        every { startDestinationProvider.configFor(UserType.FARMER) } returns farmerConfig
        every { startDestinationProvider.configFor(UserType.ENTHUSIAST) } returns enthusiastConfig

        currentUserProvider = mockk()
        every { currentUserProvider.userIdOrNull() } returns TEST_USER_ID

        mockAuthManager = mockk(relaxUnitFun = true)
        every { mockAuthManager.isAuthenticated } returns mockDemoAuthFlow
        every { mockAuthManager.currentProfile } returns mockDemoProfileFlow
        every { mockAuthManager.allProfiles() } returns emptyList()
        every { mockAuthManager.profilesByRole(any()) } returns emptyList()
        coEvery { mockAuthManager.ensureSeeded() } just Runs
        coEvery { mockAuthManager.activateProfile(any()) } returns Resource.Success(mockk())
        coEvery { mockAuthManager.authenticate(any(), any()) } returns Resource.Error("not stubbed")
        coEvery { mockAuthManager.signOut() } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initializesWithUserRoleFromRepository() = runTest(dispatcher) {
        authFlow.value = true
        userFlow.value = Resource.Success(sampleUser(UserType.FARMER))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(UserType.FARMER, state.role)
        assertEquals(farmerConfig, state.navConfig)
        assertEquals(UserType.ENTHUSIAST, state.availableUpgrade)
    }

    @Test
    fun switchRole_updatesStateAndPersists() = runTest(dispatcher) {
        authFlow.value = true
        userFlow.value = Resource.Success(sampleUser(UserType.GENERAL))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.switchRole(UserType.ENTHUSIAST)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(UserType.ENTHUSIAST, state.role)
        assertEquals(enthusiastConfig, state.navConfig)
        assertEquals(null, state.availableUpgrade)
        assertEquals(UserType.ENTHUSIAST, rolePreferences.role.value)

        coVerify { userRepository.updateUserType(TEST_USER_ID, UserType.ENTHUSIAST) }
        coVerify { sessionManager.markAuthenticated(any(), UserType.ENTHUSIAST) }
    }

    @Test
    fun upgradeRole_movesToNextRole() = runTest(dispatcher) {
        authFlow.value = true
        userFlow.value = Resource.Success(sampleUser(UserType.GENERAL))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.upgradeRole()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(UserType.FARMER, state.role)
        assertEquals(farmerConfig, state.navConfig)
        assertEquals(UserType.ENTHUSIAST, state.availableUpgrade)

        coVerify { userRepository.updateUserType(TEST_USER_ID, UserType.FARMER) }
    }

    private fun createViewModel(): SessionViewModel {
        return SessionViewModel(
            authRepository = authRepository,
            userRepository = userRepository,
            sessionManager = sessionManager,
            rolePreferences = rolePreferences,
            startDestinationProvider = startDestinationProvider,
            currentUserProvider = currentUserProvider,
            mockAuthManager = mockAuthManager
        )
    }

    private fun sampleUser(role: UserType): UserEntity =
        UserEntity(
            userId = TEST_USER_ID,
            fullName = "Demo",
            userType = role,
            verificationStatus = VerificationStatus.VERIFIED
        )

    private class FakeRolePreferences(initial: UserType? = null) : RolePreferenceDataSource {
        private val state = MutableStateFlow<UserType?>(initial)
        override val role: StateFlow<UserType?> = state
        override fun persist(role: UserType) {
            state.value = role
        }
        override fun clear() {
            state.value = null
        }
    }

    companion object {
        private const val TEST_USER_ID = "user-123"
    }
}
