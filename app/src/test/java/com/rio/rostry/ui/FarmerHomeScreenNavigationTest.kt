package com.rio.rostry.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.rio.rostry.ui.farmer.FarmerHomeScreen
import com.rio.rostry.ui.farmer.FarmerHomeUiState
import com.rio.rostry.ui.farmer.FarmerHomeViewModel
import com.rio.rostry.ui.navigation.Routes
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FarmerHomeScreenNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `clicking Overdue Tasks card navigates to filtered monitoring tasks route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(tasksOverdueCount = 5)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Overdue Tasks").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.monitoringTasks("overdue")
        assertEquals(expectedRoute, capturedRoute)
    }

    @Test
    fun `clicking Overdue Vaccinations card navigates to filtered monitoring vaccination route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(vaccinationOverdueCount = 3)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Overdue Vaccinations").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.monitoringVaccinationWithFilter("overdue")
        assertEquals(expectedRoute, capturedRoute)
    }

    @Test
    fun `clicking Quarantine Updates Due card navigates to filtered monitoring quarantine route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(quarantineUpdatesDue = 2)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Quarantine Updates Due").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.monitoringQuarantine("due")
        assertEquals(expectedRoute, capturedRoute)
    }

    @Test
    fun `clicking Batches Ready to Split card navigates to filtered monitoring growth route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(batchesDueForSplit = 1)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Batches Ready to Split").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.monitoringGrowthWithFilter("ready_to_split")
        assertEquals(expectedRoute, capturedRoute)
    }

    @Test
    fun `clicking Ready to List card navigates to filtered products route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(productsReadyToListCount = 3)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Ready to List").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.productsWithFilter("ready_to_list")
        assertEquals(expectedRoute, capturedRoute)
    }

    @Test
    fun `clicking Eligible for Transfer card navigates to filtered transfers route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(productsEligibleForTransferCount = 4)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Eligible for Transfer").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.transfersWithFilter("ELIGIBLE")
        assertEquals(expectedRoute, capturedRoute)
    }

    @Test
    fun `clicking Vaccination Today card navigates to filtered monitoring vaccination route`() {
        // Given
        val mockViewModel = mockk<FarmerHomeViewModel>()
        var capturedRoute: String? = null
        val uiState = createTestUiState(vaccinationDueCount = 2, tasksOverdueCount = 0)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When
        composeRule.setContent {
            FarmerHomeScreen(
                viewModel = mockViewModel,
                onNavigateRoute = { route -> capturedRoute = route }
            )
        }

        // Then
        composeRule.onNodeWithText("Vaccination Today").performClick()
        composeRule.waitForIdle()

        val expectedRoute = Routes.Builders.monitoringVaccinationWithFilter("today")
        assertEquals(expectedRoute, capturedRoute)
    }

    private fun createTestUiState(
        tasksOverdueCount: Int = 0,
        vaccinationOverdueCount: Int = 0,
        quarantineUpdatesDue: Int = 0,
        batchesDueForSplit: Int = 0,
        productsReadyToListCount: Int = 0,
        productsEligibleForTransferCount: Int = 0,
        vaccinationDueCount: Int = 0
    ): FarmerHomeUiState {
        return FarmerHomeUiState(
            vaccinationDueCount = vaccinationDueCount,
            vaccinationOverdueCount = vaccinationOverdueCount,
            growthRecordsThisWeek = 0,
            quarantineActiveCount = 0,
            quarantineUpdatesDue = quarantineUpdatesDue,
            hatchingBatchesActive = 0,
            hatchingDueThisWeek = 0,
            mortalityLast7Days = 0,
            breedingPairsActive = 0,
            productsReadyToListCount = productsReadyToListCount,
            tasksDueCount = 0,
            tasksOverdueCount = tasksOverdueCount,
            dailyLogsThisWeek = 0,
            unreadAlerts = emptyList(),
            weeklySnapshot = null,
            batchesDueForSplit = batchesDueForSplit,
            urgentKpiCount = 0,
            isLoading = false,
            transfersPendingCount = 0,
            transfersAwaitingVerificationCount = 0,
            recentlyAddedBirdsCount = 0,
            recentlyAddedBatchesCount = 0,
            productsEligibleForTransferCount = productsEligibleForTransferCount,
            complianceAlertsCount = 0,
            kycVerified = true,
            dailyGoals = emptyList(),
            analyticsInsights = emptyList()
        )
    }


}
