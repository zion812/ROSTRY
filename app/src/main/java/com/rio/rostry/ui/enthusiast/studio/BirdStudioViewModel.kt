package com.rio.rostry.ui.enthusiast.studio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.deriveAppearanceFromBreed
import com.rio.rostry.domain.model.parseAppearanceFromJson
import com.rio.rostry.domain.model.toAppearanceJson
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class BirdStudioUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val asset: FarmAssetEntity? = null,
    val appearance: BirdAppearance? = null,
    val savedSuccessfully: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BirdStudioViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FarmAssetRepository
) : ViewModel() {

    private val productId: String = savedStateHandle["productId"] ?: ""

    private val _uiState = MutableStateFlow(BirdStudioUiState())
    val uiState: StateFlow<BirdStudioUiState> = _uiState.asStateFlow()

    init {
        loadAsset()
    }

    private fun loadAsset() {
        viewModelScope.launch {
            repository.getAssetById(productId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val asset = resource.data
                        if (asset != null) {
                            // Try to parse existing appearance from metadataJson
                            val appearance = tryParseAppearance(asset)
                                ?: deriveAppearanceFromBreed(
                                    asset.breed,
                                    asset.gender,
                                    asset.ageWeeks ?: 24
                                )

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    asset = asset,
                                    appearance = appearance
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, error = "Bird not found")
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = resource.message)
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun tryParseAppearance(asset: FarmAssetEntity): BirdAppearance? {
        val json = asset.metadataJson ?: return null
        if (json.isBlank()) return null
        return try {
            // Try to extract appearance JSON from metadataJson
            // metadataJson might contain other data too, so we look for the appearance block
            val parsed = parseAppearanceFromJson(json)
            Timber.d("Loaded existing appearance from metadataJson for ${asset.assetId}")
            parsed
        } catch (e: Exception) {
            Timber.w(e, "Failed to parse appearance from metadataJson")
            null
        }
    }

    fun saveAppearance(appearance: BirdAppearance) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val asset = _uiState.value.asset
            if (asset == null) {
                _uiState.update { it.copy(isSaving = false, error = "No asset loaded") }
                return@launch
            }

            try {
                val appearanceJson = appearance.toAppearanceJson()

                // Merge with existing metadataJson if it has other data
                val finalJson = mergeAppearanceIntoMetadata(asset.metadataJson, appearanceJson)

                when (val result = repository.updateMetadataJson(asset.assetId, finalJson)) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                appearance = appearance,
                                savedSuccessfully = true
                            )
                        }
                        Timber.d("Appearance saved for ${asset.assetId}")
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isSaving = false, error = result.message)
                        }
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save appearance")
                _uiState.update {
                    it.copy(isSaving = false, error = "Failed to save: ${e.message}")
                }
            }
        }
    }

    private fun mergeAppearanceIntoMetadata(
        existingJson: String?,
        appearanceJson: String
    ): String {
        // If no existing metadata, just use the appearance JSON directly
        if (existingJson.isNullOrBlank()) return appearanceJson

        // Try to parse existing as JSON object and merge
        return try {
            val existingObj = org.json.JSONObject(existingJson)
            val appearanceObj = org.json.JSONObject(appearanceJson)

            // Copy all appearance keys into existing
            appearanceObj.keys().forEach { key ->
                existingObj.put(key, appearanceObj.get(key))
            }

            existingObj.toString()
        } catch (e: Exception) {
            // If existing is not valid JSON, just replace it
            appearanceJson
        }
    }

    fun clearSaveFlag() {
        _uiState.update { it.copy(savedSuccessfully = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
