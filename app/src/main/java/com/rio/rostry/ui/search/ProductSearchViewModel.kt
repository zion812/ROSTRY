package com.rio.rostry.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(
    private val searchRepository: ProductSearchRepository
) : ViewModel() {

    data class SearchUiState(
        val query: String = "",
        val results: List<ProductEntity> = emptyList(),
        val isLoading: Boolean = false,
        val isEmpty: Boolean = false // True if query is not empty but no results match
    )

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(300) // Debounce input to avoid spamming DB
            .distinctUntilChanged()
            .flatMapLatest { query ->
                _uiState.update { it.copy(isLoading = true) }
                if (query.isBlank()) {
                    flowOf(emptyList()) // Or getAllProducts() if we want default list
                } else {
                    searchRepository.searchProducts(query)
                }
            }
            .onEach { products ->
                _uiState.update { 
                    it.copy(
                        results = products,
                        isLoading = false,
                        isEmpty = _searchQuery.value.isNotBlank() && products.isEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        _uiState.update { it.copy(query = newQuery) }
    }
    
    fun clearQuery() {
        onQueryChanged("")
    }
}
