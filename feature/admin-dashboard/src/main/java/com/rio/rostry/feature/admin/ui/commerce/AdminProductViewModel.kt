package com.rio.rostry.feature.admin.ui.commerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.admin.repository.AdminProductRepository
import com.rio.rostry.domain.account.repository.UserRepository
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
    private val adminProductRepository: AdminProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    enum class ProductFilter { ALL, FLAGGED, ACTIVE, HIDDEN }

    data class UiState(
        val products: List<ProductEntity> = emptyList(),
        val filteredProducts: List<ProductEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val currentFilter: ProductFilter = ProductFilter.ALL,
        val processingId: String? = null
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
                    is com.rio.rostry.core.model.Result.Success<*> -> {
                        val products = (result.data as? List<ProductEntity>) ?: emptyList()
                        _uiState.update { 
                            it.copy(
                                products = products, 
                                isLoading = false
                            )
                        }
                        applyFilters()
                    }
                    is com.rio.rostry.core.model.Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.exception.message) }
                        _toastEvent.emit("Failed to load products: ${result.exception.message}")
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFilterChanged(filter: ProductFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.products

        // Apply filter
        filtered = when (state.currentFilter) {
            ProductFilter.ALL -> filtered
            ProductFilter.FLAGGED -> filtered.filter { it.adminFlagged }
            ProductFilter.ACTIVE -> filtered.filter { it.status == "active" && !it.adminFlagged }
            ProductFilter.HIDDEN -> filtered.filter { it.status == "private" || it.status == "hidden" }
        }

        // Apply search
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.name.lowercase().contains(query) ||
                it.sellerId.lowercase().contains(query) ||
                (it.moderationNote?.lowercase()?.contains(query) == true)
            }
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    fun flagProduct(productId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = productId) }
            when (val result = adminProductRepository.flagProduct(productId, reason)) {
                is com.rio.rostry.core.model.Result.Success -> {
                    _toastEvent.emit("Product flagged")
                    updateProductLocally(productId) { it.copy(adminFlagged = true, moderationNote = reason) }
                }
                is com.rio.rostry.core.model.Result.Error -> {
                    _toastEvent.emit("Failed to flag: ${result.exception.message}")
                }
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun unflagProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = productId) }
            when (val result = adminProductRepository.unflagProduct(productId)) {
                is com.rio.rostry.core.model.Result.Success -> {
                    _toastEvent.emit("Product unflagged")
                    updateProductLocally(productId) { it.copy(adminFlagged = false, moderationNote = null) }
                }
                is com.rio.rostry.core.model.Result.Error -> {
                    _toastEvent.emit("Failed to unflag: ${result.exception.message}")
                }
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun hideProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = productId) }
            when (val result = adminProductRepository.hideProduct(productId)) {
                is com.rio.rostry.core.model.Result.Success -> {
                    _toastEvent.emit("Product hidden (Private)")
                    updateProductLocally(productId) { it.copy(status = "private") }
                }
                is com.rio.rostry.core.model.Result.Error -> {
                    _toastEvent.emit("Failed to hide: ${result.exception.message}")
                }
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = productId) }
            when (val result = adminProductRepository.deleteProduct(productId)) {
                is com.rio.rostry.core.model.Result.Success -> {
                    _toastEvent.emit("Product deleted")
                    _uiState.update { state ->
                        state.copy(
                            products = state.products.filter { it.productId != productId },
                            filteredProducts = state.filteredProducts.filter { it.productId != productId }
                        )
                    }
                }
                is com.rio.rostry.core.model.Result.Error -> {
                    _toastEvent.emit("Failed to delete: ${result.exception.message}")
                }
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun suspendSeller(sellerId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = sellerId) }
            when (val result = userRepository.suspendUser(sellerId, reason, null)) {
                is com.rio.rostry.utils.Resource.Success -> {
                    _toastEvent.emit("Seller suspended")
                }
                is com.rio.rostry.utils.Resource.Error -> {
                    _toastEvent.emit("Failed to suspend seller: ${result.message}")
                }
                else -> {}
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    fun restoreProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = productId) }
            when (val result = adminProductRepository.restoreProduct(productId)) {
                is com.rio.rostry.core.model.Result.Success -> {
                    _toastEvent.emit("Product restored to active")
                    updateProductLocally(productId) { it.copy(status = "active") }
                }
                is com.rio.rostry.core.model.Result.Error -> {
                    _toastEvent.emit("Failed to restore: ${result.exception.message}")
                }
            }
            _uiState.update { it.copy(processingId = null) }
        }
    }

    private fun updateProductLocally(productId: String, transform: (ProductEntity) -> ProductEntity) {
        _uiState.update { state ->
            val updated = state.products.map { if (it.productId == productId) transform(it) else it }
            state.copy(products = updated)
        }
        applyFilters()
    }
}

