package com.rio.rostry.ui.enthusiast.breeding.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.breeding.BreedingService
import com.rio.rostry.domain.breeding.SimulatedOffspring
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedingSimulatorViewModel @Inject constructor(
    private val breedingService: BreedingService,
    private val productRepository: ProductRepository,
    private val breedingPlanRepository: com.rio.rostry.data.repository.BreedingPlanRepository,
    private val sessionManager: com.rio.rostry.data.session.UserSessionManager,
    private val gson: com.google.gson.Gson
) : ViewModel() {

    data class UiState(
        val sire: ProductEntity? = null,
        val dam: ProductEntity? = null,
        val offspring: List<SimulatedOffspring> = emptyList(),
        val isLoading: Boolean = false,
        val isAnimating: Boolean = false,
        val error: String? = null,
        val availableSires: List<ProductEntity> = emptyList(),
        val availableDams: List<ProductEntity> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCandidates()
    }

    private fun loadCandidates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // In a real app, we might filter by species/breed or have a better picker
                // For now, load all and filter by gender in memory
                productRepository.getAllProducts().collect { resource ->
                    if (resource is Resource.Success) {
                        val allBirds = resource.data ?: emptyList()
                        val sires = allBirds.filter { it.gender.equals("male", true) }
                        val dams = allBirds.filter { it.gender.equals("female", true) }
                        _uiState.update { 
                            it.copy(
                                availableSires = sires,
                                availableDams = dams,
                                isLoading = false
                            ) 
                        }
                    } else if (resource is Resource.Error) {
                         _uiState.update { it.copy(error = resource.message, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectSire(bird: ProductEntity) {
        _uiState.update { it.copy(sire = bird, offspring = emptyList()) }
    }

    fun selectDam(bird: ProductEntity) {
        _uiState.update { it.copy(dam = bird, offspring = emptyList()) }
    }

    fun breed() {
        val sire = _uiState.value.sire
        val dam = _uiState.value.dam

        if (sire == null || dam == null) {
            _uiState.update { it.copy(error = "Select both parents first") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAnimating = true, error = null, offspring = emptyList()) }
            
            // Artificial delay for "Hatching" animation suspense
            delay(1500)

            try {
                val clutch = breedingService.simulateClutch(sire, dam, clutchSize = 3)
                _uiState.update { 
                    it.copy(
                        isAnimating = false, 
                        offspring = clutch
                    ) 
                } // Closes _uiState.update
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isAnimating = false, 
                        error = "Simulation failed: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun savePlan() {
        val currentState = _uiState.value
        val sire = currentState.sire ?: return
        val dam = currentState.dam ?: return
        val offspring = currentState.offspring
        
        if (offspring.isEmpty()) return

        viewModelScope.launch {
            val user = sessionManager.currentUser.first() ?: return@launch
            
            val plan = com.rio.rostry.data.database.entity.BreedingPlanEntity(
                farmerId = user.uid,
                sireId = sire.productId,
                sireName = sire.name,
                damId = dam.productId,
                damName = dam.name,
                simulatedOffspringJson = gson.toJson(offspring),
                status = "PLANNED",
                priority = 1
            )
            
            val result = breedingPlanRepository.savePlan(plan)
            if (result is com.rio.rostry.utils.Resource.Success) {
                // Ideally show success message, for now just log or no-op
                // You might want to add a 'planSaved' event to UiState
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
