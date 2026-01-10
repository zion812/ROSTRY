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
 * Integration test for Farm-to-Market flow.
 * 
 * Tests the complete flow:
 * 1. Create farm asset
 * 2. Navigate to asset details
 * 3. Click "Sell This Asset" → "Fixed Price"
 * 4. Fill listing form
 * 5. Submit listing
 * 6. Verify listing appears in marketplace
 * 7. Verify asset marked as listed
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FarmToMarketFlowTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun farmToMarketFlow_createsListingSuccessfully() {
        // 1. Navigate to Create Asset (assuming farmer is logged in)
        composeTestRule.onNodeWithText("Create").performClick()
        composeTestRule.waitForIdle()
        
        // 2. Fill asset creation form
        composeTestRule.onNodeWithTag("assetNameField").performTextInput("Test Batch 1")
        composeTestRule.onNodeWithTag("assetTypeDropdown").performClick()
        composeTestRule.onNodeWithText("Flock").performClick()
        composeTestRule.onNodeWithTag("quantityField").performTextInput("50")
        
        // 3. Save asset
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitForIdle()
        
        // 4. Navigate to asset details
        composeTestRule.onNodeWithText("Test Batch 1").performClick()
        composeTestRule.waitForIdle()
        
        // 5. Click "Sell This Asset"
        composeTestRule.onNodeWithText("Sell This Asset").assertExists()
        composeTestRule.onNodeWithText("Fixed Price").performClick()
        composeTestRule.waitForIdle()
        
        // 6. Fill listing form
        composeTestRule.onNodeWithTag("listingTitleField").performTextInput("Fresh Birds for Sale")
        composeTestRule.onNodeWithTag("priceField").performTextInput("1000")
        composeTestRule.onNodeWithTag("quantityField").performTextInput("10")
        
        // 7. Submit listing
        composeTestRule.onNodeWithText("Publish Listing").performClick()
        composeTestRule.waitForIdle()
        
        // 8. Verify success
        composeTestRule.onNodeWithText("Listing created successfully").assertExists()
    }
    
    @Test
    fun farmToMarketFlow_auctionCreation() {
        // Navigate to asset
        composeTestRule.onNodeWithText("Test Batch 1").performClick()
        composeTestRule.waitForIdle()
        
        // Select Auction
        composeTestRule.onNodeWithText("Auction").performClick()
        composeTestRule.waitForIdle()
        
        // Fill auction form
        composeTestRule.onNodeWithTag("startingBidField").performTextInput("500")
        composeTestRule.onNodeWithTag("reservePriceField").performTextInput("1000")
        composeTestRule.onNodeWithTag("buyNowPriceField").performTextInput("1500")
        
        // Submit
        composeTestRule.onNodeWithText("Create Auction").performClick()
        composeTestRule.waitForIdle()
        
        // Verify
        composeTestRule.onNodeWithText("Auction created").assertExists()
    }
    
    @Test
    fun farmToMarketFlow_preventsDuplicateListing() {
        // Navigate to already listed asset
        composeTestRule.onNodeWithText("Listed Batch").performClick()
        composeTestRule.waitForIdle()
        
        // Verify sell button is disabled or shows "View Listing"
        composeTestRule.onNodeWithText("View Listing").assertExists()
        composeTestRule.onNodeWithText("Sell This Asset").assertDoesNotExist()
    }
    
    @Test
    fun farmToMarketFlow_rbacEnforcementForGeneral() {
        // Assume logged in as GENERAL user
        // Navigate to explore and find a way to try listing
        
        // Try accessing create listing directly
        // Should show permission error or redirect
        
        composeTestRule.onNodeWithText("Verification required").assertExists()
    }
    
    @Test
    fun farmToMarketFlow_validationErrors() {
        // Navigate to create listing
        composeTestRule.onNodeWithText("Test Batch 1").performClick()
        composeTestRule.onNodeWithText("Fixed Price").performClick()
        composeTestRule.waitForIdle()
        
        // Try to submit with zero price
        composeTestRule.onNodeWithTag("priceField").performTextClearance()
        composeTestRule.onNodeWithTag("priceField").performTextInput("0")
        composeTestRule.onNodeWithText("Publish Listing").performClick()
        
        // Verify validation error
        composeTestRule.onNodeWithText("Invalid price").assertExists()
    }
}
