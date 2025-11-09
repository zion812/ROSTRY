package com.rio.rostry.domain.auth.usecase

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for resending OTP code.
 * 
 * This handles:
 * - Resend cooldown validation
 * - Using ForceResendingToken
 * - Sending new OTP
 * 
 * @property authRepository Repository for authentication operations
 */
class ResendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Resend OTP code to the same phone number
     * 
     * @param activity Android activity for SafetyNet verification
     * @return AuthResult with new VerificationId if successful
     */
    suspend operator fun invoke(
        activity: Activity
    ): AuthResult<VerificationId> {
        return authRepository.resendVerificationCode(activity)
    }
}
