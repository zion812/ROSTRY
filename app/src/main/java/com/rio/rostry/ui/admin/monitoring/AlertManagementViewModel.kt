package com.rio.rostry.ui.admin.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AlertManagementViewModel @Inject constructor(
    private val alertRepository: com.rio.rostry.data.repository.AlertRepository
) : ViewModel() {

    data class SystemAlert(
        val id: String,
        val type: AlertType,
        val title: String,
        val message: String,
        val severity: AlertSeverity,
        val createdAt: Date,
        val isRead: Boolean = false,
        val isActionable: Boolean = false,
        val actionLabel: String? = null
    )

    enum class AlertType {
        SECURITY, SYSTEM, VERIFICATION, COMMERCE, BIOSECURITY, MORTALITY, UNKNOWN
    }

    enum class AlertSeverity {
        INFO, WARNING, ERROR, CRITICAL
    }

    data class UiState(
        val isLoading: Boolean = true,
        val alerts: List<SystemAlert> = emptyList(),
        val unreadCount: Int = 0,
        val filterType: AlertType? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadAlerts()
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            alertRepository.streamAlerts().collect { entities ->
                val systemAlerts = entities.map { entity ->
                    SystemAlert(
                        id = entity.id,
                        type = mapType(entity.type),
                        severity = mapSeverity(entity.severity),
                        title = entity.title,
                        message = entity.message,
                        createdAt = Date(entity.createdAt),
                        isRead = entity.isRead,
                        isActionable = entity.relatedId != null,
                        actionLabel = if (entity.relatedId != null) "View" else null
                    )
                }
                
                _state.update { currentState ->
                    val filtered = if (currentState.filterType != null) {
                        systemAlerts.filter { it.type == currentState.filterType }
                    } else {
                        systemAlerts
                    }
                    
                    currentState.copy(
                        isLoading = false,
                        alerts = filtered,
                        unreadCount = systemAlerts.count { !it.isRead }
                    )
                }
            }
        }
    }

    private fun mapType(typeStr: String): AlertType {
        return try {
            AlertType.valueOf(typeStr)
        } catch (e: Exception) {
            AlertType.UNKNOWN
        }
    }

    private fun mapSeverity(severityStr: String): AlertSeverity {
        return try {
            AlertSeverity.valueOf(severityStr)
        } catch (e: Exception) {
            AlertSeverity.INFO
        }
    }

    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            alertRepository.markAsRead(alertId)
            // State updates automatically via flow
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            alertRepository.markAllAsRead()
            _toastEvent.emit("All alerts marked as read")
        }
    }

    fun dismissAlert(alertId: String) {
        viewModelScope.launch {
            alertRepository.dismissAlert(alertId)
        }
    }

    fun setFilter(type: AlertType?) {
        _state.update { it.copy(filterType = type) }
        loadAlerts() 
    }

    fun refresh() {
        // Flow updates automatically, but we can clear error if any
    }
}
