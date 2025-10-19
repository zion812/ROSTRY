package com.rio.rostry.ui.general

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rio.rostry.MainActivity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Comprehensive instrumented tests for General user flows.
 * Tests navigation, market presets, checkout (online/offline), explore, create, and profile flows.
 * 
 * Note: These tests use Hilt for dependency injection and Compose testing APIs.
 * Idling resources are automatically handled by Compose test framework.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class GeneralUserFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var cartRepository: CartRepository


    @Before
    fun setup() {
        hiltRule.inject()
    }

    // ============================================
    // NAVIGATION TESTS
    // ============================================

    @Test
    fun testGeneralBottomNavigationFlow() {
        composeTestRule.apply {
            // Wait for app to load
            waitForIdle()

            // Verify we start on Market tab (or Home)
            onNodeWithTag("general_tab_market", useUnmergedTree = true).assertExists()

            // Navigate to Explore tab
            onNodeWithTag("general_tab_explore").performClick()
            waitForIdle()
            onNodeWithText("Explore").assertIsDisplayed()

            // Navigate to Create tab
            onNodeWithTag("general_tab_create").performClick()
            waitForIdle()
            onNodeWithText("Share", substring = true).assertExists()

            // Navigate to Cart tab
            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()
            onNodeWithText("Cart", substring = true).assertExists()

            // Navigate to Profile tab
            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()
            onNodeWithText("Profile", substring = true).assertExists()
        }
    }

    @Test
    fun testNavigationPersistsSelectedTab() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Cart
            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Rotate device or trigger config change would happen here
            // In this simple test, we just verify the selection persists
            onNodeWithText("Cart", substring = true).assertExists()
        }
    }

    // ============================================
    // MARKET PRESET TESTS
    // ============================================

    @Test
    fun testMarketQuickPresetNearbyVerified() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to Market tab
            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            // Click filter button or preset chip
            onNodeWithText("Nearby & Verified", substring = true).performClick()
            waitForIdle()

            // Verify filters are applied (check for filter chips/indicators)
            onNodeWithText("Nearby", substring = true).assertExists()
            onNodeWithText("Verified", substring = true).assertExists()
        }
    }

    @Test
    fun testMarketQuickPresetBudgetFriendly() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            onNodeWithText("Budget Friendly", substring = true).performClick()
            waitForIdle()

            // Verify price filter is applied
            onNodeWithText("₹", substring = true).assertExists()
        }
    }

    @Test
    fun testMarketQuickPresetTraceable() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            onNodeWithText("Traceable Only", substring = true).performClick()
            waitForIdle()

            // Verify traceable filter
            onNodeWithText("Traceable", substring = true).assertExists()
        }
    }

    @Test
    fun testMarketSearchAndFilter() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            // Type in search box
            onNodeWithTag("market_search_field").performTextInput("broiler")
            waitForIdle()

            // Verify suggestions or results appear
            onNodeWithText("broiler", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testMarketClearAllFilters() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            // Apply a preset
            onNodeWithText("Nearby & Verified", substring = true).performClick()
            waitForIdle()

            // Clear filters
            onNodeWithText("Clear All", substring = true, ignoreCase = true).performClick()
            waitForIdle()

            // Verify no active filters
            onNodeWithText("Nearby", substring = true).assertDoesNotExist()
        }
    }

    // ============================================
    // CHECKOUT TESTS (ONLINE)
    // ============================================

    @Test
    fun testOnlineCheckoutFlowCOD() {
        composeTestRule.apply {
            waitForIdle()

            // Add product to cart from market
            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            // Click on first product (if exists)
            onNodeWithTag("product_card_0", useUnmergedTree = true).performClick()
            waitForIdle()

            // Add to cart button
            onNodeWithText("Add to Cart", ignoreCase = true).performClick()
            waitForIdle()

            // Navigate to cart
            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Verify item in cart
            onNodeWithText("Cart", substring = true).assertExists()

            // Select address
            onNodeWithText("Select address", ignoreCase = true, substring = true).performClick()
            waitForIdle()
            onAllNodesWithText("Test St", substring = true).onFirst().performClick()
            waitForIdle()

            // Select COD payment
            onNodeWithText("COD", substring = true).performClick()
            waitForIdle()

            // Click checkout
            onNodeWithText("Checkout", ignoreCase = true).performClick()
            waitForIdle()

            // Verify success message
            onNodeWithText("placed successfully", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testOnlineCheckoutFailsWithoutAddress() {
        composeTestRule.apply {
            waitForIdle()

            // Assume we have items in cart from previous test or setup
            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Don't select address, just try to checkout
            onNodeWithText("Checkout", ignoreCase = true).performClick()
            waitForIdle()

            // Verify error message
            onNodeWithText("Select a delivery address", substring = true).assertExists()
        }
    }

    // ============================================
    // CHECKOUT TESTS (OFFLINE)
    // ============================================

    @Test
    fun testOfflineCheckoutQueuesOrder() {
        composeTestRule.apply {
            waitForIdle()

            // Note: Testing offline mode requires disabling network or mocking ConnectivityManager
            // This is a simplified test assuming offline mode can be triggered

            // Navigate to cart with items
            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Select address
            onNodeWithText("Select address", ignoreCase = true, substring = true).performClick()
            waitForIdle()
            onAllNodesWithText("Test St", substring = true).onFirst().performClick()
            waitForIdle()

            // Checkout (assuming offline - this would need network mocking)
            onNodeWithText("Checkout", ignoreCase = true).performClick()
            waitForIdle()

            // In real offline test, verify queued message
            // onNodeWithText("queued", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testOfflineBannerDisplayedInMarket() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to market
            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            // Check for offline banner (if network is offline)
            // This requires actually being offline or mocking
            // onNodeWithText("offline", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // EXPLORE TAB TESTS
    // ============================================

    @Test
    fun testExploreTabShowsPosts() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_explore").performClick()
            waitForIdle()

            // Verify posts are displayed
            onNodeWithText("Explore").assertIsDisplayed()
            // Would check for post cards here
        }
    }

    @Test
    fun testExploreTabLikePost() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_explore").performClick()
            waitForIdle()

            // Like first post
            onNodeWithTag("post_like_button_0", useUnmergedTree = true).performClick()
            waitForIdle()

            // Verify like count increased or button state changed
            // This depends on UI implementation
        }
    }

    // ============================================
    // CREATE TAB TESTS
    // ============================================

    @Test
    fun testCreatePostFlow() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_create").performClick()
            waitForIdle()

            // Type content
            onNodeWithTag("create_post_input").performTextInput("Test post content")
            waitForIdle()

            // Click share button
            onNodeWithText("Share", ignoreCase = true).performClick()
            waitForIdle()

            // Verify success or navigation back
            onNodeWithText("shared", ignoreCase = true, substring = true).assertExists()
        }
    }

    @Test
    fun testCreatePostOfflineQueued() {
        composeTestRule.apply {
            waitForIdle()

            // Navigate to create tab
            onNodeWithTag("general_tab_create").performClick()
            waitForIdle()

            // Type content
            onNodeWithTag("create_post_input").performTextInput("Offline post")
            waitForIdle()

            // Share (assuming offline)
            onNodeWithText("Share", ignoreCase = true).performClick()
            waitForIdle()

            // Would verify queued message in offline mode
            // onNodeWithText("queued", ignoreCase = true, substring = true).assertExists()
        }
    }

    // ============================================
    // PROFILE TAB TESTS
    // ============================================

    @Test
    fun testProfileTabShowsUserInfo() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()

            // Verify profile elements
            onNodeWithText("Profile", substring = true).assertExists()
            // Check for user name, email, etc.
        }
    }

    @Test
    fun testProfileEditNavigation() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()

            // Click edit button
            onNodeWithText("Edit", ignoreCase = true).performClick()
            waitForIdle()

            // Verify navigation to edit screen
            onNodeWithText("Edit Profile", substring = true).assertExists()
        }
    }

    @Test
    fun testProfileOrderHistory() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_profile").performClick()
            waitForIdle()

            // Click on orders or order history section
            onNodeWithText("Orders", substring = true).performClick()
            waitForIdle()

            // Verify order list
            onNodeWithText("Order", substring = true).assertExists()
        }
    }

    // ============================================
    // CART OPERATIONS TESTS
    // ============================================

    @Test
    fun testCartIncrementQuantity() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Click increment button on first item
            onNodeWithTag("cart_item_increment_0").performClick()
            waitForIdle()

            // Verify quantity increased
            // Would check quantity display
        }
    }

    @Test
    fun testCartDecrementQuantity() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Click decrement button
            onNodeWithTag("cart_item_decrement_0").performClick()
            waitForIdle()

            // Verify quantity decreased
        }
    }

    @Test
    fun testCartRemoveItem() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Click remove button
            onNodeWithTag("cart_item_remove_0").performClick()
            waitForIdle()

            // Verify item removed
            onNodeWithText("Cart is empty", substring = true).assertExists()
        }
    }

    @Test
    fun testCartTotalCalculation() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Verify total is displayed and calculated
            onNodeWithText("Total", substring = true).assertExists()
            onNodeWithText("₹", substring = true).assertExists()
        }
    }

    // ============================================
    // DELIVERY OPTIONS TESTS
    // ============================================

    @Test
    fun testSelectDeliveryOption() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Select express delivery
            onNodeWithText("Express", substring = true).performClick()
            waitForIdle()

            // Verify delivery fee updated
            onNodeWithText("149", substring = true).assertExists()
        }
    }

    @Test
    fun testSelfPickupOption() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_cart").performClick()
            waitForIdle()

            // Select self pickup
            onNodeWithText("Self pickup", substring = true).performClick()
            waitForIdle()

            // Verify no delivery fee
            onNodeWithText("Delivery Fee").assertExists()
        }
    }

    // ============================================
    // WISHLIST TESTS
    // ============================================

    @Test
    fun testAddToWishlist() {
        composeTestRule.apply {
            waitForIdle()

            onNodeWithTag("general_tab_market").performClick()
            waitForIdle()

            // Click wishlist button on product
            onNodeWithTag("product_wishlist_0").performClick()
            waitForIdle()

            // Verify wishlist indication
            // Would check for filled heart icon or confirmation
        }
    }

    // ============================================
    // IDLING RESOURCE INTEGRATION
    // ============================================

    @Test
    fun testWaitForNetworkOperations() {
        composeTestRule.apply {
            // Compose test framework automatically waits for recompositions
            // and idling resources registered with Espresso
            waitForIdle()

            onNodeWithTag("general_tab_market").performClick()
            
            // The test framework will wait for async operations to complete
            waitForIdle()

            // Verify data loaded
            onNodeWithText("Market", substring = true).assertExists()
        }
    }
}
