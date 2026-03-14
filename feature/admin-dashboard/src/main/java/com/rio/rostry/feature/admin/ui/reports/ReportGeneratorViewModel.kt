package com.rio.rostry.feature.admin.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.admin.repository.ReportRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Enum representing available report types for admin dashboard.
 */
enum class ReportType(val displayName: String, val description: String) {
    USER_GROWTH("User Growth", "Track user registration and growth trends"),
    COMMERCE("Commerce", "Order and transaction summary report"),
    INVENTORY("Inventory", "Complete inventory status and stock levels"),
    FINANCIAL("Financial", "Revenue, expenses, and profit analysis"),
    ENGAGEMENT("Engagement", "User activity and engagement metrics"),
    VETERINARY("Veterinary", "Health records and veterinary activities")
}

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

    private val _reportProgress = MutableStateFlow<ReportProgress?>(null)
    val reportProgress = _reportProgress.asStateFlow()

    fun generateReport(type: ReportType) {
        viewModelScope.launch {
            _isGenerating.value = true
            _reportProgress.value = ReportProgress(type, 0, "Starting report generation...")
            try {
                Timber.d("Generating report: ${type.displayName}")
                _reportProgress.value = _reportProgress.value?.copy(status = "Fetching data...")

                val result = when (type) {
                    ReportType.USER_GROWTH -> {
                        _reportProgress.value = _reportProgress.value?.copy(progress = 30)
                        reportRepository.generateUserGrowthReport()
                    }
                    ReportType.COMMERCE -> {
                        _reportProgress.value = _reportProgress.value?.copy(progress = 30)
                        reportRepository.generateCommerceReport()
                    }
                    ReportType.INVENTORY -> {
                        _reportProgress.value = _reportProgress.value?.copy(progress = 20)
                        reportRepository.generateInventoryReport()
                    }
                    ReportType.FINANCIAL -> {
                        _reportProgress.value = _reportProgress.value?.copy(progress = 20)
                        reportRepository.generateFinancialReport()
                    }
                    ReportType.ENGAGEMENT -> {
                        _reportProgress.value = _reportProgress.value?.copy(progress = 20)
                        reportRepository.generateEngagementReport()
                    }
                    ReportType.VETERINARY -> {
                        _reportProgress.value = _reportProgress.value?.copy(progress = 20)
                        reportRepository.generateVeterinaryReport()
                    }
                }

                _reportProgress.value = _reportProgress.value?.copy(progress = 90, status = "Finalizing...")

                when (result) {
                    is com.rio.rostry.core.model.Result.Success<*> -> {
                        val file = result.data as? File
                        if (file != null) {
                            Timber.d("Report generated successfully: ${file.absolutePath}")
                            _reportProgress.value = _reportProgress.value?.copy(progress = 100, status = "Complete")
                            _generatedFile.emit(file)
                        } else {
                            _errorEvent.emit("Report file is null")
                        }
                    }
                    is com.rio.rostry.core.model.Result.Error -> {
                        Timber.e(result.exception, "Report generation failed: ${type.displayName}")
                        _errorEvent.emit(result.exception.message ?: "Failed to generate ${type.displayName} report")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during report generation: ${type.displayName}")
                _errorEvent.emit(e.message ?: "Unknown error during generation")
            } finally {
                _isGenerating.value = false
                _reportProgress.value = null
            }
        }
    }

    fun clearError() {
        viewModelScope.launch {
            _errorEvent.emit("")
        }
    }
}

/**
 * Represents the progress of report generation.
 */
data class ReportProgress(
    val reportType: ReportType,
    val progress: Int,
    val status: String
)