package com.rio.rostry.ui.auction

import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.repository.AuctionRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class AuctionViewModelTest {

    private lateinit var viewModel: AuctionViewModel
    private val auctionRepository: AuctionRepository = mockk()
    private val currentUserProvider: CurrentUserProvider = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuctionViewModel(auctionRepository, currentUserProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAuction updates state with auction data`() = runTest {
        val auctionId = "auction1"
        val auction = AuctionEntity(auctionId = auctionId, currentPrice = 100.0)
        
        coEvery { auctionRepository.observeAuction(auctionId) } returns flowOf(auction)
        every { currentUserProvider.userIdOrNull() } returns "user1"

        viewModel.loadAuction(auctionId)

        viewModel.auctionState.test {
            // Initial state
            val initialState = awaitItem()
            assert(initialState.isLoading)

            // Loaded state
            val loadedState = awaitItem()
            assertEquals(auction, loadedState.auction)
            assertEquals(false, loadedState.isLoading)
            assertEquals(false, loadedState.isWinning)
        }
    }

    @Test
    fun `placeBid calls repository and emits success event`() = runTest {
        val auctionId = "auction1"
        val userId = "user1"
        val amount = 150.0

        // Setup initial state
        coEvery { auctionRepository.observeAuction(auctionId) } returns flowOf(AuctionEntity(auctionId = auctionId))
        every { currentUserProvider.userIdOrNull() } returns userId
        coEvery { auctionRepository.placeBid(auctionId, userId, amount) } returns Resource.Success(Unit)

        viewModel.loadAuction(auctionId)
        
        viewModel.bidEvent.test {
            viewModel.placeBid(amount)
            assertEquals("Bid placed successfully", awaitItem())
        }
    }

    @Test
    fun `placeBid emits error event on failure`() = runTest {
        val auctionId = "auction1"
        val userId = "user1"
        val amount = 150.0
        val errorMessage = "Bid failed"

        // Setup initial state
        coEvery { auctionRepository.observeAuction(auctionId) } returns flowOf(AuctionEntity(auctionId = auctionId))
        every { currentUserProvider.userIdOrNull() } returns userId
        coEvery { auctionRepository.placeBid(auctionId, userId, amount) } returns Resource.Error(errorMessage)

        viewModel.loadAuction(auctionId)

        viewModel.bidEvent.test {
            viewModel.placeBid(amount)
            assertEquals(errorMessage, awaitItem())
        }
    }

    @Test
    fun `placeBid emits login required event if user not logged in`() = runTest {
        val auctionId = "auction1"
        val amount = 150.0

        // Setup initial state
        coEvery { auctionRepository.observeAuction(auctionId) } returns flowOf(AuctionEntity(auctionId = auctionId))
        every { currentUserProvider.userIdOrNull() } returns null

        viewModel.loadAuction(auctionId)

        viewModel.bidEvent.test {
            viewModel.placeBid(amount)
            assertEquals("Please login to bid", awaitItem())
        }
    }
}
