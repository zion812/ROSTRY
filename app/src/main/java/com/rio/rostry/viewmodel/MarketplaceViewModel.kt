package com.rio.rostry.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.MarketListing
import com.rio.rostry.data.models.ListingStatus
import com.rio.rostry.repository.MarketplaceRepository
import com.rio.rostry.repository.DataException
import com.rio.rostry.repository.DataErrorType
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MarketplaceViewModel(private val repo: MarketplaceRepository = MarketplaceRepository()) : ViewModel() {
    private val _listings = mutableStateOf<List<MarketListing>>(emptyList())
    val listings = _listings

    private val _userListings = mutableStateOf<List<MarketListing>>(emptyList())
    val userListings = _userListings

    private val _loading = mutableStateOf(false)
    val loading = _loading

    private val _error = mutableStateOf<MarketplaceError?>(null)
    val error = _error

    private val _selectedListing = mutableStateOf<MarketListing?>(null)
    val selectedListing = _selectedListing

    fun fetchActiveListings(retryCount: Int = 3) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.getActiveListings()
            if (result.isSuccess) {
                _listings.value = result.getOrNull() ?: emptyList()
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    fetchActiveListings(retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun fetchUserListings(userId: String, retryCount: Int = 3) {
        // Don't fetch if user ID is empty
        if (userId.isBlank()) {
            _error.value = MarketplaceError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.getUserListings(userId)
            if (result.isSuccess) {
                _userListings.value = result.getOrNull() ?: emptyList()
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    fetchUserListings(userId, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun createListing(listing: MarketListing, retryCount: Int = 3) {
        // Don't create if user ID is empty
        if (listing.sellerId.isBlank()) {
            _error.value = MarketplaceError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.createListing(listing)
            if (result.isSuccess) {
                // Refresh user listings
                fetchUserListings(listing.sellerId)
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    createListing(listing, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun updateListing(listing: MarketListing, retryCount: Int = 3) {
        // Don't update if user ID is empty
        if (listing.sellerId.isBlank()) {
            _error.value = MarketplaceError(
                type = DataErrorType.UNKNOWN_ERROR,
                message = "User not authenticated"
            )
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            val result = repo.updateListing(listing)
            if (result.isSuccess) {
                // Refresh user listings
                fetchUserListings(listing.sellerId)
            } else {
                // Implement retry mechanism
                if (retryCount > 0) {
                    delay(1000) // Wait 1 second before retrying
                    updateListing(listing, retryCount - 1)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
            
            _loading.value = false
        }
    }

    fun selectListing(listing: MarketListing?) {
        _selectedListing.value = listing
    }

    fun clearError() {
        _error.value = null
    }
    
    private fun handleError(throwable: Throwable?) {
        when (throwable) {
            is DataException -> {
                _error.value = MarketplaceError(
                    type = throwable.errorType,
                    message = throwable.message ?: "Data operation failed"
                )
            }
            is IllegalArgumentException -> {
                _error.value = MarketplaceError(
                    type = DataErrorType.UNKNOWN_ERROR,
                    message = throwable.message ?: "Invalid data provided"
                )
            }
            else -> {
                _error.value = MarketplaceError(
                    type = DataErrorType.UNKNOWN_ERROR,
                    message = throwable?.message ?: "An unknown error occurred"
                )
            }
        }
    }
}

// Marketplace error data class
data class MarketplaceError(
    val type: DataErrorType,
    val message: String
)