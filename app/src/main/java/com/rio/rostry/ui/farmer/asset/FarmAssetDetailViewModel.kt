package com.rio.rostry.ui.farmer.asset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.CostPerBirdAnalysis
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.FarmFinancialsRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmAssetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FarmAssetRepository,
    private val activityLogDao: com.rio.rostry.data.database.dao.FarmActivityLogDao,
    private val financialsRepository: FarmFinancialsRepository
) : ViewModel() {

    private val assetId: String = savedStateHandle.get<String>("assetId") ?: ""
    
    private val _uiState = MutableStateFlow(FarmAssetDetailUiState())
    val uiState = _uiState.asStateFlow()
    
    // Cost analysis for the asset
    private val _costAnalysis = MutableStateFlow<CostPerBirdAnalysis?>(null)
    val costAnalysis = _costAnalysis.asStateFlow()
    
    private val _costAnalysisLoading = MutableStateFlow(false)
    val costAnalysisLoading = _costAnalysisLoading.asStateFlow()
    
    // Recent events for inline history display
    private val _recentEvents = MutableStateFlow<List<RecentActivityEvent>>(emptyList())
    val recentEvents = _recentEvents.asStateFlow()

    init {
        if (assetId.isNotBlank()) {
            loadAsset()
            loadRecentEvents()
            loadCostAnalysis()
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Invalid asset ID") }
        }
    }
    
    private fun loadRecentEvents() {
        viewModelScope.launch {
            try {
                activityLogDao.observeForProduct(assetId)
                    .collect { logs ->
                        _recentEvents.value = logs.take(3).map { log ->
                            RecentActivityEvent(
                                type = log.activityType,
                                timestamp = log.createdAt,
                                notes = log.notes,
                                quantity = log.quantity
                            )
                        }
                    }
            } catch (e: Exception) {
                // Silent fail - just show empty history
            }
        }
    }
    
    /**
     * Load cost-per-bird analysis for this asset.
     */
    fun loadCostAnalysis() {
        viewModelScope.launch {
            financialsRepository.getCostPerBird(assetId)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _costAnalysisLoading.value = true
                        is Resource.Success -> {
                            _costAnalysisLoading.value = false
                            _costAnalysis.value = result.data
                        }
                        is Resource.Error -> {
                            _costAnalysisLoading.value = false
                            // Silent fail for cost analysis - not critical
                        }
                    }
                }
        }
    }

    fun loadAsset() {
        viewModelScope.launch {
            repository.getAssetById(assetId)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            // Parse Tag Groups
                            val tags = try {
                                val gson = com.google.gson.Gson()
                                val type = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
                                val meta: Map<String, Any> = gson.fromJson(result.data?.metadataJson ?: "{}", type)
                                val groupsJson = gson.toJson(meta["tagGroups"])
                                if (meta.containsKey("tagGroups")) {
                                    val groupType = object : com.google.gson.reflect.TypeToken<List<TagGroup>>() {}.type
                                    gson.fromJson<List<TagGroup>>(groupsJson, groupType)
                                } else {
                                    emptyList()
                                }
                            } catch (e: Exception) {
                                emptyList()
                            }
                            
                            _uiState.update { 
                                it.copy(isLoading = false, asset = result.data, tagGroups = tags, error = null) 
                            }
                            // Load Performance Metrics
                            result.data?.let { loadPerformance(it) }
                        }
                        is Resource.Error -> _uiState.update { 
                            it.copy(isLoading = false, error = result.message) 
                        }
                    }
                }
        }
    }

    fun updateHealthStatus(status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            when (val result = repository.updateHealthStatus(assetId, status)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isUpdating = false, successMessage = "Health status updated") }
                    loadAsset() // Refresh
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isUpdating = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun updateQuantity(newQuantity: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            when (val result = repository.updateQuantity(assetId, newQuantity)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isUpdating = false, successMessage = "Quantity updated") }
                    loadAsset() // Refresh
                    loadCostAnalysis() // Refresh cost analysis
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isUpdating = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun toggleShowcase() {
        val current = _uiState.value.asset ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val updated = current.copy(isShowcase = !current.isShowcase)
            when (val result = repository.updateAsset(updated)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isUpdating = false, successMessage = "Showcase updated") }
                    loadAsset() // Refresh
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isUpdating = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }

    private fun loadPerformance(asset: FarmAssetEntity) {
        viewModelScope.launch {
            try {
                // Mortality Rate
                val initial = asset.initialQuantity
                val current = asset.quantity
                val mortalityRate = if (initial > 0) ((initial - current) / initial) * 100 else 0.0
                
                // FCR Calculation
                // Total Feed (kg) / Total Weight Gain (kg)
                // Gain = (Avg Weight g / 1000) * Current Qty
                // Note: This assumes initial weight is negligible for FCR calc or included in efficiency
                val totalFeedKg = activityLogDao.getTotalFeedQuantityForAsset(asset.assetId)
                val totalWeightKg = (asset.weightGrams ?: 0.0) / 1000.0 * current
                
                val fcr = if (totalWeightKg > 0) totalFeedKg / totalWeightKg else 0.0
                
                // Grading
                val grade = when {
                    fcr > 0 && fcr <= 1.6 && mortalityRate <= 3.0 -> "A"
                    fcr <= 2.0 && mortalityRate <= 5.0 -> "B"
                    fcr == 0.0 -> "-" // Not enough data
                    else -> "C"
                }

                val performance = BatchPerformance(
                    fcr = fcr,
                    mortalityRate = mortalityRate,
                    grade = grade,
                    totalFeedConsumed = totalFeedKg
                )
                
                _uiState.update { it.copy(performance = performance) }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }

    fun saveTagGroups(groups: List<TagGroup>) {
        val current = _uiState.value.asset ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            try {
                // Parse existing metadata or create new
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<MutableMap<String, Any>>() {}.type
                val metadata: MutableMap<String, Any> = try {
                    gson.fromJson(current.metadataJson, type) ?: mutableMapOf()
                } catch (e: Exception) {
                    mutableMapOf()
                }
                
                // Update tagGroups key
                metadata["tagGroups"] = groups
                
                val updatedJson = gson.toJson(metadata)
                val updatedAsset = current.copy(metadataJson = updatedJson, dirty = true)
                
                when (val result = repository.updateAsset(updatedAsset)) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isUpdating = false, successMessage = "Tag groups saved") }
                        loadAsset()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isUpdating = false, error = result.message) }
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdating = false, error = "Failed to save tags: ${e.message}") }
            }
        }
    }

    fun canCreateListing(): Boolean {
        val asset = _uiState.value.asset ?: return false
        // Cannot list quarantined or archived assets
        return asset.status == "ACTIVE" && asset.quantity > 0
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

data class FarmAssetDetailUiState(
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val asset: FarmAssetEntity? = null,
    val tagGroups: List<TagGroup> = emptyList(),
    val performance: BatchPerformance? = null,
    val error: String? = null,
    val successMessage: String? = null
)

data class BatchPerformance(
    val fcr: Double,
    val mortalityRate: Double,
    val grade: String,
    val totalFeedConsumed: Double
)



