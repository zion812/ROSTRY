package com.rio.rostry.ui.market

import android.Manifest
import android.location.Location
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.google.firebase.firestore.GeoPoint
import com.rio.rostry.MainActivity
import com.rio.rostry.data.location.LocationService
import com.rio.rostry.data.models.market.MarketplaceListing
import com.rio.rostry.data.repo.MarketplaceRepository
import com.rio.rostry.utils.Result
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MarketplaceScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: MarketplaceRepository

    @Inject
    lateinit var locationService: LocationService

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        hiltRule.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun filterByLocation_whenPermissionGranted_showsFilteredList() {
        // Given
        val userLocation = Location("").apply { latitude = 10.0; longitude = 10.0 }
        val listings = listOf(
            MarketplaceListing(id = "1", breed = "Nearby Listing", location = GeoPoint(10.0, 10.0)),
            MarketplaceListing(id = "2", breed = "Far Listing", location = GeoPoint(20.0, 20.0))
        )
        coEvery { repository.getListings(any(), any()) } returns flowOf(Result.Success(listings))
        coEvery { locationService.requestLocationUpdates() } returns flowOf(Result.Success(userLocation))
        coEvery { repository.getListings(any(), 10.0) } returns flowOf(Result.Success(listOf(listings[0])))

        // When
        composeTestRule.onNodeWithText("Farmer").performClick() // Navigate to Farmer view
        composeTestRule.onNodeWithContentDescription("Marketplace").performClick() // Navigate to Marketplace
        composeTestRule.onNodeWithContentDescription("Filter by location").performClick()

        // Then
        composeTestRule.onNodeWithText("Nearby Listing").assertExists()
        composeTestRule.onNodeWithText("Far Listing").assertDoesNotExist()
    }

    @Test
    fun filterByLocation_whenPermissionNotGranted_requestsPermission() {
        // Given
        coEvery { repository.getListings(any(), any()) } returns flowOf(Result.Success(emptyList()))

        // When
        composeTestRule.onNodeWithText("Farmer").performClick()
        composeTestRule.onNodeWithContentDescription("Marketplace").performClick()
        composeTestRule.onNodeWithContentDescription("Filter by location").performClick()

        // Then
        val permissionDialog = device.findObject(UiSelector().textContains("allow"))
        assert(permissionDialog.exists())
    }
}
