package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.MarketListingRepository
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import com.rio.rostry.ui.farmer.Listing

@HiltViewModel
class FarmerMarketViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val marketplaceRepository: ProductMarketplaceRepository,
    private val marketListingRepository: MarketListingRepository,
    private val farmAssetRepository: FarmAssetRepository,
    private val userRepository: UserRepository,
    private val analytics: GeneralAnalyticsTracker,
    private val analyticsRepository: AnalyticsRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val isLoadingBrowse: Boolean = false,
        val isLoadingMine: Boolean = false,
        val browse: List<Listing> = emptyList(),
        val filteredBrowse: List<Listing> = emptyList(),
        val mine: List<Listing> = emptyList(),
        val categoryFilter: CategoryFilter = CategoryFilter.All,
        val traceFilter: TraceFilter = TraceFilter.All,
        val minPrice: Double? = null,
        val maxPrice: Double? = null,
        val breed: String? = null,
        val startDate: Long? = null,
        val endDate: Long? = null,
        val query: String = "",
        val selectedTabIndex: Int = 0, // 0=Browse 1=Sell
        // Metrics for Sell mode
        val metricsRevenue: Double = 0.0,
        val metricsOrders: Int = 0,
        val metricsViews: Int = 0,
        val verificationStatus: com.rio.rostry.domain.model.VerificationStatus = com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED,
        val error: String? = null
    ) {
        val isDateFilterActive: Boolean
            get() = startDate != null || endDate != null
    }

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init {
        // Restore persisted
        val restoredTab = savedState.get<Int>(KEY_TAB_INDEX) ?: 0
        val restoredCat = savedState.get<String>(KEY_FILTER_CATEGORY)?.let { runCatching { CategoryFilter.valueOf(it) }.getOrNull() } ?: CategoryFilter.All
        val restoredTrace = savedState.get<String>(KEY_FILTER_TRACE)?.let { runCatching { TraceFilter.valueOf(it) }.getOrNull() } ?: TraceFilter.All
        val restoredMin = savedState.get<Double?>(KEY_MIN_PRICE)
        val restoredMax = savedState.get<Double?>(KEY_MAX_PRICE)
        val restoredBreed = savedState.get<String?>(KEY_BREED)
        val restoredStartDate = savedState.get<Long?>(KEY_START_DATE)
        val restoredEndDate = savedState.get<Long?>(KEY_END_DATE)
        _ui.value = _ui.value.copy(
            selectedTabIndex = restoredTab,
            categoryFilter = restoredCat,
            traceFilter = restoredTrace,
            minPrice = restoredMin,
            maxPrice = restoredMax,
            breed = restoredBreed,
            startDate = restoredStartDate,
            endDate = restoredEndDate
        )
        refresh()
        viewModelScope.launch { loadMetrics() }
    }

    fun refresh() {
        viewModelScope.launch { loadBrowse() }
        viewModelScope.launch { loadMine() }
    }

    fun onFilterApplied(key: String, value: String?) {
        analytics.marketFilterApply(key, value)
    }

    fun onQueryChange(newQuery: String) {
        _ui.value = _ui.value.copy(query = newQuery)
        val filtered = applyFilters(_ui.value.browse, _ui.value.categoryFilter, _ui.value.traceFilter, newQuery)
        _ui.value = _ui.value.copy(filteredBrowse = filtered)
    }

    private fun loadBrowse() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoadingBrowse = true)
            combine(
                productRepository.getAllProducts(),
                marketListingRepository.getPublicListings()
            ) { legacy, new ->
                val combined = mutableListOf<Listing>()
                val seen = mutableSetOf<String>() // Track by title+price composite key
                
                // Add MarketListingEntity items first (newer source, higher priority)
                (new.data ?: emptyList()).forEach { entity ->
                    val key = "${entity.title.lowercase().trim()}_${entity.price}"
                    if (key !in seen) {
                        seen.add(key)
                        combined.add(mapMarketListingToListing(entity))
                    }
                }
                
                // Add legacy Product items only if not already added from MarketListing
                (legacy.data ?: emptyList())
                    .filter { it.isPublic && it.status != "sold_out" && it.status != "suspended" && it.status != "SOLD" }
                    .forEach { entity ->
                        val title = entity.name.ifBlank { entity.category }.lowercase().trim()
                        val key = "${title}_${entity.price}"
                        if (key !in seen) {
                            seen.add(key)
                            combined.add(mapProductToListing(entity))
                        }
                    }
                
                // Final deduplication by ID as safety net
                combined.distinctBy { it.id }
            }.collect { combined ->
                _ui.value = _ui.value.copy(
                    isLoadingBrowse = false,
                    browse = combined,
                    filteredBrowse = applyFilters(combined, _ui.value.categoryFilter, _ui.value.traceFilter, _ui.value.query)
                )
            }
        }
    }

    private fun loadMine() {
        viewModelScope.launch {
            val current = getCurrentUserOrNull() ?: return@launch
            _ui.value = _ui.value.copy(isLoadingMine = true, verificationStatus = current.verificationStatus)
            combine(
                productRepository.getProductsBySeller(current.userId),
                marketListingRepository.getListingsBySeller(current.userId)
            ) { legacy, new ->
                val combined = mutableListOf<Listing>()
                val seen = mutableSetOf<String>() // Track by title+price composite key
                
                // Add MarketListingEntity items first (newer source, higher priority)
                (new.data ?: emptyList()).forEach { entity ->
                    val key = "${entity.title.lowercase().trim()}_${entity.price}"
                    if (key !in seen) {
                        seen.add(key)
                        combined.add(mapMarketListingToListing(entity))
                    }
                }
                
                // Add legacy Product items only if not already added from MarketListing
                (legacy.data ?: emptyList()).forEach { entity ->
                    val title = (entity.name.ifBlank { entity.category }).lowercase().trim()
                    val key = "${title}_${entity.price}"
                    if (key !in seen) {
                        seen.add(key)
                        combined.add(mapProductToListing(entity))
                    }
                }
                
                // Final deduplication by ID as safety net
                combined.distinctBy { it.id }
            }.collect { combined ->
                _ui.value = _ui.value.copy(
                    isLoadingMine = false,
                    mine = combined
                )
            }
        }
    }

    private fun mapProductToListing(e: ProductEntity): Listing = Listing(
        id = e.productId,
        title = e.name.ifBlank { e.category },
        price = e.price,
        views = 0, // Mock for now
        inquiries = 0,
        orders = 0,
        isBatch = e.isBatch ?: false,
        quantity = e.quantity.toInt(),
        category = e.category ?: "",
        isTraceable = e.familyTreeId != null
    )

    private fun mapMarketListingToListing(e: com.rio.rostry.data.database.entity.MarketListingEntity): Listing = Listing(
        id = e.listingId,
        title = e.title,
        price = e.price,
        views = e.viewsCount,
        inquiries = e.inquiriesCount,
        orders = 0, // Needs order repository link
        isBatch = false, // Add to entity if needed
        quantity = 1, // Needs inventory lookup
        category = e.category,
        isTraceable = true
    )

    private suspend fun getCurrentUserOrNull(): UserEntity? {
        return when (val res = userRepository.getCurrentUser().first()) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }

    // Filters
    enum class CategoryFilter { All, Meat, Adoption }
    enum class TraceFilter { All, Traceable, NonTraceable }

    private fun applyFilters(source: List<Listing>, c: CategoryFilter, t: TraceFilter, query: String = ""): List<Listing> {
        return source.asSequence()
            .filter { 
                if (query.isBlank()) true else {
                    it.title.contains(query, ignoreCase = true)
                }
            }
            .filter {
                when (c) {
                    CategoryFilter.All -> true
                    CategoryFilter.Meat -> it.category.contains("MEAT", ignoreCase = true) || it.category.equals("Meat", ignoreCase = true)
                    CategoryFilter.Adoption -> it.category.contains("ADOPTION", ignoreCase = true) || it.category.equals("Adoption", ignoreCase = true)
                }
            }
            .filter {
                when (t) {
                    TraceFilter.All -> true
                    TraceFilter.Traceable -> it.isTraceable
                    TraceFilter.NonTraceable -> !it.isTraceable
                }
            }
            .toList()
    }

    fun selectCategory(filter: CategoryFilter) {
        val newFilter = if (_ui.value.categoryFilter == filter) CategoryFilter.All else filter
        analytics.marketFilterApply("category", newFilter.name)
        val filtered = applyFilters(_ui.value.browse, newFilter, _ui.value.traceFilter, _ui.value.query)
        _ui.value = _ui.value.copy(categoryFilter = newFilter, filteredBrowse = filtered)
        savedState[KEY_FILTER_CATEGORY] = newFilter.name
    }

    fun selectTrace(filter: TraceFilter) {
        val newFilter = if (_ui.value.traceFilter == filter) TraceFilter.All else filter
        analytics.marketFilterApply("traceability", newFilter.name)
        val filtered = applyFilters(_ui.value.browse, _ui.value.categoryFilter, newFilter, _ui.value.query)
        _ui.value = _ui.value.copy(traceFilter = newFilter, filteredBrowse = filtered)
        savedState[KEY_FILTER_TRACE] = newFilter.name
    }

    fun applyPriceBreed(min: Double?, max: Double?, breed: String?) {
        viewModelScope.launch {
            val lower = min ?: 0.0
            val upper = max ?: Double.MAX_VALUE
            _ui.value = _ui.value.copy(isLoadingBrowse = true)
            when (val res = marketplaceRepository.filterByPriceBreed(lower, upper, breed?.takeIf { it.isNotBlank() })) {
                is Resource.Success -> {
                    val base = (res.data ?: emptyList()).map { mapProductToListing(it) }
                    // Apply category/trace filters on top of repo price/breed filtering
                    val filtered = applyFilters(base, _ui.value.categoryFilter, _ui.value.traceFilter, _ui.value.query)
                    _ui.value = _ui.value.copy(isLoadingBrowse = false, browse = base, filteredBrowse = filtered, minPrice = min, maxPrice = max, breed = breed)
                    savedState[KEY_MIN_PRICE] = min
                    savedState[KEY_MAX_PRICE] = max
                    savedState[KEY_BREED] = breed
                }
                is Resource.Error -> _ui.value = _ui.value.copy(isLoadingBrowse = false, error = res.message)
                is Resource.Loading -> _ui.value = _ui.value.copy(isLoadingBrowse = true)
            }
            analytics.marketFilterApply("price_breed", "min=${min ?: ""},max=${max ?: ""},breed=${breed ?: ""}")
        }
    }

    fun setTab(index: Int) {
        _ui.value = _ui.value.copy(selectedTabIndex = index)
        savedState[KEY_TAB_INDEX] = index
    }

    fun applyDateFilter(startDate: Long?, endDate: Long?) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoadingBrowse = true)
            when (val res = marketplaceRepository.filterByDateRange(startDate, endDate)) {
                is Resource.Success -> {
                    val base = (res.data ?: emptyList()).map { mapProductToListing(it) }
                    val filtered = applyFilters(base, _ui.value.categoryFilter, _ui.value.traceFilter, _ui.value.query)
                    _ui.value = _ui.value.copy(
                        isLoadingBrowse = false,
                        browse = base,
                        filteredBrowse = filtered,
                        startDate = startDate,
                        endDate = endDate
                    )
                    savedState[KEY_START_DATE] = startDate
                    savedState[KEY_END_DATE] = endDate
                }
                is Resource.Error -> _ui.value = _ui.value.copy(isLoadingBrowse = false, error = res.message)
                is Resource.Loading -> _ui.value = _ui.value.copy(isLoadingBrowse = true)
            }
            analytics.marketFilterApply("date_range", "start=$startDate,end=$endDate")
        }
    }

    fun clearDateFilter() {
        _ui.value = _ui.value.copy(startDate = null, endDate = null)
        savedState[KEY_START_DATE] = null
        savedState[KEY_END_DATE] = null
        refresh()
    }

    private suspend fun loadMetrics() {
        val current = getCurrentUserOrNull() ?: return
        // Pull farmer dashboard snapshot
        when (val dash = analyticsRepository.farmerDashboard(current.userId).first()) {
            is com.rio.rostry.data.repository.analytics.FarmerDashboard -> {
                _ui.value = _ui.value.copy(
                    metricsRevenue = dash.revenue,
                    metricsOrders = dash.orders,
                    metricsViews = dash.productViews
                )
            }
            else -> Unit
        }
    }

    private companion object {
        const val KEY_TAB_INDEX = "market_tab_index"
        const val KEY_FILTER_CATEGORY = "market_filter_category"
        const val KEY_FILTER_TRACE = "market_filter_trace"
        const val KEY_MIN_PRICE = "market_min_price"
        const val KEY_MAX_PRICE = "market_max_price"
        const val KEY_BREED = "market_breed"
        const val KEY_START_DATE = "market_start_date"
        const val KEY_END_DATE = "market_end_date"
    }
}
