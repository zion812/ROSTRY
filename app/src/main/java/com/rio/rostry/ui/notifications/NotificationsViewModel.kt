package com.rio.rostry.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val messagingRepository: MessagingRepository,
) : ViewModel() {

    data class UiState(
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
        val type: String, // order, message, system
        val timestamp: Long,
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun refresh() {
        viewModelScope.launch {
            // Get current user (seller)
            val current = when (val res = userRepository.getCurrentUser().first()) {
                is Resource.Success -> res.data
                else -> null
            }
            val sellerId = current?.userId
            val pending = if (sellerId != null) {
                val orders = orderRepository.getOrdersBySeller(sellerId).first()
                orders.count { it.status == "pending_payment" || it.status == "processing" }
            } else 0
            fun buildItems(unread: Int, pendingCount: Int, previews: List<NotificationItem>) = buildList {
                if (pending > 0) {
                    add(
                        NotificationItem(
                            id = "orders-pending",
                            title = "Pending orders",
                            body = "$pendingCount orders need your attention",
                            route = com.rio.rostry.ui.navigation.Routes.TRANSFER_LIST,
                            type = "order",
                            timestamp = System.currentTimeMillis(),
                        )
                    )
                }
                if (unread > 0) {
                    add(
                        NotificationItem(
                            id = "messages-unread",
                            title = "Unread messages",
                            body = "You have $unread unread messages",
                            route = com.rio.rostry.ui.navigation.Routes.MESSAGES_OUTBOX,
                            type = "message",
                            timestamp = System.currentTimeMillis(),
                        )
                    )
                }
                addAll(previews)
            }
            // Build initial previews (best-effort)
            val userId = current?.userId
            var previews: List<NotificationItem> = emptyList()
            if (userId != null) {
                // Take a snapshot of thread IDs and build simple previews from last message
                val threadIds = messagingRepository.streamUserThreads(userId).first()
                previews = threadIds.take(5).mapNotNull { tid ->
                    val msgs = messagingRepository.streamThread(tid).first()
                    val last = msgs.lastOrNull()
                    last?.let {
                        NotificationItem(
                            id = "thread-$tid",
                            title = "New activity in a conversation",
                            body = "${it.fromUserId.take(6)}: ${it.text}",
                            route = "messages/$tid",
                            type = "message",
                            timestamp = it.timestamp,
                        )
                    }
                }
            }
            // Emit initial with pending orders and previews
            _ui.emit(_ui.value.copy(pendingOrders = pending, items = buildItems(_ui.value.unreadMessages, pending, previews)))

            // Start collecting unread messages count for current user
            if (userId != null) {
                messagingRepository.streamUnreadCount(userId).collect { unread ->
                    _ui.emit(_ui.value.copy(unreadMessages = unread, items = buildItems(unread, _ui.value.pendingOrders, previews)))
                }
            }
        }
    }
}
