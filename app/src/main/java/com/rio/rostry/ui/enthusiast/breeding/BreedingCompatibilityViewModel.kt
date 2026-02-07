package com.rio.rostry.ui.enthusiast.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.BreedingService
import com.rio.rostry.domain.breeding.CompatibilityResult
import com.rio.rostry.domain.breeding.TraitPrediction
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedingCompatibilityViewModel @Inject constructor(
    private val breedingService: BreedingService,
    private val pedigreeRepository: PedigreeRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val availableSires: List<ProductEntity> = emptyList(),
        val availableDams: List<ProductEntity> = emptyList(),
        val selectedSire: ProductEntity? = null,
        val selectedDam: ProductEntity? = null,
        val compatibility: CompatibilityResult? = null,
        val prediction: TraitPrediction? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadPotentialParents()
    }

    private fun loadPotentialParents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = currentUserProvider.userIdOrNull() ?: return@launch

            // Fetch Sires (Males)
            val siresResult = pedigreeRepository.getPotentialParents(userId, "", "male")
            // Fetch Dams (Females)
            val damsResult = pedigreeRepository.getPotentialParents(userId, "", "female")

            if (siresResult is Resource.Success && damsResult is Resource.Success) {
                _state.update { it.copy(
                    isLoading = false,
                    availableSires = siresResult.data ?: emptyList(),
                    availableDams = damsResult.data ?: emptyList()
                ) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to load potential parents") }
            }
        }
    }

    fun selectSire(sire: ProductEntity) {
        _state.update { it.copy(selectedSire = sire) }
        calculateIfReady()
    }

    fun selectDam(dam: ProductEntity) {
        _state.update { it.copy(selectedDam = dam) }
        calculateIfReady()
    }

    private fun calculateIfReady() {
        val sire = _state.value.selectedSire
        val dam = _state.value.selectedDam

        if (sire != null && dam != null) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, compatibility = null, prediction = null) }
                
                // Calculate Compatibility
                breedingService.calculateCompatibility(sire, dam).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val prediction = breedingService.predictOffspring(sire, dam)
                            _state.update { it.copy(
                                isLoading = false,
                                compatibility = result.data,
                                prediction = prediction
                            ) }
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(isLoading = false, error = result.message) }
                        }
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }
    
    fun clearSelection() {
        _state.update { it.copy(selectedSire = null, selectedDam = null, compatibility = null, prediction = null) }
    }
}
