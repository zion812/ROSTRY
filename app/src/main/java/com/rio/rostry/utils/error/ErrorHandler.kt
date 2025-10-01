package com.rio.rostry.utils.error

/**
 * Centralized error handling utilities. Integrates conceptually with Resource wrapper and repositories.
 * Pure-Kotlin scaffolding to avoid coupling here. Actual integration can be wired in repositories/ViewModels.
 */
object ErrorHandler {
    fun categorize(throwable: Throwable): ErrorCategory = when (throwable) {
        is java.net.SocketTimeoutException -> ErrorCategory.Network.Timeout
        is java.net.UnknownHostException -> ErrorCategory.Network.Unreachable
        is IllegalArgumentException -> ErrorCategory.Validation
        is SecurityException -> ErrorCategory.Security
        else -> ErrorCategory.Unknown
    }

    fun userMessage(category: ErrorCategory): String = when (category) {
        ErrorCategory.Network.Timeout -> "Request timed out. Please try again."
        ErrorCategory.Network.Unreachable -> "No internet connection."
        ErrorCategory.Validation -> "Some inputs are invalid."
        ErrorCategory.Security -> "Security check failed."
        ErrorCategory.Database -> "A data error occurred."
        ErrorCategory.Conflict -> "A conflict occurred. Please refresh."
        ErrorCategory.Unknown -> "Something went wrong."
    }

    fun shouldRetry(category: ErrorCategory): Boolean = when (category) {
        is ErrorCategory.Network -> true
        ErrorCategory.Conflict -> true
        ErrorCategory.Database, ErrorCategory.Validation, ErrorCategory.Security, ErrorCategory.Unknown -> false
    }
}

sealed interface ErrorCategory {
    sealed interface Network : ErrorCategory {
        data object Timeout : Network
        data object Unreachable : Network
    }
    data object Validation : ErrorCategory
    data object Security : ErrorCategory
    data object Database : ErrorCategory
    data object Conflict : ErrorCategory
    data object Unknown : ErrorCategory
}
