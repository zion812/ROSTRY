package com.rio.rostry.ui.monitoring

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rio.rostry.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive instrumented tests for Monitoring Modules.
 * Tests vaccination, growth tracking, quarantine, hatching, mortality, breeding flows,
 * task completion, offline monitoring, and farm data validation.
 *
 * Note: These tests use Hilt for dependency injection and Compose testing APIs.
 * Assumes navigation to monitoring screens is available (e.g., via FarmerHomeScreen).
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MonitoringModulesTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        // Assume user is logged in as Farmer and navigated to monitoring section
        // In real tests, might need to mock login or navigate from home
    }

    // ============================================
    // VACCINATION TESTS
    // ============================================

    @Test
    fun testVaccinationSchedulingFlow() {
        composeTestRule.apply {
            // Navigate to vaccination screen (assume via navigation)
            // For this test, assume we can directly test the screen or navigate
            waitForIdle()

            // Assume we are on VaccinationScheduleScreen
            onNodeWithText("Vaccination").assertIsDisplayed()

            // Enter product ID
            onNodeWithTag("product_id_input").performTextInput("TEST_PRODUCT_123")
            onNodeWithText("Load").performClick()
            waitForIdle()

            // Enter vaccine type
            onNodeWithTag("vaccine_type_input").performTextInput("Newcastle Disease")
            onNodeWithText("Pick Schedule Date").performClick()
            waitForIdle()

            // Select date (assume date picker is shown)
            onNodeWithText("Schedule").performClick()
            waitForIdle()

            // Verify scheduled
            onNodeWithText("scheduled", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testVaccinationTaskCompletionFlow() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Vaccination").assertIsDisplayed()

            // Assume due tasks are shown
            onNodeWithText("Due").performClick()
            waitForIdle()

            // Click complete on first task
            onNodeWithText("Complete").performClick()
            waitForIdle()

            // Fill completion dialog
            onNodeWithTag("complete_vaccine_type").performTextInput("Newcastle")
            onNodeWithTag("complete_batch_code").performTextInput("BATCH_001")
            onNodeWithText("Save & Complete").performClick()
            waitForIdle()

            // Verify completion
            onNodeWithText("completed", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testVaccinationHistoryTimeline() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Vaccination").assertIsDisplayed()

            // Load product with records
            onNodeWithTag("product_id_input").performTextInput("TEST_PRODUCT_123")
            onNodeWithText("Load").performClick()
            waitForIdle()

            // Verify history section
            onNodeWithText("Vaccination History").assertIsDisplayed()
            onNodeWithText("Scheduled:").assertExists()
        }
    }

    // ============================================
    // GROWTH TRACKING TESTS
    // ============================================

    @Test
    fun testGrowthRecordCreation() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Growth Tracking").assertIsDisplayed()

            // Enter product ID
            onNodeWithTag("growth_product_id").performTextInput("TEST_PRODUCT_456")
            waitForIdle()

            // Enter weight and height
            onNodeWithTag("weight_input").performTextInput("1500")
            onNodeWithTag("height_input").performTextInput("25")
            onNodeWithText("Record today").performClick()
            waitForIdle()

            // Verify record created
            onNodeWithText("records: 1").assertExists()
        }
    }

    @Test
    fun testGrowthChartDisplay() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Growth Tracking").assertIsDisplayed()

            // Load product with multiple records
            onNodeWithTag("growth_product_id").performTextInput("TEST_PRODUCT_456")
            waitForIdle()

            // Verify chart is displayed (assuming chart component has test tag)
            onNodeWithTag("growth_chart").assertExists()
        }
    }

    @Test
    fun testGrowthToMarketplaceListing() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Growth Tracking").assertIsDisplayed()

            // Load product with records
            onNodeWithTag("growth_product_id").performTextInput("TEST_PRODUCT_456")
            waitForIdle()

            // Click list on marketplace
            onNodeWithText("List on Marketplace").performClick()
            waitForIdle()

            // Verify navigation to listing (or success message)
            onNodeWithText("listed", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // QUARANTINE TESTS
    // ============================================

    @Test
    fun testQuarantineStartFlow() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Quarantine Management").assertIsDisplayed()

            // Enter reason
            onNodeWithTag("quarantine_reason").performTextInput("Suspected infection")
            onNodeWithText("Start Quarantine").performClick()
            waitForIdle()

            // Verify started
            onNodeWithText("started", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testQuarantineUpdateFlow() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Quarantine Management").assertIsDisplayed()

            // Assume active quarantine exists
            onNodeWithText("Update").performClick()
            waitForIdle()

            // Fill update dialog
            onNodeWithTag("vet_notes").performTextInput("Bird showing improvement")
            onNodeWithText("IMPROVING").performClick()
            onNodeWithText("Save Update").performClick()
            waitForIdle()

            // Verify updated
            onNodeWithText("updated", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testQuarantineDischargeFlow() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Quarantine Management").assertIsDisplayed()

            // Assume quarantine can be discharged
            onNodeWithText("Discharge").performClick()
            waitForIdle()

            // Verify discharged
            onNodeWithText("discharged", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // HATCHING TESTS
    // ============================================

    @Test
    fun testHatchingBatchCreation() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Hatching").assertIsDisplayed()

            // Create new batch
            onNodeWithText("New Batch").performClick()
            waitForIdle()

            // Fill batch details
            onNodeWithTag("batch_name").performTextInput("Batch_001")
            onNodeWithTag("egg_count").performTextInput("100")
            onNodeWithText("Create").performClick()
            waitForIdle()

            // Verify created
            onNodeWithText("created", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testHatchingOutcomeLogging() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Hatching").assertIsDisplayed()

            // Assume batch exists
            onNodeWithText("Log Outcome").performClick()
            waitForIdle()

            // Fill outcome
            onNodeWithTag("hatched_count").performTextInput("85")
            onNodeWithTag("unhatched_count").performTextInput("15")
            onNodeWithText("Save").performClick()
            waitForIdle()

            // Verify logged
            onNodeWithText("logged", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // MORTALITY TESTS
    // ============================================

    @Test
    fun testMortalityRecordingWithAllFields() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Mortality").assertIsDisplayed()

            // Record mortality
            onNodeWithText("Record Mortality").performClick()
            waitForIdle()

            // Fill all fields
            onNodeWithTag("product_id").performTextInput("TEST_PRODUCT_789")
            onNodeWithTag("cause").performTextInput("Disease")
            onNodeWithTag("age_days").performTextInput("30")
            onNodeWithTag("weight").performTextInput("1200")
            onNodeWithText("Save").performClick()
            waitForIdle()

            // Verify recorded
            onNodeWithText("recorded", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // BREEDING TESTS
    // ============================================

    @Test
    fun testBreedingPairCreation() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Breeding").assertIsDisplayed()

            // Create pair
            onNodeWithText("New Pair").performClick()
            waitForIdle()

            // Fill pair details
            onNodeWithTag("male_id").performTextInput("MALE_001")
            onNodeWithTag("female_id").performTextInput("FEMALE_001")
            onNodeWithText("Create").performClick()
            waitForIdle()

            // Verify created
            onNodeWithText("created", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testEggCollectionFlow() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Breeding").assertIsDisplayed()

            // Assume pair exists
            onNodeWithText("Collect Eggs").performClick()
            waitForIdle()

            // Fill collection
            onNodeWithTag("eggs_collected").performTextInput("12")
            onNodeWithText("Save").performClick()
            waitForIdle()

            // Verify collected
            onNodeWithText("collected", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // TASK COMPLETION TESTS
    // ============================================

    @Test
    fun testTaskCompletionAcrossModules() {
        composeTestRule.apply {
            waitForIdle()

            // Test vaccination task completion (already covered above)
            onNodeWithText("Vaccination").assertIsDisplayed()
            onNodeWithText("Complete").performClick()
            waitForIdle()
            onNodeWithText("completed").assertExists()

            // Test growth task (if applicable)
            // Assuming growth has tasks
            onNodeWithText("Growth Tracking").assertIsDisplayed()
            onNodeWithText("Record today").performClick()
            waitForIdle()
            onNodeWithText("recorded").assertExists()

            // Test quarantine task
            onNodeWithText("Quarantine Management").assertIsDisplayed()
            onNodeWithText("Update").performClick()
            waitForIdle()
            onNodeWithText("updated").assertExists()
        }
    }

    // ============================================
    // OFFLINE MONITORING TESTS
    // ============================================

    @Test
    fun testOfflineMonitoringWithSync() {
        composeTestRule.apply {
            waitForIdle()

            // Assume offline mode (would need network mocking in real test)
            onNodeWithText("Vaccination").assertIsDisplayed()

            // Perform actions offline
            onNodeWithTag("product_id_input").performTextInput("OFFLINE_PRODUCT")
            onNodeWithText("Load").performClick()
            waitForIdle()

            // Verify offline banner or queued message
            onNodeWithText("offline", ignoreCase = true, substring = true).assertExists()

            // On sync (simulated), verify data appears
            // In real test, would trigger sync and verify
        }
    }

    // ============================================
    // FARM DATA VALIDATION TESTS
    // ============================================

    @Test
    fun testFarmDataValidationForMarketplaceListing() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Growth Tracking").assertIsDisplayed()

            // Load product
            onNodeWithTag("growth_product_id").performTextInput("INVALID_PRODUCT")
            waitForIdle()

            // Try to list
            onNodeWithText("List on Marketplace").performClick()
            waitForIdle()

            // Verify validation error
            onNodeWithText("validation failed", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testValidFarmDataAllowsListing() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithText("Growth Tracking").assertIsDisplayed()

            // Load valid product
            onNodeWithTag("growth_product_id").performTextInput("VALID_PRODUCT")
            waitForIdle()

            // List successfully
            onNodeWithText("List on Marketplace").performClick()
            waitForIdle()

            // Verify success
            onNodeWithText("listed successfully", ignoreCase = true, substring = true).assertExists()
        }
    }
}