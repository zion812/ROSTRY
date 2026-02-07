package com.rio.rostry.ui.enthusiast.pedigree.export

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedigreeExportViewModel @Inject constructor(
    private val pedigreeRepository: PedigreeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val birdId: String = savedStateHandle.get<String>("birdId") ?: ""

    data class UiState(
        val isLoading: Boolean = false,
        val pedigreeTree: PedigreeTree? = null,
        val showBreederInfo: Boolean = true,
        val showDate: Boolean = true,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadPedigree()
    }

    private fun loadPedigree() {
        if (birdId.isBlank()) {
            _state.update { it.copy(isLoading = false, error = "Invalid Bird ID") }
            return
        }

        viewModelScope.launch {
            when (val result = pedigreeRepository.getFullPedigree(birdId, 3)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, pedigreeTree = result.data) }
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

    fun toggleBreederInfo(show: Boolean) {
        _state.update { it.copy(showBreederInfo = show) }
    }

    fun toggleDate(show: Boolean) {
        _state.update { it.copy(showDate = show) }
    }
}
