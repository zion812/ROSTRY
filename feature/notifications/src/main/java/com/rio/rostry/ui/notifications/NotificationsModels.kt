package com.rio.rostry.ui.notifications
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

data class NotificationsUiState(
    val unreadMessages: Int = 0,
    val pendingOrders: Int = 0,
    val items: List<NotificationItem> = emptyList()
) {
    val total: Int get() = unreadMessages + pendingOrders
}

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val route: String,
    val type: String,
    val timestamp: Long
)
