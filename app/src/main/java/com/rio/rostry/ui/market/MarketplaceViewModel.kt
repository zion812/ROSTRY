package com.rio.rostry.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.market.MarketplaceListing
import android.location.Location
import com.rio.rostry.data.location.LocationService
import com.rio.rostry.data.repo.MarketplaceRepository
import com.rio.rostry.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface MarketplaceUiState {
    object Loading : MarketplaceUiState
    data class Success(val listings: List<MarketplaceListing>) : MarketplaceUiState
    data class Error(val message: String) : MarketplaceUiState
}

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketplaceUiState>(MarketplaceUiState.Loading)
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    init {
        fetchMarketplaceListings()
    }

    private var currentLocation: Location? = null

    fun fetchMarketplaceListings(location: Location? = null, radiusInKm: Double? = null) {
        marketplaceRepository.getListings(location, radiusInKm).onEach { result ->
            _uiState.value = when (result) {
                is Result.Loading -> MarketplaceUiState.Loading
                is Result.Success -> MarketplaceUiState.Success(result.data)
                is Result.Error -> MarketplaceUiState.Error(result.exception.message ?: "An unknown error occurred")
            }
        }.launchIn(viewModelScope)
    }

    fun fetchListingsNearMe(radiusInKm: Double) {
        locationService.requestLocationUpdates().onEach { result ->
            when (result) {
                is Result.Success -> {
                    currentLocation = result.data
                    fetchMarketplaceListings(currentLocation, radiusInKm)
                }
                is Result.Error -> {
                    _uiState.value = MarketplaceUiState.Error(result.exception.message ?: "Could not get location")
                }
                is Result.Loading -> {
                    _uiState.value = MarketplaceUiState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }
}
