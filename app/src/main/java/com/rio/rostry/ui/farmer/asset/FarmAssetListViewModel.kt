package com.rio.rostry.ui.farmer.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.FarmFinancialsRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Preset actionable filters for quick filtering.
 */
enum class QuickFilter(val displayName: String, val icon: String) {
    ALL("All", "🏠"),
    READY_TO_LAY("Ready to Lay", "🥚"),
    CULL_CANDIDATES("Cull Candidates", "⚠️"),
    VACCINATION_DUE("Vaccination Due", "💉"),
    HEALTHY("Healthy", "✅"),
    SICK("Needs Attention", "🏥")
}

@HiltViewModel
class FarmAssetListViewModel @Inject constructor(
    private val repository: FarmAssetRepository,
    private val financialsRepository: FarmFinancialsRepository,
    private val activityLogRepository: com.rio.rostry.data.repository.FarmActivityLogRepository,
    private val auth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmAssetListUiState())
    val uiState = _uiState.asStateFlow()
    
    // Quick filter counts
    private val _filterCounts = MutableStateFlow<Map<QuickFilter, Int>>(emptyMap())
    val filterCounts = _filterCounts.asStateFlow()
    
    // Current quick filter
    private val _currentQuickFilter = MutableStateFlow(QuickFilter.ALL)
    val currentQuickFilter = _currentQuickFilter.asStateFlow()

    init {
        loadAssets()
        loadFilterCounts()
    }

    private fun loadAssets() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
            return
        }

        viewModelScope.launch {
            repository.getAssetsByFarmer(userId)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    assets = result.data ?: emptyList(),
                                    filteredAssets = filterAssets(result.data ?: emptyList(), state.filter, _currentQuickFilter.value)
                                )
                            }
                        }
                        is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
        }
    }
    
    /**
     * Load counts for quick filters.
     */
    private fun loadFilterCounts() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            combine(
                financialsRepository.getReadyToLayCount(userId),
                financialsRepository.getCullCandidatesCount(userId),
                financialsRepository.getVaccinationDueSoonCount(userId)
            ) { readyToLay, cullCandidates, vaccinationDue ->
                mapOf(
                    QuickFilter.READY_TO_LAY to readyToLay,
                    QuickFilter.CULL_CANDIDATES to cullCandidates,
                    QuickFilter.VACCINATION_DUE to vaccinationDue
                )
            }.collect { counts ->
                _filterCounts.value = counts
            }
        }
    }
    
    /**
     * Apply a quick filter.
     */
    fun applyQuickFilter(filter: QuickFilter) {
        _currentQuickFilter.value = filter
        _uiState.update { state ->
            state.copy(
                filteredAssets = filterAssets(state.assets, state.filter, filter)
            )
        }
    }

    fun updateFilter(filter: String) {
        _uiState.update { state ->
            val newFilter = if (filter == "ALL") null else filter
            state.copy(
                filter = newFilter,
                filteredAssets = filterAssets(state.assets, newFilter, _currentQuickFilter.value),
                selectedAssetIds = emptySet(), // Clear selection on filter change
                isSelectionMode = false
            )
        }
    }

    // Selection Handling
    fun toggleSelection(assetId: String) {
        _uiState.update { state ->
            val currentSelected = state.selectedAssetIds.toMutableSet()
            if (currentSelected.contains(assetId)) {
                currentSelected.remove(assetId)
            } else {
                currentSelected.add(assetId)
            }
            state.copy(
                selectedAssetIds = currentSelected,
                isSelectionMode = currentSelected.isNotEmpty()
            )
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedAssetIds = emptySet(), isSelectionMode = false) }
    }

    fun selectAll() {
        _uiState.update { state ->
            val allIds = state.filteredAssets.map { it.assetId }.toSet()
            state.copy(
                selectedAssetIds = allIds,
                isSelectionMode = true
            )
        }
    }

    /**
     * Submit a log entry for all selected assets.
     */
    fun submitBulkLog(
        activityType: String,
        value: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val selectedIds = _uiState.value.selectedAssetIds
            
            if (selectedIds.isEmpty()) return@launch
            
            // Map QuickLogType or raw string to appropriate fields
            val logType = activityType.uppercase()
            val quantity = if (logType == "FEED" || logType == "WEIGHT") value else null
            val amount = if (logType == "EXPENSE") value else null
            
            val category = when(logType) {
                "FEED" -> "Feed"
                "VACCINATION" -> "Health - Vaccination"
                else -> "General"
            }
            
            selectedIds.forEach { assetId ->
                try {
                    activityLogRepository.logActivity(
                        farmerId = userId,
                        productId = assetId,
                        activityType = logType,
                        amount = amount,
                        quantity = quantity,
                        category = category,
                        description = "Bulk $logType Log",
                        notes = notes
                    )
                } catch (e: Exception) {
                    // Log error but continue
                    e.printStackTrace()
                }
            }
            
            // Clear selection after successful bulk action
            clearSelection()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            // Trigger sync (repository implementation will handle fetching from remote and updating DB)
            val result = repository.syncAssets()
            _uiState.update { state ->
                state.copy(
                    isRefreshing = false, 
                    error = if (result is Resource.Error) result.message else null
                )
            }
            loadFilterCounts() // Refresh counts
        }
    }

    private fun filterAssets(
        assets: List<FarmAssetEntity>, 
        filter: String?,
        quickFilter: QuickFilter = QuickFilter.ALL
    ): List<FarmAssetEntity> {
        var filtered = assets
        
        // Apply type/status filter
        if (filter != null) {
            filtered = filtered.filter { 
                it.assetType.equals(filter, ignoreCase = true) || it.status.equals(filter, ignoreCase = true) 
            }
        }
        
        // Apply quick filter
        filtered = when (quickFilter) {
            QuickFilter.ALL -> filtered
            QuickFilter.READY_TO_LAY -> filtered.filter { 
                it.gender?.equals("FEMALE", ignoreCase = true) == true && 
                it.ageWeeks != null && it.ageWeeks in 18..22 
            }
            QuickFilter.CULL_CANDIDATES -> filtered.filter { 
                it.healthStatus.equals("POOR", ignoreCase = true) || 
                (it.ageWeeks != null && it.ageWeeks > 72) 
            }
            QuickFilter.VACCINATION_DUE -> filtered.filter { 
                it.nextVaccinationDate != null && 
                it.nextVaccinationDate <= System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000)
            }
            QuickFilter.HEALTHY -> filtered.filter { 
                it.healthStatus.equals("HEALTHY", ignoreCase = true) 
            }
            QuickFilter.SICK -> filtered.filter { 
                it.healthStatus.equals("SICK", ignoreCase = true) || 
                it.healthStatus.equals("POOR", ignoreCase = true) ||
                it.healthStatus.equals("WATCH", ignoreCase = true)
            }
        }
        
        return filtered
    }

    /**
     * Submit batch tagging rules.
     * This saves the configuration to the Batch Asset's metadataJson.
     * The BatchGraduationWorker will pick this up to generate individual assets.
     */
    fun submitBatchTags(batchId: String, groups: List<com.rio.rostry.ui.farmer.asset.TagGroupInput>) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            
            // 1. Fetch current asset
             repository.getAssetById(batchId).first().data?.let { asset ->
                
                // 2. Serialize groups to JSON map
                // In a real app we'd use Gson, but manual JSON is fine for this structure
                // structure: { "tagGroups": [ { "prefix": "A", "start": 1, "count": 10, ... } ] }
                val groupsJson = groups.joinToString(separator = ",", prefix = "[", postfix = "]") { group ->
                    """
                    {
                        "prefix": "${group.prefix}",
                        "startNumber": ${group.startNumber.toIntOrNull() ?: 1},
                        "count": ${group.count.toIntOrNull() ?: 1},
                        "gender": "${group.gender}",
                        "color": "${group.tagColor}"
                    }
                    """.trimIndent()
                }
                
                val metadataJson = """{"tagGroups": $groupsJson}"""
                
                // 3. Update Asset
                val updatedAsset = asset.copy(
                    metadataJson = metadataJson,
                    dirty = true,
                    updatedAt = System.currentTimeMillis()
                )
                
                repository.updateAsset(updatedAsset)
                
                // 4. Trigger Worker (Optional immediate trigger for UX)
                // In production, we might just let the daily job pick it up, 
                // but for testing we want instant gratification.
                // WorkManagerHelper.triggerBatchGraduation(batchId)
            }
        }
    }
}

data class FarmAssetListUiState(
    val isLoading: Boolean = true,
    val assets: List<FarmAssetEntity> = emptyList(),
    val filteredAssets: List<FarmAssetEntity> = emptyList(),
    val error: String? = null,
    val filter: String? = null, // e.g. "BATCH", "ANIMAL", "ACTIVE"
    val isRefreshing: Boolean = false,
    val selectedAssetIds: Set<String> = emptySet(), // Multi-selection state
    val isSelectionMode: Boolean = false
) {
    val selectionCount: Int get() = selectedAssetIds.size
}

