package com.rio.rostry.ui.general

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.CartItemEntity
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.PaymentRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.cart.GeneralCartViewModel
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
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
class GeneralCartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var currentUserProvider: CurrentUserProvider
    private lateinit var outboxDao: OutboxDao
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var gson: Gson
    
    private lateinit var viewModel: GeneralCartViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        cartRepository = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        paymentRepository = mockk(relaxed = true)
        currentUserProvider = mockk(relaxed = true)
        outboxDao = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        gson = Gson()
        
        // Default mock behaviors
        every { currentUserProvider.userIdOrNull() } returns "test-user-123"
        coEvery { cartRepository.observeCart(any()) } returns flowOf(emptyList())
        coEvery { orderRepository.getOrdersByBuyer(any()) } returns flowOf(emptyList())
        coEvery { outboxDao.observePendingByUser(any()) } returns flowOf(emptyList())
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(emptyList()))
        coEvery { userRepository.getCurrentUser() } returns flowOf(Resource.Success(createTestUser()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestUser(
        id: String = "test-user-123",
        userType: UserType = UserType.GENERAL
    ) = UserEntity(
        userId = id,
        phoneNumber = "+1234567890",
        email = "test@example.com",
        fullName = "Test User",
        address = "123 Test St, Bangalore",
        profilePictureUrl = null,
        userType = userType,
        farmLocationLat = null,
        farmLocationLng = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun createTestProduct(
        id: String = "prod-1",
        name: String = "Broiler Chick",
        price: Double = 250.0
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
        imageUrls = listOf("https://example.com/img.jpg"),
        breed = "Broiler",
        familyTreeId = null,
        parentIdsJson = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        lastModifiedAt = System.currentTimeMillis(),
        isDeleted = false,
        dirty = false
    )

    private fun createTestCartItem(
        id: String = "cart-1",
        productId: String = "prod-1",
        quantity: Double = 2.0
    ) = CartItemEntity(
        id = id,
        userId = "test-user-123",
        productId = productId,
        quantity = quantity,
        addedAt = System.currentTimeMillis()
    )

    @Test
    fun `unauthenticated user shows error state`() = runTest {
        every { currentUserProvider.userIdOrNull() } returns null
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertEquals("Sign in to manage your cart", state.error)
    }

    @Test
    fun `empty cart shows correct state`() = runTest {
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertTrue(state.items.isEmpty())
        assertEquals(0.0, state.subtotal, 0.01)
        assertEquals(0.0, state.total, 0.01)
    }

    @Test
    fun `cart with items calculates totals correctly`() = runTest {
        val product = createTestProduct("prod-1", "Chick A", 200.0)
        val cartItem = createTestCartItem("cart-1", "prod-1", 3.0)
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals(600.0, state.subtotal, 0.01) // 200 * 3
        assertTrue(state.total > state.subtotal) // Should include fees
    }

    @Test
    fun `increment quantity updates cart`() = runTest {
        val product = createTestProduct("prod-1", "Chick A", 200.0)
        val cartItem = createTestCartItem("cart-1", "prod-1", 2.0)
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        coEvery { cartRepository.addOrUpdateItem(any(), any(), any(), any(), any()) } returns Resource.Success(Unit)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.incrementQuantity("prod-1", 1.0)
        advanceUntilIdle()
        
        coVerify { cartRepository.addOrUpdateItem("test-user-123", "prod-1", 3.0, any(), any()) }
    }

    @Test
    fun `decrement quantity updates cart`() = runTest {
        val product = createTestProduct("prod-1", "Chick A", 200.0)
        val cartItem = createTestCartItem("cart-1", "prod-1", 3.0)
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        coEvery { cartRepository.addOrUpdateItem(any(), any(), any(), any(), any()) } returns Resource.Success(Unit)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.decrementQuantity("prod-1", 1.0)
        advanceUntilIdle()
        
        coVerify { cartRepository.addOrUpdateItem("test-user-123", "prod-1", 2.0, any(), any()) }
    }

    @Test
    fun `remove item calls repository`() = runTest {
        coEvery { cartRepository.removeItem(any(), any()) } returns Resource.Success(Unit)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.removeItem("prod-1")
        advanceUntilIdle()
        
        coVerify { cartRepository.removeItem("test-user-123", "prod-1") }
    }

    @Test
    fun `select delivery option updates state`() = runTest {
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.selectDeliveryOption("express")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("express", state.selectedDelivery?.id)
        assertEquals(149.0, state.selectedDelivery?.fee ?: 0.0, 0.01)
    }

    @Test
    fun `select payment method updates state`() = runTest {
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.selectPayment(GeneralCartViewModel.PaymentMethod.MOCK_PAYMENT)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(GeneralCartViewModel.PaymentMethod.MOCK_PAYMENT, state.selectedPayment)
    }

    @Test
    fun `checkout fails with empty cart`() = runTest {
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.selectAddress("123 Test St")
        viewModel.checkout()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Your cart is empty", state.error)
        
        coVerify(exactly = 0) { orderRepository.upsert(any()) }
    }

    @Test
    fun `checkout fails without address`() = runTest {
        val product = createTestProduct()
        val cartItem = createTestCartItem()
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        // Don't select an address
        viewModel.checkout()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Select a delivery address", state.error)
    }

    @Test
    fun `online checkout with COD succeeds`() = runTest {
        val product = createTestProduct()
        val cartItem = createTestCartItem()
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        every { connectivityManager.isOnline() } returns true
        coEvery { orderRepository.upsert(any()) } just Runs
        coEvery { paymentRepository.codReservation(any(), any(), any()) } returns Resource.Success(
            PaymentEntity(
                paymentId = "pay-1",
                orderId = "order-1",
                userId = "test-user-123",
                method = "COD",
                amount = 500.0,
                status = "PENDING",
                idempotencyKey = "COD-order-1",
                createdAt = System.currentTimeMillis()
            )
        )
        coEvery { cartRepository.removeItem(any(), any()) } returns Resource.Success(Unit)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.selectAddress("123 Test St")
        viewModel.selectPayment(GeneralCartViewModel.PaymentMethod.COD)
        viewModel.checkout()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Order placed successfully", state.successMessage)
        assertFalse(state.isCheckingOut)
        
        coVerify { orderRepository.upsert(any()) }
        coVerify { paymentRepository.codReservation(any(), "test-user-123", any()) }
        coVerify { cartRepository.removeItem("test-user-123", "prod-1") }
    }

    @Test
    fun `online checkout with MOCK_PAYMENT succeeds`() = runTest {
        val product = createTestProduct()
        val cartItem = createTestCartItem()
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        every { connectivityManager.isOnline() } returns true
        coEvery { orderRepository.upsert(any()) } just Runs
        coEvery { paymentRepository.cardWalletDemo(any(), any(), any(), any()) } returns Resource.Success(
            PaymentEntity(
                paymentId = "pay-2",
                orderId = "order-2",
                userId = "test-user-123",
                method = "CARD",
                amount = 500.0,
                status = "PENDING",
                idempotencyKey = "test-key",
                createdAt = System.currentTimeMillis()
            )
        )
        coEvery { paymentRepository.markPaymentResult(any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { cartRepository.removeItem(any(), any()) } returns Resource.Success(Unit)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.selectAddress("123 Test St")
        viewModel.selectPayment(GeneralCartViewModel.PaymentMethod.MOCK_PAYMENT)
        viewModel.checkout()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Order placed successfully", state.successMessage)
        
        coVerify { paymentRepository.cardWalletDemo(any(), "test-user-123", any(), any()) }
        coVerify { paymentRepository.markPaymentResult(any(), true, any()) }
    }

    @Test
    fun `offline checkout queues order in outbox`() = runTest {
        val product = createTestProduct()
        val cartItem = createTestCartItem()
        
        coEvery { cartRepository.observeCart(any()) } returns flowOf(listOf(cartItem))
        coEvery { productRepository.getAllProducts() } returns flowOf(Resource.Success(listOf(product)))
        every { connectivityManager.isOnline() } returns false // OFFLINE
        coEvery { outboxDao.insert(any()) } returns Unit
        coEvery { orderRepository.upsert(any()) } just Runs
        coEvery { cartRepository.removeItem(any(), any()) } returns Resource.Success(Unit)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        viewModel.selectAddress("123 Test St")
        viewModel.checkout()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Order queued for submission when online", state.successMessage)
        
        // Verify outbox entry was created
        coVerify { outboxDao.insert(match { it.entityType == "ORDER" && it.operation == "CREATE" }) }
        
        // Verify order was saved locally with dirty flag
        coVerify { orderRepository.upsert(match { it.status == "PLACED" && it.dirty }) }
        
        // Verify cart was cleared
        coVerify { cartRepository.removeItem("test-user-123", "prod-1") }
        
        // Verify payment was NOT processed
        coVerify(exactly = 0) { paymentRepository.codReservation(any(), any(), any()) }
    }

    @Test
    fun `pending outbox items are reflected in state`() = runTest {
        val mockOutboxEntry = com.rio.rostry.data.database.entity.OutboxEntity(
            outboxId = "outbox-1",
            userId = "test-user-123",
            entityType = "ORDER",
            entityId = "order-1",
            operation = "CREATE",
            payloadJson = "{}",
            createdAt = System.currentTimeMillis(),
            status = "PENDING"
        )
        
        coEvery { outboxDao.observePendingByUser(any()) } returns flowOf(listOf(mockOutboxEntry))
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state.hasPendingOutbox)
    }

    @Test
    fun `order history is displayed`() = runTest {
        val orders = listOf(
            OrderEntity(
                orderId = "order-1",
                buyerId = "test-user-123",
                sellerId = "seller-1",
                totalAmount = 500.0,
                status = "PLACED",
                shippingAddress = "123 Test St",
                paymentMethod = "COD",
                orderDate = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                lastModifiedAt = System.currentTimeMillis(),
                isDeleted = false,
                dirty = false
            )
        )
        
        coEvery { orderRepository.getOrdersByBuyer(any()) } returns flowOf(orders)
        
        viewModel = GeneralCartViewModel(
            cartRepository,
            productRepository,
            orderRepository,
            userRepository,
            paymentRepository,
            currentUserProvider,
            outboxDao,
            connectivityManager,
            gson
        )
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.orderHistory.size)
        assertEquals("order-1", state.orderHistory[0].orderId)
        assertEquals("PLACED", state.orderHistory[0].status)
    }
}
