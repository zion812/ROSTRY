package com.rio.rostry.ui.admin.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ReportRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReportGeneratorViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _generatedFile = MutableSharedFlow<File?>()
    val generatedFile = _generatedFile.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun generateReport(type: ReportType) {
        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val result = when (type) {
                    ReportType.USER_GROWTH -> reportRepository.generateUserGrowthReport()
                    ReportType.COMMERCE -> reportRepository.generateCommerceReport()
                    else -> Resource.Error("Report type not implemented yet")
                }

                if (result is Resource.Success && result.data != null) {
                    _generatedFile.emit(result.data)
                } else {
                    _errorEvent.emit(result.message ?: "Failed to generate report")
                }
            } catch (e: Exception) {
                _errorEvent.emit(e.message ?: "Unknown error during generation")
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
