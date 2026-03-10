package com.rio.rostry.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

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
            // Placeholder submission path while feedback repository wiring is migrated.
            _uiState.update {
                it.copy(
                    isLoading = false,
                    submissionSuccess = true,
                    content = ""
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
