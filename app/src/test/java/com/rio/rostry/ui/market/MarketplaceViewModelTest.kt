package com.rio.rostry.ui.market

import android.location.Location
import app.cash.turbine.test
import com.google.firebase.firestore.GeoPoint
import com.rio.rostry.data.location.LocationService
import com.rio.rostry.data.models.market.MarketplaceListing
import com.rio.rostry.data.repo.MarketplaceRepository
import com.rio.rostry.rules.MainCoroutineRule
import com.rio.rostry.utils.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MarketplaceViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MarketplaceViewModel
    private val marketplaceRepository: MarketplaceRepository = mockk()
    private val locationService: LocationService = mockk()

    @Before
    fun setUp() {
        // Default stub so init { fetchMarketplaceListings() } doesn't fail before tests override
        coEvery { marketplaceRepository.getListings(any(), any()) } returns flowOf(Result.Loading)
        viewModel = MarketplaceViewModel(marketplaceRepository, locationService)
    }

    @Test
    fun `fetchMarketplaceListings success updates state with listings`() = runTest {
        val listings = listOf(MarketplaceListing(id = "1", breed = "Test Listing"))
        coEvery { marketplaceRepository.getListings(any(), any()) } returns flowOf(Result.Success(listings))

        viewModel.uiState.test {
            viewModel.fetchMarketplaceListings()
            assertEquals(MarketplaceUiState.Loading, awaitItem())
            val successState = awaitItem() as MarketplaceUiState.Success
            assertEquals(listings, successState.listings)
        }
    }

    @Test
    fun `fetchMarketplaceListings error updates state with error`() = runTest {
        val errorMessage = "Failed to fetch listings"
        coEvery { marketplaceRepository.getListings(any(), any()) } returns flowOf(Result.Error(Exception(errorMessage)))

        viewModel.uiState.test {
            viewModel.fetchMarketplaceListings()
            assertEquals(MarketplaceUiState.Loading, awaitItem())
            val errorState = awaitItem() as MarketplaceUiState.Error
            assertEquals(errorMessage, errorState.message)
        }
    }

    @Test
    fun `fetchListingsNearMe success updates state with filtered listings`() = runTest {
        val userLocation = mockk<Location>(relaxed = true).apply {
            every { latitude } returns 10.0
            every { longitude } returns 10.0
        }
        val listings = listOf(
            MarketplaceListing(id = "1", breed = "Nearby Listing", location = GeoPoint(10.0, 10.0)),
            MarketplaceListing(id = "2", breed = "Far Listing", location = GeoPoint(20.0, 20.0))
        )
        coEvery { locationService.requestLocationUpdates() } returns flowOf(Result.Success(userLocation))
        // Emit Loading first, then Success to reflect repository behavior and expected UI sequence
        coEvery { marketplaceRepository.getListings(any(), any()) } returns kotlinx.coroutines.flow.flow {
            emit(Result.Loading)
            emit(Result.Success(listOf(listings[0])))
        }

        viewModel.uiState.test {
            viewModel.fetchListingsNearMe(10.0)
            assertEquals(MarketplaceUiState.Loading, awaitItem()) // Initial state
            // Repository may emit Success immediately or go through Loading first. Handle both.
            val next = awaitItem()
            val successState = when (next) {
                is MarketplaceUiState.Loading -> awaitItem() as MarketplaceUiState.Success
                is MarketplaceUiState.Success -> next
                is MarketplaceUiState.Error -> throw AssertionError("Unexpected Error state: ${'$'}{next.message}")
            }
            assertEquals(1, successState.listings.size)
            assertEquals("Nearby Listing", successState.listings[0].breed)
        }
    }
}
