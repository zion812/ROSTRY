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

    data class UiState(
        val orders: List<OrderEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
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
                    _uiState.update { it.copy(orders = result.data ?: emptyList(), isLoading = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                    _toastEvent.emit("Failed to load orders: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.adminCancelOrder(orderId, reason)) {
                is Resource.Success -> {
                    _toastEvent.emit("Order cancelled by Admin")
                    // Optimistic update
                     _uiState.update { state ->
                        val updated = state.orders.map { 
                            if (it.orderId == orderId) it.copy(status = "CANCELLED", cancellationReason = "Admin: $reason", cancellationTime = System.currentTimeMillis()) else it
                        }
                        state.copy(orders = updated, isLoading = false)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _toastEvent.emit("Failed to cancel: ${result.message}")
                }
                else -> Unit
            }
        }
    }
}
