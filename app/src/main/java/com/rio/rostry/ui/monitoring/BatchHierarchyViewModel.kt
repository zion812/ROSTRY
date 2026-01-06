package com.rio.rostry.ui.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Node in batch hierarchy tree
 */
data class BatchNode(
    val batch: HatchingBatchEntity,
    val childBirds: List<FarmAssetEntity> = emptyList(),
    val level: Int = 0
)

/**
 * ViewModel for batch hierarchy visualization and management
 */
@HiltViewModel
class BatchHierarchyViewModel @Inject constructor(
    private val hatchingBatchDao: HatchingBatchDao,
    private val farmAssetDao: FarmAssetDao,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val batches: List<BatchNode> = emptyList(),
        val expandedBatchIds: Set<String> = emptySet(),
        val selectedBatchId: String? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadBatchHierarchy()
    }

    private fun loadBatchHierarchy() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val farmerId = currentUserProvider.userIdOrNull() ?: return@launch
                
                // Get all batches for farmer
                hatchingBatchDao.observeBatches().collect { allBatches ->
                    val farmerBatches = allBatches.filter { it.farmerId == farmerId }
                    
                    // Get all assets for farmer from Flow
                    val allAssets = farmAssetDao.getAssetsByFarmer(farmerId).first()
                    
                    // Build batch nodes with associated birds
                    val batchNodes = farmerBatches
                        .sortedByDescending { it.startedAt }
                        .map { batch ->
                            val childBirds = allAssets.filter { it.batchId == batch.batchId }
                            BatchNode(
                                batch = batch,
                                childBirds = childBirds,
                                level = 0
                            )
                        }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        batches = batchNodes
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load batch hierarchy"
                )
            }
        }
    }

    fun toggleExpanded(batchId: String) {
        val current = _uiState.value.expandedBatchIds
        val newExpanded = if (batchId in current) {
            current - batchId
        } else {
            current + batchId
        }
        _uiState.value = _uiState.value.copy(expandedBatchIds = newExpanded)
    }

    fun selectBatch(batchId: String?) {
        _uiState.value = _uiState.value.copy(selectedBatchId = batchId)
    }
}
