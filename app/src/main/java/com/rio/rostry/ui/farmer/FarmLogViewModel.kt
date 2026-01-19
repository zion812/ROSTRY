package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import com.rio.rostry.data.repository.FarmActivityLogRepository
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for the Farm Log screen.
 * Provides access to all farm activity logs with filtering capabilities.
 */
@HiltViewModel
class FarmLogViewModel @Inject constructor(
    private val repository: FarmActivityLogRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class DailySummary(
        val feedKg: Double = 0.0,
        val expenseInr: Double = 0.0,
        val mortalityCount: Int = 0
    )

    data class UiState(
        val logs: List<FarmActivityLogEntity> = emptyList(),
        val filteredLogs: List<FarmActivityLogEntity> = emptyList(),
        val groupedLogs: Map<String, List<FarmActivityLogEntity>> = emptyMap(),
        val dailySummaries: Map<String, DailySummary> = emptyMap(),
        val selectedType: String? = null, // null = all types
        val isLoading: Boolean = true,
        val totalExpenses: Double = 0.0
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            
            combine(
                repository.observeForFarmer(userId),
                _selectedType
            ) { logs, type ->
                val filtered = if (type == null) logs else logs.filter { it.activityType == type }
                val totalExpenses = logs
                    .filter { it.activityType == "EXPENSE" }
                    .sumOf { it.amountInr ?: 0.0 }
                
                // Grouping Logic
                val grouped = filtered.groupBy { log ->
                    val date = java.time.Instant.ofEpochMilli(log.createdAt)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    val today = java.time.LocalDate.now()
                    when (date) {
                        today -> "Today"
                        today.minusDays(1) -> "Yesterday"
                        else -> java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy").format(date)
                    }
                }
                
                // Daily Summaries Logic
                val summaries = grouped.mapValues { (_, groupLogs) ->
                    DailySummary(
                        feedKg = groupLogs.filter { it.activityType == "FEED" }.sumOf { it.quantity ?: 0.0 },
                        expenseInr = groupLogs.filter { it.activityType == "EXPENSE" }.sumOf { it.amountInr ?: 0.0 },
                        mortalityCount = groupLogs.filter { it.activityType == "MORTALITY" }.sumOf { (it.quantity ?: 0.0).toInt() }
                    )
                }
                
                UiState(
                    logs = logs,
                    filteredLogs = filtered,
                    groupedLogs = grouped,
                    dailySummaries = summaries,
                    selectedType = type,
                    isLoading = false,
                    totalExpenses = totalExpenses
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(type: String?) {
        _selectedType.value = type
    }

    fun getActivityTypes(): List<String> = listOf(
        "MORTALITY", "FEED", "EXPENSE", "WEIGHT", 
        "VACCINATION", "DEWORMING", "MEDICATION", 
        "SANITATION", "MAINTENANCE", "OTHER"
    )

    companion object {
        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
