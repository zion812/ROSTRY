package com.rio.rostry.ui.farmer

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DigitalFarmViewModelTest {

    private lateinit var viewModel: DigitalFarmViewModel
    private val productRepository: ProductRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel calculates isNew correctly based on 24h window`() = runTest {
        // Given
        val userId = "test_user_id"
        val user = UserEntity(userId = userId)
        val now = System.currentTimeMillis()
        
        // Bird added 1 hour ago (Should be NEW)
        val newBird = ProductEntity(
            productId = "bird_new",
            sellerId = userId,
            status = "private",
            lifecycleStatus = "ACTIVE",
            createdAt = now - (1 * 60 * 60 * 1000), // 1 hour ago
            name = "Bird A"
        )
        
        // Bird added 25 hours ago (Should NOT be NEW)
        val oldBird = ProductEntity(
            productId = "bird_old",
            sellerId = userId,
            status = "private",
            lifecycleStatus = "ACTIVE",
            createdAt = now - (25 * 60 * 60 * 1000), // 25 hours ago
            name = "Bird B"
        )

        every { userRepository.getCurrentUser() } returns flowOf(Resource.Success(user))
        every { productRepository.getProductsBySeller(userId) } returns flowOf(Resource.Success(listOf(newBird, oldBird)))

        // When
        viewModel = DigitalFarmViewModel(productRepository, userRepository)
        
        // Start collecting uiState to trigger the operator chain
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("State should be Success but was $state", state is DigitalFarmUiState.Success)
        val birds = (state as DigitalFarmUiState.Success).birds
        
        assertEquals(2, birds.size)
        
        val visualBirdNew = birds.find { it.id == "bird_new" }
        val visualBirdOld = birds.find { it.id == "bird_old" }
        
        assertTrue("Bird created 1h ago should be marked as new", visualBirdNew?.isNew == true)
        assertTrue("Bird created 25h ago should NOT be marked as new", visualBirdOld?.isNew == false)
    }

    @Test
    fun `viewModel filters out non-active birds`() = runTest {
        // Given
        val userId = "test_user_id"
        val user = UserEntity(userId = userId)
        
        val activeBird = ProductEntity(productId = "1", sellerId = userId, status = "private", lifecycleStatus = "ACTIVE", name = "Active")
        val quarantineBird = ProductEntity(productId = "2", sellerId = userId, status = "private", lifecycleStatus = "QUARANTINE", name = "Quarantine")
        val soldBird = ProductEntity(productId = "3", sellerId = userId, status = "private", lifecycleStatus = "SOLD", name = "Sold")
        val deletedBird = ProductEntity(productId = "4", sellerId = userId, status = "private", lifecycleStatus = "DELETED", name = "Deleted")
        // Note: draftBird with null lifecycleStatus defaults to "ACTIVE" in ViewModel logic
        val draftBird = ProductEntity(productId = "5", sellerId = userId, status = "private", lifecycleStatus = null, name = "Draft (Defaults Active)")
        val explicitDraftBird = ProductEntity(productId = "6", sellerId = userId, status = "private", lifecycleStatus = "DRAFT", name = "Explicit Draft")


        every { userRepository.getCurrentUser() } returns flowOf(Resource.Success(user))
        every { productRepository.getProductsBySeller(userId) } returns flowOf(Resource.Success(listOf(activeBird, quarantineBird, soldBird, deletedBird, draftBird, explicitDraftBird)))

        // When
        viewModel = DigitalFarmViewModel(productRepository, userRepository)

        // Start collecting uiState to trigger the operator chain
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("State should be Success but was $state", state is DigitalFarmUiState.Success)
        val birds = (state as DigitalFarmUiState.Success).birds
        
        val includedIds = birds.map { it.id }.toSet()
        
        assertTrue("Should include ACTIVE", includedIds.contains("1"))
        assertTrue("Should include QUARANTINE", includedIds.contains("2"))
        assertTrue("Should include null lifecycle status as ACTIVE", includedIds.contains("5"))
        
        // Verify others are excluded
        assertTrue("Should exclude SOLD", !includedIds.contains("3"))
        assertTrue("Should exclude DELETED", !includedIds.contains("4"))
        assertTrue("Should exclude explicit DRAFT", !includedIds.contains("6"))
    }
}
