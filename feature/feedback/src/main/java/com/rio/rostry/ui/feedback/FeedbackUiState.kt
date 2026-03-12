package com.rio.rostry.ui.feedback
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

data class FeedbackUiState(
    val content: String = "",
    val type: String = "General",
    val isLoading: Boolean = false,
    val submissionSuccess: Boolean = false,
    val error: String? = null
)
