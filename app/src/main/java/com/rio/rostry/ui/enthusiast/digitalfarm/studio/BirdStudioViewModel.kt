package com.rio.rostry.ui.enthusiast.digitalfarm.studio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.DigitalFarmConfig
import com.rio.rostry.domain.model.deriveAppearanceFromBreed
import com.rio.rostry.domain.model.toAppearanceJson
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.ui.enthusiast.digitalfarm.parseAppearanceFromJson
// import com.rio.rostry.ui.enthusiast.digitalfarm.DigitalFarmState // Remove DigitalFarmState, use BirdStudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BirdStudioState(
    val birdId: String? = null,
    val breed: String = "Unknown",
    val gender: String = "Male",
    val ageWeeks: Int = 12, // Default to young adult
    val originalAppearance: BirdAppearance? = null,
    val currentAppearance: BirdAppearance = BirdAppearance(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val config: DigitalFarmConfig = DigitalFarmConfig.ENTHUSIAST // For preview settings
)

@HiltViewModel
class BirdStudioViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BirdStudioState())
    val uiState: StateFlow<BirdStudioState> = _uiState.asStateFlow()

    private val birdId: String? = savedStateHandle.get<String>("birdId")

    init {
        if (birdId != null) {
            loadBird(birdId)
        } else {
            // New creation mode - default to Aseel Male
            val defaultAppearance = deriveAppearanceFromBreed("Aseel", "Male", 20)
            _uiState.update { it.copy(
                birdId = null,
                breed = "Aseel",
                gender = "Male",
                currentAppearance = defaultAppearance,
                originalAppearance = defaultAppearance
            ) }
        }
    }

    private fun loadBird(id: String) {
        viewModelScope.launch {
            try {
                // val bird = productRepository.getById(id) // Ideally use suspend version if available
                // Flow version fallback
                productRepository.getProductById(id).collect { resource ->
                    if (resource is com.rio.rostry.utils.Resource.Success) {
                        resource.data?.let { b ->
                            val appearance = b.metadataJson?.let { json ->
                                parseAppearanceFromJson(json)
                            } ?: deriveAppearanceFromBreed(b.breed ?: "Unknown", b.gender ?: "Unknown", b.ageWeeks ?: 0)

                            _uiState.update { it.copy(
                                birdId = id,
                                breed = b.breed ?: "Unknown",
                                gender = b.gender ?: "Unknown",
                                ageWeeks = b.ageWeeks ?: 0,
                                currentAppearance = appearance,
                                originalAppearance = appearance
                            ) }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load bird: ${e.message}") }
            }
        }
    }

    fun updateAppearance(newAppearance: BirdAppearance) {
        _uiState.update { item -> item.copy(currentAppearance = newAppearance) }
    }

    fun resetToBreedStandard() {
        val state = _uiState.value
        val standard = deriveAppearanceFromBreed(state.breed, state.gender, state.ageWeeks)
        _uiState.update { it.copy(currentAppearance = standard) }
    }

    fun saveAppearance() {
        val state = _uiState.value
        val id = state.birdId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val json = state.currentAppearance.toAppearanceJson()
                val result = productRepository.updateProductMetadata(id, json)
                
                if (result is com.rio.rostry.utils.Resource.Success) {
                    _uiState.update { it.copy(isSaving = false, success = true) }
                } else {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
