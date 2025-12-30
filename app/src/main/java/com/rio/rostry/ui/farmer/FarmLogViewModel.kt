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

    data class UiState(
        val logs: List<FarmActivityLogEntity> = emptyList(),
        val filteredLogs: List<FarmActivityLogEntity> = emptyList(),
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
                
                UiState(
                    logs = logs,
                    filteredLogs = filtered,
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
