package com.rio.rostry.ui.notifications

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
