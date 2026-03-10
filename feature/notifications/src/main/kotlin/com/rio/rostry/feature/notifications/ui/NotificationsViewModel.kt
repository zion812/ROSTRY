package com.rio.rostry.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    private val _ui = MutableStateFlow(
        NotificationsUiState(
            unreadMessages = 2,
            pendingOrders = 1,
            items = listOf(
                NotificationItem(
                    id = "orders-pending",
                    title = "Pending orders",
                    body = "1 order needs your attention",
                    route = "orders",
                    type = "order",
                    timestamp = System.currentTimeMillis()
                ),
                NotificationItem(
                    id = "messages-unread",
                    title = "Unread messages",
                    body = "You have 2 unread messages",
                    route = "messages/outbox",
                    type = "message",
                    timestamp = System.currentTimeMillis()
                )
            )
        )
    )
    val ui: StateFlow<NotificationsUiState> = _ui

    fun refresh() {
        viewModelScope.launch {
            // Placeholder refresh while repository wiring is migrated.
            _ui.emit(_ui.value.copy())
        }
    }
}
