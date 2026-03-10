package com.rio.rostry.feature.listing.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.commerce.repository.MarketplaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingManagementViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingManagementUiState())
    val uiState: StateFlow<ListingManagementUiState> = _uiState.asStateFlow()

    fun loadListings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun createListing(listing: MarketListing) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Listing created")
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class ListingManagementUiState(
    val isLoading: Boolean = false,
    val listings: List<MarketListing> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)
