package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.ui.enthusiast.components.FeaturedBird
import com.rio.rostry.ui.enthusiast.components.ExploreCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber

@HiltViewModel
class EnthusiastExploreViewModel @Inject constructor(
    private val repo: ProductMarketplaceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val breed: String = "",
        val priceRange: String = "",
        val region: String = "",
        val traits: String = "",
        val sort: SortOption = SortOption.RECENCY,
        val items: List<ProductEntity> = emptyList(),
        val loading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val error: String? = null,
        val hasMore: Boolean = true,
        val page: Int = 0,
        val pageSize: Int = 20,
    )

    enum class SortOption { VERIFIED_FIRST, ENGAGEMENT, RECENCY, PRICE_ASC, PRICE_DESC }

    private val _ui = MutableStateFlow(
        UiState(
            breed = savedStateHandle.get<String>(KEY_BREED) ?: "",
            priceRange = savedStateHandle.get<String>(KEY_PRICE_RANGE) ?: "",
            region = savedStateHandle.get<String>(KEY_REGION) ?: "",
            traits = savedStateHandle.get<String>(KEY_TRAITS) ?: "",
            sort = savedStateHandle.get<String>(KEY_SORT)?.let { runCatching { SortOption.valueOf(it) }.getOrNull() } ?: SortOption.RECENCY
        )
    )
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    // ========== Featured Birds for VerticalPager (Comment 1) ==========
    
    private val _featuredItems = MutableStateFlow<List<FeaturedBird>>(emptyList())
    val featuredItems: StateFlow<List<FeaturedBird>> = _featuredItems.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow(ExploreCategory.ALL)
    val selectedCategory: StateFlow<ExploreCategory> = _selectedCategory.asStateFlow()
    
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    private val _isLoadingFeatured = MutableStateFlow(false)
    val isLoadingFeatured: StateFlow<Boolean> = _isLoadingFeatured.asStateFlow()
    
    private var featuredPage = 0
    private var hasMoreFeatured = true
    
    // Debounce search trigger
    private val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            refreshRequests
                .debounce(300)
                .collect {
                    // Launch paged refresh without requiring suspend context here
                    loadMore(reset = true)
                }
        }
        // Load initial featured items
        loadFeaturedItems(reset = true)
    }
    
    /**
     * Update selected category and reload featured items.
     */
    fun selectCategory(category: ExploreCategory) {
        _selectedCategory.value = category
        loadFeaturedItems(reset = true)
    }
    
    /**
     * Track current page for prefetching.
     */
    fun setCurrentPage(page: Int) {
        _currentPage.value = page
        // Prefetch when near end
        if (page >= _featuredItems.value.size - 3 && hasMoreFeatured && !_isLoadingFeatured.value) {
            loadFeaturedItems(reset = false)
        }
    }
    
    /**
     * Respect a bird (like/appreciate) - sends to backend and updates local state.
     */
    fun respectBird(birdId: String) {
        viewModelScope.launch {
            try {
                // TODO: Call backend API when available
                // repo.respectBird(birdId)
                
                // Update local state optimistically
                _featuredItems.value = _featuredItems.value.map { bird ->
                    if (bird.id == birdId && !bird.isRespected) {
                        bird.copy(isRespected = true, respectCount = bird.respectCount + 1)
                    } else bird
                }
                Timber.d("Respected bird: $birdId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to respect bird")
            }
        }
    }
    
    /**
     * Load featured items for the VerticalPager.
     */
    fun loadFeaturedItems(reset: Boolean = false) {
        if (_isLoadingFeatured.value) return
        if (!reset && !hasMoreFeatured) return
        
        viewModelScope.launch {
            _isLoadingFeatured.value = true
            if (reset) {
                featuredPage = 0
                hasMoreFeatured = true
            }
            
            val category = _selectedCategory.value
            val pageSize = 10
            
            // Fetch products and convert to FeaturedBird
            val result = when (category) {
                ExploreCategory.ALL -> repo.filterByPriceBreed(0.0, Double.MAX_VALUE, null, pageSize, featuredPage * pageSize)
                ExploreCategory.VERIFIED -> repo.filterByPriceBreed(0.0, Double.MAX_VALUE, null, pageSize, featuredPage * pageSize)
                ExploreCategory.CHAMPIONS -> repo.filterByPriceBreed(0.0, Double.MAX_VALUE, null, pageSize, featuredPage * pageSize)
                else -> repo.filterByPriceBreed(0.0, Double.MAX_VALUE, null, pageSize, featuredPage * pageSize)
            }
            
            when (result) {
                is Resource.Success -> {
                    val products = result.data ?: emptyList()
                    val featured = products.map { it.toFeaturedBird(category == ExploreCategory.VERIFIED) }
                    
                    _featuredItems.value = if (reset) featured else _featuredItems.value + featured
                    featuredPage++
                    hasMoreFeatured = products.size >= pageSize
                }
                is Resource.Error -> {
                    Timber.e("Failed to load featured: ${result.message}")
                }
                else -> {}
            }
            _isLoadingFeatured.value = false
        }
    }
    
    /**
     * Convert ProductEntity to FeaturedBird.
     */
    private fun ProductEntity.toFeaturedBird(forceVerified: Boolean = false): FeaturedBird {
        return FeaturedBird(
            id = productId,
            name = name.ifBlank { "Bird #${productId.take(6)}" },
            imageUrl = imageUrls.firstOrNull(),
            farmName = "Farm ${sellerId.take(6)}", // No farmName field, use sellerId
            breed = breed ?: "Unknown",
            location = location.ifBlank { "Philippines" },
            description = description,
            respectCount = (1..200).random(), // Mock until backend supports
            commentCount = (0..50).random(),
            isRespected = false,
            isVerified = forceVerified || status.equals("verified", ignoreCase = true)
        )
    }

    fun update(field: String, value: String) {
        _ui.value = when (field) {
            "breed" -> _ui.value.copy(breed = value)
            "priceRange" -> _ui.value.copy(priceRange = value)
            "region" -> _ui.value.copy(region = value)
            "traits" -> _ui.value.copy(traits = value)
            "sort" -> _ui.value.copy(sort = SortOption.valueOf(value))
            else -> _ui.value
        }
        // persist in SavedStateHandle
        when (field) {
            "breed" -> savedStateHandle[KEY_BREED] = value
            "priceRange" -> savedStateHandle[KEY_PRICE_RANGE] = value
            "region" -> savedStateHandle[KEY_REGION] = value
            "traits" -> savedStateHandle[KEY_TRAITS] = value
            "sort" -> savedStateHandle[KEY_SORT] = value
        }
        // Schedule refresh on filter/sort changes
        scheduleRefresh()
    }

    fun refresh() {
        scheduleRefresh()
        loadFeaturedItems(reset = true)
    }

    private fun scheduleRefresh() {
        _ui.value = _ui.value.copy(items = emptyList(), page = 0, hasMore = true)
        refreshRequests.tryEmit(Unit)
    }

    fun loadMore(reset: Boolean = false) {
        val s = _ui.value
        if ((s.loading && !reset) || s.isLoadingMore || (!reset && !s.hasMore)) return
        viewModelScope.launch {
            _ui.value = if (reset) s.copy(loading = true, error = null) else s.copy(isLoadingMore = true, error = null)
            val (minPrice, maxPrice) = parsePriceRange(s.priceRange)
            val breed = s.breed.ifBlank { null }
            val regionBox = parseRegion(s.region)
            val baseRes = if (regionBox != null) {
                repo.filterByBoundingBox(
                    minLat = regionBox.first.first,
                    maxLat = regionBox.first.second,
                    minLng = regionBox.second.first,
                    maxLng = regionBox.second.second,
                    limit = s.pageSize,
                    offset = s.page * s.pageSize
                )
            } else {
                repo.filterByPriceBreed(minPrice, maxPrice, breed, limit = s.pageSize, offset = s.page * s.pageSize)
            }
            when (baseRes) {
                is Resource.Success -> {
                    val list = baseRes.data ?: emptyList()
                    val filtered = applyTraitFilter(list, s.traits)
                    val sorted = applySort(filtered, s.sort)
                    val pageItems = sorted
                    val new = if (reset) pageItems else s.items + pageItems
                    _ui.value = s.copy(
                        items = new,
                        loading = if (reset) false else s.loading,
                        isLoadingMore = false,
                        error = null,
                        page = s.page + 1,
                        hasMore = list.size >= s.pageSize
                    )
                }
                is Resource.Error -> _ui.value = s.copy(loading = false, isLoadingMore = false, error = baseRes.message, hasMore = false)
                else -> _ui.value = s.copy(loading = false, isLoadingMore = false)
            }
        }
    }

    private fun parsePriceRange(input: String): Pair<Double, Double> {
        val cleaned = input.replace(" ", "")
        return if ("-" in cleaned) {
            val parts = cleaned.split("-")
            val min = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val max = parts.getOrNull(1)?.toDoubleOrNull() ?: Double.MAX_VALUE
            min to max
        } else {
            val v = cleaned.toDoubleOrNull()
            if (v == null) 0.0 to Double.MAX_VALUE else 0.0 to v
        }
    }

    private fun parseRegion(input: String): Pair<Pair<Double, Double>, Pair<Double, Double>>? {
        val cleaned = input.replace(" ", "")
        val parts = cleaned.split(",")
        if (parts.size != 4) return null
        val minLat = parts[0].toDoubleOrNull()
        val minLng = parts[1].toDoubleOrNull()
        val maxLat = parts[2].toDoubleOrNull()
        val maxLng = parts[3].toDoubleOrNull()
        return if (minLat != null && minLng != null && maxLat != null && maxLng != null) {
            (minLat to maxLat) to (minLng to maxLng)
        } else null
    }

    private fun applyTraitFilter(list: List<ProductEntity>, traits: String): List<ProductEntity> {
        if (traits.isBlank()) return list
        val tokens = traits.lowercase().split(" ", ",", ";").filter { it.isNotBlank() }
        if (tokens.isEmpty()) return list
        return list.filter { p ->
            val hay = listOf(
                p.name,
                p.description,
                p.category,
                p.breed,
                p.color,
                p.gender
            ).joinToString(" ").lowercase()
            tokens.all { it in hay }
        }
    }

    private fun applySort(list: List<ProductEntity>, sort: SortOption): List<ProductEntity> = when (sort) {
        SortOption.RECENCY -> list.sortedByDescending { it.updatedAt }
        SortOption.PRICE_ASC -> list.sortedBy { it.price }
        SortOption.PRICE_DESC -> list.sortedByDescending { it.price }
        SortOption.VERIFIED_FIRST -> list.sortedByDescending { it.status.equals("verified", ignoreCase = true) }
        SortOption.ENGAGEMENT -> list.sortedByDescending { (it.imageUrls.size * 2) + (it.description.length.coerceAtMost(200) / 20) }
    }

    companion object {
        private const val KEY_BREED = "explore_breed"
        private const val KEY_PRICE_RANGE = "explore_price_range"
        private const val KEY_REGION = "explore_region"
        private const val KEY_TRAITS = "explore_traits"
        private const val KEY_SORT = "explore_sort"
    }
}

