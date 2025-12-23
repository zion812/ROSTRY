package com.rio.rostry.ui.farmer

import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.monitoring.DailyGoalRepository
import com.rio.rostry.data.repository.monitoring.MonitoringRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalCoroutinesApi::class)
class FarmerHomeViewModelTest {

    private lateinit var viewModel: FarmerHomeViewModel
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val rbacGuard: RbacGuard = mockk(relaxed = true)
    private val analyticsRepository: AnalyticsRepository = mockk(relaxed = true)
    private val dailyGoalRepository: DailyGoalRepository = mockk(relaxed = true)
    private val monitoringRepository: MonitoringRepository = mockk(relaxed = true)
    private val vaccinationRepository: VaccinationRepository = mockk(relaxed = true)
    private val dailyLogRepository: DailyLogRepository = mockk(relaxed = true)
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState updates verification status from repository`() = runTest {
        // Given
        val userId = "user123"
        val user = UserEntity(
            userId = userId,
            verificationStatus = "VERIFIED"
        )
        
        // Mock current user for ID
        every { userRepository.getCurrentUser() } returns flowOf(Resource.Success(user))
        // Mock get user by ID for status flow
        every { userRepository.getUserById(userId) } returns flowOf(Resource.Success(user))
        
        // Mock other repositories to return empty/default flows to prevent crash
        every { vaccinationRepository.getDueVaccinations(any()) } returns flowOf(emptyList())
        every { monitoringRepository.getComplianceAlerts(any()) } returns flowOf(emptyList())
        every { dailyGoalRepository.getDailyGoals(any()) } returns flowOf(emptyList())
        every { analyticsRepository.getActionableInsights(any()) } returns flowOf(emptyList())
        every { monitoringRepository.getRecentActivity(any()) } returns flowOf(emptyList())
        // Assuming other calls return safe defaults due to relaxed mock or explicit mocks if needed

        // When
        viewModel = FarmerHomeViewModel(
            userRepository,
            rbacGuard,
            analyticsRepository,
            dailyGoalRepository,
            monitoringRepository,
            vaccinationRepository,
            dailyLogRepository
        )

        // Trigger data loading
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(VerificationStatus.VERIFIED, state.verificationStatus)
    }
    
    @Test
    fun `uiState handles unverified status correctly`() = runTest {
         // Given
        val userId = "user123"
        val user = UserEntity(
            userId = userId,
            verificationStatus = "UNVERIFIED"
        )
        
        every { userRepository.getCurrentUser() } returns flowOf(Resource.Success(user))
        every { userRepository.getUserById(userId) } returns flowOf(Resource.Success(user))
        
        every { vaccinationRepository.getDueVaccinations(any()) } returns flowOf(emptyList())
        every { monitoringRepository.getComplianceAlerts(any()) } returns flowOf(emptyList())
        every { dailyGoalRepository.getDailyGoals(any()) } returns flowOf(emptyList())
        every { analyticsRepository.getActionableInsights(any()) } returns flowOf(emptyList())
        every { monitoringRepository.getRecentActivity(any()) } returns flowOf(emptyList())

        // When
        viewModel = FarmerHomeViewModel(
            userRepository,
            rbacGuard,
            analyticsRepository,
            dailyGoalRepository,
            monitoringRepository,
            vaccinationRepository,
            dailyLogRepository
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(VerificationStatus.UNVERIFIED, state.verificationStatus)
    }
}
