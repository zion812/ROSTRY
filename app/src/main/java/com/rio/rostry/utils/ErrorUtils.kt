package com.rio.rostry.utils

import com.rio.rostry.viewmodel.AuthError
import com.rio.rostry.viewmodel.FowlError
import com.rio.rostry.viewmodel.MarketplaceError
import com.rio.rostry.repository.AuthErrorType
import com.rio.rostry.repository.DataErrorType

object ErrorUtils {
    
    fun getFriendlyAuthErrorMessage(error: AuthError): String {
        return when (error.type) {
            AuthErrorType.INVALID_CREDENTIALS -> {
                if (error.message.contains("password", ignoreCase = true)) {
                    "Incorrect password. Please try again."
                } else if (error.message.contains("email", ignoreCase = true)) {
                    "Invalid email address. Please check and try again."
                } else {
                    "Invalid email or password. Please try again."
                }
            }
            AuthErrorType.USER_NOT_FOUND -> {
                "No account found with this email address. Please check your email or sign up."
            }
            AuthErrorType.EMAIL_ALREADY_EXISTS -> {
                "An account already exists with this email address. Please try logging in instead."
            }
            AuthErrorType.NETWORK_ERROR -> {
                "Network error. Please check your internet connection and try again."
            }
            AuthErrorType.UNKNOWN_ERROR -> {
                error.message.ifEmpty { "An unknown error occurred. Please try again." }
            }
        }
    }
    
    fun getFriendlyFowlErrorMessage(error: FowlError): String {
        return when (error.type) {
            DataErrorType.NOT_FOUND -> {
                "No fowls found. Add your first fowl to get started!"
            }
            DataErrorType.DATABASE_ERROR -> {
                "Unable to connect to database. Please check your internet connection and try again."
            }
            DataErrorType.DATA_CORRUPTION -> {
                "Data corruption detected. Please contact support."
            }
            DataErrorType.UNKNOWN_ERROR -> {
                error.message.ifEmpty { "An unexpected error occurred. Please try again." }
            }
        }
    }
    
    fun getFriendlyMarketplaceErrorMessage(error: MarketplaceError): String {
        return when (error.type) {
            DataErrorType.NOT_FOUND -> {
                "No listings found. Check back later for new listings!"
            }
            DataErrorType.DATABASE_ERROR -> {
                "Unable to connect to database. Please check your internet connection and try again."
            }
            DataErrorType.DATA_CORRUPTION -> {
                "Data corruption detected. Please contact support."
            }
            DataErrorType.UNKNOWN_ERROR -> {
                error.message.ifEmpty { "An unexpected error occurred. Please try again." }
            }
        }
    }
    
    fun getFriendlyErrorMessage(throwable: Throwable): String {
        return when {
            throwable is AuthError -> getFriendlyAuthErrorMessage(throwable)
            throwable is FowlError -> getFriendlyFowlErrorMessage(throwable)
            throwable is MarketplaceError -> getFriendlyMarketplaceErrorMessage(throwable)
            else -> throwable.message ?: "An unexpected error occurred."
        }
    }
}