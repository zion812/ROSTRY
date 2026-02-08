package com.rio.rostry.ui.enthusiast.pedigree.export

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.export.PedigreeImageGenerator
import com.rio.rostry.utils.export.PedigreePdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedigreeExportViewModel @Inject constructor(
    private val pedigreeRepository: PedigreeRepository,
    private val pdfGenerator: PedigreePdfGenerator,
    private val imageGenerator: PedigreeImageGenerator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val birdId: String = savedStateHandle.get<String>("birdId") ?: ""

    data class UiState(
        val isLoading: Boolean = false,
        val pedigreeTree: PedigreeTree? = null,
        val showBreederInfo: Boolean = true,
        val showDate: Boolean = true,
        val exportMessage: String? = null,
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

    fun exportAsPdf() {
        val currentTree = _state.value.pedigreeTree ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = pdfGenerator.generateAndSavePdf(currentTree.bird.name, currentTree)
            if (result != null) {
                _state.update { it.copy(isLoading = false, exportMessage = "PDF saved to Downloads: $result") }
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to generate PDF") }
            }
        }
    }

    fun exportAsImage() {
        val currentTree = _state.value.pedigreeTree ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = imageGenerator.generateAndSaveImage(currentTree.bird.name, currentTree)
            if (result != null) {
                _state.update { it.copy(isLoading = false, exportMessage = "Image saved to Gallery: $result") }
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to generate Image") }
            }
        }
    }
    
    fun clearMessage() {
        _state.update { it.copy(exportMessage = null, error = null) }
    }
}

