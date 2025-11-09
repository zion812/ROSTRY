package com.rio.rostry.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rio.rostry.MainActivity
import com.rio.rostry.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Comprehensive instrumented tests for Profile Edit Flow.
 * Tests profile edit screen rendering, field validation, successful updates,
 * error handling, unsaved changes dialog, photo upload, role-specific fields,
 * and offline sync.
 * 
 * Note: These tests use Hilt for dependency injection and Compose testing APIs.
 * Idling resources are automatically handled by Compose test framework.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ProfileEditFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // ============================================
    // PROFILE EDIT SCREEN RENDERING TESTS
    // ============================================

    @Test
    fun testProfileEditScreenRendersWithCurrentUserData() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile tab
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()

            // Click Edit Profile button
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Verify screen renders with form fields
            onNodeWithText("Edit Profile", substring = true).assertIsDisplayed()
            onNodeWithText("Name", substring = true).assertExists()
            onNodeWithText("Email", substring = true).assertExists()
            onNodeWithText("Phone", substring = true).assertExists()
            onNodeWithText("Bio", substring = true).assertExists()

            // Verify current user data is populated (assuming test user exists)
            onNodeWithText("Test User", substring = true).assertExists()
        }
    }

    @Test
    fun testRoleSpecificFieldsDisplayForFarmer() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit (assuming user is Farmer)
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Verify Farmer-specific fields
            onNodeWithText("Farm Location", substring = true).assertExists()
            onNodeWithText("Farm Address", substring = true).assertExists()
        }
    }

    @Test
    fun testRoleSpecificFieldsDisplayForEnthusiast() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit (assuming user is Enthusiast)
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Verify Enthusiast-specific fields
            onNodeWithText("KYC Level", substring = true).assertExists()
            onNodeWithText("Verification Status", substring = true).assertExists()
        }
    }

    // ============================================
    // FIELD VALIDATION TESTS
    // ============================================

    @Test
    fun testEmailFieldValidation() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Clear and enter invalid email
            onNodeWithTag("profile_edit_email").performTextClearance()
            onNodeWithTag("profile_edit_email").performTextInput("invalid-email")
            waitForIdle()

            // Verify validation error
            onNodeWithText("Invalid email format", substring = true).assertExists()

            // Enter valid email
            onNodeWithTag("profile_edit_email").performTextClearance()
            onNodeWithTag("profile_edit_email").performTextInput("test@example.com")
            waitForIdle()

            // Verify error is gone
            onNodeWithText("Invalid email format", substring = true).assertDoesNotExist()
        }
    }

    @Test
    fun testPhoneFieldValidation() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Clear and enter invalid phone
            onNodeWithTag("profile_edit_phone").performTextClearance()
            onNodeWithTag("profile_edit_phone").performTextInput("123")
            waitForIdle()

            // Verify validation error
            onNodeWithText("Invalid phone format", substring = true).assertExists()

            // Enter valid phone
            onNodeWithTag("profile_edit_phone").performTextClearance()
            onNodeWithTag("profile_edit_phone").performTextInput("+919876543210")
            waitForIdle()

            // Verify error is gone
            onNodeWithText("Invalid phone format", substring = true).assertDoesNotExist()
        }
    }

    @Test
    fun testRequiredFieldsValidation() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Clear required name field
            onNodeWithTag("profile_edit_name").performTextClearance()
            waitForIdle()

            // Try to save
            onNodeWithText("Save", substring = true).performClick()
            waitForIdle()

            // Verify validation error for required field
            onNodeWithText("Name is required", substring = true).assertExists()
        }
    }

    // ============================================
    // SUCCESSFUL UPDATE FLOW TESTS
    // ============================================

    @Test
    fun testSuccessfulProfileUpdateFlow() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Update fields
            onNodeWithTag("profile_edit_name").performTextClearance()
            onNodeWithTag("profile_edit_name").performTextInput("Updated Name")
            onNodeWithTag("profile_edit_bio").performTextClearance()
            onNodeWithTag("profile_edit_bio").performTextInput("Updated bio")
            waitForIdle()

            // Click Save
            onNodeWithText("Save", substring = true).performClick()
            waitForIdle()

            // Verify success message
            onNodeWithText("Profile updated successfully", substring = true).assertExists()

            // Verify navigation back to profile
            onNodeWithText("Profile", substring = true).assertIsDisplayed()
        }
    }

    // ============================================
    // ERROR HANDLING TESTS
    // ============================================

    @Test
    fun testNetworkFailureErrorHandling() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Update a field
            onNodeWithTag("profile_edit_name").performTextClearance()
            onNodeWithTag("profile_edit_name").performTextInput("Network Fail Test")
            waitForIdle()

            // Click Save (assuming network failure is mocked or simulated)
            onNodeWithText("Save", substring = true).performClick()
            waitForIdle()

            // Verify error message
            onNodeWithText("Failed to update profile", substring = true).assertExists()
            onNodeWithText("network", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testValidationErrorHandling() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Enter invalid data
            onNodeWithTag("profile_edit_email").performTextClearance()
            onNodeWithTag("profile_edit_email").performTextInput("invalid")
            onNodeWithTag("profile_edit_name").performTextClearance()
            waitForIdle()

            // Click Save
            onNodeWithText("Save", substring = true).performClick()
            waitForIdle()

            // Verify validation errors are shown
            onNodeWithText("Invalid email format", substring = true).assertExists()
            onNodeWithText("Name is required", substring = true).assertExists()
        }
    }

    // ============================================
    // UNSAVED CHANGES WARNING TESTS
    // ============================================

    @Test
    fun testUnsavedChangesWarningDialog() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Make changes
            onNodeWithTag("profile_edit_bio").performTextClearance()
            onNodeWithTag("profile_edit_bio").performTextInput("Unsaved changes test")
            waitForIdle()

            // Try to navigate back (using system back or cancel button)
            onNodeWithContentDescription("Navigate back").performClick()
            waitForIdle()

            // Verify unsaved changes dialog appears
            onNodeWithText("Unsaved changes", substring = true).assertExists()
            onNodeWithText("Discard", substring = true).assertExists()
            onNodeWithText("Save", substring = true).assertExists()
        }
    }

    @Test
    fun testUnsavedChangesDiscardAction() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Make changes
            onNodeWithTag("profile_edit_bio").performTextInput("Test changes")
            waitForIdle()

            // Navigate back and discard
            onNodeWithContentDescription("Navigate back").performClick()
            waitForIdle()
            onNodeWithText("Discard", substring = true).performClick()
            waitForIdle()

            // Verify navigated back without saving
            onNodeWithText("Profile", substring = true).assertIsDisplayed()
        }
    }

    // ============================================
    // PHOTO UPLOAD TESTS
    // ============================================

    @Test
    fun testPhotoUploadFunctionality() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Click photo upload button
            onNodeWithContentDescription("Change profile photo").performClick()
            waitForIdle()

            // Verify photo picker or camera options appear
            onNodeWithText("Camera", substring = true).assertExists()
            onNodeWithText("Gallery", substring = true).assertExists()
        }
    }

    // ============================================
    // OFFLINE SYNC TESTS
    // ============================================

    @Test
    fun testOfflineProfileEditWithSync() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Make changes offline
            onNodeWithTag("profile_edit_name").performTextClearance()
            onNodeWithTag("profile_edit_name").performTextInput("Offline Update")
            waitForIdle()

            // Save (assuming offline - would queue the update)
            onNodeWithText("Save", substring = true).performClick()
            waitForIdle()

            // Verify offline success message or queued indication
            onNodeWithText("saved offline", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // ADDITIONAL UI TESTS
    // ============================================

    @Test
    fun testProfileEditCancelButton() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Click Cancel
            onNodeWithText("Cancel", substring = true).performClick()
            waitForIdle()

            // Verify navigation back without saving
            onNodeWithText("Profile", substring = true).assertIsDisplayed()
        }
    }

    @Test
    fun testProfileEditLoadingState() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Profile Edit
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Edit Profile", substring = true).performClick()
            waitForIdle()

            // Make changes and save
            onNodeWithTag("profile_edit_name").performTextClearance()
            onNodeWithTag("profile_edit_name").performTextInput("Loading Test")
            onNodeWithText("Save", substring = true).performClick()
            waitForIdle()

            // Verify loading indicator appears during save
            onNodeWithContentDescription("Saving profile").assertExists()
        }
    }
}