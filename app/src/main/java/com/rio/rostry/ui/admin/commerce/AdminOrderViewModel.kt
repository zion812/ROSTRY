package com.rio.rostry.ui.admin.commerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    enum class OrderFilter { ALL, PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED }

    companion object {
        val ORDER_STATUSES = listOf("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "REFUNDED")
    }

    data class UiState(
        val orders: List<OrderEntity> = emptyList(),
        val filteredOrders: List<OrderEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val currentFilter: OrderFilter = OrderFilter.ALL,
        val processingId: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.getAllOrdersAdmin()) {
                is Resource.Success -> {
                    val orders = result.data ?: emptyList()
                    _uiState.update { it.copy(orders = orders, isLoading = false) }
                    applyFilters()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                    _toastEvent.emit("Failed to load orders: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun refreshOrders() {
        loadOrders()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFilterChanged(filter: OrderFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.orders

        // Apply status filter
        filtered = when (state.currentFilter) {
            OrderFilter.ALL -> filtered
            OrderFilter.PENDING -> filtered.filter { it.status == "PENDING" }
            OrderFilter.PROCESSING -> filtered.filter { it.status == "PROCESSING" }
            OrderFilter.SHIPPED -> filtered.filter { it.status == "SHIPPED" }
            OrderFilter.DELIVERED -> filtered.filter { it.status == "DELIVERED" }
            OrderFilter.CANCELLED -> filtered.filter { it.status == "CANCELLED" }
            OrderFilter.REFUNDED -> filtered.filter { it.status == "REFUNDED" }
        }

        // Apply search
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.orderId.lowercase().contains(query) ||
                (it.buyerId?.lowercase()?.contains(query) == true) ||
                it.sellerId.lowercase().contains(query)
            }
        }

        _uiState.update { it.copy(filteredOrders = filtered) }
    }

    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = orderId) }
            when (val result = orderRepository.adminCancelOrder(orderId, reason)) {
                is Resource.Success -> {
                    _toastEvent.emit("Order cancelled")
                    updateOrderLocally(orderId) { 
                        it.copy(
                            status = "CANCELLED", 
                            cancellationReason = "Admin: $reason", 
                            cancellationTime = System.currentTimeMillis()
                        ) 
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to cancel: ${result.message}")
                }
                else -> Unit
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun refundOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = orderId) }
            when (val result = orderRepository.adminRefundOrder(orderId, reason)) {
                is Resource.Success -> {
                    _toastEvent.emit("Order refunded")
                    updateOrderLocally(orderId) { it.copy(status = "REFUNDED") }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to refund: ${result.message}")
                }
                else -> Unit
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = orderId) }
            when (val result = orderRepository.adminUpdateOrderStatus(orderId, newStatus)) {
                is Resource.Success -> {
                    _toastEvent.emit("Status updated to $newStatus")
                    updateOrderLocally(orderId) { it.copy(status = newStatus) }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to update status: ${result.message}")
                }
                else -> Unit
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun forceComplete(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = orderId) }
            when (val result = orderRepository.adminForceComplete(orderId)) {
                is Resource.Success -> {
                    _toastEvent.emit("Order forced to complete")
                    updateOrderLocally(orderId) { it.copy(status = "DELIVERED") }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed: ${result.message}")
                }
                else -> Unit
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    private fun updateOrderLocally(orderId: String, transform: (OrderEntity) -> OrderEntity) {
        _uiState.update { state ->
            val updated = state.orders.map { if (it.orderId == orderId) transform(it) else it }
            state.copy(orders = updated)
        }
        applyFilters()
    }
}
