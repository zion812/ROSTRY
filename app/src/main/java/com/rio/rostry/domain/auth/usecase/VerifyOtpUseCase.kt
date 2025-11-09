package com.rio.rostry.domain.auth.usecase

import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for verifying OTP code.
 * 
 * This handles:
 * - OTP validation
 * - Firebase credential verification
 * - User sign-in
 * 
 * @property authRepository Repository for authentication operations
 */
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Verify OTP code and sign in user
     * 
     * @param verificationId Verification ID from phone auth
     * @param otpCode 6-digit OTP code
     * @return AuthResult indicating success or failure
     */
    suspend operator fun invoke(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit> {
        return authRepository.verifyOtp(verificationId, otpCode)
    }
}
