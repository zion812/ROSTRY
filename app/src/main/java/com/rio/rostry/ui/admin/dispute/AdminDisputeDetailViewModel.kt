package com.rio.rostry.ui.admin.dispute

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import com.rio.rostry.data.repository.DisputeRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
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
    private val chatRepository: com.rio.rostry.data.repository.ChatRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val disputeId: String = savedStateHandle.get<String>("disputeId") ?: ""

    data class UiState(
        val isLoading: Boolean = true,
        val dispute: DisputeEntity? = null,
        val reporterName: String? = null,
        val reportedUserName: String? = null,
        val isProcessing: Boolean = false,
        val error: String? = null,
        val isResolved: Boolean = false,
        val chatMessages: List<com.rio.rostry.data.database.entity.ChatMessageEntity> = emptyList()
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
                is Resource.Success -> {
                    val dispute = result.data
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
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadUserName(userId: String, isReporter: Boolean) {
        viewModelScope.launch {
            userRepository.getUserById(userId).collect { result ->
                if (result is Resource.Success) {
                    val name = result.data?.fullName ?: "User ${userId.take(8)}"
                    _state.update { 
                        if (isReporter) it.copy(reporterName = name)
                        else it.copy(reportedUserName = name)
                    }
                }
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
                is Resource.Success -> {
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
                is Resource.Error -> {
                    _toastEvent.emit("Failed: ${result.message}")
                    _state.update { it.copy(isProcessing = false) }
                }
                else -> {}
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
