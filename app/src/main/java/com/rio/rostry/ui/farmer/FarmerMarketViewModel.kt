package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.ProductMarketplaceRepository
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

@HiltViewModel
class FarmerMarketViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val marketplaceRepository: ProductMarketplaceRepository,
    private val userRepository: UserRepository,
    private val analytics: GeneralAnalyticsTracker,
    private val analyticsRepository: AnalyticsRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val isLoadingBrowse: Boolean = false,
        val isLoadingMine: Boolean = false,
        val browse: List<ProductEntity> = emptyList(),
        val filteredBrowse: List<ProductEntity> = emptyList(),
        val mine: List<ProductEntity> = emptyList(),
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

    private suspend fun loadBrowse() {
        _ui.value = _ui.value.copy(isLoadingBrowse = true, error = null)
        when (val res = productRepository.getAllProducts().first()) {
            is Resource.Success -> {
                val base = res.data ?: emptyList()
                _ui.value = _ui.value.copy(
                    isLoadingBrowse = false,
                    browse = base,
                    filteredBrowse = applyFilters(base, _ui.value.categoryFilter, _ui.value.traceFilter, _ui.value.query)
                )
            }
            is Resource.Error -> _ui.value = _ui.value.copy(isLoadingBrowse = false, error = res.message)
            is Resource.Loading -> _ui.value = _ui.value.copy(isLoadingBrowse = true)
        }
    }

    private suspend fun loadMine() {
        val current = getCurrentUserOrNull() ?: run {
            _ui.value = _ui.value.copy(isLoadingMine = false, error = "Not authenticated")
            return
        }
        _ui.value = _ui.value.copy(isLoadingMine = true, error = null)
        when (val res = productRepository.getProductsBySeller(current.userId).first()) {
            is Resource.Success -> _ui.value = _ui.value.copy(
                isLoadingMine = false, 
                mine = res.data ?: emptyList(),
                verificationStatus = current.verificationStatus
            )
            is Resource.Error -> _ui.value = _ui.value.copy(isLoadingMine = false, error = res.message)
            is Resource.Loading -> _ui.value = _ui.value.copy(isLoadingMine = true)
        }
    }

    private suspend fun getCurrentUserOrNull(): UserEntity? {
        return when (val res = userRepository.getCurrentUser().first()) {
            is Resource.Success -> res.data
            else -> null
        }
    }

    // Filters
    enum class CategoryFilter { All, Meat, Adoption }
    enum class TraceFilter { All, Traceable, NonTraceable }

    private fun applyFilters(source: List<ProductEntity>, c: CategoryFilter, t: TraceFilter, query: String = ""): List<ProductEntity> {
        return source.asSequence()
            // Exclude farm-only private items from marketplace
            .filter { p -> p.status?.equals("private", ignoreCase = true) != true }
            .filter { p -> p.status?.equals("SPLIT", ignoreCase = true) != true }
            // Exclude quarantined items
            .filter { p -> p.lifecycleStatus?.equals("QUARANTINE", ignoreCase = true) != true }
            .filter { p ->
                when (c) {
                    CategoryFilter.All -> true
                    CategoryFilter.Meat -> p.category.equals("Meat", ignoreCase = true)
                    CategoryFilter.Adoption -> p.category.equals("Adoption", ignoreCase = true)
                }
            }
            .filter { p ->
                when (t) {
                    TraceFilter.All -> true
                    TraceFilter.Traceable -> p.condition?.equals("traceable", ignoreCase = true) == true
                    TraceFilter.NonTraceable -> p.condition?.equals("traceable", ignoreCase = true) != true
                }
            }
            .filter { p ->
                if (query.isBlank()) true else {
                    p.name.contains(query, ignoreCase = true) ||
                    (p.description?.contains(query, ignoreCase = true) == true) ||
                    p.breed?.contains(query, ignoreCase = true) == true
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
                    val base = res.data ?: emptyList()
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
                    val base = res.data ?: emptyList()
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
