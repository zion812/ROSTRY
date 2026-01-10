package com.rio.rostry.ui.analytics

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ReportGenerationRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for Monthly Report generation.
 */
@HiltViewModel
class MonthlyReportViewModel @Inject constructor(
    private val reportRepository: ReportGenerationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState = _reportState.asStateFlow()
    
    // Selected month/year
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val selectedMonth = _selectedMonth.asStateFlow()
    
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear = _selectedYear.asStateFlow()
    
    // Generated report URI
    private val _reportUri = MutableStateFlow<Uri?>(null)
    val reportUri = _reportUri.asStateFlow()
    
    // Available months for selection
    val availableMonths = (1..12).toList()
    
    // Available years (current year and 2 previous years)
    val availableYears: List<Int>
        get() {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            return listOf(currentYear, currentYear - 1, currentYear - 2)
        }
    
    /**
     * Update selected month.
     */
    fun selectMonth(month: Int) {
        _selectedMonth.value = month
    }
    
    /**
     * Update selected year.
     */
    fun selectYear(year: Int) {
        _selectedYear.value = year
    }
    
    /**
     * Generate report for selected month/year.
     */
    fun generateReport() {
        viewModelScope.launch {
            _reportState.value = ReportState.Generating
            
            // Get current user
            val userResult = userRepository.getCurrentUser().first()
            if (userResult !is Resource.Success || userResult.data == null) {
                _reportState.value = ReportState.Error("User not found")
                return@launch
            }
            
            val user = userResult.data
            
            when (val result = reportRepository.generateMonthlyReport(
                farmerId = user.userId,
                farmerName = user.fullName ?: "Farmer",
                month = _selectedMonth.value,
                year = _selectedYear.value
            )) {
                is Resource.Success -> {
                    result.data?.let { uri ->
                        _reportUri.value = uri
                        _reportState.value = ReportState.Ready(uri)
                    } ?: run {
                        _reportState.value = ReportState.Error("Report generated but URI is null")
                    }
                }
                is Resource.Error -> {
                    _reportState.value = ReportState.Error(result.message ?: "Failed to generate report")
                }
                is Resource.Loading -> {
                    _reportState.value = ReportState.Generating
                }
            }
        }
    }
    
    /**
     * Reset state.
     */
    fun resetState() {
        _reportState.value = ReportState.Idle
        _reportUri.value = null
    }
    
    /**
     * Get month display name.
     */
    fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }
    }
}

sealed class ReportState {
    data object Idle : ReportState()
    data object Generating : ReportState()
    data class Ready(val uri: Uri) : ReportState()
    data class Error(val message: String) : ReportState()
}
