package com.rio.rostry.ui.enthusiast.lineage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TraitRecordingUiState(
    val bird: ProductEntity? = null,
    val traitRecords: List<BirdTraitRecordEntity> = emptyList(),
    val selectedCategory: String = BirdTraitRecordEntity.CATEGORY_PHYSICAL,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val recordedTraitNames: Set<String> = emptySet()
)

@HiltViewModel
class TraitRecordingViewModel @Inject constructor(
    private val traitRecordDao: BirdTraitRecordDao,
    private val productDao: ProductDao,
    private val currentUserProvider: CurrentUserProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""
    
    private val _uiState = MutableStateFlow(TraitRecordingUiState())
    val uiState: StateFlow<TraitRecordingUiState> = _uiState.asStateFlow()

    init {
        loadBirdAndTraits()
    }

    private fun loadBirdAndTraits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bird = productDao.findById(productId)
                _uiState.update { it.copy(bird = bird) }
                
                // Observe trait records reactively
                traitRecordDao.observeByBird(productId).collect { records ->
                    val recordedNames = records.map { it.traitName }.toSet()
                    _uiState.update { 
                        it.copy(
                            traitRecords = records, 
                            isLoading = false,
                            recordedTraitNames = recordedNames
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun saveTrait(
        traitName: String,
        traitCategory: String,
        traitValue: String,
        traitUnit: String?,
        ageWeeks: Int?,
        notes: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }
            try {
                val numericValue = traitValue.toDoubleOrNull()
                val record = BirdTraitRecordEntity(
                    recordId = UUID.randomUUID().toString(),
                    productId = productId,
                    ownerId = currentUserProvider.userIdOrNull() ?: "",
                    traitCategory = traitCategory,
                    traitName = traitName,
                    traitValue = traitValue,
                    traitUnit = traitUnit,
                    numericValue = numericValue,
                    ageWeeks = ageWeeks,
                    notes = notes
                )
                traitRecordDao.insert(record)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "Failed to save: ${e.message}") }
            }
        }
    }

    fun deleteTrait(recordId: String) {
        viewModelScope.launch {
            try {
                traitRecordDao.delete(recordId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete: ${e.message}") }
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        /** Returns trait definitions for a given category */
        fun getTraitsForCategory(category: String): List<TraitDefinition> = when (category) {
            BirdTraitRecordEntity.CATEGORY_PHYSICAL -> listOf(
                TraitDefinition(BirdTraitRecordEntity.TRAIT_BODY_WEIGHT, "Body Weight", "grams", TraitInputType.NUMERIC),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_SHANK_LENGTH, "Shank Length", "cm", TraitInputType.NUMERIC),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_COMB_TYPE, "Comb Type", null, TraitInputType.DROPDOWN, BirdTraitRecordEntity.COMB_TYPES),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_PLUMAGE_COLOR, "Plumage Color", null, TraitInputType.TEXT),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_PLUMAGE_PATTERN, "Plumage Pattern", null, TraitInputType.TEXT),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_EYE_COLOR, "Eye Color", null, TraitInputType.TEXT),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_LEG_COLOR, "Leg Color", null, TraitInputType.TEXT),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_WINGSPAN, "Wing Span", "cm", TraitInputType.NUMERIC)
            )
            BirdTraitRecordEntity.CATEGORY_BEHAVIORAL -> listOf(
                TraitDefinition(BirdTraitRecordEntity.TRAIT_AGGRESSION, "Aggression", "score_1_10", TraitInputType.SLIDER),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_ALERTNESS, "Alertness", "score_1_10", TraitInputType.SLIDER),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_STAMINA, "Stamina", "score_1_10", TraitInputType.SLIDER),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_BROODING_TENDENCY, "Brooding Tendency", "score_1_10", TraitInputType.SLIDER)
            )
            BirdTraitRecordEntity.CATEGORY_PRODUCTION -> listOf(
                TraitDefinition(BirdTraitRecordEntity.TRAIT_EGGS_PER_MONTH, "Eggs per Month", "count", TraitInputType.NUMERIC),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_EGG_WEIGHT, "Egg Weight", "grams", TraitInputType.NUMERIC),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_FERTILITY_RATE, "Fertility Rate", "%", TraitInputType.NUMERIC),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_HATCH_RATE, "Hatch Rate", "%", TraitInputType.NUMERIC)
            )
            BirdTraitRecordEntity.CATEGORY_QUALITY -> listOf(
                TraitDefinition(BirdTraitRecordEntity.TRAIT_CONFORMATION, "Conformation Score", "score_1_10", TraitInputType.SLIDER),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_FEATHER_QUALITY, "Feather Quality", "score_1_10", TraitInputType.SLIDER),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_BREEDER_RATING, "Breeder Rating", "score_1_10", TraitInputType.SLIDER),
                TraitDefinition(BirdTraitRecordEntity.TRAIT_SHOW_READINESS, "Show Readiness", "score_1_10", TraitInputType.SLIDER)
            )
            else -> emptyList()
        }
    }
}

data class TraitDefinition(
    val key: String,
    val label: String,
    val unit: String?,
    val inputType: TraitInputType,
    val options: List<String> = emptyList()
)

enum class TraitInputType {
    NUMERIC, TEXT, SLIDER, DROPDOWN
}
