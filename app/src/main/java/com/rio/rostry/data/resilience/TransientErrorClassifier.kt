package com.rio.rostry.data.resilience

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * Classifies errors as transient (retryable) or permanent.
 * Used by retry logic to determine whether an operation should be retried.
 */
object TransientErrorClassifier {

    /** HTTP status codes that indicate transient failures. */
    private val RETRYABLE_HTTP_CODES = setOf(
        408, // Request Timeout
        429, // Too Many Requests
        500, // Internal Server Error
        502, // Bad Gateway
        503, // Service Unavailable
        504  // Gateway Timeout
    )

    /** HTTP status codes that indicate permanent failures (do NOT retry). */
    private val PERMANENT_HTTP_CODES = setOf(
        400, // Bad Request
        401, // Unauthorized
        403, // Forbidden
        404, // Not Found
        405, // Method Not Allowed
        409, // Conflict
        422  // Unprocessable Entity
    )

    /**
     * Returns true if the error is transient (retryable).
     */
    fun isTransient(error: Throwable): Boolean {
        return when (error) {
            // Network issues are always transient
            is SocketTimeoutException -> true
            is UnknownHostException -> true
            is java.net.ConnectException -> true

            // IO issues are usually transient
            is IOException -> {
                // SSL errors are typically NOT transient
                error !is SSLHandshakeException
            }

            // HTTP errors: check status code
            is HttpException -> {
                error.code() in RETRYABLE_HTTP_CODES
            }

            // Circuit breaker errors are transient (service may recover)
            is CircuitOpenException -> true
            is RetryBudgetExhaustedException -> false // Budget exhausted, don't retry

            // Default: not transient
            else -> false
        }
    }

    /**
     * Returns the recommended retry delay for 429 (Too Many Requests) responses.
     * If the Retry-After header is present, uses that value.
     */
    fun getRetryAfterMs(error: Throwable): Long? {
        if (error is HttpException && error.code() == 429) {
            val retryAfter = error.response()?.headers()?.get("Retry-After")
            return retryAfter?.toLongOrNull()?.times(1000L)
        }
        return null
    }
}
