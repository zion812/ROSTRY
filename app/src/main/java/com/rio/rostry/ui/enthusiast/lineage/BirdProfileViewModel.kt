package com.rio.rostry.ui.enthusiast.lineage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.dao.MedicalEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import com.rio.rostry.data.database.entity.MedicalEventEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.service.BreedingValueResult
import com.rio.rostry.domain.service.BreedingValueService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BirdProfileUiState(
    val bird: ProductEntity? = null,
    val sire: ProductEntity? = null,
    val dam: ProductEntity? = null,
    val offspring: List<ProductEntity> = emptyList(),
    val traitRecords: List<BirdTraitRecordEntity> = emptyList(),
    val healthEvents: List<MedicalEventEntity> = emptyList(),
    val traitCompleteness: Float = 0f,
    val bvi: BreedingValueResult? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BirdProfileViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val traitRecordDao: BirdTraitRecordDao,
    private val medicalEventDao: MedicalEventDao,
    private val breedingValueService: BreedingValueService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""
    
    private val _uiState = MutableStateFlow(BirdProfileUiState())
    val uiState: StateFlow<BirdProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Load bird
                val bird = productDao.findById(productId)
                if (bird == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Bird not found") }
                    return@launch
                }

                // Load parents
                val sire = bird.parentMaleId?.let { productDao.findById(it) }
                val dam = bird.parentFemaleId?.let { productDao.findById(it) }

                // Load offspring (birds where this bird is a parent)
                val offspring = productDao.getOffspring(productId)

                _uiState.update { 
                    it.copy(
                        bird = bird,
                        sire = sire,
                        dam = dam,
                        offspring = offspring
                    ) 
                }

                // Load trait records reactively
                launch {
                    traitRecordDao.observeByBird(productId).collect { records ->
                        val completeness = calculateTraitCompleteness(records)
                        _uiState.update { 
                            it.copy(traitRecords = records, traitCompleteness = completeness, isLoading = false) 
                        }
                    }
                }

                // Load health events reactively
                launch {
                    medicalEventDao.observeByBird(productId).collect { events ->
                        _uiState.update { it.copy(healthEvents = events) }
                    }
                }

                // Load BVI in background
                launch {
                    try {
                        val bviResult = breedingValueService.calculateBVI(productId)
                        _uiState.update { it.copy(bvi = bviResult) }
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun calculateTraitCompleteness(records: List<BirdTraitRecordEntity>): Float {
        // Total possible traits = sum of all categories
        val totalTraits = listOf(
            BirdTraitRecordEntity.CATEGORY_PHYSICAL,
            BirdTraitRecordEntity.CATEGORY_BEHAVIORAL,
            BirdTraitRecordEntity.CATEGORY_PRODUCTION,
            BirdTraitRecordEntity.CATEGORY_QUALITY
        ).sumOf { cat -> TraitRecordingViewModel.getTraitsForCategory(cat).size }
        
        if (totalTraits == 0) return 0f
        val recordedTraits = records.map { it.traitName }.distinct().size
        return (recordedTraits.toFloat() / totalTraits).coerceIn(0f, 1f)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
