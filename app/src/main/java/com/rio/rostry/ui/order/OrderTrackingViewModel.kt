package com.rio.rostry.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import com.rio.rostry.data.repository.InvoiceRepository
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.ReviewRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import kotlinx.coroutines.flow.combine
import com.rio.rostry.domain.model.OrderStatus
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val orderManagementRepository: OrderManagementRepository,
    private val trackingEventDao: OrderTrackingEventDao,
    private val invoiceRepository: InvoiceRepository,
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val userDao: UserDao,
    private val currentUserProvider: com.rio.rostry.session.CurrentUserProvider,
    private val analyticsRepository: com.rio.rostry.data.repository.analytics.AnalyticsRepository
) : ViewModel() {

    enum class UiOrderStatus { PLACED, CONFIRMED, PROCESSING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED }

    data class OrderItem(
        val productId: String,
        val name: String,
        val quantity: Double,
        val price: Double,
        val imageUrl: String?
    )

    data class TimelineEvent(
        val status: UiOrderStatus,
        val note: String?,
        val timestamp: Long,
        val hubId: String?
    )

    data class OrderDetail(
        val orderId: String,
        val status: UiOrderStatus,
        val items: List<OrderItem>,
        val total: Double,
        val deliveryAddress: String,
        val estimatedDeliveryDate: Long?,
        val paymentMethod: String,
        val paymentStatus: String,
        val buyerId: String?,
        val sellerId: String,
        val sellerName: String,
        val sellerPhone: String,
        val canCancel: Boolean,
        val timelineEvents: List<TimelineEvent>,
        val isBuyer: Boolean = false,
        val isSeller: Boolean = false,
        val negotiationStatus: String? = null,
        // COD Verification
        val deliveryOtp: String? = null,
        val isVerified: Boolean = false
    )

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<OrderTrackingEvent>()
    val events = _events.receiveAsFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                orderRepository.getOrderById(orderId),
                orderRepository.getOrderItems(orderId),
                trackingEventDao.observeByOrder(orderId)
            ) { order, items, events ->
                Triple(order, items, events)
            }.collectLatest { (order, items, events) ->
                if (order == null) {
                    _uiState.update { it.copy(order = null, isLoading = false) }
                    return@collectLatest
                }

                // Fetch seller details
                val seller = userDao.findById(order.sellerId)
                
                // Fetch product details for names/images
                val orderItems = items.map { item ->
                    val product = productRepository.findById(item.productId)
                    OrderItem(
                        productId = item.productId,
                        name = product?.name ?: "Unknown Product",
                        quantity = item.quantity,
                        price = item.priceAtPurchase,
                        imageUrl = product?.imageUrls?.firstOrNull()
                    )
                }

                val detail = mapToDetail(order, events, orderItems, seller)
                _uiState.update { it.copy(order = detail, isLoading = false) }
            }
        }
    }

    fun cancelOrder(reason: String?) {
        val currentOrder = uiState.value.order
        if (currentOrder == null) {
            viewModelScope.launch {
                _events.send(OrderTrackingEvent.Error("Order not found"))
            }
            return
        }

        // Validation: only allow cancellation if not shipped or delivered
        val cancellableStatuses = listOf(UiOrderStatus.PLACED, UiOrderStatus.CONFIRMED, UiOrderStatus.PROCESSING)
        if (currentOrder.status !in cancellableStatuses) {
            viewModelScope.launch {
                _events.send(OrderTrackingEvent.Error("Order cannot be cancelled at this stage"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = orderManagementRepository.cancelOrder(currentOrder.orderId, reason)
            _uiState.update { it.copy(isLoading = false) }
            if (result is Resource.Success) {
                _events.send(OrderTrackingEvent.OrderCancelled)
                analyticsRepository.trackOrderCancelled(currentOrder.orderId, reason)
            } else {
                _events.send(OrderTrackingEvent.Error(result.message ?: "Failed to cancel order"))
            }
        }
    }

    fun downloadInvoice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // First try to get existing invoice
            val orderId = _uiState.value.order?.orderId ?: return@launch
            var result = invoiceRepository.getInvoiceByOrder(orderId)
            if (result is Resource.Error) {
                // If not found, generate new invoice (assuming order has items, but since OrderEntity may not, this is placeholder)
                // In real impl, get order items from repository or entity
                // For now, assume generation requires items, so emit error if not possible
                _events.send(OrderTrackingEvent.Error("Invoice generation requires order items"))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            _uiState.update { it.copy(isLoading = false) }
            if (result is Resource.Success) {
                val data = result.data
                if (data != null) {
                    _events.send(OrderTrackingEvent.InvoiceDownloaded(data))
                } else {
                    _events.send(OrderTrackingEvent.Error("Invoice not available"))
                }
            } else {
                _events.send(OrderTrackingEvent.Error(result.message ?: "Failed to download invoice"))
            }
        }
    }

    fun submitRating(orderId: String, rating: Int, review: String) {
        val currentOrder = uiState.value.order ?: return
        val uid = currentUserProvider.userIdOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = reviewRepository.submitReview(
                productId = currentOrder.items.firstOrNull()?.productId,
                sellerId = currentOrder.sellerId,
                orderId = orderId,
                reviewerId = uid,
                rating = rating,
                title = "Order Review",
                content = review,
                isVerifiedPurchase = true
            )
            _uiState.update { it.copy(isLoading = false) }

            if (result is Resource.Success) {
                _events.send(OrderTrackingEvent.Error("Thank you for your feedback!"))
                analyticsRepository.trackOrderRated(orderId, rating)
            } else {
                _events.send(OrderTrackingEvent.Error(result.message ?: "Failed to submit rating"))
            }
        }
    }

    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val currentOrder = orderRepository.getOrderById(orderId).firstOrNull()
                if (currentOrder != null) {
                    val updated = currentOrder.copy(
                        negotiationStatus = "AGREED",
                        status = "PLACED", // Ensure it's placed
                        dirty = true,
                        updatedAt = System.currentTimeMillis()
                    )
                    orderRepository.upsert(updated)
                    _events.send(OrderTrackingEvent.Error("Order accepted"))
                    analyticsRepository.trackOrderAccepted(orderId)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError("Failed to accept order: ${e.message}")
            }
        }
    }

    fun submitBill(orderId: String, amount: Double, billImageUri: String?) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val currentOrder = orderRepository.getOrderById(orderId).firstOrNull()
                if (currentOrder != null) {
                    val updated = currentOrder.copy(
                        totalAmount = amount,
                        billImageUri = billImageUri,
                        status = "PENDING_PAYMENT",
                        dirty = true,
                        updatedAt = System.currentTimeMillis()
                    )
                    orderRepository.upsert(updated)
                    _events.send(OrderTrackingEvent.Error("Bill submitted successfully"))
                    analyticsRepository.trackBillSubmitted(orderId, amount)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError("Failed to submit bill: ${e.message}")
            }
        }
    }

    fun uploadPaymentSlip(orderId: String, slipImageUri: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val currentOrder = orderRepository.getOrderById(orderId).firstOrNull()
                if (currentOrder != null) {
                    val updated = currentOrder.copy(
                        paymentStatus = "submitted",
                        paymentSlipUri = slipImageUri,
                        dirty = true,
                        updatedAt = System.currentTimeMillis()
                    )
                    orderRepository.upsert(updated)
                    _events.send(OrderTrackingEvent.Error("Payment slip uploaded"))
                    analyticsRepository.trackPaymentSlipUploaded(orderId)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError("Failed to upload slip: ${e.message}")
            }
        }
    }

    fun confirmPayment(orderId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val currentOrder = orderRepository.getOrderById(orderId).firstOrNull()
                if (currentOrder != null) {
                    val updated = currentOrder.copy(
                        paymentStatus = "success",
                        status = "CONFIRMED",
                        dirty = true,
                        updatedAt = System.currentTimeMillis()
                    )
                    orderRepository.upsert(updated)
                    _events.send(OrderTrackingEvent.Error("Payment confirmed"))
                    analyticsRepository.trackPaymentConfirmed(orderId)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError("Failed to confirm payment: ${e.message}")
            }
        }
    }

    // ===== COD Verification =====

    fun generateDeliveryOtp() {
        val currentOrder = uiState.value.order ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.generateDeliveryOtp(currentOrder.orderId)) {
                is Resource.Success -> {
                    _events.send(OrderTrackingEvent.Error("OTP Generated")) // Using Error meant as Toast/Snackbar message in this VM convention
                    // State updates via flow observation
                }
                is Resource.Error -> {
                    _events.send(OrderTrackingEvent.Error(result.message ?: "Failed to generate OTP"))
                }
                else -> {}
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun verifyDeliveryOtp(otpInput: String) {
        val currentOrder = uiState.value.order ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.confirmDeliveryWithOtp(currentOrder.orderId, otpInput)) {
                is Resource.Success -> {
                    _events.send(OrderTrackingEvent.Error("Delivery Verified Successfully"))
                    // State updates via flow observation
                }
                is Resource.Error -> {
                    _events.send(OrderTrackingEvent.Error(result.message ?: "Verification Failed"))
                }
                else -> {}
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // Handle network errors gracefully by showing in UI state
    private fun handleError(message: String) {
        _uiState.update { it.copy(error = message, isLoading = false) }
    }

    private fun mapToDetail(
        order: OrderEntity, 
        events: List<OrderTrackingEventEntity>, 
        orderItems: List<OrderItem>,
        seller: com.rio.rostry.data.database.entity.UserEntity?
    ): OrderDetail {
        val statusEnum = OrderStatus.fromString(order.status)
        val uiStatus = when (statusEnum) {
            OrderStatus.PLACED -> UiOrderStatus.PLACED
            OrderStatus.CONFIRMED -> UiOrderStatus.CONFIRMED
            OrderStatus.PROCESSING -> UiOrderStatus.PROCESSING
            OrderStatus.OUT_FOR_DELIVERY -> UiOrderStatus.OUT_FOR_DELIVERY
            OrderStatus.DELIVERED -> UiOrderStatus.DELIVERED
            OrderStatus.CANCELLED -> UiOrderStatus.CANCELLED
            OrderStatus.REFUNDED -> UiOrderStatus.REFUNDED
            else -> UiOrderStatus.PLACED
        }

        val canCancel = if (uiStatus !in listOf(UiOrderStatus.PLACED, UiOrderStatus.CONFIRMED, UiOrderStatus.PROCESSING)) {
            false
        } else if (order.paymentMethod == "COD") {
            val elapsed = System.currentTimeMillis() - order.createdAt
            elapsed <= 30 * 60 * 1000 // 30 minutes
        } else {
            order.paymentStatus == "pending"
        }

        val mappedEvents = events.sortedBy { it.timestamp }.map { mapTimelineEvent(it) }

        val uid = currentUserProvider.userIdOrNull()
        val isBuyer = uid == order.buyerId
        val isSeller = uid == order.sellerId

        return OrderDetail(
            orderId = order.orderId,
            status = uiStatus,
            items = orderItems,
            total = order.totalAmount,
            deliveryAddress = order.shippingAddress,
            estimatedDeliveryDate = order.expectedDeliveryDate,
            paymentMethod = order.paymentMethod ?: "Unknown",
            paymentStatus = order.paymentStatus,
            buyerId = order.buyerId,
            sellerId = order.sellerId,
            sellerName = seller?.fullName ?: "Unknown Seller",
            sellerPhone = seller?.phoneNumber ?: "",
            canCancel = canCancel,
            timelineEvents = mappedEvents,
            isBuyer = isBuyer,
            isSeller = isSeller,
            negotiationStatus = order.negotiationStatus,
            deliveryOtp = order.otp,
            isVerified = order.isVerified
        )
    }

    private fun mapTimelineEvent(e: OrderTrackingEventEntity): TimelineEvent {
        val statusEnum = OrderStatus.fromString(e.status)
        val uiStatus = when (statusEnum) {
            OrderStatus.PLACED -> UiOrderStatus.PLACED
            OrderStatus.CONFIRMED -> UiOrderStatus.CONFIRMED
            OrderStatus.PROCESSING -> UiOrderStatus.PROCESSING
            OrderStatus.OUT_FOR_DELIVERY -> UiOrderStatus.OUT_FOR_DELIVERY
            OrderStatus.DELIVERED -> UiOrderStatus.DELIVERED
            OrderStatus.CANCELLED -> UiOrderStatus.CANCELLED
            OrderStatus.REFUNDED -> UiOrderStatus.REFUNDED
            else -> UiOrderStatus.PLACED
        }
        return TimelineEvent(status = uiStatus, note = e.note, timestamp = e.timestamp, hubId = e.hubId)
    }
}

data class OrderTrackingUiState(
    val order: OrderTrackingViewModel.OrderDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class OrderTrackingEvent {
    object OrderCancelled : OrderTrackingEvent()
    data class InvoiceDownloaded(val invoice: Pair<com.rio.rostry.data.database.entity.InvoiceEntity, List<com.rio.rostry.data.database.entity.InvoiceLineEntity>>) : OrderTrackingEvent()
    data class Error(val message: String) : OrderTrackingEvent()
}
