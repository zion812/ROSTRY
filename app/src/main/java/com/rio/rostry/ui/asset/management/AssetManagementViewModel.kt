package com.rio.rostry.ui.asset.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.domain.lifecycle.AssetLifecycleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetManagementViewModel @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val lifecycleManager: AssetLifecycleManager
) : ViewModel() {

    private val _assets = MutableStateFlow<List<FarmAssetEntity>>(emptyList())
    val assets: StateFlow<List<FarmAssetEntity>> = _assets.asStateFlow()

    fun loadAssets(farmerId: String) {
        viewModelScope.launch {
            // Fetching from local repository
            assetRepository.getAssetsByFarmer(farmerId).collect { result ->
                if (result is com.rio.rostry.utils.Resource.Success) {
                    _assets.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun recordStageChange(assetId: String, farmerId: String, fromStage: String?, toStage: String, notes: String?) {
        viewModelScope.launch {
            lifecycleManager.recordStageChange(assetId, farmerId, fromStage, toStage, notes)
            loadAssets(farmerId) // Refresh
        }
    }
}
