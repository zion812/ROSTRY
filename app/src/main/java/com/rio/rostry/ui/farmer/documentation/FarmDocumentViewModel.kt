package com.rio.rostry.ui.farmer.documentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.AssetSummary
import com.rio.rostry.data.repository.FarmDocumentation
import com.rio.rostry.data.repository.FarmDocumentationService
import com.rio.rostry.utils.export.FarmDocumentPdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FarmDocumentUiState(
    val isLoading: Boolean = true,
    val documentation: FarmDocumentation? = null,
    val assetSummaries: List<AssetSummary> = emptyList(),
    val exportedFileName: String? = null,
    val exportError: String? = null,
    val isExporting: Boolean = false
)

@HiltViewModel
class FarmDocumentViewModel @Inject constructor(
    private val documentationService: FarmDocumentationService,
    private val pdfGenerator: FarmDocumentPdfGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmDocumentUiState())
    val uiState: StateFlow<FarmDocumentUiState> = _uiState.asStateFlow()

    init {
        loadDocumentation()
    }

    private fun loadDocumentation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val documentation = documentationService.loadFarmDocumentation()
            val summaries = documentationService.generateAssetSummaries()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                documentation = documentation,
                assetSummaries = summaries
            )
        }
    }

    fun exportToPdf() {
        val doc = _uiState.value.documentation ?: return
        val summaries = _uiState.value.assetSummaries
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportError = null)
            
            try {
                val fileName = pdfGenerator.generatePdf(doc, summaries)
                
                if (fileName != null) {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportedFileName = fileName
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportError = "Failed to generate PDF"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportError = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun clearExportState() {
        _uiState.value = _uiState.value.copy(
            exportedFileName = null,
            exportError = null
        )
    }
}
