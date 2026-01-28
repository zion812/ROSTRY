package com.rio.rostry.ui.enthusiast.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.breeding.BreedingCompatibilityCalculator
import com.rio.rostry.domain.model.BreedingPrediction
import com.rio.rostry.domain.usecase.CalculateOffspringStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedingCalculatorViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val calculateOffspringStats: CalculateOffspringStatsUseCase,
    private val compatibilityCalculator: BreedingCompatibilityCalculator
) : ViewModel() {

    // Selecting Sire and Dam from locally available birds
    // We only care about user's own birds or maybe favorited ones? For now, user's own "Active" birds.
    val myBirds = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.rio.rostry.utils.Resource.Loading())

    private val _selectedSireId = MutableStateFlow<String?>(null)
    val selectedSireId = _selectedSireId.asStateFlow()

    private val _selectedDamId = MutableStateFlow<String?>(null)
    val selectedDamId = _selectedDamId.asStateFlow()

    private val _prediction = MutableStateFlow<BreedingPrediction?>(null)
    val prediction = _prediction.asStateFlow()

    private val _compatibility = MutableStateFlow<BreedingCompatibilityCalculator.CompatibilityResult?>(null)
    val compatibility = _compatibility.asStateFlow()
    
    // Combining selection and birds to get ProductEntity objects
    val selectionState = combine(selectedSireId, selectedDamId, myBirds) { sireId, damId, birdsResource ->
        if (birdsResource is com.rio.rostry.utils.Resource.Success) {
            val birds = birdsResource.data ?: emptyList()
            val sire = birds.find { it.productId == sireId }
            val dam = birds.find { it.productId == damId }
            Triple(sire, dam, birds)
        } else {
            Triple(null, null, emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(null, null, emptyList()))

    fun selectSire(id: String) {
        _selectedSireId.value = id
        calculateIfReady()
    }

    fun selectDam(id: String) {
        _selectedDamId.value = id
        calculateIfReady()
    }

    private fun calculateIfReady() {
        // Need to collect current state from flows eagerly or wait? 
        // selectionState is derived, so we can launch and collect logic
        viewModelScope.launch {
            // Give time for derived state to update? 
            // Better to re-fetch from ID or just rely on state collection
            val currentState = selectionState.value
            val sire = currentState.first ?: return@launch
            val dam = currentState.second ?: return@launch
            
            // Double check IDs match (state consistency)
            if (sire.productId != _selectedSireId.value || dam.productId != _selectedDamId.value) return@launch

            _prediction.value = calculateOffspringStats(sire, dam)
            _compatibility.value = compatibilityCalculator.calculateCompatibility(sire, dam)
        }
    }
}
