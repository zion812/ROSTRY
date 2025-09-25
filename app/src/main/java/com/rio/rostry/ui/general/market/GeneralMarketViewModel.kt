package com.rio.rostry.ui.general.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class GeneralMarketViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val productMarketplaceRepository: ProductMarketplaceRepository,
    private val cartRepository: CartRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val analytics: GeneralAnalyticsTracker
) : ViewModel() {

    data class LatLong(val latitude: Double, val longitude: Double)

    data class MarketFilters(
        val nearbyEnabled: Boolean = false,
        val verifiedOnly: Boolean = false,
        val selectedBreed: String? = null,
        val selectedAgeGroup: ValidationUtils.AgeGroup? = null,
        val radiusKm: Double = 25.0,
        val currentLocation: LatLong? = null
    )

    data class MarketUiState(
        val isLoading: Boolean = true,
        val products: List<ProductEntity> = emptyList(),
        val query: String = "",
        val suggestions: List<String> = emptyList(),
        val filters: MarketFilters = MarketFilters(),
        val error: String? = null,
        val lastCartMessage: String? = null
    )

    private val filters = MutableStateFlow(MarketFilters())
    private val query = MutableStateFlow("")
    private val suggestions = MutableStateFlow<List<String>>(emptyList())
    private val baseProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)
    private val lastCartMessage = MutableStateFlow<String?>(null)
    private val verifiedProductIds = MutableStateFlow<Set<String>>(emptySet())

    private val baseState: StateFlow<MarketUiState> = combine(
        baseProducts,
        query,
        suggestions,
        filters,
        isLoading
    ) { products, q, sugg, filterState, loading ->
        val filtered = applyFilters(products, filterState, q.lowercase())
        MarketUiState(
            isLoading = loading,
            products = filtered,
            query = q,
            suggestions = sugg,
            filters = filterState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MarketUiState()
    )

    val uiState: StateFlow<MarketUiState> = combine(
        baseState,
        error,
        lastCartMessage
    ) { state, err, cartMsg ->
        state.copy(error = err, lastCartMessage = cartMsg)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MarketUiState()
    )

    private var suggestionJob: Job? = null

    init {
        observeProducts()
    }

    private fun observeProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { resource ->
                when (resource) {
                    is Resource.Loading -> isLoading.value = true
                    is Resource.Error -> {
                        isLoading.value = false
                        error.value = resource.message ?: "Unable to load products"
                    }
                    is Resource.Success -> {
                        isLoading.value = false
                        error.value = null
                        baseProducts.value = resource.data.orEmpty()
                    }
                }
            }
        }
    }

    private fun applyFilters(
        products: List<ProductEntity>,
        filters: MarketFilters,
        query: String
    ): List<ProductEntity> {
        val byQuery = if (query.isBlank()) products else products.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
                (product.description?.contains(query, ignoreCase = true) == true) ||
                product.breed?.contains(query, ignoreCase = true) == true ||
                product.location.contains(query, ignoreCase = true)
        }

        val byBreed = filters.selectedBreed?.let { breed ->
            byQuery.filter { it.breed.equals(breed, ignoreCase = true) }
        } ?: byQuery

        val byAge = filters.selectedAgeGroup?.let { group ->
            byBreed.filter { product ->
                val age = ValidationUtils.ageGroup(product.birthDate)
                age == group
            }
        } ?: byBreed

        val byVerified = if (filters.verifiedOnly) {
            val verifiedIds = verifiedProductIds.value
            byAge.filter { verifiedIds.contains(it.productId) }
        } else {
            byAge
        }

        val byLocation = if (filters.nearbyEnabled) {
            val location = filters.currentLocation
            if (location == null) {
                emptyList()
            } else {
                byVerified.filter { product ->
                    val lat = product.latitude
                    val lon = product.longitude
                    lat != null && lon != null &&
                        ValidationUtils.distanceKm(lat, lon, location.latitude, location.longitude) <= filters.radiusKm
                }
            }
        } else {
            byVerified
        }

        return byLocation.sortedBy { it.updatedAt }.reversed()
    }

    fun onQueryChange(value: String) {
        query.value = value
        suggestionJob?.cancel()
        if (value.length < 2) {
            suggestions.value = emptyList()
            return
        }
        suggestionJob = viewModelScope.launch {
            when (val result = productMarketplaceRepository.autocomplete(value, limit = 6)) {
                is Resource.Success -> suggestions.value = result.data.orEmpty().map { it.name }.distinct()
                is Resource.Error -> error.value = result.message ?: "Autocomplete failed"
                else -> Unit
            }
        }
    }

    fun clearSuggestions() {
        suggestions.value = emptyList()
    }

    fun updateFilters(transform: (MarketFilters) -> MarketFilters) {
        val previous = filters.value
        val newFilters = transform(previous)
        filters.value = newFilters
        if (!newFilters.verifiedOnly) {
            verifiedProductIds.value = emptySet()
        } else {
            loadVerifiedProducts()
        }
        logFilterDiff(previous, newFilters)
    }

    fun setLocation(lat: Double, lon: Double) {
        filters.value = filters.value.copy(
            currentLocation = LatLong(lat, lon)
        )
        analytics.marketFilterApply("location", "${lat},${lon}")
    }

    private fun logFilterDiff(old: MarketFilters, new: MarketFilters) {
        if (old.nearbyEnabled != new.nearbyEnabled) {
            analytics.marketFilterApply("nearby_enabled", new.nearbyEnabled.toString())
        }
        if (old.verifiedOnly != new.verifiedOnly) {
            analytics.marketFilterApply("verified_only", new.verifiedOnly.toString())
        }
        if (old.selectedBreed != new.selectedBreed) {
            analytics.marketFilterApply("breed", new.selectedBreed)
        }
        if (old.selectedAgeGroup != new.selectedAgeGroup) {
            analytics.marketFilterApply("age_group", new.selectedAgeGroup?.name)
        }
    }

    fun notifyOfflineBannerSeen(context: String = "market") {
        analytics.offlineBannerSeen(context)
    }

    private fun loadVerifiedProducts() {
        viewModelScope.launch {
            when (val result = productMarketplaceRepository.filterVerified(limit = 200)) {
                is Resource.Success -> {
                    verifiedProductIds.value = result.data.orEmpty().map { it.productId }.toSet()
                }
                is Resource.Error -> {
                    verifiedProductIds.value = emptySet()
                    error.value = result.message ?: "Unable to filter verified sellers"
                }
                else -> Unit
            }
        }
    }

    fun addToCart(product: ProductEntity, quantity: Double = 1.0) {
        val userId = currentUserProvider.userIdOrNull()
        if (userId == null) {
            lastCartMessage.value = "Please sign in to add items to your cart"
            return
        }
        val buyerLocation = filters.value.currentLocation
        viewModelScope.launch {
            val resource = cartRepository.addOrUpdateItem(
                userId = userId,
                productId = product.productId,
                quantity = quantity,
                buyerLat = buyerLocation?.latitude,
                buyerLon = buyerLocation?.longitude
            )
            lastCartMessage.value = when (resource) {
                is Resource.Success -> "Added ${product.name} to cart"
                is Resource.Error -> resource.message ?: "Failed to add to cart"
                else -> null
            }
        }
    }

    fun acknowledgeCartMessage() {
        lastCartMessage.value = null
    }

    fun clearError() {
        error.value = null
    }
}
