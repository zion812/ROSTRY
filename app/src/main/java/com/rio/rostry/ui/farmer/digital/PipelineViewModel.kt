package com.rio.rostry.ui.farmer.digital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the DigitalFarmPipeline component.
 * Provides stage counts, navigation events, and pipeline-specific data.
 */
@HiltViewModel
class PipelineViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val farmAssetRepository: FarmAssetRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(PipelineUiState())
    val uiState: StateFlow<PipelineUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent.asSharedFlow()

    init {
        loadPipelineData()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadPipelineData() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch

            combine(
                productRepository.getProductsBySeller(userId),
                farmAssetRepository.getAssetsByFarmer(userId)
            ) { productsRes, assetsRes ->
                val products = productsRes.data ?: emptyList()
                val assets = assetsRes.data ?: emptyList()

                // Calculate stage counts
                val stageCounts = mutableMapOf<LifecycleStage, Int>()

                products.forEach { product ->
                    val stage = when {
                        (product.ageWeeks ?: 0) < 8 -> LifecycleStage.CHICK
                        (product.ageWeeks ?: 0) < 18 -> LifecycleStage.GROWER
                        product.breedingStatus == "active" -> LifecycleStage.BREEDER
                        else -> LifecycleStage.LAYER
                    }
                    stageCounts[stage] = (stageCounts[stage] ?: 0) + 1
                }

                assets.forEach { asset ->
                    val stage = when {
                        (asset.ageWeeks ?: 0) < 8 -> LifecycleStage.CHICK
                        (asset.ageWeeks ?: 0) < 18 -> LifecycleStage.GROWER
                        else -> LifecycleStage.LAYER
                    }
                    stageCounts[stage] = (stageCounts[stage] ?: 0) + 1
                }

                val totalBirds = products.size + assets.size
                val hatchingCount = 0 // Would come from HatchingBatch repository
                val readyToGrowCount = products.count { (it.ageWeeks ?: 0) in 7..8 }
                val readyToLayCount = products.count { (it.ageWeeks ?: 0) in 17..18 }

                PipelineUiState(
                    totalBirds = totalBirds,
                    hatchingCount = hatchingCount,
                    stageCounts = stageCounts,
                    readyToGrowCount = readyToGrowCount,
                    readyToLayCount = readyToLayCount,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun navigateToModule(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }
}

data class PipelineUiState(
    val totalBirds: Int = 0,
    val hatchingCount: Int = 0,
    val stageCounts: Map<LifecycleStage, Int> = emptyMap(),
    val readyToGrowCount: Int = 0,
    val readyToLayCount: Int = 0,
    val isLoading: Boolean = true
)
