package com.rio.rostry.feature.asset.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.farm.repository.FarmAssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetManagementViewModel @Inject constructor(
    private val farmAssetRepository: FarmAssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetManagementUiState())
    val uiState: StateFlow<AssetManagementUiState> = _uiState.asStateFlow()

    fun loadAssets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Load assets
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun createAsset(asset: FarmAsset) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = farmAssetRepository.createAsset(asset)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Asset created")
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class AssetManagementUiState(
    val isLoading: Boolean = false,
    val assets: List<FarmAsset> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)
