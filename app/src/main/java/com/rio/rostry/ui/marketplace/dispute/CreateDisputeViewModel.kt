package com.rio.rostry.ui.marketplace.dispute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.repository.DisputeRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateDisputeViewModel @Inject constructor(
    private val repository: DisputeRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val reason: String = "",
        val description: String = ""
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun onReasonChanged(reason: String) {
        _state.value = _state.value.copy(reason = reason)
    }

    fun onDescriptionChanged(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun submitDispute(transferId: String, reportedUserId: String) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        val s = _state.value
        
        if (s.reason.isBlank() || s.description.isBlank()) {
            _state.value = s.copy(error = "Please fill all fields")
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            val dispute = DisputeEntity(
                disputeId = UUID.randomUUID().toString(),
                transferId = transferId,
                reporterId = userId,
                reportedUserId = reportedUserId,
                reason = s.reason,
                description = s.description,
                createdAt = System.currentTimeMillis()
            )
            val result = repository.createDispute(dispute)
            if (result is Resource.Success) {
                _state.value = s.copy(isLoading = false, success = true)
            } else {
                _state.value = s.copy(isLoading = false, error = result.message)
            }
        }
    }
}
