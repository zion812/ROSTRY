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
import kotlinx.coroutines.delay
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
    private val wishlistRepository: com.rio.rostry.data.repository.WishlistRepository,
    private val recommendationEngine: com.rio.rostry.ai.RecommendationEngine,
    private val currentUserProvider: CurrentUserProvider,
    private val analytics: GeneralAnalyticsTracker,
    private val breedRepository: com.rio.rostry.data.repository.BreedRepository
) : ViewModel() {

    data class LatLong(val latitude: Double, val longitude: Double)

    enum class QuickPreset { NEARBY_VERIFIED, TRACEABLE_ONLY, BUDGET_FRIENDLY, PREMIUM }

    data class FilterPreset(
        val id: QuickPreset,
        val label: String,
        val description: String
    )

    data class MarketFilters(
        val nearbyEnabled: Boolean = false,
        val verifiedOnly: Boolean = false,
        val selectedBreed: String? = null,
        val selectedAgeGroup: ValidationUtils.AgeGroup? = null,
        val radiusKm: Double = 25.0,
        val currentLocation: LatLong? = null,
        val traceableOnly: Boolean = false,
        val quickPreset: QuickPreset? = null,
        val minPrice: Double? = null,
        val maxPrice: Double? = null,
        val selectedCulinaryProfile: String? = null
    )

    data class MarketUiState(
        val isLoading: Boolean = true,
        val products: List<ProductEntity> = emptyList(),
        val query: String = "",
        val suggestions: List<String> = emptyList(),
        val filters: MarketFilters = MarketFilters(),
        val error: String? = null,
        val lastCartMessage: String? = null,
        val trendingProducts: List<ProductEntity> = emptyList(),
        val recommendedProducts: List<ProductEntity> = emptyList(),
        val wishlistProductIds: Set<String> = emptySet(),
        val activeFilterCount: Int = 0,
        val filterPresets: List<FilterPreset> = emptyList(),
        val isLocationPermissionGranted: Boolean = false,
        val isAutoDetectingLocation: Boolean = false
    )

    private val filters = MutableStateFlow(MarketFilters())
    private val query = MutableStateFlow("")
    private val suggestions = MutableStateFlow<List<String>>(emptyList())
    private val baseProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)
    private val lastCartMessage = MutableStateFlow<String?>(null)
    private val verifiedProductIds = MutableStateFlow<Set<String>>(emptySet())
    private val trendingProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val recommendedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val wishlistIds = MutableStateFlow<Set<String>>(emptySet())
    private val breedProfiles = MutableStateFlow<Map<String, String>>(emptyMap()) // breedName -> culinaryProfile

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
        lastCartMessage,
        trendingProducts,
        recommendedProducts,
        wishlistIds,
        filters
    ) { values ->
        val state = values[0] as MarketUiState
        val err = values[1] as String?
        val cartMsg = values[2] as String?
        val trending = values[3] as List<ProductEntity>
        val recommended = values[4] as List<ProductEntity>
        val wishlist = values[5] as Set<String>
        val currentFilters = values[6] as MarketFilters
        
        state.copy(
            error = err,
            lastCartMessage = cartMsg,
            trendingProducts = trending,
            recommendedProducts = recommended,
            wishlistProductIds = wishlist,
            activeFilterCount = calculateActiveFilterCount(),
            filterPresets = listOf(
                FilterPreset(QuickPreset.NEARBY_VERIFIED, "Nearby & Verified", "Products from verified sellers near you"),
                FilterPreset(QuickPreset.TRACEABLE_ONLY, "Traceable Only", "Products with full traceability"),
                FilterPreset(QuickPreset.BUDGET_FRIENDLY, "Budget Friendly", "Affordable options under ₹500"),
                FilterPreset(QuickPreset.PREMIUM, "Premium", "High-quality verified traceable products")
            ),
            isLocationPermissionGranted = currentFilters.currentLocation != null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MarketUiState()
    )

    private var suggestionJob: Job? = null
    private var filterJob: Job? = null

    init {
        observeProducts()
        observeWishlist()
        loadBreedProfiles()
        
        // Refresh trending and recommendations after products load
        viewModelScope.launch {
            baseProducts.collect { products ->
                if (products.isNotEmpty()) {
                    loadTrendingProducts()
                    loadRecommendations()
                }
            }
        }
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
        // Debounced fetch using repository advanced filters
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            isLoading.value = true
            delay(150) // debounce rapid toggles
            val fetched = fetchByFilters(newFilters, query.value)
            baseProducts.value = fetched
            isLoading.value = false
        }
    }

    fun setLocation(lat: Double, lon: Double) {
        filters.value = filters.value.copy(
            currentLocation = LatLong(lat, lon)
        )
        analytics.marketFilterApply("location", "${lat},${lon}")
        // Auto-refresh when location changes and nearby is enabled
        if (filters.value.nearbyEnabled) {
            updateFilters { it }
        }
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
        if (old.selectedCulinaryProfile != new.selectedCulinaryProfile) {
            analytics.marketFilterApply("culinary_profile", new.selectedCulinaryProfile)
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

    private fun loadBreedProfiles() {
        viewModelScope.launch {
            breedRepository.getAllBreeds().collect { breeds ->
                breedProfiles.value = breeds.associate { it.name.lowercase() to it.culinaryProfile }
            }
        }
    }

    private suspend fun fetchByFilters(filters: MarketFilters, q: String): List<ProductEntity> {
        val repo = productRepository
        val results = try {
            val base: List<ProductEntity> = if (filters.nearbyEnabled && filters.currentLocation != null) {
                val loc = filters.currentLocation!!
                val res = repo.filterNearby(
                    centerLat = loc.latitude,
                    centerLng = loc.longitude,
                    radiusKm = filters.radiusKm,
                    limit = 200
                )
                (res as? Resource.Success)?.data ?: emptyList()
            } else if (filters.selectedAgeGroup != null) {
                val bounds = when (filters.selectedAgeGroup) {
                    ValidationUtils.AgeGroup.DAY_OLD -> 0 to 7
                    ValidationUtils.AgeGroup.CHICK -> 8 to 35
                    ValidationUtils.AgeGroup.GROWER -> 36 to 140
                    ValidationUtils.AgeGroup.ADULT -> 141 to 9999
                }
                val res = repo.filterByAgeDays(
                    minAgeDays = bounds.first,
                    maxAgeDays = bounds.second,
                    limit = 200
                )
                (res as? Resource.Success)?.data ?: emptyList()
            } else if (filters.selectedBreed != null || filters.verifiedOnly) {
                val start = if (filters.verifiedOnly) {
                    (repo.filterVerified(limit = 200) as? Resource.Success)?.data ?: emptyList()
                } else emptyList()
                val res = repo.filterByBreed(
                    breed = filters.selectedBreed,
                    minPrice = 0.0,
                    maxPrice = Double.MAX_VALUE,
                    limit = 200
                )
                val breedList = (res as? Resource.Success)?.data ?: emptyList()
                if (start.isNotEmpty()) start.intersect(breedList.toSet()).toList() else breedList
            } else {
                baseProducts.value
            }

            val traceFiltered = if (filters.traceableOnly) {
                val res = repo.filterTraceable(true, base)
                (res as? Resource.Success)?.data ?: base
            } else base

            val normQ = q.lowercase()
            if (normQ.isBlank()) traceFiltered else traceFiltered.filter { product ->
                product.name.contains(normQ, true) ||
                    (product.description?.contains(normQ, true) == true) ||
                    product.breed?.contains(normQ, true) == true ||
                    product.location.contains(normQ, true)
            }

            // In-memory filter for culinary profile since it requires a join/lookup not in repo
            if (filters.selectedCulinaryProfile != null) {
                val targetProfile = filters.selectedCulinaryProfile
                val profiles = breedProfiles.value
                return@fetchByFilters traceFiltered.filter { product ->
                    val breedName = product.breed?.lowercase()
                    val profile = profiles[breedName]
                    profile.equals(targetProfile, ignoreCase = true)
                }
            }
            
            traceFiltered
        } catch (_: Exception) {
            emptyList()
        }
        return results.sortedBy { it.updatedAt }.reversed()
    }

    private fun observeProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { res ->
                when (res) {
                    is Resource.Loading -> isLoading.value = true
                    is Resource.Success -> {
                        baseProducts.value = res.data.orEmpty()
                        isLoading.value = false
                    }
                    is Resource.Error -> {
                        error.value = res.message
                        isLoading.value = false
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
        val byVerified = if (filters.verifiedOnly) products.filter { verifiedProductIds.value.contains(it.productId) } else products
        val byTrace = if (filters.traceableOnly) byVerified.filter { it.familyTreeId != null || !it.parentIdsJson.isNullOrBlank() } else byVerified
        val normQ = query.lowercase()
        return if (normQ.isBlank()) byTrace else byTrace.filter { p ->
            p.name.contains(normQ, true) ||
                (p.description?.contains(normQ, true) == true) ||
                p.breed?.contains(normQ, true) == true ||
                p.location.contains(normQ, true)
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

    fun loadTrendingProducts() {
        viewModelScope.launch {
            try {
                val recommendations = recommendationEngine.trendingProducts(limit = 10)
                val productIds = recommendations.map { it.id }.toSet()
                val allProducts = baseProducts.value
                // Maintain recommendation order
                val productsById = allProducts.filter { it.productId in productIds }.associateBy { it.productId }
                trendingProducts.value = recommendations.mapNotNull { productsById[it.id] }
                if (trendingProducts.value.isNotEmpty()) {
                    analytics.trendingSectionViewed()
                }
            } catch (e: Exception) {
                error.value = "Failed to load trending products"
            }
        }
    }

    fun loadRecommendations() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            try {
                val recommendations = recommendationEngine.personalizedForUser(userId, limit = 10)
                val productIds = recommendations.map { it.id }.toSet()
                val allProducts = baseProducts.value
                // Maintain recommendation order
                val productsById = allProducts.filter { it.productId in productIds }.associateBy { it.productId }
                recommendedProducts.value = recommendations.mapNotNull { productsById[it.id] }
            } catch (e: Exception) {
                error.value = "Failed to load recommendations"
            }
        }
    }

    fun toggleWishlist(product: ProductEntity) {
        val userId = currentUserProvider.userIdOrNull() ?: run {
            lastCartMessage.value = "Please sign in to save items"
            return
        }
        viewModelScope.launch {
            val isInWishlist = wishlistIds.value.contains(product.productId)
            val result = if (isInWishlist) {
                wishlistRepository.remove(userId, product.productId)
            } else {
                wishlistRepository.add(userId, product.productId)
            }
            if (result is com.rio.rostry.utils.Resource.Error) {
                error.value = result.message
            } else {
                analytics.wishlistToggled(product.productId, !isInWishlist)
            }
        }
    }

    fun trackProductView(productId: String) {
        analytics.productViewTracked(productId, 0)
    }

    private fun observeWishlist() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            wishlistRepository.observe(userId).collect { wishlistItems ->
                wishlistIds.value = wishlistItems.map { it.productId }.toSet()
            }
        }
    }

    fun trendingSectionViewed() {
        analytics.trendingSectionViewed()
    }

    fun recommendationClicked(productId: String, reason: String) {
        analytics.recommendationClicked(productId, reason)
    }

    fun applyQuickPreset(preset: QuickPreset) {
        val newFilters = when (preset) {
            QuickPreset.NEARBY_VERIFIED -> filters.value.copy(
                nearbyEnabled = true,
                verifiedOnly = true,
                radiusKm = 25.0,
                quickPreset = preset
            )
            QuickPreset.TRACEABLE_ONLY -> filters.value.copy(
                traceableOnly = true,
                quickPreset = preset
            )
            QuickPreset.BUDGET_FRIENDLY -> filters.value.copy(
                maxPrice = 500.0,
                quickPreset = preset
            )
            QuickPreset.PREMIUM -> filters.value.copy(
                verifiedOnly = true,
                traceableOnly = true,
                minPrice = 1000.0,
                quickPreset = preset
            )
        }
        updateFilters { newFilters }
    }

    fun clearAllFilters() {
        updateFilters { MarketFilters() }
    }

    fun removeFilter(filterType: String) {
        updateFilters { current ->
            when (filterType) {
                "nearby" -> current.copy(nearbyEnabled = false)
                "verified" -> current.copy(verifiedOnly = false)
                "breed" -> current.copy(selectedBreed = null)
                "ageGroup" -> current.copy(selectedAgeGroup = null)
                "traceable" -> current.copy(traceableOnly = false)
                "price" -> current.copy(minPrice = null, maxPrice = null)
                "culinaryProfile" -> current.copy(selectedCulinaryProfile = null)
                else -> current
            }
        }
    }

    fun autoDetectLocation() {
        // This would integrate with FusedLocationProviderClient
        // For now, placeholder that emits an event
    }

    fun getActiveFilterTags(): List<String> {
        val f = filters.value
        return buildList {
            if (f.nearbyEnabled) add("Nearby (${f.radiusKm.toInt()}km)")
            if (f.verifiedOnly) add("Verified")
            if (f.traceableOnly) add("Traceable")
            f.selectedBreed?.let { add(it) }
            f.selectedAgeGroup?.let { add(it.name.replace('_', ' ')) }
            if (f.minPrice != null || f.maxPrice != null) {
                val range = "₹${f.minPrice?.toInt() ?: 0}-${f.maxPrice?.toInt() ?: "∞"}"
                add(range)
            }
            f.selectedCulinaryProfile?.let { add("Taste: $it") }
        }
    }

    private fun calculateActiveFilterCount(): Int {
        val f = filters.value
        var count = 0
        if (f.nearbyEnabled) count++
        if (f.verifiedOnly) count++
        if (f.traceableOnly) count++
        if (f.selectedBreed != null) count++
        if (f.selectedAgeGroup != null) count++
        if (f.minPrice != null || f.maxPrice != null) count++
        if (f.selectedCulinaryProfile != null) count++
        return count
    }
}
