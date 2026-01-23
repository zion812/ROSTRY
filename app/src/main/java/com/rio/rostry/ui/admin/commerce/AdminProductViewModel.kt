package com.rio.rostry.ui.admin.commerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.AdminProductRepository
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
class AdminProductViewModel @Inject constructor(
    private val adminProductRepository: AdminProductRepository
) : ViewModel() {

    data class UiState(
        val products: List<ProductEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            adminProductRepository.getAllProductsAdmin().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(products = result.data ?: emptyList(), isLoading = false) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                        _toastEvent.emit("Failed to load products: ${result.message}")
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun flagProduct(productId: String, reason: String) {
        viewModelScope.launch {
            when (val result = adminProductRepository.flagProduct(productId, reason)) {
                is Resource.Success -> {
                    _toastEvent.emit("Product flagged")
                    // Optimistic update
                    _uiState.update { state ->
                        val updated = state.products.map { 
                            if (it.productId == productId) it.copy(adminFlagged = true, moderationNote = reason) else it
                        }
                        state.copy(products = updated)
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to flag: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun hideProduct(productId: String) {
        viewModelScope.launch {
            when (val result = adminProductRepository.hideProduct(productId)) {
                is Resource.Success -> {
                     _toastEvent.emit("Product hidden (Private)")
                    // Optimistic update
                    _uiState.update { state ->
                        val updated = state.products.map { 
                            if (it.productId == productId) it.copy(status = "private") else it
                        }
                        state.copy(products = updated)
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to hide: ${result.message}")
                }
                else -> Unit
            }
        }
    }
}
