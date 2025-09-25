package com.rio.rostry.ui.general.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.CartItemEntity
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GeneralCartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    enum class PaymentMethod { COD, MOCK_PAYMENT }

    data class DeliveryOption(
        val id: String,
        val label: String,
        val eta: String,
        val fee: Double
    )

    data class CartItemUi(
        val id: String,
        val productId: String,
        val sellerId: String,
        val name: String,
        val price: Double,
        val quantity: Double,
        val unit: String,
        val imageUrl: String?,
        val location: String,
        val subtotal: Double
    )

    data class OrderSummary(
        val orderId: String,
        val status: String,
        val amount: Double,
        val paymentMethod: String?,
        val createdAt: Long
    )

    data class CartUiState(
        val isAuthenticated: Boolean = true,
        val isLoading: Boolean = true,
        val items: List<CartItemUi> = emptyList(),
        val deliveryOptions: List<DeliveryOption> = emptyList(),
        val selectedDelivery: DeliveryOption? = null,
        val paymentMethods: List<PaymentMethod> = PaymentMethod.values().toList(),
        val selectedPayment: PaymentMethod = PaymentMethod.COD,
        val addresses: List<String> = emptyList(),
        val selectedAddress: String? = null,
        val subtotal: Double = 0.0,
        val deliveryFee: Double = 0.0,
        val total: Double = 0.0,
        val orderHistory: List<OrderSummary> = emptyList(),
        val isCheckingOut: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
    )

    private val deliveryOptions = listOf(
        DeliveryOption(id = "standard", label = "Standard (3-5 days)", eta = "Arrives in 3-5 days", fee = 79.0),
        DeliveryOption(id = "express", label = "Express (1-2 days)", eta = "Arrives in 1-2 days", fee = 149.0),
        DeliveryOption(id = "pickup", label = "Self pickup", eta = "Ready in 24 hours", fee = 0.0)
    )

    private val selectedDelivery = MutableStateFlow(deliveryOptions.first())
    private val selectedPayment = MutableStateFlow(PaymentMethod.COD)
    private val selectedAddress = MutableStateFlow<String?>(null)
    private val isCheckingOut = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val successMessage = MutableStateFlow<String?>(null)

    private val userId: String? = currentUserProvider.userIdOrNull()

    private val cartItems: Flow<List<CartItemEntity>> = userId?.let { uid ->
        cartRepository.observeCart(uid)
    } ?: flowOf(emptyList())

    private val orders: Flow<List<OrderEntity>> = userId?.let { uid ->
        orderRepository.getOrdersByBuyer(uid)
    } ?: flowOf(emptyList())

    private val products: StateFlow<Resource<List<ProductEntity>>> = productRepository
        .getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading()
        )

    private val userResource: StateFlow<Resource<UserEntity?>> = userRepository
        .getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading()
        )

    private data class BaseInputs(
        val cart: List<CartItemEntity>,
        val products: Resource<List<ProductEntity>>,
        val orders: List<OrderEntity>,
        val user: Resource<UserEntity?>
    )

    private val baseInputs: StateFlow<BaseInputs> = combine(
        cartItems,
        products,
        orders,
        userResource
    ) { cart, productResource, orderList, userRes ->
        BaseInputs(
            cart = cart,
            products = productResource,
            orders = orderList,
            user = userRes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BaseInputs(
            cart = emptyList(),
            products = Resource.Loading(),
            orders = emptyList(),
            user = Resource.Loading()
        )
    )

    private data class SelectionInputs(
        val delivery: DeliveryOption,
        val payment: PaymentMethod,
        val address: String?
    )

    private val selectionInputs: StateFlow<SelectionInputs> = combine(
        selectedDelivery,
        selectedPayment,
        selectedAddress
    ) { delivery, payment, address ->
        SelectionInputs(delivery, payment, address)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SelectionInputs(
            delivery = deliveryOptions.first(),
            payment = PaymentMethod.COD,
            address = null
        )
    )

    private data class StatusInputs(
        val checkingOut: Boolean,
        val error: String?,
        val success: String?
    )

    private val statusInputs: StateFlow<StatusInputs> = combine(
        isCheckingOut,
        error,
        successMessage
    ) { checkingOut, errorMessage, successMsg ->
        StatusInputs(checkingOut, errorMessage, successMsg)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatusInputs(
            checkingOut = false,
            error = null,
            success = null
        )
    )

    val uiState: StateFlow<CartUiState> = combine(
        baseInputs,
        selectionInputs,
        statusInputs
    ) { base, selection, status ->
        Triple(base, selection, status)
    }.map { (base, selection, status) ->
        if (userId == null) {
            return@map CartUiState(
                isAuthenticated = false,
                isLoading = false,
                deliveryOptions = deliveryOptions,
                selectedDelivery = selection.delivery,
                paymentMethods = PaymentMethod.values().toList(),
                selectedPayment = selection.payment,
                error = "Sign in to manage your cart"
            )
        }
        val productsData = base.products.data.orEmpty()
        val productMap = productsData.associateBy { it.productId }
        val items = base.cart.map { entity ->
            val product = productMap[entity.productId]
            CartItemUi(
                id = entity.id,
                productId = entity.productId,
                sellerId = product?.sellerId ?: "",
                name = product?.name ?: "Unknown product",
                price = product?.price ?: 0.0,
                quantity = entity.quantity,
                unit = product?.unit ?: "unit",
                imageUrl = product?.imageUrls?.firstOrNull(),
                location = product?.location ?: "Unknown location",
                subtotal = (product?.price ?: 0.0) * entity.quantity
            )
        }
        val subtotal = items.sumOf { it.subtotal }
        val deliveryFee = selection.delivery.fee
        val addresses = buildList {
            base.user.data?.address?.let { if (it.isNotBlank()) add(it) }
            add("Pickup hub (Bengaluru)")
            add("Save new address…")
        }
        val resolvedAddress = selection.address ?: addresses.firstOrNull()
        val orderHistory = base.orders.sortedByDescending { it.orderDate }.take(10).map {
            OrderSummary(
                orderId = it.orderId,
                status = it.status,
                amount = it.totalAmount,
                paymentMethod = it.paymentMethod,
                createdAt = it.orderDate
            )
        }
        CartUiState(
            isAuthenticated = true,
            isLoading = base.products is Resource.Loading && items.isEmpty(),
            items = items,
            deliveryOptions = deliveryOptions,
            selectedDelivery = selection.delivery,
            paymentMethods = PaymentMethod.values().toList(),
            selectedPayment = selection.payment,
            addresses = addresses,
            selectedAddress = resolvedAddress,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            total = subtotal + deliveryFee,
            orderHistory = orderHistory,
            isCheckingOut = status.checkingOut,
            error = status.error ?: (base.products as? Resource.Error)?.message,
            successMessage = status.success
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState(isAuthenticated = userId != null)
    )

    fun incrementQuantity(productId: String, step: Double = 1.0) {
        updateQuantity(productId) { current -> current + step }
    }

    fun decrementQuantity(productId: String, step: Double = 1.0) {
        updateQuantity(productId) { current -> (current - step).coerceAtLeast(step) }
    }

    private fun updateQuantity(productId: String, transform: (Double) -> Double) {
        val uid = userId ?: run {
            error.value = "Sign in to update cart"
            return
        }
        val currentState = uiState.value
        val currentItem = currentState.items.firstOrNull { it.productId == productId }
        val currentQuantity = currentItem?.quantity ?: 0.0
        val newQuantity = transform(currentQuantity)
        viewModelScope.launch {
            val result = cartRepository.addOrUpdateItem(
                userId = uid,
                productId = productId,
                quantity = newQuantity,
                buyerLat = null,
                buyerLon = null
            )
            if (result is Resource.Error) {
                error.value = result.message ?: "Unable to update quantity"
            }
        }
    }

    fun removeItem(productId: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            val result = cartRepository.removeItem(uid, productId)
            if (result is Resource.Error) {
                error.value = result.message ?: "Unable to remove item"
            }
        }
    }

    fun selectDeliveryOption(optionId: String) {
        deliveryOptions.firstOrNull { it.id == optionId }?.let { selectedDelivery.value = it }
    }

    fun selectPayment(method: PaymentMethod) {
        selectedPayment.value = method
    }

    fun selectAddress(address: String) {
        selectedAddress.value = address
    }

    fun clearMessages() {
        error.value = null
        successMessage.value = null
    }

    fun checkout() {
        val uid = userId ?: run {
            error.value = "Sign in to checkout"
            return
        }
        val currentState = uiState.value
        if (currentState.items.isEmpty()) {
            error.value = "Your cart is empty"
            return
        }
        if (currentState.selectedAddress.isNullOrBlank()) {
            error.value = "Select a delivery address"
            return
        }
        if (isCheckingOut.value) return
        isCheckingOut.value = true
        viewModelScope.launch {
            runCatching {
                val orderId = UUID.randomUUID().toString()
                val primarySeller = currentState.items.firstOrNull()?.sellerId ?: ""
                val order = OrderEntity(
                    orderId = orderId,
                    buyerId = uid,
                    sellerId = primarySeller,
                    totalAmount = currentState.total,
                    status = "pending_payment",
                    shippingAddress = currentState.selectedAddress,
                    paymentMethod = currentState.selectedPayment.name,
                    orderDate = System.currentTimeMillis()
                )
                orderRepository.upsert(order)
                currentState.items.forEach { item ->
                    cartRepository.removeItem(uid, item.productId)
                }
            }.onSuccess {
                successMessage.value = "Order placed successfully"
            }.onFailure { throwable ->
                error.value = throwable.message ?: "Unable to place order"
            }
            isCheckingOut.value = false
        }
    }
}
