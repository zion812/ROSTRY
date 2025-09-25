package com.rio.rostry.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.marketplace.payment.DemoPaymentService
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceSandboxViewModel @Inject constructor(
    private val productRepo: ProductMarketplaceRepository,
    private val demoPayments: DemoPaymentService
) : ViewModel() {

    data class UiState(
        val lastValidationResult: ProductValidator.ValidationResult? = null,
        val lastCreateResult: Resource<String>? = null,
        val lastPaymentResult: Resource<com.rio.rostry.marketplace.payment.MockPaymentManager.MockResult>? = null,
        val isLoading: Boolean = false,
        val message: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun validateSample(product: ProductEntity) {
        val res = ProductValidator.validate(product)
        _ui.value = _ui.value.copy(lastValidationResult = res, message = if (res.valid) "Validation passed" else res.reasons.joinToString("; "))
    }

    fun createSample(product: ProductEntity) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, message = null)
            val result = productRepo.createProduct(product, imageBytes = emptyList())
            _ui.value = _ui.value.copy(isLoading = false, lastCreateResult = result, message = (result as? Resource.Error)?.message)
        }
    }

    fun payFixed(orderId: String, userId: String, amount: Double, failNext: Boolean = false) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val res = demoPayments.payFixedPrice(orderId, userId, amount, failNext)
            _ui.value = _ui.value.copy(isLoading = false, lastPaymentResult = res, message = (res as? Resource.Error)?.message)
        }
    }

    fun payAuction(orderId: String, userId: String, amount: Double, failNext: Boolean = false) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val res = demoPayments.payAuctionBid(orderId, userId, amount, failNext)
            _ui.value = _ui.value.copy(isLoading = false, lastPaymentResult = res, message = (res as? Resource.Error)?.message)
        }
    }

    fun payCod(orderId: String, userId: String, amount: Double) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val res = demoPayments.payCashOnDelivery(orderId, userId, amount)
            _ui.value = _ui.value.copy(isLoading = false, lastPaymentResult = res, message = (res as? Resource.Error)?.message)
        }
    }

    fun payAdvance(orderId: String, userId: String, amount: Double, failNext: Boolean = false) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val res = demoPayments.payAdvance(orderId, userId, amount, failNext)
            _ui.value = _ui.value.copy(isLoading = false, lastPaymentResult = res, message = (res as? Resource.Error)?.message)
        }
    }
}
