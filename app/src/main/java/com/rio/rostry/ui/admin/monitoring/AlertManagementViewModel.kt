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
class AlertManagementViewModel @Inject constructor() : ViewModel() {

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
        SECURITY, SYSTEM, VERIFICATION, COMMERCE, BIOSECURITY, MORTALITY
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
            _state.update { it.copy(isLoading = true) }
            
            // Simulate loading alerts - in production this would come from a repository
            delay(500)
            
            val mockAlerts = listOf(
                SystemAlert(
                    id = "1",
                    type = AlertType.BIOSECURITY,
                    title = "New Disease Zone Created",
                    message = "A new lockdown zone has been created in Karnataka",
                    severity = AlertSeverity.WARNING,
                    createdAt = Date(System.currentTimeMillis() - 3600000),
                    isRead = false
                ),
                SystemAlert(
                    id = "2",
                    type = AlertType.VERIFICATION,
                    title = "Pending Verifications",
                    message = "5 new KYC verifications awaiting review",
                    severity = AlertSeverity.INFO,
                    createdAt = Date(System.currentTimeMillis() - 7200000),
                    isRead = false,
                    isActionable = true,
                    actionLabel = "Review"
                ),
                SystemAlert(
                    id = "3",
                    type = AlertType.MORTALITY,
                    title = "Elevated Mortality Rate",
                    message = "Maharashtra region showing 12% mortality rate",
                    severity = AlertSeverity.ERROR,
                    createdAt = Date(System.currentTimeMillis() - 14400000),
                    isRead = true
                ),
                SystemAlert(
                    id = "4",
                    type = AlertType.COMMERCE,
                    title = "Disputed Order",
                    message = "Order #ORD-2456 has an active dispute",
                    severity = AlertSeverity.WARNING,
                    createdAt = Date(System.currentTimeMillis() - 86400000),
                    isRead = true,
                    isActionable = true,
                    actionLabel = "View"
                )
            )
            
            _state.update { it.copy(
                isLoading = false,
                alerts = mockAlerts,
                unreadCount = mockAlerts.count { !it.isRead }
            ) }
        }
    }

    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            _state.update { state ->
                val updatedAlerts = state.alerts.map { alert ->
                    if (alert.id == alertId) alert.copy(isRead = true) else alert
                }
                state.copy(
                    alerts = updatedAlerts,
                    unreadCount = updatedAlerts.count { !it.isRead }
                )
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    alerts = state.alerts.map { it.copy(isRead = true) },
                    unreadCount = 0
                )
            }
            _toastEvent.emit("All alerts marked as read")
        }
    }

    fun dismissAlert(alertId: String) {
        viewModelScope.launch {
            _state.update { state ->
                val updatedAlerts = state.alerts.filter { it.id != alertId }
                state.copy(
                    alerts = updatedAlerts,
                    unreadCount = updatedAlerts.count { !it.isRead }
                )
            }
        }
    }

    fun setFilter(type: AlertType?) {
        _state.update { it.copy(filterType = type) }
    }

    fun refresh() {
        loadAlerts()
    }
}
