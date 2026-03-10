package com.rio.rostry.domain.account.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User

/**
 * Use case for signing in with phone number and OTP.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines authentication use case interface.
 */
interface SignInWithPhoneUseCase {
    /**
     * Sign in with phone number and OTP.
     * @param phoneNumber The phone number to authenticate
     * @param otp The one-time password
     * @return Result containing the authenticated user or error
     */
    suspend operator fun invoke(phoneNumber: String, otp: String): Result<User>
}
