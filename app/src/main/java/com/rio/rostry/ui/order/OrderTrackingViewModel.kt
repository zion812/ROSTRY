package com.rio.rostry.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import com.rio.rostry.data.repository.InvoiceRepository
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
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
    private val currentUserProvider: com.rio.rostry.session.CurrentUserProvider,
    // TODO: Inject dedicated analytics tracker for orders when available
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
        val negotiationStatus: String? = null
    )

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<OrderTrackingEvent>()
    val events = _events.receiveAsFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).collectLatest { orderEntity ->
                val detail = mapToDetail(orderEntity, emptyList())
                _uiState.update { it.copy(order = detail, isLoading = false) }
            }
        }

        viewModelScope.launch {
            trackingEventDao.observeByOrder(orderId).collectLatest { events ->
                val mapped = events.sortedBy { it.timestamp }.map { e -> mapTimelineEvent(e) }
                val current = _uiState.value.order
                _uiState.update { it.copy(order = current?.copy(timelineEvents = mapped)) }
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
                // TODO: Track order cancellation via analytics when tracker supports it
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
        viewModelScope.launch {
            try {
                // Placeholder: persist via repository when available
                // For now, just emit a success event message
                _events.send(OrderTrackingEvent.Error("Thank you for your feedback!"))
            } catch (e: Exception) {
                _events.send(OrderTrackingEvent.Error("Failed to submit rating"))
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
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError("Failed to confirm payment: ${e.message}")
            }
        }
    }

    // Handle network errors gracefully by showing in UI state
    private fun handleError(message: String) {
        _uiState.update { it.copy(error = message, isLoading = false) }
    }

    private fun mapToDetail(order: OrderEntity?, events: List<OrderTrackingEventEntity>): OrderDetail {
        if (order == null) return OrderDetail(
            orderId = "",
            status = UiOrderStatus.PLACED,
            items = emptyList(),
            total = 0.0,
            deliveryAddress = "",
            estimatedDeliveryDate = null,
            paymentMethod = "",
            paymentStatus = "pending",
            sellerName = "",
            sellerPhone = "",
            canCancel = true,
            timelineEvents = emptyList(),
            isBuyer = false,
            isSeller = false,
            negotiationStatus = null,
            buyerId = null,
            sellerId = ""
        )

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

        val items = if (false) {
            // Placeholder if line items become accessible
            emptyList()
        } else {
            listOf(
                OrderItem(
                    productId = order.orderId,
                    name = order.notes ?: "Order",
                    quantity = 1.0,
                    price = order.totalAmount,
                    imageUrl = null
                )
            )
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

        val currentUserId = currentUserProvider.userIdOrNull()
        val isBuyer = currentUserId == order.buyerId
        val isSeller = currentUserId == order.sellerId

        return OrderDetail(
            orderId = order.orderId,
            status = uiStatus,
            items = items,
            total = order.totalAmount,
            deliveryAddress = order.shippingAddress,
            estimatedDeliveryDate = order.expectedDeliveryDate,
            paymentMethod = order.paymentMethod ?: "Unknown",
            paymentStatus = order.paymentStatus,
            buyerId = order.buyerId,
            sellerId = order.sellerId,
            sellerName = "",
            sellerPhone = "",
            canCancel = canCancel,
            timelineEvents = mappedEvents,
            isBuyer = isBuyer,
            isSeller = isSeller,
            negotiationStatus = order.negotiationStatus
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
