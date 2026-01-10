package com.rio.rostry.ui.farmer.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.MarketListingRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val listingRepository: MarketListingRepository,
    private val rbac: com.rio.rostry.domain.rbac.RbacGuard
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateListingUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAsset(assetId: String) {
        viewModelScope.launch {
            assetRepository.getAssetById(assetId)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val asset = result.data
                            if (asset != null) {
                                _uiState.update { 
                                    it.copy(
                                        isLoading = false,
                                        asset = asset,
                                        listingPrice = 0.0, // Default
                                        listingTitle = "${asset.breed} ${asset.name}".trim(),
                                        listingDescription = asset.description,
                                        quantityToSell = asset.quantity
                                    ) 
                                }
                            } else {
                                _uiState.update { it.copy(isLoading = false, error = "Asset not found") }
                            }
                        }
                        is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    }
                }
        }
    }

    fun onQuantityChanged(qty: String) {
        _uiState.update { it.copy(quantityToSell = qty.toDoubleOrNull() ?: 0.0) }
    }

    fun onPriceChanged(price: String) {
        _uiState.update { it.copy(listingPrice = price.toDoubleOrNull() ?: 0.0) }
    }
    
    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(listingTitle = title) }
    }

    fun onDescriptionChanged(desc: String) {
        _uiState.update { it.copy(listingDescription = desc) }
    }

    fun submitListing() {
        val currentState = _uiState.value
        val asset = currentState.asset ?: return
        
        timber.log.Timber.d("submitListing: Starting listing creation for asset ${asset.assetId}")

        if (currentState.quantityToSell <= 0 || currentState.listingPrice <= 0) {
            _uiState.update { it.copy(error = "Invalid price or quantity") }
            return
        }
        
        if (currentState.listingTitle.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a title") }
            return
        }

        viewModelScope.launch {
            // Check verification status before proceeding
            val canList = rbac.canListProduct()
            timber.log.Timber.d("submitListing: RBAC check result: $canList")
            
            if (!canList) {
                _uiState.update { 
                    it.copy(
                        error = "Complete KYC verification to list products. Go to Profile → Verification.",
                        verificationRequired = true
                    ) 
                }
                return@launch
            }

            _uiState.update { it.copy(isSubmitting = true, error = null) }
            
            // Use the new repository method for offline-first listing creation
            val result = listingRepository.createListingFromAsset(
                assetId = asset.assetId,
                price = currentState.listingPrice,
                quantity = currentState.quantityToSell,
                title = currentState.listingTitle,
                description = currentState.listingDescription
            )
            
            when (result) {
                is Resource.Success -> {
                    timber.log.Timber.d("submitListing: Listing created with ID ${result.data}")
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    timber.log.Timber.e("submitListing: Failed - ${result.message}")
                    _uiState.update { 
                        it.copy(isSubmitting = false, error = result.message ?: "Failed to create listing") 
                    }
                }
                is Resource.Loading -> {
                    // Should not reach here for suspend function
                }
            }
        }
    }
}

data class CreateListingUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val asset: FarmAssetEntity? = null,
    val listingTitle: String = "",
    val listingDescription: String = "",
    val listingCategory: String = "",
    val listingPrice: Double = 0.0,
    val quantityToSell: Double = 0.0,
    val error: String? = null,
    val verificationRequired: Boolean = false
)
