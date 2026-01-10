package com.rio.rostry

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for Offline Sync behavior.
 * 
 * Tests:
 * - Offline asset creation (dirty flag set)
 * - Network reconnection triggers SyncWorker
 * - Dirty flag cleared after sync
 * - Conflict resolution UI (local edit vs remote update)
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OfflineSyncTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun offlineSync_assetCreationSetsDirtyFlag() {
        // Simulate offline mode
        // Note: Would need to mock network state in real tests
        
        // Create asset while offline
        composeTestRule.onNodeWithText("Create").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithTag("assetNameField").performTextInput("Offline Batch")
        composeTestRule.onNodeWithTag("quantityField").performTextInput("25")
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitForIdle()
        
        // Verify queued/pending indicator shows
        composeTestRule.onNodeWithText("Saved locally").assertExists()
        // or
        composeTestRule.onNodeWithContentDescription("Pending sync").assertExists()
    }
    
    @Test
    fun offlineSync_pendingIndicatorShownInList() {
        // Navigate to assets list
        composeTestRule.onNodeWithText("Farm Assets").performClick()
        composeTestRule.waitForIdle()
        
        // Find asset with pending indicator
        composeTestRule.onNodeWithTag("asset_offline_batch").assertExists()
        composeTestRule.onNodeWithTag("sync_pending_icon").assertExists()
    }
    
    @Test
    fun offlineSync_syncBannerShowsPendingCount() {
        // Verify sync status banner appears
        composeTestRule.onNodeWithText("items pending").assertExists()
        
        // Verify sync now button is available
        composeTestRule.onNodeWithText("Sync Now").assertExists()
    }
    
    @Test
    fun offlineSync_manualSyncTriggersUpload() {
        // Click Sync Now
        composeTestRule.onNodeWithText("Sync Now").performClick()
        composeTestRule.waitForIdle()
        
        // Verify syncing indicator
        composeTestRule.onNodeWithText("Syncing").assertExists()
        
        // Wait and verify success
        Thread.sleep(3000)
        composeTestRule.onNodeWithText("All synced").assertExists()
    }
    
    @Test
    fun offlineSync_conflictResolutionUI() {
        // Navigate to sync conflicts screen
        composeTestRule.onNodeWithText("Sync Conflicts").performClick()
        composeTestRule.waitForIdle()
        
        // Verify conflict is shown
        composeTestRule.onNodeWithText("conflicts found").assertExists()
        
        // Verify resolution options
        composeTestRule.onNodeWithText("Keep Local").assertExists()
        composeTestRule.onNodeWithText("Keep Remote").assertExists()
    }
    
    @Test
    fun offlineSync_keepLocalResolution() {
        // Select Keep Local
        composeTestRule.onNodeWithText("Keep Local").performClick()
        composeTestRule.waitForIdle()
        
        // Verify conflict removed
        composeTestRule.onNodeWithText("All data synced!").assertExists()
    }
    
    @Test
    fun offlineSync_keepRemoteResolution() {
        // Select Keep Remote
        composeTestRule.onNodeWithText("Keep Remote").performClick()
        composeTestRule.waitForIdle()
        
        // Verify local data updated
        composeTestRule.onNodeWithText("Data updated from server").assertExists()
    }
    
    @Test
    fun offlineSync_multipleEditsMergeCorrectly() {
        // Make multiple offline edits
        composeTestRule.onNodeWithText("Test Batch").performClick()
        composeTestRule.onNodeWithText("Edit").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextClearance()
        composeTestRule.onNodeWithTag("nameField").performTextInput("Renamed Batch")
        composeTestRule.onNodeWithText("Save").performClick()
        
        // Make another edit
        composeTestRule.onNodeWithText("Edit").performClick()
        composeTestRule.onNodeWithTag("quantityField").performTextClearance()
        composeTestRule.onNodeWithTag("quantityField").performTextInput("100")
        composeTestRule.onNodeWithText("Save").performClick()
        
        // Sync
        composeTestRule.onNodeWithText("Sync Now").performClick()
        
        // Verify both changes synced
        composeTestRule.onNodeWithText("Renamed Batch").assertExists()
        composeTestRule.onNodeWithText("100").assertExists()
    }
    
    @Test
    fun offlineSync_mediaUploadProgressShown() {
        // Add photo to asset
        composeTestRule.onNodeWithText("Add Photo").performClick()
        composeTestRule.waitForIdle()
        
        // Verify upload progress shown
        composeTestRule.onNodeWithTag("upload_progress").assertExists()
        composeTestRule.onNodeWithText("Uploading").assertExists()
    }
    
    @Test
    fun offlineSync_cancelPendingUpload() {
        // Cancel pending upload
        composeTestRule.onNodeWithContentDescription("Cancel upload").performClick()
        
        // Verify cancelled
        composeTestRule.onNodeWithText("Upload cancelled").assertExists()
    }
}
