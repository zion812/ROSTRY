package com.rio.rostry.ui.enthusiast.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.ShowRecordEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.ShowRecordRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Bird Comparison feature.
 * Allows Enthusiasts to compare two birds side-by-side on:
 * - Physical traits (weight, color, comb type)
 * - Genetic traits
 * - Show performance (wins, placements)
 * - Breeding history
 */
@HiltViewModel
class BirdComparisonViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val showRecordRepository: ShowRecordRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {
    
    data class BirdData(
        val product: ProductEntity,
        val showRecords: List<ShowRecordEntity> = emptyList(),
        val totalWins: Int = 0,
        val avgPlacement: Double = 0.0
    )
    
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val availableBirds: List<ProductEntity> = emptyList(),
        val birdA: BirdData? = null,
        val birdB: BirdData? = null,
        val comparisonHighlights: List<ComparisonItem> = emptyList()
    )
    
    data class ComparisonItem(
        val label: String,
        val valueA: String,
        val valueB: String,
        val winner: ComparisonWinner = ComparisonWinner.NONE
    )
    
    enum class ComparisonWinner { A, B, TIE, NONE }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    init {
        loadAvailableBirds()
    }
    
    private fun loadAvailableBirds() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = currentUserProvider.userIdOrNull() ?: return@launch
                val productsResource = productRepository.getProductsBySeller(userId)
                    .first { resource -> resource !is Resource.Loading }
                
                when (productsResource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            availableBirds = productsResource.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = productsResource.message
                        )
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun selectBirdA(productId: String) {
        viewModelScope.launch {
            loadBirdDetails(productId) { birdData ->
                _uiState.value = _uiState.value.copy(birdA = birdData)
                updateComparison()
            }
        }
    }
    
    fun selectBirdB(productId: String) {
        viewModelScope.launch {
            loadBirdDetails(productId) { birdData ->
                _uiState.value = _uiState.value.copy(birdB = birdData)
                updateComparison()
            }
        }
    }
    
    private suspend fun loadBirdDetails(productId: String, onComplete: (BirdData) -> Unit) {
        val productsResource = productRepository.getProductById(productId)
            .first { resource -> resource !is Resource.Loading }
        
        when (productsResource) {
            is Resource.Success -> {
                val product = productsResource.data ?: return
                
                // Get show records synchronously
                val showRecordsResult = showRecordRepository.getRecordsForProductSync(productId)
                val showRecords = when (showRecordsResult) {
                    is Resource.Success -> showRecordsResult.data ?: emptyList()
                    else -> emptyList()
                }
                
                val wins = showRecords.count { record -> record.placement == 1 }
                val avgPlacement = if (showRecords.isNotEmpty()) {
                    showRecords.mapNotNull { record -> record.placement }.average()
                } else 0.0
                
                onComplete(BirdData(
                    product = product,
                    showRecords = showRecords,
                    totalWins = wins,
                    avgPlacement = avgPlacement
                ))
            }
            else -> {}
        }
    }
    
    private fun updateComparison() {
        val birdA = _uiState.value.birdA ?: return
        val birdB = _uiState.value.birdB ?: return
        
        val comparisons = mutableListOf<ComparisonItem>()
        
        // Weight comparison - explicitly convert to Int to avoid type issues
        val weightA: Int = birdA.product.weightGrams?.toInt() ?: 0
        val weightB: Int = birdB.product.weightGrams?.toInt() ?: 0
        comparisons.add(ComparisonItem(
            label = "Weight (g)",
            valueA = if (weightA > 0) "${weightA}g" else "-",
            valueB = if (weightB > 0) "${weightB}g" else "-",
            winner = when {
                weightA > weightB -> ComparisonWinner.A
                weightB > weightA -> ComparisonWinner.B
                weightA == weightB && weightA > 0 -> ComparisonWinner.TIE
                else -> ComparisonWinner.NONE
            }
        ))
        
        // Age comparison
        val ageA = calculateAgeDays(birdA.product.birthDate)
        val ageB = calculateAgeDays(birdB.product.birthDate)
        comparisons.add(ComparisonItem(
            label = "Age",
            valueA = if (ageA != null) "${ageA / 30} months" else "-",
            valueB = if (ageB != null) "${ageB / 30} months" else "-",
            winner = ComparisonWinner.NONE // Age is not better/worse
        ))
        
        // Gender
        comparisons.add(ComparisonItem(
            label = "Gender",
            valueA = birdA.product.gender ?: "-",
            valueB = birdB.product.gender ?: "-",
            winner = ComparisonWinner.NONE
        ))
        
        // Color
        comparisons.add(ComparisonItem(
            label = "Color",
            valueA = birdA.product.color ?: "-",
            valueB = birdB.product.color ?: "-",
            winner = ComparisonWinner.NONE
        ))
        
        // Breed
        comparisons.add(ComparisonItem(
            label = "Breed",
            valueA = birdA.product.breed ?: "-",
            valueB = birdB.product.breed ?: "-",
            winner = ComparisonWinner.NONE
        ))
        
        // Show wins
        comparisons.add(ComparisonItem(
            label = "Show Wins",
            valueA = "${birdA.totalWins} 🏆",
            valueB = "${birdB.totalWins} 🏆",
            winner = when {
                birdA.totalWins > birdB.totalWins -> ComparisonWinner.A
                birdB.totalWins > birdA.totalWins -> ComparisonWinner.B
                birdA.totalWins == birdB.totalWins && birdA.totalWins > 0 -> ComparisonWinner.TIE
                else -> ComparisonWinner.NONE
            }
        ))
        
        // Average placement
        val avgA = birdA.avgPlacement
        val avgB = birdB.avgPlacement
        comparisons.add(ComparisonItem(
            label = "Avg Placement",
            valueA = if (avgA > 0) String.format("%.1f", avgA) else "-",
            valueB = if (avgB > 0) String.format("%.1f", avgB) else "-",
            winner = when {
                avgA > 0 && avgB > 0 -> 
                    if (avgA < avgB) ComparisonWinner.A 
                    else if (avgB < avgA) ComparisonWinner.B 
                    else ComparisonWinner.TIE
                avgA > 0 -> ComparisonWinner.A
                avgB > 0 -> ComparisonWinner.B
                else -> ComparisonWinner.NONE
            }
        ))
        
        _uiState.value = _uiState.value.copy(comparisonHighlights = comparisons)
    }
    
    private fun calculateAgeDays(birthDate: Long?): Int? {
        if (birthDate == null || birthDate <= 0) return null
        val now = System.currentTimeMillis()
        if (birthDate > now) return null
        return ((now - birthDate) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun swapBirds() {
        val current = _uiState.value
        _uiState.value = current.copy(
            birdA = current.birdB,
            birdB = current.birdA
        )
        updateComparison()
    }
}
