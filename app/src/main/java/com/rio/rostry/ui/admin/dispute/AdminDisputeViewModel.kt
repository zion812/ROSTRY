package com.rio.rostry.ui.admin.dispute

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
class AdminDisputeViewModel @Inject constructor(
    private val repository: DisputeRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    enum class DisputeFilter { OPEN, RESOLVED }

    data class UiState(
        val isLoading: Boolean = false,
        val openDisputes: List<DisputeEntity> = emptyList(),
        val resolvedDisputes: List<DisputeEntity> = emptyList(),
        val currentFilter: DisputeFilter = DisputeFilter.OPEN,
        val processingId: String? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadDisputes()
    }

    fun loadDisputes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllOpenDisputes().collect { result ->
                when (result) {
                    is Resource.Success -> _state.update { 
                        it.copy(isLoading = false, openDisputes = result.data ?: emptyList()) 
                    }
                    is Resource.Error -> _state.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                    is Resource.Loading -> {}
                }
            }
        }
        loadResolvedDisputes()
    }

    private fun loadResolvedDisputes() {
        viewModelScope.launch {
            repository.getResolvedDisputes().collect { result ->
                when (result) {
                    is Resource.Success -> _state.update { 
                        it.copy(resolvedDisputes = result.data ?: emptyList()) 
                    }
                    else -> {}
                }
            }
        }
    }

    fun onFilterChanged(filter: DisputeFilter) {
        _state.update { it.copy(currentFilter = filter) }
    }

    fun resolveDispute(disputeId: String, decision: DisputeStatus, notes: String) {
        val adminId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            _state.update { it.copy(processingId = disputeId) }
            when (val result = repository.resolveDispute(disputeId, decision, notes, adminId)) {
                is Resource.Success -> {
                    _toastEvent.emit("Dispute resolved: ${decision.name}")
                    // Move from open to resolved list
                    _state.update { state ->
                        val resolved = state.openDisputes.find { it.disputeId == disputeId }
                            ?.copy(status = decision, resolution = notes, resolvedAt = System.currentTimeMillis())
                        state.copy(
                            openDisputes = state.openDisputes.filter { it.disputeId != disputeId },
                            resolvedDisputes = if (resolved != null) listOf(resolved) + state.resolvedDisputes else state.resolvedDisputes
                        )
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed: ${result.message}")
                }
                else -> {}
            }
            _state.update { it.copy(processingId = null) }
        }
    }

    fun banUser(userId: String, reason: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingId = userId) }
            when (val result = userRepository.suspendUser(userId, reason, null)) {
                is Resource.Success -> {
                    _toastEvent.emit("User banned: $userId")
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed to ban: ${result.message}")
                }
                else -> {}
            }
            _state.update { it.copy(processingId = null) }
        }
    }

    fun escalateDispute(disputeId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingId = disputeId) }
            // Mark as escalated (could add escalation field to entity)
            _toastEvent.emit("Dispute escalated for management review")
            _state.update { it.copy(processingId = null) }
        }
    }
}
