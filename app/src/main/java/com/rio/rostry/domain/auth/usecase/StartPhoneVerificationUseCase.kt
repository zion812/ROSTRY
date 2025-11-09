package com.rio.rostry.domain.auth.usecase

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for starting phone number verification.
 * 
 * This handles:
 * - Phone number validation
 * - Rate limiting check
 * - Sending OTP via Firebase
 * 
 * @property authRepository Repository for authentication operations
 */
class StartPhoneVerificationUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Start phone verification process
     * 
     * @param activity Android activity for SafetyNet verification
     * @param phoneNumber Phone number in E.164 format
     * @return AuthResult with VerificationId if successful
     */
    suspend operator fun invoke(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId> {
        return authRepository.startPhoneVerification(activity, phoneNumber)
    }
}
