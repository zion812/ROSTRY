package com.rio.rostry.domain.error

import com.rio.rostry.data.database.dao.ErrorLogDao
import com.rio.rostry.data.database.entity.ErrorLogEntity
import com.rio.rostry.data.resilience.CircuitOpenException
import com.rio.rostry.data.resilience.FallbackManager
import com.rio.rostry.data.resilience.RetryBudgetExhaustedException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-grade centralized error handler.
 *
 * - Categorizes errors into RECOVERABLE / USER_ACTIONABLE / FATAL
 * - Executes recovery strategies when provided
 * - Logs every error with full context to Room and Timber
 * - Reports fatal errors to Firebase Crashlytics
 * - Returns user-friendly messages (never exposes stack traces)
 */
@Singleton
class CentralizedErrorHandler @Inject constructor(
    private val errorLogDao: ErrorLogDao,
    private val fallbackManager: FallbackManager
) : ErrorHandler {

    override suspend fun handle(
        error: Throwable,
        operationName: String,
        recoveryStrategy: RecoveryStrategy?
    ): ErrorResult {
        val category = categorize(error)
        val userMessage = getUserMessage(error)

        // Build context
        val context = ErrorContext(
            operationName = operationName,
            stackTrace = error.stackTraceToString(),
            additionalData = mapOf(
                "category" to category.name,
                "errorType" to (error::class.simpleName ?: "Unknown")
            )
        )

        // 1. Log to Timber
        when (category) {
            ErrorCategory.FATAL -> Timber.e(error, "FATAL [$operationName]: ${error.message}")
            ErrorCategory.USER_ACTIONABLE -> Timber.w(error, "USER_ACTIONABLE [$operationName]: ${error.message}")
            ErrorCategory.RECOVERABLE -> Timber.d(error, "RECOVERABLE [$operationName]: ${error.message}")
        }

        // 2. Persist to local database
        persistErrorLog(context, category, error)

        // 3. Report to Crashlytics if needed
        if (shouldReport(error)) {
            try {
                FirebaseCrashlytics.getInstance().apply {
                    setCustomKey("operationName", operationName)
                    setCustomKey("errorCategory", category.name)
                    recordException(error)
                }
            } catch (e: Exception) {
                Timber.w(e, "Failed to report to Crashlytics")
            }
        }

        // 4. Attempt recovery
        var recovered = false
        if (recoveryStrategy != null && category != ErrorCategory.FATAL) {
            val recoveryResult = try {
                recoveryStrategy.recover(error, context)
            } catch (e: Exception) {
                Timber.w(e, "Recovery strategy failed for $operationName")
                Result.failure(e)
            }
            recovered = recoveryResult.isSuccess
        }

        return ErrorResult(
            handled = true,
            recovered = recovered,
            userMessage = userMessage,
            shouldRetry = category == ErrorCategory.RECOVERABLE && !recovered
        )
    }

    override fun categorize(error: Throwable): ErrorCategory {
        return when (error) {
            // Network / IO — usually transient
            is SocketTimeoutException,
            is UnknownHostException,
            is IOException,
            is CircuitOpenException,
            is RetryBudgetExhaustedException -> ErrorCategory.RECOVERABLE

            // Client errors — user needs to fix something
            is IllegalArgumentException,
            is IllegalStateException,
            is SecurityException,
            is UnsupportedOperationException -> ErrorCategory.USER_ACTIONABLE

            // Fatal — system-level failures
            is OutOfMemoryError,
            is StackOverflowError -> ErrorCategory.FATAL

            // Default: check if it wraps a known cause
            else -> {
                val cause = error.cause
                if (cause != null && cause !== error) {
                    categorize(cause)
                } else {
                    // Unknown errors default to user-actionable to surface them
                    ErrorCategory.USER_ACTIONABLE
                }
            }
        }
    }

    override fun getUserMessage(error: Throwable): String {
        // Delegates to existing FallbackManager for consistency
        return fallbackManager.getFriendlyErrorMessage(error)
    }

    override fun shouldReport(error: Throwable): Boolean {
        return when (categorize(error)) {
            ErrorCategory.FATAL -> true
            ErrorCategory.USER_ACTIONABLE -> false
            ErrorCategory.RECOVERABLE -> false
        }
    }

    private suspend fun persistErrorLog(
        context: ErrorContext,
        category: ErrorCategory,
        error: Throwable
    ) {
        try {
            val entity = ErrorLogEntity(
                id = UUID.randomUUID().toString(),
                timestamp = context.timestamp,
                userId = context.userId,
                operationName = context.operationName,
                errorCategory = category.name,
                errorMessage = error.message ?: "Unknown error",
                stackTrace = context.stackTrace,
                additionalData = context.additionalData.entries.joinToString("; ") { "${it.key}=${it.value}" },
                reported = shouldReport(error)
            )
            errorLogDao.insert(entity)
        } catch (e: Exception) {
            // Don't let logging failures propagate
            Timber.w(e, "Failed to persist error log")
        }
    }
}
