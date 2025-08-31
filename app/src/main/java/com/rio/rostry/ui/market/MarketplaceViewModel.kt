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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

enum class SortOrder {
    NONE,
    PRICE_ASC,
    PRICE_DESC,
    DATE_NEWEST,
    DATE_OLDEST
}

data class MarketplaceFilters(
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.NONE
)

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

    private val _filters = MutableStateFlow(MarketplaceFilters())
    val filters: StateFlow<MarketplaceFilters> = _filters.asStateFlow()

    private var originalListings: List<MarketplaceListing> = emptyList()

    init {
        fetchMarketplaceListings()
    }

    private var currentLocation: Location? = null

    fun onSearchQueryChanged(query: String) {
        _filters.value = _filters.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onSortOrderChanged(sortOrder: SortOrder) {
        _filters.value = _filters.value.copy(sortOrder = sortOrder)
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = originalListings.filter {
            it.breed.contains(_filters.value.searchQuery, ignoreCase = true) ||
            it.description.contains(_filters.value.searchQuery, ignoreCase = true)
        }

        val sorted = when (_filters.value.sortOrder) {
            SortOrder.PRICE_ASC -> filtered.sortedBy { it.price }
            SortOrder.PRICE_DESC -> filtered.sortedByDescending { it.price }
            SortOrder.DATE_NEWEST -> filtered.sortedByDescending { it.createdTimestamp }
            SortOrder.DATE_OLDEST -> filtered.sortedBy { it.createdTimestamp }
            SortOrder.NONE -> filtered
        }

        _uiState.value = MarketplaceUiState.Success(sorted)
    }

    fun fetchMarketplaceListings(location: Location? = null, radiusInKm: Double? = null) {
        marketplaceRepository.getListings(location, radiusInKm).onEach { result ->
            _uiState.value = when (result) {
                is Result.Loading -> MarketplaceUiState.Loading
                is Result.Success -> {
                    originalListings = result.data
                    applyFilters()
                    // We return here because applyFilters will set the state.
                    return@onEach
                }
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
