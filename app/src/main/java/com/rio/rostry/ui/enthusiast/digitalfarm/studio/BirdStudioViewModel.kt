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
import com.rio.rostry.domain.logic.BirdColorClassifier
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
                // Timber.d("Loading bird: $id")
                productRepository.getProductById(id).collect { resource ->
                    if (resource is com.rio.rostry.utils.Resource.Success) {
                        resource.data?.let { b ->
                            // Timber.d("Bird loaded from DB. Metadata present: ${b.metadataJson != null}")
                            val appearance = b.metadataJson?.let { json ->
                                val parsed = parseAppearanceFromJson(json)
                                if (parsed == null) {
                                    // Timber.e("Failed to parse appearance JSON: $json")
                                }
                                parsed
                            } ?: deriveAppearanceFromBreed(b.breed ?: "Unknown", b.gender ?: "Unknown", b.ageWeeks ?: 0)
                            
                            // Classify on load
                            val classified = BirdColorClassifier.classify(appearance)
                            val finalAppearance = appearance.copy(localType = classified.type)

                            _uiState.update { it.copy(
                                birdId = id,
                                breed = b.breed ?: "Unknown",
                                gender = b.gender ?: "Unknown",
                                ageWeeks = b.ageWeeks ?: 0,
                                currentAppearance = finalAppearance,
                                originalAppearance = finalAppearance
                            ) }
                        }
                    } else if (resource is com.rio.rostry.utils.Resource.Error) {
                         _uiState.update { it.copy(error = "Error loading bird: ${resource.message}") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load bird: ${e.message}") }
            }
        }
    }

    fun updateAppearance(newAppearance: BirdAppearance) {
        // 1. Auto-classify Color
        val classified = BirdColorClassifier.classify(newAppearance)
        
        // 2. Calculate ASI from Digital Twin Profile (if exists) or map from Appearance
        // For now, let's assume we map legacy appearance to structure profile dynamically
        val structure = mapAppearanceToStructure(newAppearance)
        val asi = com.rio.rostry.domain.digitaltwin.StructuralEngine.calculateASI(structure)
        
        // 3. Update State
        val finalAppearance = newAppearance.copy(
            localType = classified.type,
            digitalTwinProfile = com.rio.rostry.domain.digitaltwin.DigitalTwinProfile(
                structure = structure,
                ageStage = com.rio.rostry.domain.digitaltwin.AgeStage.ADULT, // TODO: Derive from ageWeeks
                asiScore = asi
            )
        )
        
        _uiState.update { item -> item.copy(currentAppearance = finalAppearance) }
    }

    // Helper to bridge Legacy -> Structure Profile
    private fun mapAppearanceToStructure(a: BirdAppearance): com.rio.rostry.domain.digitaltwin.StructureProfile {
        return com.rio.rostry.domain.digitaltwin.StructureProfile(
            neckLength = if (a.neck == com.rio.rostry.domain.model.NeckStyle.LONG) 0.8f else 0.5f,
            legLength = a.legLength, // Direct morph linkage
            boneThickness = a.legThickness,
            chestDepth = if (a.breast == com.rio.rostry.domain.model.BreastShape.DEEP) 0.8f else 0.5f,
            featherTightness = 0.8f, // Default to tight for Aseel
            tailCarriage = a.tailAngle,
            postureAngle = if (a.stance == com.rio.rostry.domain.model.Stance.UPRIGHT) 0.8f else 0.5f,
            bodyWidth = a.bodyWidth
        )
    }

    // FIELD MODE PRESETS
    fun applyFieldPreset(presetName: String) {
        val current = _uiState.value.currentAppearance
        val newAppearance = when (presetName) {
            "Tall Lean" -> current.copy(
                stance = com.rio.rostry.domain.model.Stance.UPRIGHT,
                neck = com.rio.rostry.domain.model.NeckStyle.LONG,
                legLength = 0.85f,
                bodyWidth = 0.3f,
                bodyRoundness = 0.2f
            )
            "Power Frame" -> current.copy(
                stance = com.rio.rostry.domain.model.Stance.GAME_READY,
                neck = com.rio.rostry.domain.model.NeckStyle.MUSCULAR,
                legLength = 0.7f,
                legThickness = 0.9f,
                bodyWidth = 0.8f,
                breast = com.rio.rostry.domain.model.BreastShape.DEEP
            )
            "Compact" -> current.copy(
                stance = com.rio.rostry.domain.model.Stance.NORMAL,
                neck = com.rio.rostry.domain.model.NeckStyle.SHORT,
                legLength = 0.4f,
                bodyWidth = 0.6f,
                bodyRoundness = 0.7f
            )
            else -> current
        }
        updateAppearance(newAppearance)
    }

    fun resetToBreedStandard() {
        val state = _uiState.value
        // Regenerate standard appearance based on current breed/gender/age
        val standard = deriveAppearanceFromBreed(state.breed, state.gender, state.ageWeeks)
        
        // We use updateAppearance to ensure ASI and Digital Twin profile are recalculated
        updateAppearance(standard)
    }

    fun saveAppearance() {
        val state = _uiState.value
        val birdId = state.birdId
        
        if (birdId == null) {
            _uiState.update { it.copy(error = "Cannot save: No bird ID") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                // Serialize current appearance to JSON
                // Timber.d("Saving appearance for bird $birdId")
                val json = state.currentAppearance.toAppearanceJson()
                // Timber.d("Generated JSON length: ${json.length}")
                
                // Save to repository (metadata field)
                val result = productRepository.updateProductMetadata(birdId, json)
                
                if (result is com.rio.rostry.utils.Resource.Success) {
                    // Timber.d("Save successful")
                    _uiState.update { it.copy(isSaving = false, success = true) }
                } else {
                    // Timber.e("Save failed: ${result.message}")
                    _uiState.update { it.copy(isSaving = false, error = result.message ?: "Failed to save") }
                }
            } catch (e: Exception) {
                // Timber.e(e, "Exception during save")
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
