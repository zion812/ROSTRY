package com.rio.rostry.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class MarketplaceSandboxViewModel @Inject constructor(
    private val productRepo: ProductMarketplaceRepository,
    private val productValidator: ProductValidator,
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: com.rio.rostry.data.repository.UserRepository
) : ViewModel() {

    data class UiState(
        val lastValidationResult: ProductValidator.ValidationResult? = null,
        val lastCreateResult: Resource<String>? = null,
        val isLoading: Boolean = false,
        val message: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun validateSample(product: ProductEntity) {
        viewModelScope.launch {
            val res = productValidator.validateWithTraceability(product)
            _ui.value = _ui.value.copy(
                lastValidationResult = res,
                message = if (res.valid) "Validation passed" else res.reasons.joinToString("; ")
            )
        }
    }

    fun createSample(product: ProductEntity) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, message = null)

            // Check verification status
            val currentUser = getCurrentUserOrNull()
            if (currentUser == null) {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    lastCreateResult = Resource.Error("User not authenticated"),
                    message = "Authentication required"
                )
                return@launch
            }

            if (currentUser.verificationStatus != VerificationStatus.VERIFIED) {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    lastCreateResult = Resource.Error("Complete KYC verification to list products"),
                    message = "User not verified"
                )
                return@launch
            }

            val result = productRepo.createProduct(product, imageBytes = emptyList())
            _ui.value = _ui.value.copy(isLoading = false, lastCreateResult = result, message = (result as? Resource.Error)?.message)
        }
    }

    private suspend fun getCurrentUserOrNull(): com.rio.rostry.data.database.entity.UserEntity? {
        return when (val res = userRepository.getCurrentUser().first()) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }
}

