package com.rio.rostry.feature.admin.ui.dispute

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.core.model.Dispute
import com.rio.rostry.core.model.DisputeStatus
import com.rio.rostry.domain.commerce.repository.DisputeRepository
import com.rio.rostry.domain.account.repository.UserRepository
import com.rio.rostry.domain.social.repository.ChatRepository
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDisputeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DisputeRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val disputeId: String = savedStateHandle.get<String>("disputeId") ?: ""

    data class UiState(
        val isLoading: Boolean = true,
        val dispute: Dispute? = null,
        val reporterName: String? = null,
        val reportedUserName: String? = null,
        val isProcessing: Boolean = false,
        val error: String? = null,
        val isResolved: Boolean = false,
        val chatMessages: List<com.rio.rostry.core.model.ChatMessage> = emptyList<com.rio.rostry.core.model.ChatMessage>()
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadDispute()
    }

    private fun loadDispute() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            when (val result = repository.getDisputeById(disputeId)) {
                is com.rio.rostry.core.common.Result.Success<*> -> {
                    val dispute = result.data as? Dispute
                    _state.update { it.copy(
                        isLoading = false,
                        dispute = dispute,
                        isResolved = dispute?.status != DisputeStatus.OPEN && 
                                     dispute?.status != DisputeStatus.UNDER_REVIEW
                    ) }
                    
                    // Load user names
                    dispute?.let { d ->
                        loadUserName(d.reporterId, isReporter = true)
                        loadUserName(d.reportedUserId, isReporter = false)
                        loadChatHistory(d.reporterId, d.reportedUserId)
                    }
                }
                is com.rio.rostry.core.common.Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                }
                is com.rio.rostry.core.common.Result.Loading -> {}
            }
        }
    }

    private fun loadUserName(userId: String, isReporter: Boolean) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            if (result is com.rio.rostry.core.model.Result.Success) {
                val user = result.data as? com.rio.rostry.core.model.User
                val name = user?.displayName ?: "User ${userId.take(8)}"
                _state.update { 
                    if (isReporter) it.copy(reporterName = name)
                    else it.copy(reportedUserName = name)
                }
            } else if (result is com.rio.rostry.core.model.Result.Error) {
                // Ignore or handle
            }
        }
    }

    private fun loadChatHistory(userA: String, userB: String) {
        viewModelScope.launch {
            chatRepository.conversation(userA, userB).collect { messages ->
                _state.update { it.copy(chatMessages = messages.sortedBy { msg -> msg.sentAt }) }
            }
        }
    }

    fun resolveDispute(decision: DisputeStatus, notes: String) {
        val adminId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            when (val result = repository.resolveDispute(disputeId, decision, notes, adminId)) {
                is com.rio.rostry.core.common.Result.Success<*> -> {
                    _toastEvent.emit("Dispute resolved: ${decision.name}")
                    _state.update { it.copy(
                        isProcessing = false,
                        isResolved = true,
                        dispute = it.dispute?.copy(
                            status = decision,
                            resolution = notes,
                            resolvedAt = System.currentTimeMillis(),
                            resolvedByAdminId = adminId
                        )
                    ) }
                }
                is com.rio.rostry.core.common.Result.Error -> {
                    _toastEvent.emit("Failed: ${result.exception.message}")
                    _state.update { it.copy(isProcessing = false) }
                }
                is com.rio.rostry.core.common.Result.Loading -> {}
            }
        }
    }

    fun banUser(reason: String) {
        val userId = _state.value.dispute?.reportedUserId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            when (val result = userRepository.suspendUser(userId, reason, null)) {
                is Resource.Success -> {
                    _toastEvent.emit("User banned successfully")
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to ban user: ${result.message}")
                }
                else -> {}
            }
            _state.update { it.copy(isProcessing = false) }
        }
    }

    fun escalate() {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            _toastEvent.emit("Dispute escalated for management review")
            _state.update { it.copy(isProcessing = false) }
        }
    }
}
