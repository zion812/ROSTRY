package com.rio.rostry.ui.admin.dispute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import com.rio.rostry.data.repository.DisputeRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDisputeViewModel @Inject constructor(
    private val repository: DisputeRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val disputes: List<DisputeEntity> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadOpenDisputes()
    }

    fun loadOpenDisputes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getAllOpenDisputes().collect { result ->
                when (result) {
                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false,
                        disputes = result.data ?: emptyList()
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun resolveDispute(disputeId: String, decision: DisputeStatus, notes: String) {
        val adminId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            repository.resolveDispute(disputeId, decision, notes, adminId)
            // Ideally refresh or let the Flow update automatically if using real-time listener
        }
    }
}
