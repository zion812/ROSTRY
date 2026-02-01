package com.rio.rostry.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.RoleUpgradeRequestEntity
import com.rio.rostry.data.repository.RoleUpgradeRequestRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.upgrade.RoleUpgradeManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminUpgradeRequestsViewModel @Inject constructor(
    private val roleUpgradeRequestRepository: RoleUpgradeRequestRepository,
    private val roleUpgradeManager: RoleUpgradeManager,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val REJECTION_REASONS = listOf(
            "Insufficient experience documented",
            "Bird count below requirement",
            "Missing or unclear certifications",
            "References could not be verified",
            "Profile information incomplete",
            "Duplicate or suspicious request",
            "Other (see admin notes)"
        )
    }

    data class UiState(
        val isLoading: Boolean = false,
        val empty: Boolean = false,
        val pendingRequests: List<RoleUpgradeRequestEntity> = emptyList(),
        val historyRequests: List<RoleUpgradeRequestEntity> = emptyList(),
        val error: String? = null,
        val processingId: String? = null,
        val selectedRequest: RoleUpgradeRequestEntity? = null
    )
    
    // Keep backward compatibility
    val UiState.requests: List<RoleUpgradeRequestEntity> get() = pendingRequests
    
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    private val _uiState = MutableStateFlow(UiState(isLoading = true))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        loadPendingRequests()
        loadHistoryRequests()
    }

    fun loadPendingRequests() {
        viewModelScope.launch {
            roleUpgradeRequestRepository.observePendingRequests()
                .collectLatest { list ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            pendingRequests = list,
                            empty = list.isEmpty()
                        )
                    }
                }
        }
    }

    fun loadHistoryRequests() {
        viewModelScope.launch {
            roleUpgradeRequestRepository.observeProcessedRequests()
                .collectLatest { list ->
                    _uiState.update { 
                        it.copy(historyRequests = list)
                    }
                }
        }
    }

    fun selectRequest(request: RoleUpgradeRequestEntity) {
        _uiState.update { it.copy(selectedRequest = request) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedRequest = null) }
    }

    fun approveRequest(requestId: String, adminId: String, notes: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = requestId) }
            
            val result = roleUpgradeManager.approveRequest(requestId, adminId, notes)
            
            if (result is Resource.Success<*>) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Request Approved"))
            } else {
                _uiEvent.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to approve"))
            }
            
            _uiState.update { it.copy(processingId = null, selectedRequest = null) }
        }
    }

    fun rejectRequest(requestId: String, adminId: String, reason: String?) {
         viewModelScope.launch {
            _uiState.update { it.copy(processingId = requestId) }
            
            val result = roleUpgradeManager.rejectRequest(requestId, adminId, reason)
            
            if (result is Resource.Success<*>) {
                 _uiEvent.emit(UiEvent.ShowSnackbar("Request Rejected"))
            } else {
                _uiEvent.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to reject"))
            }
            
            _uiState.update { it.copy(processingId = null, selectedRequest = null) }
        }
    }
}
