package com.rio.rostry.ui.enthusiast.halloffame

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
 * ViewModel for Hall of Fame screen.
 * Shows top birds by price (as proxy for quality).
 */
@HiltViewModel
class HallOfFameViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HallOfFameUiState>(HallOfFameUiState.Loading)
    val uiState: StateFlow<HallOfFameUiState> = _uiState.asStateFlow()

    init {
        loadHallOfFame()
    }

    private fun loadHallOfFame() {
        viewModelScope.launch {
            _uiState.value = HallOfFameUiState.Loading

            try {
                // Get all products
                val productsResult = productRepository.getAllProducts().first()
                val products = productsResult.data ?: emptyList()

                // Top by price (proxy for highest sales)
                val topByPrice = products
                    .filter { (it.price ?: 0.0) > 0 }
                    .sortedByDescending { it.price ?: 0.0 }
                    .take(10)
                    .map { product ->
                        HallOfFameBird(
                            product = product,
                            achievement = "Listed at ₹${product.price?.toInt() ?: 0}",
                            achievementType = AchievementType.HIGHEST_SALE,
                            value = product.price ?: 0.0
                        )
                    }

                // Top by weight (heavy birds)
                val topByWeight = products
                    .filter { (it.weightGrams ?: 0.0) > 0 }
                    .sortedByDescending { it.weightGrams ?: 0.0 }
                    .take(10)
                    .map { product ->
                        HallOfFameBird(
                            product = product,
                            achievement = "Weight: ${product.weightGrams?.toInt() ?: 0}g",
                            achievementType = AchievementType.TOP_RATED,
                            value = product.weightGrams ?: 0.0
                        )
                    }

                _uiState.value = HallOfFameUiState.Success(
                    topByPrice = topByPrice,
                    topByTransfers = topByWeight // Reusing for heaviest birds
                )
            } catch (e: Exception) {
                _uiState.value = HallOfFameUiState.Error(e.message ?: "Failed to load Hall of Fame")
            }
        }
    }

    fun refresh() {
        loadHallOfFame()
    }
}

sealed class HallOfFameUiState {
    object Loading : HallOfFameUiState()
    data class Success(
        val topByPrice: List<HallOfFameBird>,
        val topByTransfers: List<HallOfFameBird>
    ) : HallOfFameUiState()
    data class Error(val message: String) : HallOfFameUiState()
}

data class HallOfFameBird(
    val product: ProductEntity,
    val achievement: String,
    val achievementType: AchievementType,
    val value: Double
)

enum class AchievementType {
    HIGHEST_SALE,
    MOST_TRANSFERS,
    TOP_RATED
}
