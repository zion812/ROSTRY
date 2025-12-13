package com.rio.rostry.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.FeedbackRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    data class UiState(
        val content: String = "",
        val type: String = "General",
        val isLoading: Boolean = false,
        val submissionSuccess: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateContent(text: String) {
        _uiState.update { it.copy(content = text) }
    }

    fun updateType(type: String) {
        _uiState.update { it.copy(type = type) }
    }

    fun submit() {
        if (_uiState.value.content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val userId = firebaseAuth.currentUser?.uid
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
                return@launch
            }

            val result = feedbackRepository.submitFeedback(userId, _uiState.value.content, _uiState.value.type)
            
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, submissionSuccess = true, content = "") }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Unknown error") }
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
