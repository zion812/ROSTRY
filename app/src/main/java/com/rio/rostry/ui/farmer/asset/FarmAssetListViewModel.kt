package com.rio.rostry.ui.farmer.asset

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
class FarmAssetListViewModel @Inject constructor(
    private val repository: FarmAssetRepository,
    private val auth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmAssetListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAssets()
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
                                    filteredAssets = filterAssets(result.data ?: emptyList(), state.filter)
                                )
                            }
                        }
                        is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
        }
    }

    fun updateFilter(filter: String) {
        _uiState.update { state ->
            val newFilter = if (state.filter == filter) null else filter // Toggle
            state.copy(
                filter = newFilter,
                filteredAssets = filterAssets(state.assets, newFilter)
            )
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
        }
    }

    private fun filterAssets(assets: List<FarmAssetEntity>, filter: String?): List<FarmAssetEntity> {
        return if (filter == null) assets
        else assets.filter { it.assetType.equals(filter, ignoreCase = true) || it.status.equals(filter, ignoreCase = true) }
    }
}

data class FarmAssetListUiState(
    val isLoading: Boolean = true,
    val assets: List<FarmAssetEntity> = emptyList(),
    val filteredAssets: List<FarmAssetEntity> = emptyList(),
    val error: String? = null,
    val filter: String? = null, // e.g. "BATCH", "ANIMAL", "ACTIVE"
    val isRefreshing: Boolean = false
)
