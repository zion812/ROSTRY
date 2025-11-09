package com.rio.rostry.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.rio.rostry.utils.network.ConnectivityManager as AppConnectivityManager
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.ui.components.ConflictDetails

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val outboxDao: OutboxDao,
    private val currentUserProvider: CurrentUserProvider,
    private val gson: Gson,
    private val connectivityManager: AppConnectivityManager,
    private val syncManager: SyncManager,
) : ViewModel() {

    // Track bind job to prevent memory leaks from multiple collectors
    private var bindJob: Job? = null

    private val _messages = MutableStateFlow<List<MessagingRepository.MessageDTO>>(emptyList())
    val messages: StateFlow<List<MessagingRepository.MessageDTO>> = _messages.asStateFlow()

    private val _pendingMessagesCount = MutableStateFlow(0)
    val pendingMessagesCount: StateFlow<Int> = _pendingMessagesCount.asStateFlow()

    private val _pendingOutboxIds = MutableStateFlow<Set<String>>(emptySet())
    val pendingOutboxIds: StateFlow<Set<String>> = _pendingOutboxIds.asStateFlow()

    private val _conflictDetails = MutableStateFlow<ConflictDetails?>(null)
    val conflictDetails: StateFlow<ConflictDetails?> = _conflictDetails.asStateFlow()

    val isOnline: StateFlow<Boolean> = connectivityManager
        .observe()
        .map { it.isOnline }
        .let { flow ->
            val s = MutableStateFlow(false)
            viewModelScope.launch { flow.collect { s.value = it } }
            s.asStateFlow()
        }

    init {
        val userId = currentUserProvider.userIdOrNull()
        if (userId != null) {
            viewModelScope.launch {
                outboxDao.observePendingCountByType(userId, OutboxEntity.TYPE_GROUP_MESSAGE).collect { count ->
                    _pendingMessagesCount.value = count
                }
            }
            viewModelScope.launch {
                outboxDao.observePendingByUser(userId).collect { list ->
                    _pendingOutboxIds.value = list
                        .filter { it.entityType == OutboxEntity.TYPE_GROUP_MESSAGE && it.status == "PENDING" }
                        .map { it.entityId }
                        .toSet()
                }
            }
        }
        // Observe conflict events for chat messages
        viewModelScope.launch {
            syncManager.conflictEvents.collect { ev ->
                if (ev.entityType == OutboxEntity.TYPE_CHAT_MESSAGE) {
                    _conflictDetails.value = ConflictDetails(
                        entityType = "chat_message",
                        entityId = ev.entityId,
                        conflictFields = ev.conflictFields,
                        mergedAt = ev.mergedAt,
                        message = "Message was updated remotely. Your local changes were merged."
                    )
                }
            }
        }
    }

    fun bind(groupId: String) {
        // Cancel previous bind job to prevent memory leak
        bindJob?.cancel()
        
        // Create new bind job
        bindJob = viewModelScope.launch {
            messagingRepository.streamGroup(groupId).collect { list ->
                _messages.value = list
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bindJob?.cancel()
    }

    fun sendQueuedGroup(groupId: String, fromUserId: String, text: String) {
        viewModelScope.launch {
            val outboxEntry = OutboxEntity(
                outboxId = UUID.randomUUID().toString(),
                userId = fromUserId,
                entityType = OutboxEntity.TYPE_GROUP_MESSAGE,
                entityId = UUID.randomUUID().toString(),
                operation = "SEND",
                payloadJson = gson.toJson(mapOf("groupId" to groupId, "fromUserId" to fromUserId, "text" to text)),
                createdAt = System.currentTimeMillis(),
                priority = "NORMAL"
            )
            outboxDao.insert(outboxEntry)
        }
    }

    fun sendQueuedGroupSelf(groupId: String, text: String) {
        val uid = currentUserProvider.userIdOrNull() ?: return
        sendQueuedGroup(groupId = groupId, fromUserId = uid, text = text)
    }

    fun retryFailedMessage(messageId: String) {
        viewModelScope.launch {
            val failed = outboxDao.getByTypeAndStatus(OutboxEntity.TYPE_GROUP_MESSAGE, "FAILED", limit = 100)
                .firstOrNull { it.entityId == messageId }
            if (failed != null) {
                outboxDao.resetRetryAndStatus(failed.outboxId, "PENDING", System.currentTimeMillis())
            }
        }
    }

    fun dismissConflict() { _conflictDetails.value = null }
    fun viewConflictDetails(entityId: String) { _conflictDetails.value = null }
}