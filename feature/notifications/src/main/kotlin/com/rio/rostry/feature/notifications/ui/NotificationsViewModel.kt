package com.rio.rostry.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val showRecordRepository: ShowRecordRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _ui = MutableStateFlow(
        NotificationsUiState(
            unreadMessages = 0,
            pendingOrders = 0,
            items = emptyList()
        )
    )
    val ui: StateFlow<NotificationsUiState> = _ui

    init {
        loadNotifications()
    }

    fun refresh() {
        viewModelScope.launch {
            loadNotifications()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val userId = currentUserProvider.userIdOrNull()

            if (userId == null) {
                _ui.value = _ui.value.copy(isLoading = false)
                return@launch
            }

            try {
                // Load pending orders count
                val ordersResult = showRecordRepository.getPendingOrders(userId)
                val pendingOrders = when (ordersResult) {
                    is Resource.Success -> ordersResult.data?.size ?: 0
                    else -> 0
                }

                // Load unread messages count (placeholder - would need MessagingRepository)
                val unreadMessages = 0 // Would come from messaging repository

                // Build notification items based on actual data
                val items = mutableListOf<NotificationItem>()

                if (pendingOrders > 0) {
                    items.add(
                        NotificationItem(
                            id = "orders-pending",
                            title = "Pending orders",
                            body = "$pendingOrders order${if (pendingOrders > 1) "s" else ""} need${if (pendingOrders == 1) "s" else ""} your attention",
                            route = "orders",
                            type = "order",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                if (unreadMessages > 0) {
                    items.add(
                        NotificationItem(
                            id = "messages-unread",
                            title = "Unread messages",
                            body = "You have $unreadMessages unread message${if (unreadMessages > 1) "s" else ""}",
                            route = "messages/outbox",
                            type = "message",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                _ui.value = _ui.value.copy(
                    unreadMessages = unreadMessages,
                    pendingOrders = pendingOrders,
                    items = items,
                    isLoading = false
                )
                Timber.d("Notifications loaded: $pendingOrders pending orders, $unreadMessages unread messages")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load notifications")
                _ui.value = _ui.value.copy(isLoading = false, error = errorHandler.handleError(e))
            }
        }
    }
}
