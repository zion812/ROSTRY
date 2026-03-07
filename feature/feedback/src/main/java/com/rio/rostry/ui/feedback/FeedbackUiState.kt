package com.rio.rostry.ui.feedback

data class FeedbackUiState(
    val content: String = "",
    val type: String = "General",
    val isLoading: Boolean = false,
    val submissionSuccess: Boolean = false,
    val error: String? = null
)
