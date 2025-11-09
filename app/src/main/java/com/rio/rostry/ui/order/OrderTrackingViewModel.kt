package com.rio.rostry.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import com.rio.rostry.data.repository.InvoiceRepository
import com.rio.rostry.data.repository.OrderManagementRepository
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    // TODO: Inject dedicated analytics tracker for orders when available
) : ViewModel() {

    enum class OrderStatus { PLACED, CONFIRMED, PROCESSING, OUT_FOR_DELIVERY, DELIVERED }

    data class OrderItem(
        val productId: String,
        val name: String,
        val quantity: Double,
        val price: Double,
        val imageUrl: String?
    )

    data class TimelineEvent(
        val status: OrderStatus,
        val note: String?,
        val timestamp: Long,
        val hubId: String?
    )

    data class OrderDetail(
        val orderId: String,
        val status: OrderStatus,
        val items: List<OrderItem>,
        val total: Double,
        val deliveryAddress: String,
        val estimatedDeliveryDate: Long?,
        val paymentMethod: String,
        val paymentStatus: String,
        val sellerName: String,
        val sellerPhone: String,
        val canCancel: Boolean,
        val timelineEvents: List<TimelineEvent>
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
        val cancellableStatuses = listOf(OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PROCESSING)
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

    // Handle network errors gracefully by showing in UI state
    private fun handleError(message: String) {
        _uiState.update { it.copy(error = message, isLoading = false) }
    }

    private fun mapToDetail(order: OrderEntity?, events: List<OrderTrackingEventEntity>): OrderDetail {
        if (order == null) return OrderDetail(
            orderId = "",
            status = OrderStatus.PLACED,
            items = emptyList(),
            total = 0.0,
            deliveryAddress = "",
            estimatedDeliveryDate = null,
            paymentMethod = "",
            paymentStatus = "pending",
            sellerName = "",
            sellerPhone = "",
            canCancel = true,
            timelineEvents = emptyList()
        )

        val statusEnum = when (order.status.uppercase()) {
            "PLACED" -> OrderStatus.PLACED
            "CONFIRMED" -> OrderStatus.CONFIRMED
            "PROCESSING" -> OrderStatus.PROCESSING
            "OUT_FOR_DELIVERY" -> OrderStatus.OUT_FOR_DELIVERY
            "DELIVERED" -> OrderStatus.DELIVERED
            else -> OrderStatus.PLACED
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

        val canCancel = statusEnum < OrderStatus.OUT_FOR_DELIVERY

        val mappedEvents = events.sortedBy { it.timestamp }.map { mapTimelineEvent(it) }

        return OrderDetail(
            orderId = order.orderId,
            status = statusEnum,
            items = items,
            total = order.totalAmount,
            deliveryAddress = order.shippingAddress,
            estimatedDeliveryDate = order.expectedDeliveryDate,
            paymentMethod = order.paymentMethod ?: "Unknown",
            paymentStatus = order.paymentStatus,
            sellerName = "",
            sellerPhone = "",
            canCancel = canCancel,
            timelineEvents = mappedEvents
        )
    }

    private fun mapTimelineEvent(e: OrderTrackingEventEntity): TimelineEvent {
        val st = when (e.status.uppercase()) {
            "PLACED" -> OrderStatus.PLACED
            "CONFIRMED" -> OrderStatus.CONFIRMED
            "PROCESSING" -> OrderStatus.PROCESSING
            "OUT_FOR_DELIVERY" -> OrderStatus.OUT_FOR_DELIVERY
            "DELIVERED" -> OrderStatus.DELIVERED
            else -> OrderStatus.PLACED
        }
        return TimelineEvent(status = st, note = e.note, timestamp = e.timestamp, hubId = e.hubId)
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