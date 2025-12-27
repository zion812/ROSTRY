package com.rio.rostry.ui.general.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Discover Home screen.
 * Provides curated product sections: Near You, Festival Specials, High-Rated Farms.
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadDiscoverSections()
    }

    fun loadDiscoverSections() {
        viewModelScope.launch {
            _uiState.value = DiscoverUiState.Loading

            try {
                // Get all products
                val productsResult = productRepository.getAllProducts().first()
                val allProducts = productsResult.data ?: emptyList()

                // Near You - Show newest products
                val nearYouProducts = allProducts
                    .sortedByDescending { it.createdAt }
                    .take(10)

                // Festival Specials - Products with custom status or description containing festival
                val festivalProducts = allProducts.filter { product ->
                    product.customStatus?.contains("festival", ignoreCase = true) == true ||
                    product.name.contains("festival", ignoreCase = true) ||
                    product.description?.contains("festival", ignoreCase = true) == true
                }.take(10).ifEmpty {
                    // Fallback: show high-value products
                    allProducts.sortedByDescending { it.price ?: 0.0 }.take(10)
                }

                // High-Rated Farms - Products sorted by price as proxy for quality
                val highRatedProducts = allProducts
                    .sortedByDescending { it.price ?: 0.0 }
                    .take(10)

                _uiState.value = DiscoverUiState.Success(
                    nearYou = nearYouProducts,
                    festivalSpecials = festivalProducts,
                    highRatedFarms = highRatedProducts
                )
            } catch (e: Exception) {
                _uiState.value = DiscoverUiState.Error(e.message ?: "Failed to load discover sections")
            }
        }
    }

    fun refreshDiscoverData() {
        loadDiscoverSections()
    }
}

sealed class DiscoverUiState {
    object Loading : DiscoverUiState()
    data class Success(
        val nearYou: List<ProductEntity>,
        val festivalSpecials: List<ProductEntity>,
        val highRatedFarms: List<ProductEntity>
    ) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}
