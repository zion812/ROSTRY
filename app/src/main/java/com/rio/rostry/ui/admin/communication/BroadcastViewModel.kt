package com.rio.rostry.ui.admin.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val estimatedAudienceSize: Int = 0,
        val isSending: Boolean = false,
        val selectedRole: UserType? = null // null means 'All Users'
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        updateAudienceEstimate()
    }

    fun setTargetAudience(role: UserType?) {
        _state.update { it.copy(selectedRole = role) }
        updateAudienceEstimate()
    }

    private fun updateAudienceEstimate() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val count = userRepository.getAudienceSize(_state.value.selectedRole)
            _state.update { it.copy(isLoading = false, estimatedAudienceSize = count) }
        }
    }

    fun sendBroadcast(title: String, message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            
            // Simulation of sending logic (e.g., using Firebase Cloud Messaging topic or individual tokens)
            // In a real app, this would call a backend function.
            // For now, we simulate a delay.
            kotlinx.coroutines.delay(1500)
            
            _toastEvent.emit("Broadcast sent to ${_state.value.estimatedAudienceSize} users")
            _state.update { it.copy(isSending = false) }
        }
    }
}
