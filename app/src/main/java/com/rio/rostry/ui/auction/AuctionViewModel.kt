package com.rio.rostry.ui.auction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.database.entity.BidEntity
import com.rio.rostry.data.repository.AuctionRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuctionViewModel @Inject constructor(
    private val auctionRepository: AuctionRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _auctionId = MutableStateFlow<String?>(null)
    
    // Combine auction data, bids, and user context into a single UI state
    val auctionState: StateFlow<AuctionUiState> = _auctionId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(AuctionUiState(isLoading = true))
            } else {
                combine(
                    auctionRepository.observeAuction(id),
                    auctionRepository.observeAuctionBids(id)
                ) { auction, bids ->
                    if (auction == null) {
                        AuctionUiState(error = "Auction not found")
                    } else {
                        val userId = currentUserProvider.userIdOrNull()
                        val isSeller = userId != null && auction.sellerId == userId
                        val isWinning = userId != null && auction.winnerId == userId
                        
                        // Check if Buy Now is available (auction active + buyNowPrice set)
                        val canBuyNow = auction.isActive && 
                                       auction.status == "ACTIVE" && 
                                       auction.buyNowPrice != null && 
                                       !isSeller
                                       
                        // Check if Cancel is available (seller + no bids)
                        val canCancel = isSeller && 
                                       auction.status == "ACTIVE" && 
                                       auction.bidCount == 0
                        
                        AuctionUiState(
                            auction = auction,
                            bids = bids,
                            isSeller = isSeller,
                            isWinning = isWinning,
                            canBuyNow = canBuyNow,
                            canCancel = canCancel,
                            isLoading = false
                        )
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuctionUiState(isLoading = true)
        )

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun loadAuction(auctionId: String) {
        _auctionId.value = auctionId
    }

    fun placeBid(amount: Double) {
        val currentState = auctionState.value
        val auctionId = _auctionId.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            emitEvent("Please login to bid")
            return
        }
        
        // Quick validations before hitting repo
        if (currentState.isSeller) {
            emitEvent("You cannot bid on your own auction")
            return
        }

        viewModelScope.launch {
            when (val result = auctionRepository.placeBid(auctionId, userId, amount)) {
                is Resource.Success -> emitEvent("Bid placed successfully")
                is Resource.Error -> emitEvent(result.message ?: "Bid failed")
                else -> {}
            }
        }
    }
    
    fun buyNow() {
        val currentState = auctionState.value
        val auctionId = _auctionId.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            emitEvent("Please login to buy")
            return
        }
        
        if (!currentState.canBuyNow) {
            emitEvent("Buy Now not available")
            return
        }
        
        viewModelScope.launch {
            when (val result = auctionRepository.buyNow(auctionId, userId)) {
                is Resource.Success -> emitEvent("Congratulations! You purchased this item.")
                is Resource.Error -> emitEvent(result.message ?: "Purchase failed")
                else -> {}
            }
        }
    }
    
    fun cancelAuction() {
        val currentState = auctionState.value
        val auctionId = _auctionId.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: return
        
        if (!currentState.canCancel) {
            emitEvent("Cannot cancel this auction")
            return
        }
        
        viewModelScope.launch {
            when (val result = auctionRepository.cancelAuction(auctionId, userId)) {
                is Resource.Success -> emitEvent("Auction cancelled")
                is Resource.Error -> emitEvent(result.message ?: "Cancellation failed")
                else -> {}
            }
        }
    }
    
    private fun emitEvent(message: String) {
        viewModelScope.launch { _event.emit(message) }
    }
}

data class AuctionUiState(
    val auction: AuctionEntity? = null,
    val bids: List<BidEntity> = emptyList(),
    val isSeller: Boolean = false,
    val isWinning: Boolean = false,
    val canBuyNow: Boolean = false,
    val canCancel: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
