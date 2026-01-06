package com.rio.rostry.utils

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User-friendly error messages mapped from technical exceptions
 */
object ErrorMessages {
    // Network errors
    const val NO_INTERNET = "No internet connection. Please check your network and try again."
    const val SERVER_UNREACHABLE = "Server is temporarily unavailable. Please try again later."
    const val TIMEOUT = "Request timed out. Please try again."
    const val SLOW_CONNECTION = "Connection is slow. Some features may be limited."
    
    // Auth errors
    const val SESSION_EXPIRED = "Your session has expired. Please sign in again."
    const val UNAUTHORIZED = "You don't have permission to perform this action."
    
    // Data errors
    const val NOT_FOUND = "The requested item was not found."
    const val SYNC_FAILED = "Sync failed. Your changes are saved locally."
    const val SAVE_FAILED = "Failed to save. Please try again."
    
    // General
    const val UNKNOWN_ERROR = "Something went wrong. Please try again."
    const val OPERATION_CANCELLED = "Operation was cancelled."
}

/**
 * Action that can be taken after an error
 */
data class ErrorAction(
    val label: String,
    val action: () -> Unit
)

/**
 * Result of error handling with user-friendly message and optional action
 */
data class ErrorResult(
    val message: String,
    val action: ErrorAction? = null,
    val shouldLog: Boolean = true,
    val shouldReport: Boolean = false
)

/**
 * User-friendly error handler that converts technical exceptions
 * to human-readable messages with appropriate actions.
 */
@Singleton
class UserFriendlyErrorHandler @Inject constructor() {

    companion object {
        private const val TAG = "ErrorHandler"
    }

    /**
     * Handle an exception and return user-friendly result
     */
    fun handle(
        exception: Throwable,
        context: String? = null,
        retryAction: (() -> Unit)? = null
    ): ErrorResult {
        val contextInfo = context?.let { "[$it] " } ?: ""
        Timber.e(exception, "${contextInfo}Error occurred: ${exception.message}")

        return when (exception) {
            // Network errors
            is UnknownHostException, is IOException -> {
                if (exception.message?.contains("Unable to resolve host") == true) {
                    ErrorResult(
                        message = ErrorMessages.NO_INTERNET,
                        action = retryAction?.let { ErrorAction("Retry", it) }
                    )
                } else {
                    ErrorResult(
                        message = ErrorMessages.SERVER_UNREACHABLE,
                        action = retryAction?.let { ErrorAction("Retry", it) }
                    )
                }
            }

            is SocketTimeoutException -> {
                ErrorResult(
                    message = ErrorMessages.TIMEOUT,
                    action = retryAction?.let { ErrorAction("Retry", it) }
                )
            }

            // Security/Auth errors
            is SecurityException -> {
                ErrorResult(
                    message = ErrorMessages.UNAUTHORIZED,
                    shouldReport = true
                )
            }

            // Illegal state
            is IllegalStateException -> {
                ErrorResult(
                    message = exception.message ?: ErrorMessages.UNKNOWN_ERROR,
                    shouldReport = true
                )
            }

            // Cancellation
            is kotlinx.coroutines.CancellationException -> {
                ErrorResult(
                    message = ErrorMessages.OPERATION_CANCELLED,
                    shouldLog = false
                )
            }

            // General fallback
            else -> {
                ErrorResult(
                    message = exception.message?.take(100) ?: ErrorMessages.UNKNOWN_ERROR,
                    shouldReport = true
                )
            }
        }
    }

    /**
     * Show error in snackbar with optional retry action
     */
    fun showInSnackbar(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        exception: Throwable,
        context: String? = null,
        onRetry: (() -> Unit)? = null
    ) {
        val result = handle(exception, context, onRetry)
        
        scope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = result.message,
                actionLabel = result.action?.label,
                duration = SnackbarDuration.Long
            )
            
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                result.action?.action?.invoke()
            }
        }
    }

    /**
     * Extract user-friendly message from any throwable
     */
    fun getMessageForUser(exception: Throwable): String {
        return handle(exception).message
    }

    /**
     * Check if error is retryable
     */
    fun isRetryable(exception: Throwable): Boolean {
        return exception is IOException ||
               exception is SocketTimeoutException ||
               exception is UnknownHostException
    }
}
