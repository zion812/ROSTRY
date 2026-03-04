package com.rio.rostry.domain.error

/**
 * Context information attached to every error for structured logging.
 */
data class ErrorContext(
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String? = null,
    val operationName: String,
    val stackTrace: String,
    val additionalData: Map<String, Any> = emptyMap()
)

/**
 * Result of handling an error through the centralized error handler.
 */
data class ErrorResult(
    /** Whether the error was handled (logged, categorized). */
    val handled: Boolean,
    /** Whether recovery was successful. */
    val recovered: Boolean,
    /** User-friendly message suitable for UI display. */
    val userMessage: String,
    /** Whether the caller should offer a retry option. */
    val shouldRetry: Boolean
)
