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
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val outgoingDao: OutgoingMessageDao,
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessagingRepository.MessageDTO>>(emptyList())
    val messages: StateFlow<List<MessagingRepository.MessageDTO>> = _messages.asStateFlow()

    fun bind(groupId: String) {
        viewModelScope.launch {
            messagingRepository.streamGroup(groupId).collect { list ->
                _messages.value = list
            }
        }
    }

    fun sendQueuedGroup(groupId: String, fromUserId: String, text: String) {
        viewModelScope.launch {
            val msg = OutgoingMessageEntity(
                id = UUID.randomUUID().toString(),
                kind = "GROUP",
                threadOrGroupId = groupId,
                fromUserId = fromUserId,
                toUserId = null,
                bodyText = text,
                fileUri = null,
                fileName = null,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
            outgoingDao.upsert(msg)
        }
    }
}
