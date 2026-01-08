package com.rio.rostry.ui.farmer.asset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmAssetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FarmAssetRepository,
    private val activityLogDao: com.rio.rostry.data.database.dao.FarmActivityLogDao
) : ViewModel() {

    private val assetId: String = savedStateHandle.get<String>("assetId") ?: ""
    
    private val _uiState = MutableStateFlow(FarmAssetDetailUiState())
    val uiState = _uiState.asStateFlow()
    
    // Recent events for inline history display
    private val _recentEvents = MutableStateFlow<List<RecentActivityEvent>>(emptyList())
    val recentEvents = _recentEvents.asStateFlow()

    init {
        if (assetId.isNotBlank()) {
            loadAsset()
            loadRecentEvents()
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
                                notes = log.notes
                            )
                        }
                    }
            } catch (e: Exception) {
                // Silent fail - just show empty history
            }
        }
    }

    fun loadAsset() {
        viewModelScope.launch {
            repository.getAssetById(assetId)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> _uiState.update { 
                            it.copy(isLoading = false, asset = result.data, error = null) 
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
    val error: String? = null,
    val successMessage: String? = null
)
