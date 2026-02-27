package com.rio.rostry.domain.error

import com.rio.rostry.data.resilience.CircuitOpenException
import com.rio.rostry.data.resilience.RetryBudgetExhaustedException
import com.google.firebase.firestore.FirebaseFirestoreException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps technical errors to specific, actionable user-facing messages.
 * 
 * Replaces generic "Something went wrong" with context-appropriate messages.
 * Never exposes stack traces, class names, or technical internals.
 */
object UserFacingErrorMapper {

    /**
     * Get a user-friendly error message for display in the UI.
     */
    fun getMessage(error: Throwable, context: String? = null): String {
        return when (error) {
            is SocketTimeoutException ->
                "Connection timed out. Please check your internet and try again."

            is UnknownHostException ->
                "No internet connection. Your data is saved locally and will sync when you're back online."

            is java.net.ConnectException ->
                "Unable to reach the server. Please check your connection."

            is IOException ->
                "A network error occurred. Please try again."

            is HttpException -> getHttpErrorMessage(error, context)

            is CircuitOpenException ->
                "This service is temporarily unavailable. Please try again in a few minutes."

            is RetryBudgetExhaustedException ->
                "Too many requests. Please wait a moment before trying again."

            is FirebaseFirestoreException -> getFirestoreErrorMessage(error)

            is SecurityException ->
                "You don't have permission to perform this action. Please contact support if you believe this is an error."

            is IllegalArgumentException ->
                error.message?.let { sanitizeForUser(it) }
                    ?: "Invalid input. Please check your data and try again."

            is IllegalStateException ->
                "This action cannot be completed right now. Please try again later."

            is OutOfMemoryError ->
                "Your device is running low on memory. Please close some apps and try again."

            else ->
                "Something unexpected happened. Please try again or contact support."
        }
    }

    /**
     * Get a short title for error display (e.g., in snackbars or dialog titles).
     */
    fun getTitle(error: Throwable): String {
        return when (error) {
            is SocketTimeoutException, is UnknownHostException, is java.net.ConnectException ->
                "Connection Error"
            is IOException -> "Network Error"
            is HttpException -> "Server Error"
            is FirebaseFirestoreException -> "Data Error"
            is CircuitOpenException -> "Service Unavailable"
            is SecurityException -> "Permission Denied"
            is IllegalArgumentException -> "Invalid Input"
            is OutOfMemoryError -> "Device Error"
            else -> "Error"
        }
    }

    /**
     * Determine if a retry action should be offered for this error.
     */
    fun shouldOfferRetry(error: Throwable): Boolean {
        return when (error) {
            is SocketTimeoutException, is UnknownHostException, is java.net.ConnectException -> true
            is IOException -> true
            is CircuitOpenException -> true
            is HttpException -> error.code() in listOf(408, 429, 500, 502, 503, 504)
            is FirebaseFirestoreException -> error.code in listOf(
                FirebaseFirestoreException.Code.UNAVAILABLE,
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED
            )
            else -> false
        }
    }

    private fun getHttpErrorMessage(error: HttpException, context: String?): String {
        return when (error.code()) {
            400 -> "The request contained invalid data. Please check your input."
            401 -> "Your session has expired. Please sign in again."
            403 -> "You don't have permission to access this resource."
            404 -> "${context ?: "The requested item"} could not be found."
            409 -> "A conflict occurred. Someone may have modified this data. Please refresh and try again."
            422 -> "The data you provided is incomplete or invalid."
            429 -> "Too many requests. Please wait a moment before trying again."
            500, 502, 503 -> "The server is experiencing issues. Please try again in a few minutes."
            504 -> "The server took too long to respond. Please try again."
            else -> "Server error (${error.code()}). Please try again later."
        }
    }

    private fun getFirestoreErrorMessage(error: FirebaseFirestoreException): String {
        return when (error.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "You don't have permission to access this data. Try signing out and back in."
            FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                "Your session has expired. Please sign in again."
            FirebaseFirestoreException.Code.NOT_FOUND ->
                "The requested data could not be found."
            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "The service is temporarily unavailable. Please try again in a moment."
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                "The request took too long. Please check your connection and try again."
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED ->
                "Too many requests. Please wait a moment before trying again."
            FirebaseFirestoreException.Code.ALREADY_EXISTS ->
                "This item already exists."
            FirebaseFirestoreException.Code.CANCELLED ->
                "The operation was cancelled."
            else ->
                "A data error occurred. Please try again."
        }
    }

    /**
     * Remove technical details from error messages before showing to users.
     */
    private fun sanitizeForUser(message: String): String {
        // Remove class names, package names, stack traces
        var sanitized = message
            .replace(Regex("\\b[a-z]+\\.[a-z]+\\.[A-Z]\\w+"), "") // package.Class references
            .replace(Regex("at \\S+\\(\\S+:\\d+\\)"), "")         // stack trace lines
            .replace(Regex("\\s+"), " ")                           // normalize whitespace
            .trim()

        // If message becomes empty or too short after sanitization, use generic
        if (sanitized.length < 10) {
            sanitized = "Invalid input. Please check your data and try again."
        }

        return sanitized
    }
}
