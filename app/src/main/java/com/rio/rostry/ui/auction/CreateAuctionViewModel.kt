package com.rio.rostry.ui.auction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.AuctionRepository
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateAuctionViewModel @Inject constructor(
    private val auctionRepository: AuctionRepository,
    private val assetRepository: FarmAssetRepository,
    private val productRepository: ProductRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAuctionUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun loadAsset(assetId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
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
                                        startPrice = 0.0,
                                        description = asset.description
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

    fun onStartPriceChanged(price: String) {
        _uiState.update { it.copy(startPrice = price.toDoubleOrNull() ?: 0.0) }
    }

    fun onReservePriceChanged(price: String) {
        _uiState.update { it.copy(reservePrice = price.toDoubleOrNull()) }
    }

    fun onBuyNowPriceChanged(price: String) {
        _uiState.update { it.copy(buyNowPrice = price.toDoubleOrNull()) }
    }

    fun onDurationChanged(durationHours: Int) {
        _uiState.update { it.copy(durationHours = durationHours) }
    }
    
    fun onDescriptionChanged(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun createAuction() {
        val state = _uiState.value
        val asset = state.asset ?: return
        val userId = currentUserProvider.userIdOrNull() ?: return
        
        // Validation
        if (state.startPrice <= 0) {
            emitEvent("Start price must be greater than 0")
            return
        }
        
        if (state.reservePrice != null && state.reservePrice!! <= state.startPrice) {
            emitEvent("Reserve price must be greater than start price")
            return
        }
        
        if (state.buyNowPrice != null && state.buyNowPrice!! <= (state.reservePrice ?: state.startPrice)) {
            emitEvent("Buy Now price must be greater than reserve/start price")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            
            try {
                val now = System.currentTimeMillis()
                val auctionId = UUID.randomUUID().toString()
                val productId = UUID.randomUUID().toString()
                val endsAt = now + (state.durationHours * 3600 * 1000L)
                
                // 1. Create Product (required for Auction FK)
                val product = ProductEntity(
                    productId = productId,
                    sellerId = userId,
                    name = "${asset.breed} ${asset.name}".trim(),
                    description = state.description,
                    category = asset.category,
                    price = 0.0, // Determined by auction
                    quantity = asset.quantity,
                    unit = asset.unit,
                    imageUrls = asset.imageUrls,
                    sourceAssetId = asset.assetId,
                    status = "available",
                    createdAt = now,
                    updatedAt = now
                )
                
                val productResult = productRepository.upsert(product)
                if (productResult is Resource.Error) {
                    throw Exception("Failed to create product: ${productResult.message}")
                }
                
                // 2. Create Auction
                val auction = AuctionEntity(
                    auctionId = auctionId,
                    sellerId = userId,
                    productId = productId,
                    // removed product details fields that don't exist in AuctionEntity
                    startsAt = now,
                    endsAt = endsAt,
                    minPrice = state.startPrice, // Correct field name
                    currentPrice = state.startPrice,
                    reservePrice = state.reservePrice,
                    buyNowPrice = state.buyNowPrice,
                    bidCount = 0,
                    status = "ACTIVE",
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )
                
                val result = auctionRepository.createAuction(auction)
                if (result is Resource.Error) {
                    throw Exception(result.message)
                }
                
                // 3. Mark Asset as LISTED
                assetRepository.markAsListed(asset.assetId, auctionId, now)
                
                emitEvent("Auction created successfully!")
                _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                
            } catch (e: Exception) {
                emitEvent(e.message ?: "Failed to create auction")
                _uiState.update { it.copy(isSubmitting = false, error = e.message) }
            }
        }
    }
    
    private fun emitEvent(msg: String) {
        viewModelScope.launch { _event.emit(msg) }
    }
}

data class CreateAuctionUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val asset: FarmAssetEntity? = null,
    val startPrice: Double = 0.0,
    val reservePrice: Double? = null,
    val buyNowPrice: Double? = null,
    val durationHours: Int = 24, // Default 24h
    val description: String = ""
)
