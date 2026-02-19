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
import com.rio.rostry.domain.logic.BirdColorClassifier
import com.rio.rostry.domain.digitaltwin.lifecycle.*
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
    val config: DigitalFarmConfig = DigitalFarmConfig.ENTHUSIAST, // For preview settings

    // === PMLE — Lifecycle Evolution State ===
    val ageProfile: AgeProfile = AgeProfile.fromWeeks(12, true),
    val currentConstraints: StageMorphConstraints? = null,
    val growthTimeline: List<GrowthSnapshot> = emptyList(),
    val morphSummary: MorphSummary? = null,
    val eggProfile: EggProfile? = null,            // Non-null only if stage == EGG
    val weightExpectation: WeightExpectation? = null,
    val isLifecycleEnabled: Boolean = true          // Toggle PMLE on/off
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
            val ageProfile = AgeProfile.fromWeeks(20, isMale = true)
            val evolved = LifecycleMorphEngine.evolve(defaultAppearance, ageProfile)
            val constraints = LifecycleMorphEngine.getConstraints(ageProfile)

            _uiState.update { it.copy(
                birdId = null,
                breed = "Aseel",
                gender = "Male",
                ageWeeks = 20,
                currentAppearance = evolved,
                originalAppearance = evolved,
                ageProfile = ageProfile,
                currentConstraints = constraints,
                morphSummary = generateMorphSummary(ageProfile),
                weightExpectation = GrowthCurve.expectedWeight(ageProfile.ageInDays, true)
            ) }
        }
    }

    private fun loadBird(id: String) {
        viewModelScope.launch {
            try {
                productRepository.getProductById(id).collect { resource ->
                    if (resource is com.rio.rostry.utils.Resource.Success) {
                        resource.data?.let { b ->
                            val isMale = (b.gender ?: "Male").equals("Male", true)
                            val ageWeeks = b.ageWeeks ?: 0
                            val ageProfile = AgeProfile.fromWeeks(ageWeeks, isMale)

                            val appearance = b.metadataJson?.let { json ->
                                parseAppearanceFromJson(json)
                            } ?: deriveAppearanceFromBreed(b.breed ?: "Unknown", b.gender ?: "Unknown", ageWeeks)

                            // Apply lifecycle evolution if enabled
                            val evolved = if (_uiState.value.isLifecycleEnabled) {
                                LifecycleMorphEngine.evolve(appearance, ageProfile)
                            } else appearance

                            // Classify color
                            val classified = BirdColorClassifier.classify(evolved)
                            val finalAppearance = evolved.copy(localType = classified.type)

                            val constraints = LifecycleMorphEngine.getConstraints(ageProfile)

                            _uiState.update { it.copy(
                                birdId = id,
                                breed = b.breed ?: "Unknown",
                                gender = b.gender ?: "Unknown",
                                ageWeeks = ageWeeks,
                                currentAppearance = finalAppearance,
                                originalAppearance = finalAppearance,
                                ageProfile = ageProfile,
                                currentConstraints = constraints,
                                morphSummary = generateMorphSummary(ageProfile),
                                weightExpectation = GrowthCurve.expectedWeight(ageProfile.ageInDays, isMale)
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

    // ==================== APPEARANCE UPDATES ====================

    fun updateAppearance(newAppearance: BirdAppearance) {
        val state = _uiState.value

        // 1. Apply lifecycle constraints (clamp to valid range for current stage)
        val constrained = if (state.isLifecycleEnabled) {
            LifecycleMorphEngine.constrainToStage(newAppearance, state.ageProfile)
        } else newAppearance

        // 2. Auto-classify Color
        val classified = BirdColorClassifier.classify(constrained)

        // 3. Calculate ASI from Digital Twin Profile
        val structure = mapAppearanceToStructure(constrained)
        val asi = com.rio.rostry.domain.digitaltwin.StructuralEngine.calculateASI(structure)

        // 4. Derive AgeStage from PMLE BiologicalStage
        val ageStage = com.rio.rostry.domain.digitaltwin.AgeStage.fromBiologicalStage(state.ageProfile.stage)

        // 5. Update State with full Digital Twin profile
        val finalAppearance = constrained.copy(
            localType = classified.type,
            digitalTwinProfile = com.rio.rostry.domain.digitaltwin.DigitalTwinProfile(
                structure = structure,
                ageStage = ageStage,
                asiScore = asi
            )
        )

        _uiState.update { item -> item.copy(currentAppearance = finalAppearance) }
    }

    // ==================== LIFECYCLE CONTROLS ====================

    /**
     * Change the bird's age — triggers full lifecycle evolution.
     * This is the key PMLE entry point for age changes.
     */
    fun setAge(ageWeeks: Int) {
        val state = _uiState.value
        val isMale = state.gender.equals("Male", true)
        val newAgeProfile = AgeProfile.fromWeeks(ageWeeks, isMale).copy(growthMode = state.ageProfile.growthMode)

        val oldStage = state.ageProfile.stage
        val newStage = newAgeProfile.stage

        // Evolve appearance based on new age
        val baseAppearance = state.originalAppearance ?: state.currentAppearance
        val evolved = if (state.isLifecycleEnabled) {
            LifecycleMorphEngine.evolve(baseAppearance, newAgeProfile)
        } else baseAppearance

        val constraints = LifecycleMorphEngine.getConstraints(newAgeProfile)

        // Check for stage transition
        val changes = if (oldStage != newStage) {
            LifecycleMorphEngine.getTransitionChanges(
                state.ageProfile.ageInDays,
                newAgeProfile.ageInDays,
                isMale
            )
        } else emptyList()

        _uiState.update { it.copy(
            ageWeeks = ageWeeks,
            ageProfile = newAgeProfile,
            currentConstraints = constraints,
            morphSummary = generateMorphSummary(newAgeProfile),
            weightExpectation = GrowthCurve.expectedWeight(newAgeProfile.ageInDays, isMale),
            eggProfile = if (newAgeProfile.stage == BiologicalStage.EGG) EggProfile() else null
        ) }

        // Re-apply appearance through the normal update path
        updateAppearance(evolved)
    }

    /**
     * Set age in days for more precise control.
     */
    fun setAgeDays(ageDays: Int) {
        setAge(ageDays / 7)
    }

    /**
     * Switch growth mode: Auto Biological / Manual Stage / Manual Free
     */
    fun setGrowthMode(mode: GrowthMode) {
        val state = _uiState.value
        val newAgeProfile = state.ageProfile.copy(growthMode = mode)

        _uiState.update { it.copy(ageProfile = newAgeProfile) }

        // Re-evolve with new mode
        if (mode == GrowthMode.AUTO_BIOLOGICAL) {
            val base = state.originalAppearance ?: state.currentAppearance
            val evolved = LifecycleMorphEngine.evolve(base, newAgeProfile)
            updateAppearance(evolved)
        }
    }

    /**
     * Toggle PMLE on/off. When off, behaves like the original free-edit Bird Studio.
     */
    fun toggleLifecycle(enabled: Boolean) {
        _uiState.update { it.copy(isLifecycleEnabled = enabled) }

        if (enabled) {
            // Re-evolve with current age
            val state = _uiState.value
            val base = state.originalAppearance ?: state.currentAppearance
            val evolved = LifecycleMorphEngine.evolve(base, state.ageProfile)
            updateAppearance(evolved)
        }
    }

    /**
     * Generate the full growth timeline for visualization.
     */
    fun loadGrowthTimeline() {
        val state = _uiState.value
        val base = state.originalAppearance ?: state.currentAppearance
        val isMale = state.gender.equals("Male", true)

        viewModelScope.launch {
            val timeline = LifecycleMorphEngine.generateGrowthTimeline(base, isMale)
            _uiState.update { it.copy(growthTimeline = timeline) }
        }
    }

    /**
     * Preview what the bird will look like at a specific age.
     */
    fun previewAtAge(ageDays: Int): BirdAppearance {
        val state = _uiState.value
        val base = state.originalAppearance ?: state.currentAppearance
        val isMale = state.gender.equals("Male", true)
        return LifecycleMorphEngine.previewAtAge(base, ageDays, isMale)
    }

    /**
     * Update gender — triggers re-evolution since gender affects morphology.
     */
    fun setGender(gender: String) {
        val state = _uiState.value
        val isMale = gender.equals("Male", true)
        val newAgeProfile = state.ageProfile.copy(isMale = isMale)

        _uiState.update { it.copy(
            gender = gender,
            ageProfile = newAgeProfile,
            currentConstraints = LifecycleMorphEngine.getConstraints(newAgeProfile)
        ) }

        // Re-evolve
        val base = state.originalAppearance ?: state.currentAppearance
        val evolved = LifecycleMorphEngine.evolve(base.copy(isMale = isMale), newAgeProfile)
        updateAppearance(evolved)
    }

    // ==================== HELPERS ====================

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

    private fun generateMorphSummary(ageProfile: AgeProfile): MorphSummary {
        val stage = ageProfile.stage
        val constraints = MorphConstraints.forStage(stage, ageProfile.isMale)
        return MorphSummary(
            stageName = stage.displayName,
            stageEmoji = stage.emoji,
            featherTexture = constraints.defaultFeatherTexture,
            keyFeatures = buildList {
                if (stage.hasHackles && ageProfile.isMale) add("Hackle feathers visible")
                if (stage.hasSpurs && ageProfile.isMale) add("Spurs developing")
                if (stage.hasSickleFeathers && ageProfile.isMale) add("Sickle tail feathers")
                if (stage.hasFullPlumage) add("Full plumage achieved")
                if (stage == BiologicalStage.HATCHLING) add("Down-covered, oversized head")
                if (stage == BiologicalStage.CHICK) add("First feathers emerging")
                if (stage == BiologicalStage.GROWER) add("Rapid leg growth, patchy feathers")
                if (stage == BiologicalStage.SENIOR) add("Peak bone thickness, slight feather dulling")
            },
            maturityPercent = (ageProfile.maturityIndex * 100).toInt().coerceIn(0, 100)
        )
    }

    // ==================== PRESETS ====================

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

        // Evolve using lifecycle if enabled
        if (state.isLifecycleEnabled) {
            val evolved = LifecycleMorphEngine.evolve(standard, state.ageProfile)
            updateAppearance(evolved)
        } else {
            updateAppearance(standard)
        }
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
                val json = state.currentAppearance.toAppearanceJson()

                // Save to repository (metadata field)
                val result = productRepository.updateProductMetadata(birdId, json)

                if (result is com.rio.rostry.utils.Resource.Success) {
                    _uiState.update { it.copy(isSaving = false, success = true) }
                } else {
                    _uiState.update { it.copy(isSaving = false, error = result.message ?: "Failed to save") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
