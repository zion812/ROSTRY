package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmFinancialsRepository
import com.rio.rostry.data.repository.CostPerBirdAnalysis
import com.rio.rostry.data.repository.FCRAnalysis
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Performance metrics for overall farm performance.
 */
data class FarmPerformanceState(
    val isLoading: Boolean = true,
    val assets: List<FarmAssetEntity> = emptyList(),
    val selectedAssetId: String? = null,
    val fcrAnalysis: FCRAnalysis? = null,
    val costAnalysis: CostPerBirdAnalysis? = null,
    val averageFCR: Double = 0.0,
    val totalBirds: Int = 0,
    val activeBatches: Int = 0,
    val totalFeedCost: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val profitMargin: Double = 0.0,
    val error: String? = null
)

/**
 * ViewModel for the Farm Performance dashboard.
 * Aggregates FCR, cost, and profitability metrics across all assets.
 */
@HiltViewModel
class FarmPerformanceViewModel @Inject constructor(
    private val farmAssetDao: FarmAssetDao,
    private val financialsRepository: FarmFinancialsRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {
    
    private val _state = MutableStateFlow(FarmPerformanceState())
    val state: StateFlow<FarmPerformanceState> = _state.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow(30) // Default 30 days
    val selectedPeriod: StateFlow<Int> = _selectedPeriod.asStateFlow()
    
    val periodOptions = listOf(7, 14, 30, 90)
    
    init {
        loadFarmPerformance()
    }
    
    /**
     * Load overall farm performance metrics.
     */
    fun loadFarmPerformance() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                // Get all assets for the farmer using existing DAO method
                val assetsFlow = farmAssetDao.getAssetsByFarmer(userId)
                val assets = assetsFlow.first()
                
                // Filter to active assets
                val activeAssets = assets.filter { it.status?.uppercase() == "ACTIVE" }
                
                val totalBirds = activeAssets.fold(0) { acc, asset -> acc + asset.quantity.toInt() }
                val activeBatches = activeAssets.count { asset -> asset.quantity.toInt() > 1 }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    assets = activeAssets,
                    totalBirds = totalBirds,
                    activeBatches = activeBatches
                )
                
                // Auto-select first asset if available
                if (activeAssets.isNotEmpty() && _state.value.selectedAssetId == null) {
                    activeAssets.firstOrNull()?.assetId?.let { selectAsset(it) }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load performance data"
                )
            }
        }
    }
    
    /**
     * Select an asset to view detailed metrics.
     */
    fun selectAsset(assetId: String) {
        _state.value = _state.value.copy(selectedAssetId = assetId)
        loadAssetDetails(assetId)
    }
    
    /**
     * Load detailed metrics for a specific asset.
     */
    private fun loadAssetDetails(assetId: String) {
        viewModelScope.launch {
            // Load FCR Analysis
            financialsRepository.calculateFCR(assetId, _selectedPeriod.value).collect { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(fcrAnalysis = result.data)
                }
            }
            
            // Load Cost Analysis
            financialsRepository.getCostPerBird(assetId).collect { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(costAnalysis = result.data)
                }
            }
        }
    }
    
    /**
     * Update period and reload data.
     */
    fun selectPeriod(days: Int) {
        if (days in periodOptions && days != _selectedPeriod.value) {
            _selectedPeriod.value = days
            _state.value.selectedAssetId?.let { loadAssetDetails(it) }
        }
    }
    
    /**
     * Refresh all data.
     */
    fun refresh() {
        loadFarmPerformance()
    }
}
