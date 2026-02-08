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
    private val exportManager: com.rio.rostry.utils.export.AssetExportManager
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

    fun exportAsZip() {
        val doc = _uiState.value.documentation ?: return
        val timeline = _uiState.value.timeline
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportError = null)
            
            try {
                // Gather all media
                val mediaItems = documentationService.getAssetMedia(assetId)
                
                val result = exportManager.exportAssetPackage(doc, timeline, mediaItems)
                
                when (result) {
                    is com.rio.rostry.utils.export.AssetExportManager.ExportResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isExporting = false,
                            exportedFileName = result.fileName
                        )
                    }
                    is com.rio.rostry.utils.export.AssetExportManager.ExportResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isExporting = false,
                            exportError = result.message
                        )
                    }
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
