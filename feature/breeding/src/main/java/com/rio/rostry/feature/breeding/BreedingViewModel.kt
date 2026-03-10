package com.rio.rostry.feature.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BreedingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BreedingUiState())
    val uiState: StateFlow<BreedingUiState> = _uiState.asStateFlow()

    fun loadBreedingData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(isLoading = false)
    }
}

data class BreedingUiState(
    val isLoading: Boolean = false,
    val breedingPairs: Int = 0,
    val activeBreeding: Int = 0
)
