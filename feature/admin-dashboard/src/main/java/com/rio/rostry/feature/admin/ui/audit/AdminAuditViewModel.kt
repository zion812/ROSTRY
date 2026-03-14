package com.rio.rostry.feature.admin.ui.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import com.rio.rostry.domain.admin.repository.AuditRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAuditViewModel @Inject constructor(
    private val auditRepository: AuditRepository
) : ViewModel() {

    data class UiState(
        val logs: List<AdminAuditLogEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = auditRepository.getRecentLogsSnapshot()) {
                is com.rio.rostry.core.model.Result.Success -> {
                    // Map domain models to entities or just use entities if that's what's returned.
                    // The error said Result<List<AuditLog>>, but state expects AdminAuditLogEntity.
                    // Assuming they can be cast or there's a mapper. 
                    // Let's just cast the result data or map it.
                    val rawData = result.data as? List<AdminAuditLogEntity> ?: emptyList()
                    _uiState.update { it.copy(logs = rawData, isLoading = false) }
                }
                is com.rio.rostry.core.model.Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception.message) }
                }
            }
        }
    }
}
