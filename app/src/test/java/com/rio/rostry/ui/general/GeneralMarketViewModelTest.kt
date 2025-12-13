package com.rio.rostry.ui.general

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.BreedRepository
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.WishlistRepository
import com.rio.rostry.ai.RecommendationEngine
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.ui.general.market.GeneralMarketViewModel
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.ValidationUtils
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class GeneralMarketViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var productRepository: ProductRepository
    private lateinit var productMarketplaceRepository: ProductMarketplaceRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var wishlistRepository: WishlistRepository
    private lateinit var recommendationEngine: RecommendationEngine
    private lateinit var currentUserProvider: CurrentUserProvider
    private lateinit var analytics: GeneralAnalyticsTracker
    private lateinit var breedRepository: com.rio.rostry.data.repository.BreedRepository
    
    private lateinit var viewModel: GeneralMarketViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        productRepository = mockk(relaxed = true)
        productMarketplaceRepository = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        wishlistRepository = mockk(relaxed = true)
        recommendationEngine = mockk(relaxed = true)
        currentUserProvider = mockk(relaxed = true)
        analytics = mockk(relaxed = true)
        breedRepository = mockk(relaxed = true)
        
        // Default mock behaviors
        every { currentUserProvider.userIdOrNull() } returns "test-user-123"
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        coEvery { wishlistRepository.observe(any()) } returns flowOf(emptyList())
        coEvery { breedRepository.getAllBreeds() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestProduct(
        id: String = "prod-1",
        name: String = "Broiler Chick",
        price: Double = 250.0,
        breed: String = "Broiler",
        verified: Boolean = false,
        traceable: Boolean = false
    ) = ProductEntity(
        productId = id,
        sellerId = "seller-1",
        name = name,
        description = "Test product",
        category = "CHICKS",
        price = price,
        quantity = 10.0,
        unit = "piece",
        location = "Bangalore",
        latitude = null,
        longitude = null,
        imageUrls = listOf(),
        breed = breed,
        familyTreeId = if (traceable) "tree-1" else null,
        parentIdsJson = if (traceable) "[\"p1\"]" else null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        lastModifiedAt = System.currentTimeMillis(),
        isDeleted = false,
        dirty = false
    )

    @Test
    fun `initial state should be loading`() = runTest {
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `products load successfully`() = runTest {
        val testProducts = listOf(
            createTestProduct("1", "Chick A", 200.0),
            createTestProduct("2", "Chick B", 300.0)
        )
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(testProducts))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.products.size)
    }

    @Test
    fun `query filter works correctly`() = runTest {
        val testProducts = listOf(
            createTestProduct("1", "Broiler Chick", 200.0),
            createTestProduct("2", "Layer Hen", 300.0)
        )
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(testProducts))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.onQueryChange("broiler")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("broiler", state.query)
        assertEquals(1, state.products.size)
        assertEquals("Broiler Chick", state.products[0].name)
    }

    @Test
    fun `autocomplete provides suggestions`() = runTest {
        val suggestions = listOf(
            createTestProduct("1", "Broiler Chick"),
            createTestProduct("2", "Broiler Adult")
        )
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        coEvery { productMarketplaceRepository.autocomplete("bro", 6) } returns 
            Resource.Success(suggestions)
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.onQueryChange("bro")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(2, state.suggestions.size)
        assertTrue(state.suggestions.contains("Broiler Chick"))
    }

    @Test
    fun `verified filter applies correctly`() = runTest {
        val testProducts = listOf(
            createTestProduct("1", "Chick A", verified = true),
            createTestProduct("2", "Chick B", verified = false)
        )
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(testProducts))
        coEvery { productMarketplaceRepository.filterVerified(any()) } returns 
            Resource.Success(listOf(testProducts[0]))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.updateFilters { it.copy(verifiedOnly = true) }
        advanceUntilIdle()
        
        verify { analytics.marketFilterApply("verified_only", "true") }
    }

    @Test
    fun `traceable filter applies correctly`() = runTest {
        val traceableProduct = createTestProduct("1", "Traceable Chick", traceable = true)
        val normalProduct = createTestProduct("2", "Normal Chick", traceable = false)
        val testProducts = listOf(traceableProduct, normalProduct)
        
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(testProducts))
        coEvery { productRepository.filterTraceable(true, any()) } returns 
            Resource.Success(listOf(traceableProduct))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.updateFilters { it.copy(traceableOnly = true) }
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state.filters.traceableOnly)
    }

    @Test
    fun `add to cart succeeds for authenticated user`() = runTest {
        val product = createTestProduct()
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        coEvery { cartRepository.addOrUpdateItem(any(), any(), any(), any(), any()) } returns 
            Resource.Success(Unit)
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.addToCart(product, 2.0)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertNotNull(state.lastCartMessage)
        assertTrue(state.lastCartMessage!!.contains("Added"))
        
        coVerify { cartRepository.addOrUpdateItem("test-user-123", product.productId, 2.0, any(), any()) }
    }

    @Test
    fun `add to cart fails for unauthenticated user`() = runTest {
        every { currentUserProvider.userIdOrNull() } returns null
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        val product = createTestProduct()
        viewModel.addToCart(product)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Please sign in to add items to your cart", state.lastCartMessage)
        
        coVerify(exactly = 0) { cartRepository.addOrUpdateItem(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `quick preset NEARBY_VERIFIED applies both filters`() = runTest {
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.applyQuickPreset(GeneralMarketViewModel.QuickPreset.NEARBY_VERIFIED)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state.filters.nearbyEnabled)
        assertTrue(state.filters.verifiedOnly)
        assertEquals(25.0, state.filters.radiusKm, 0.01)
    }

    @Test
    fun `quick preset BUDGET_FRIENDLY sets max price`() = runTest {
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.applyQuickPreset(GeneralMarketViewModel.QuickPreset.BUDGET_FRIENDLY)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(500.0, state.filters.maxPrice)
    }

    @Test
    fun `clear all filters resets to default state`() = runTest {
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        // Apply some filters
        viewModel.updateFilters { it.copy(verifiedOnly = true, traceableOnly = true) }
        advanceUntilIdle()
        
        // Clear filters
        viewModel.clearAllFilters()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse(state.filters.verifiedOnly)
        assertFalse(state.filters.traceableOnly)
        assertEquals(0, state.activeFilterCount)
    }

    @Test
    fun `active filter count calculates correctly`() = runTest {
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.updateFilters { 
            it.copy(
                verifiedOnly = true,
                traceableOnly = true,
                selectedBreed = "Broiler"
            )
        }
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(3, state.activeFilterCount)
    }

    @Test
    fun `wishlist toggle works for authenticated user`() = runTest {
        val product = createTestProduct()
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        coEvery { wishlistRepository.add(any(), any()) } returns Resource.Success(Unit)
        coEvery { wishlistRepository.observe(any()) } returns flowOf(emptyList())
        
        viewModel = GeneralMarketViewModel(
            productRepository,
            productMarketplaceRepository,
            cartRepository,
            wishlistRepository,
            recommendationEngine,
            currentUserProvider,
            analytics,
            breedRepository
        )
        advanceUntilIdle()
        
        viewModel.toggleWishlist(product)
        advanceUntilIdle()
        
        coVerify { wishlistRepository.add("test-user-123", product.productId) }
        verify { analytics.wishlistToggled(product.productId, true) }
    }
}