package com.rio.rostry.domain.error

/**
 * Centralized error handling interface.
 * All application code should use this for consistent error logging,
 * categorization, recovery, and user-friendly messaging.
 */
interface ErrorHandler {

    /**
     * Handle an error with full context and optional recovery.
     *
     * @param error The throwable to handle.
     * @param operationName Human-readable name of the operation that failed.
     * @param recoveryStrategy Optional strategy to attempt automatic recovery.
     * @return [ErrorResult] with handling outcome and user message.
     */
    suspend fun handle(
        error: Throwable,
        operationName: String,
        recoveryStrategy: RecoveryStrategy? = null
    ): ErrorResult

    /**
     * Categorize an error without handling it.
     */
    fun categorize(error: Throwable): ErrorCategory

    /**
     * Get a user-friendly message for the given error.
     * Never exposes stack traces or technical details.
     */
    fun getUserMessage(error: Throwable): String

    /**
     * Determine whether this error should be reported to monitoring systems
     * (e.g., Firebase Crashlytics).
     */
    fun shouldReport(error: Throwable): Boolean
}
