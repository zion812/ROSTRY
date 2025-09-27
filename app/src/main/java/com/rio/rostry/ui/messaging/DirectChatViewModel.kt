package com.rio.rostry.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.OutgoingMessageDao
import com.rio.rostry.data.database.entity.OutgoingMessageEntity
import com.rio.rostry.data.repository.social.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DirectChatViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val outgoingDao: OutgoingMessageDao,
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessagingRepository.MessageDTO>>(emptyList())
    val messages: StateFlow<List<MessagingRepository.MessageDTO>> = _messages.asStateFlow()

    private val _outbox = MutableStateFlow<List<OutgoingMessageEntity>>(emptyList())
    val outbox: StateFlow<List<OutgoingMessageEntity>> = _outbox.asStateFlow()

    fun bind(threadId: String) {
        viewModelScope.launch {
            messagingRepository.streamThread(threadId).collect { list ->
                _messages.value = list
            }
        }
        viewModelScope.launch {
            outgoingDao.streamForThread(threadId).collectLatest { queued ->
                _outbox.value = queued
            }
        }
    }

    fun markSeen(threadId: String, userId: String) {
        viewModelScope.launch {
            runCatching { messagingRepository.markThreadSeen(threadId, userId) }
        }
    }

    fun sendQueuedDm(threadId: String, fromUserId: String, toUserId: String, text: String) {
        viewModelScope.launch {
            val msg = OutgoingMessageEntity(
                id = UUID.randomUUID().toString(),
                kind = "DM",
                threadOrGroupId = threadId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                bodyText = text,
                fileUri = null,
                fileName = null,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
            outgoingDao.upsert(msg)
        }
    }

    fun sendQueuedFileDm(threadId: String, fromUserId: String, toUserId: String, fileUri: android.net.Uri, fileName: String?) {
        viewModelScope.launch {
            val msg = OutgoingMessageEntity(
                id = UUID.randomUUID().toString(),
                kind = "DM",
                threadOrGroupId = threadId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                bodyText = null,
                fileUri = fileUri.toString(),
                fileName = fileName,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
            outgoingDao.upsert(msg)
        }
    }
}
