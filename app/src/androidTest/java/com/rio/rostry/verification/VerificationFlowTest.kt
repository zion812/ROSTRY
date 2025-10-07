package com.rio.rostry.verification

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.rio.rostry.ui.verification.FarmerLocationVerificationScreen
import com.rio.rostry.ui.verification.EnthusiastKycScreen

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class VerificationFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    // No custom bindings; we rely on app DI. Tests below avoid triggering uploads.

    @Test
    fun farmer_validationErrors_shown_whenMissingInputs() {
        hiltRule.inject()
        composeRule.setContent {
            FarmerLocationVerificationScreen(onDone = {})
        }
        // Enter lat/lng but do not upload images
        composeRule.onNodeWithText("Enter Farm Latitude").performTextInput("12.9716")
        composeRule.onNodeWithText("Enter Farm Longitude").performTextInput("77.5946")
        composeRule.onNodeWithText("Submit Location & Documents").performClick()
        // Expect error about missing selfie/photos
        composeRule.onNodeWithText("Please fix the following:").assertExists()
    }

    @Test
    fun enthusiast_validationErrors_shown_whenMissingInputs() {
        hiltRule.inject()
        composeRule.setContent {
            EnthusiastKycScreen(onDone = {})
        }
        // No uploads; click submit
        composeRule.onNodeWithText("Submit KYC with Documents").performClick()
        composeRule.onNodeWithText("Please upload at least one ID proof and one selfie").assertExists()
    }
}
