package com.rio.rostry.ui.farmer.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.InventoryRepository
import com.rio.rostry.data.repository.MarketListingRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val inventoryRepository: InventoryRepository,
    private val listingRepository: MarketListingRepository,
    private val rbac: com.rio.rostry.domain.rbac.RbacGuard,
    private val auth: com.google.firebase.auth.FirebaseAuth
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
        val user = auth.currentUser ?: return

        if (currentState.quantityToSell <= 0 || currentState.listingPrice <= 0) {
            _uiState.update { it.copy(error = "Invalid price or quantity") }
            return
        }

        viewModelScope.launch {
            // Check verification status before proceeding
            if (!rbac.canListProduct()) {
                _uiState.update { 
                    it.copy(
                        error = "Complete KYC verification to list products. Go to Profile → Verification.",
                        verificationRequired = true
                    ) 
                }
                return@launch
            }

            _uiState.update { it.copy(isSubmitting = true) }
            try {
                // 1. Create Inventory Item
                val inventoryId = UUID.randomUUID().toString()
                val inventory = InventoryItemEntity(
                    inventoryId = inventoryId,
                    farmerId = user.uid,
                    sourceAssetId = asset.assetId,
                    name = currentState.listingTitle,
                    category = asset.category,
                    quantityAvailable = currentState.quantityToSell,
                    unit = asset.unit,
                    createdAt = System.currentTimeMillis()
                )
                inventoryRepository.addInventory(inventory)

                // 2. Create Market Listing
                val listing = MarketListingEntity(
                    listingId = UUID.randomUUID().toString(),
                    sellerId = user.uid,
                    inventoryId = inventoryId,
                    title = currentState.listingTitle,
                    description = currentState.listingDescription,
                    price = currentState.listingPrice,
                    category = asset.category,
                    imageUrls = asset.imageUrls,
                    status = "PUBLISHED",
                    isActive = true,
                    createdAt = System.currentTimeMillis()
                )
                listingRepository.publishListing(listing)
                
                // 3. Update source asset quantity (optional, or just track reserved)
                // For now, simple deduction logic
                val newQty = asset.quantity - currentState.quantityToSell
                if (newQty >= 0) {
                     assetRepository.updateQuantity(asset.assetId, newQty)
                }

                _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, error = e.message ?: "Failed to create listing") }
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
