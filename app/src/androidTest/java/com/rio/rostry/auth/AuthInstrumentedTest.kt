package com.rio.rostry.auth

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.ui.auth.OtpScreen
import com.rio.rostry.ui.auth.PhoneInputScreen
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testPhoneInputRendersAndValidates() {
        composeTestRule.setContent {
            PhoneInputScreen(viewModel = androidx.hilt.navigation.compose.hiltViewModel(), onNavigateToOtp = {}, activity = null)
        }

        // Enter invalid phone
        composeTestRule.onNodeWithText("Phone (+91XXXXXXXXXX)").performTextInput("invalid")
        // Button should be disabled
        composeTestRule.onNodeWithText("Send OTP").assertIsNotEnabled()

        // Enter valid phone
        composeTestRule.onNodeWithText("Phone (+91XXXXXXXXXX)").performTextInput("+919876543210")
        // With null activity the button remains disabled to avoid calling Firebase
        composeTestRule.onNodeWithText("Send OTP").assertIsNotEnabled()
    }

    @Test
    fun testOtpSuccessFlow() {
        composeTestRule.setContent {
            OtpScreen(verificationId = "testVid", viewModel = androidx.hilt.navigation.compose.hiltViewModel(), onNavigateHome = {})
        }

        // Enter OTP
        composeTestRule.onNodeWithContentDescription("Enter 6-digit OTP").performTextInput("123456")

        // Verify button is enabled with valid OTP
        composeTestRule.onNodeWithContentDescription("Verify OTP").assertIsEnabled()
    }

    @Test
    fun testOtpInvalidInputDisablesButton() {
        composeTestRule.setContent {
            OtpScreen(verificationId = "testVid", viewModel = androidx.hilt.navigation.compose.hiltViewModel(), onNavigateHome = {})
        }
        // Enter invalid OTP (less than 6 digits)
        composeTestRule.onNodeWithContentDescription("Enter 6-digit OTP").performTextInput("123")
        // Button should be disabled
        composeTestRule.onNodeWithContentDescription("Verify OTP").assertIsNotEnabled()
    }

    @Test
    fun testOtpScreenRendersCorrectly() {
        composeTestRule.setContent {
            OtpScreen(verificationId = "testVid", viewModel = androidx.hilt.navigation.compose.hiltViewModel(), onNavigateHome = {})
        }

        // Verify OTP screen renders with proper elements
        composeTestRule.onNodeWithContentDescription("Enter 6-digit OTP").assertIsEnabled()
        composeTestRule.onNodeWithContentDescription("Verify OTP").assertIsNotEnabled()
    }
}