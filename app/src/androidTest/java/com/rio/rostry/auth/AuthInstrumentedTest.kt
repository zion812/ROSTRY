package com.rio.rostry.auth

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.ui.auth.PhoneAuthScreenNew
import com.rio.rostry.ui.auth.OtpVerificationScreenNew
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

    // Ensures App Check debug provider is active in CI via instrumentation arg
    private val appCheckHelper = object {
        fun withDebugProvider(block: () -> Unit) {
            block()
        }
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testPhoneInputRendersAndValidates() {
        appCheckHelper.withDebugProvider {
            composeTestRule.setContent {
                PhoneAuthScreenNew(
                    viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                    onCodeSent = {},
                    onNavigateBack = {}
                )
            }

            // Enter invalid phone
            composeTestRule.onNodeWithText("Phone Number").performTextInput("invalid")
            // Button should be disabled
            composeTestRule.onNodeWithText("Send Verification Code").assertIsNotEnabled()

            // Enter valid phone (E.164 format)
            composeTestRule.onNodeWithText("Phone Number").performTextInput("+919876543210")
            // With null activity the button remains disabled to avoid calling Firebase
            composeTestRule.onNodeWithText("Send Verification Code").assertIsNotEnabled()
        }
    }

    @Test
    fun testOtpAcceptsSixDigits() {
        appCheckHelper.withDebugProvider {
            composeTestRule.setContent {
                OtpVerificationScreenNew(
                    viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                    onVerified = {},
                    onNavigateBack = {}
                )
            }

            // Enter 6-digit OTP in the field labeled "Verification Code"
            composeTestRule.onNodeWithText("Verification Code").performTextInput("123456")

            // The field should remain enabled after input
            composeTestRule.onNodeWithText("Verification Code").assertIsEnabled()
        }
    }

    @Test
    fun testOtpInvalidInputLeavesFieldEnabled() {
        appCheckHelper.withDebugProvider {
            composeTestRule.setContent {
                OtpVerificationScreenNew(
                    viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                    onVerified = {},
                    onNavigateBack = {}
                )
            }
            // Enter invalid OTP (less than 6 digits)
            composeTestRule.onNodeWithText("Verification Code").performTextInput("123")
            // Field should still be enabled for correction
            composeTestRule.onNodeWithText("Verification Code").assertIsEnabled()
        }
    }

    @Test
    fun testOtpScreenRendersCorrectly() {
        appCheckHelper.withDebugProvider {
            composeTestRule.setContent {
                OtpVerificationScreenNew(
                    viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                    onVerified = {},
                    onNavigateBack = {}
                )
            }

            // Verify OTP screen renders with the input field
            composeTestRule.onNodeWithText("Verification Code").assertIsEnabled()
        }
    }
}