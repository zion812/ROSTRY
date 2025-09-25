package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    // Debounce search trigger
    private val refreshRequests = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            refreshRequests
                .debounce(300)
                .collect {
                    // Launch paged refresh without requiring suspend context here
                    loadMore(reset = true)
                }
        }
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
    }

    private fun scheduleRefresh() {
        _ui.value = _ui.value.copy(items = emptyList(), page = 0, hasMore = true)
        refreshRequests.tryEmit(Unit)
    }

    // removed performRefresh; debounce collector calls loadMore directly

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
                    val pageItems = sorted // server already limits; client sort is applied to page
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
        // Accept forms like "100-500" or single value "300"
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
        // Expect format: "minLat,minLng,maxLat,maxLng" or empty for none
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
