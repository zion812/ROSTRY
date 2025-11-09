package com.rio.rostry.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.OutgoingMessageDao
import com.rio.rostry.data.database.entity.OutgoingMessageEntity
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

@HiltViewModel
class ThreadViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val outgoingDao: OutgoingMessageDao,
    private val currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    // Track bind job to prevent memory leaks from multiple collectors
    private var bindJob: Job? = null

    private val _messages = MutableStateFlow<List<MessagingRepository.MessageDTO>>(emptyList())
    val messages: StateFlow<List<MessagingRepository.MessageDTO>> = _messages.asStateFlow()

    private val _threadMetadata = MutableStateFlow<MessagingRepository.ThreadMetadata?>(null)
    val threadMetadata: StateFlow<MessagingRepository.ThreadMetadata?> = _threadMetadata.asStateFlow()

    fun bind(threadId: String) {
        // Cancel previous bind job to prevent memory leak
        bindJob?.cancel()
        
        // Combine all bind operations into a single job
        bindJob = viewModelScope.launch {
            launch {
                messagingRepository.streamThread(threadId).collect { list ->
                    _messages.value = list
                }
            }
            launch {
                messagingRepository.streamThreadMetadata(threadId).collect { metadata ->
                    _threadMetadata.value = metadata
                }
            }
            launch {
                val uid = currentUserProvider.userIdOrNull() ?: return@launch
                messagingRepository.markThreadSeen(threadId, uid)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bindJob?.cancel()
    }

    fun updateThreadTitle(threadId: String, title: String) {
        viewModelScope.launch {
            messagingRepository.updateThreadMetadata(threadId, title, System.currentTimeMillis())
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

    fun sendQueuedDmSelf(threadId: String, toUserId: String, text: String) {
        val uid = currentUserProvider.userIdOrNull() ?: return
        sendQueuedDm(threadId = threadId, fromUserId = uid, toUserId = toUserId, text = text)
    }

    fun sendQueuedDmFile(threadId: String, fromUserId: String, toUserId: String, fileUri: String, fileName: String?) {
        viewModelScope.launch {
            val msg = OutgoingMessageEntity(
                id = UUID.randomUUID().toString(),
                kind = "DM",
                threadOrGroupId = threadId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                bodyText = null,
                fileUri = fileUri,
                fileName = fileName,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
            outgoingDao.upsert(msg)
        }
    }
}
