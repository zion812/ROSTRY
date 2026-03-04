package com.rio.rostry.domain.error

/**
 * Categories for classifying errors by their recoverability.
 */
enum class ErrorCategory {
    /** Automatic recovery possible (e.g., transient network errors). */
    RECOVERABLE,

    /** User can fix the issue (e.g., invalid input, permission denied). */
    USER_ACTIONABLE,

    /** Requires graceful degradation (e.g., OOM, critical service failure). */
    FATAL
}
