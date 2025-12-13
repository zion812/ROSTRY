package com.rio.rostry.ui.auction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.AuctionEntity
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
    
    val auctionState: StateFlow<AuctionUiState> = _auctionId
        .flatMapLatest { id ->
            if (id == null) flowOf(AuctionUiState(isLoading = true))
            else auctionRepository.observeAuction(id).map { auction ->
                if (auction == null) {
                    AuctionUiState(error = "Auction not found")
                } else {
                    val userId = currentUserProvider.userIdOrNull()
                    val isWinning = userId != null && auction.winnerId == userId
                    AuctionUiState(
                        auction = auction,
                        isWinning = isWinning,
                        isLoading = false
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuctionUiState(isLoading = true)
        )

    private val _bidEvent = MutableSharedFlow<String>()
    val bidEvent = _bidEvent.asSharedFlow()

    fun loadAuction(auctionId: String) {
        _auctionId.value = auctionId
    }

    fun placeBid(amount: Double) {
        val auctionId = _auctionId.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            viewModelScope.launch { _bidEvent.emit("Please login to bid") }
            return
        }

        viewModelScope.launch {
            when (val result = auctionRepository.placeBid(auctionId, userId, amount)) {
                is Resource.Success -> _bidEvent.emit("Bid placed successfully")
                is Resource.Error -> _bidEvent.emit(result.message ?: "Bid failed")
                else -> {}
            }
        }
    }
}

data class AuctionUiState(
    val auction: AuctionEntity? = null,
    val isWinning: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
