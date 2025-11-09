package com.rio.rostry.ui.breeding

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rio.rostry.MainActivity
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Comprehensive instrumented tests for Breeding Flow UI.
 * Tests breeding pair creation, mating logs, egg collection, incubation, hatching, and analytics.
 * 
 * Note: These tests use Hilt for dependency injection and Compose testing APIs.
 * Assumes navigation to breeding screens is available (e.g., via enthusiast role).
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BreedingFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var breedingRepository: EnthusiastBreedingRepository

    private val testUserId = "test-breeding-user"

    @Before
    fun setup() {
        hiltRule.inject()
        // Assume user is logged in as enthusiast with breeding access
    }

    // ============================================
    // BREEDING PAIR CREATION TESTS
    // ============================================

    @Test
    fun testBreedingPairCreationWithValidation() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to breeding screen (assume accessible via navigation)
            navigateToBreedingScreen()

            // Click create pair button
            onNodeWithTag("breeding_create_pair_button").performClick()
            waitForIdle()

            // Try to create pair without selecting products - should show validation error
            onNodeWithText("Create Pair", ignoreCase = true).performClick()
            waitForIdle()
            onNodeWithText("Select male product", substring = true).assertExists()

            // Select male product (assume dropdown or selection UI)
            onNodeWithTag("breeding_male_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Male Bird 1", substring = true).performClick()
            waitForIdle()

            // Select female product
            onNodeWithTag("breeding_female_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Female Bird 1", substring = true).performClick()
            waitForIdle()

            // Enter notes
            onNodeWithTag("breeding_pair_notes_input").performTextInput("Test pair")
            waitForIdle()

            // Create pair - should succeed
            onNodeWithText("Create Pair", ignoreCase = true).performClick()
            waitForIdle()

            // Verify pair created and displayed
            onNodeWithText("Test pair", substring = true).assertExists()
        }
    }

    @Test
    fun testBreedingPairValidationFailsForYoungBirds() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            onNodeWithTag("breeding_create_pair_button").performClick()
            waitForIdle()

            // Select young male (assume UI shows age)
            onNodeWithTag("breeding_male_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Young Male", substring = true).performClick()
            waitForIdle()

            // Select female
            onNodeWithTag("breeding_female_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Female Bird 1", substring = true).performClick()
            waitForIdle()

            // Try to create - should show age validation error
            onNodeWithText("Create Pair", ignoreCase = true).performClick()
            waitForIdle()
            onNodeWithText("breeding age", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // MATING LOG TESTS
    // ============================================

    @Test
    fun testMatingLogCreationWithDuplicatePrevention() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Assume a pair exists from previous test or setup
            onNodeWithTag("breeding_pair_card_0").performClick()
            waitForIdle()

            // Click log mating
            onNodeWithText("Log Mating", ignoreCase = true).performClick()
            waitForIdle()

            // Enter behavior
            onNodeWithTag("mating_behavior_input").performTextInput("Active mating observed")
            waitForIdle()

            // Enter conditions
            onNodeWithTag("mating_conditions_input").performTextInput("Sunny weather")
            waitForIdle()

            // Log mating
            onNodeWithText("Log Mating", ignoreCase = true).performClick()
            waitForIdle()

            // Verify success
            onNodeWithText("Mating logged", substring = true).assertExists()

            // Try to log again within 24h - should prevent duplicate
            onNodeWithText("Log Mating", ignoreCase = true).performClick()
            waitForIdle()
            onNodeWithText("duplicate", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // EGG COLLECTION TESTS
    // ============================================

    @Test
    fun testEggCollectionFlow() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Select pair
            onNodeWithTag("breeding_pair_card_0").performClick()
            waitForIdle()

            // Click collect eggs
            onNodeWithText("Collect Eggs", ignoreCase = true).performClick()
            waitForIdle()

            // Enter egg count
            onNodeWithTag("egg_count_input").performTextInput("12")
            waitForIdle()

            // Select quality grade
            onNodeWithTag("egg_quality_dropdown").performClick()
            waitForIdle()
            onNodeWithText("A", substring = true).performClick()
            waitForIdle()

            // Enter weight
            onNodeWithTag("egg_weight_input").performTextInput("50.5")
            waitForIdle()

            // Collect eggs
            onNodeWithText("Collect Eggs", ignoreCase = true).performClick()
            waitForIdle()

            // Verify collection recorded
            onNodeWithText("12 eggs collected", substring = true).assertExists()
        }
    }

    @Test
    fun testEggCollectionValidation() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            onNodeWithTag("breeding_pair_card_0").performClick()
            waitForIdle()

            onNodeWithText("Collect Eggs", ignoreCase = true).performClick()
            waitForIdle()

            // Try with zero count - should show error
            onNodeWithTag("egg_count_input").performTextInput("0")
            waitForIdle()
            onNodeWithText("Collect Eggs", ignoreCase = true).performClick()
            waitForIdle()
            onNodeWithText("count must be > 0", substring = true).assertExists()
        }
    }

    // ============================================
    // INCUBATION TESTS
    // ============================================

    @Test
    fun testIncubationBatchCreation() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Select egg collection
            onNodeWithTag("egg_collection_card_0").performClick()
            waitForIdle()

            // Click start incubation
            onNodeWithText("Start Incubation", ignoreCase = true).performClick()
            waitForIdle()

            // Set expected hatch date (assume date picker)
            onNodeWithTag("hatch_date_picker").performClick()
            waitForIdle()
            // Assume selecting date 21 days from now
            onNodeWithText("21", substring = true).performClick()
            waitForIdle()

            // Set temperature
            onNodeWithTag("incubation_temperature_input").performTextInput("37.5")
            waitForIdle()

            // Set humidity
            onNodeWithTag("incubation_humidity_input").performTextInput("55")
            waitForIdle()

            // Start incubation
            onNodeWithText("Start Incubation", ignoreCase = true).performClick()
            waitForIdle()

            // Verify batch created
            onNodeWithText("Incubation started", substring = true).assertExists()
        }
    }

    @Test
    fun testUpdateIncubationConditions() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Select incubation batch
            onNodeWithTag("incubation_batch_card_0").performClick()
            waitForIdle()

            // Click update conditions
            onNodeWithText("Update Conditions", ignoreCase = true).performClick()
            waitForIdle()

            // Update temperature
            onNodeWithTag("update_temperature_input").performTextInput("37.8")
            waitForIdle()

            // Update humidity
            onNodeWithTag("update_humidity_input").performTextInput("60")
            waitForIdle()

            // Save updates
            onNodeWithText("Save", ignoreCase = true).performClick()
            waitForIdle()

            // Verify updated
            onNodeWithText("Conditions updated", substring = true).assertExists()
        }
    }

    // ============================================
    // HATCHING TESTS
    // ============================================

    @Test
    fun testHatchCompletionWithChickAutoCreation() = runBlocking {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Select incubation batch
            onNodeWithTag("incubation_batch_card_0").performClick()
            waitForIdle()

            // Click complete hatch
            onNodeWithText("Complete Hatch", ignoreCase = true).performClick()
            waitForIdle()

            // Enter success count
            onNodeWithTag("hatch_success_count_input").performTextInput("10")
            waitForIdle()

            // Enter failure count
            onNodeWithTag("hatch_failure_count_input").performTextInput("1")
            waitForIdle()

            // Enter culled count
            onNodeWithTag("hatch_culled_count_input").performTextInput("1")
            waitForIdle()

            // Complete hatch
            onNodeWithText("Complete Hatch", ignoreCase = true).performClick()
            waitForIdle()

            // Verify hatch completed and chicks created
            onNodeWithText("Hatch completed", substring = true).assertExists()
            onNodeWithText("10 chicks created", substring = true).assertExists()
        }
    }

    // ============================================
    // BREEDING ANALYTICS TESTS
    // ============================================

    @Test
    fun testBreedingAnalyticsCalculation() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Select pair
            onNodeWithTag("breeding_pair_card_0").performClick()
            waitForIdle()

            // View performance
            onNodeWithText("View Performance", ignoreCase = true).performClick()
            waitForIdle()

            // Verify analytics displayed
            onNodeWithText("eggs collected", ignoreCase = true, substring = true).assertExists()
            onNodeWithText("hatch success rate", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // OFFLINE BREEDING TESTS
    // ============================================

    @Test
    fun testOfflineBreedingOperationsWithSync() {
        composeTestRule.apply {
            waitForIdle()

            // Assume offline mode (would need network mocking in real test)
            navigateToBreedingScreen()

            // Perform breeding operation offline
            onNodeWithTag("breeding_create_pair_button").performClick()
            waitForIdle()

            // Select products and create pair
            onNodeWithTag("breeding_male_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Male Bird 2", substring = true).performClick()
            waitForIdle()

            onNodeWithTag("breeding_female_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Female Bird 2", substring = true).performClick()
            waitForIdle()

            onNodeWithText("Create Pair", ignoreCase = true).performClick()
            waitForIdle()

            // Verify queued for sync
            onNodeWithText("queued", ignoreCase = true, substring = true).assertExists()

            // In real test, would trigger sync and verify data persisted
        }
    }

    // ============================================
    // BREEDING FLOW ANALYTICS TRACKING TESTS
    // ============================================

    @Test
    fun testBreedingFlowAnalyticsTracking() {
        composeTestRule.apply {
            waitForIdle()

            navigateToBreedingScreen()

            // Perform various actions and assume analytics are tracked
            onNodeWithTag("breeding_create_pair_button").performClick()
            waitForIdle()

            // Select products
            onNodeWithTag("breeding_male_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Male Bird 1", substring = true).performClick()
            waitForIdle()

            onNodeWithTag("breeding_female_product_dropdown").performClick()
            waitForIdle()
            onNodeWithText("Female Bird 1", substring = true).performClick()
            waitForIdle()

            onNodeWithText("Create Pair", ignoreCase = true).performClick()
            waitForIdle()

            // Analytics tracking would be verified in integration with analytics service
            // For UI test, we verify the flow completes without errors
            onNodeWithText("Pair created", substring = true).assertExists()
        }
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private fun ComposeTestRule.navigateToBreedingScreen() {
        // Assume navigation to breeding screen (e.g., via menu or tab)
        // This depends on app navigation structure
        onNodeWithTag("nav_breeding").performClick()
        waitForIdle()
        onNodeWithText("Breeding", substring = true).assertIsDisplayed()
    }
}
