package com.rio.rostry.ui.farmer.documentation

import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.AssetDocumentation
import com.rio.rostry.data.repository.AssetDocumentationService
import com.rio.rostry.data.repository.LifecycleTimelineEntry
import com.rio.rostry.utils.export.AssetDocumentPdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetDocumentUiState(
    val isLoading: Boolean = true,
    val documentation: AssetDocumentation? = null,
    val timeline: List<LifecycleTimelineEntry> = emptyList(),
    val exportedFileName: String? = null,
    val exportError: String? = null,
    val isExporting: Boolean = false
)

@HiltViewModel
class AssetDocumentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val documentationService: AssetDocumentationService,
    private val pdfGenerator: AssetDocumentPdfGenerator
) : ViewModel() {

    private val assetId: String = savedStateHandle.get<String>("assetId") ?: ""
    
    private val _uiState = MutableStateFlow(AssetDocumentUiState())
    val uiState: StateFlow<AssetDocumentUiState> = _uiState.asStateFlow()

    init {
        loadDocumentation()
    }

    private fun loadDocumentation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val documentation = documentationService.loadDocumentation(assetId)
            val timeline = documentationService.generateTimeline(assetId)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                documentation = documentation,
                timeline = timeline
            )
        }
    }

    fun exportToPdf() {
        val doc = _uiState.value.documentation ?: return
        val timeline = _uiState.value.timeline
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportError = null)
            
            try {
                val fileName = pdfGenerator.generatePdf(doc, timeline)
                
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
