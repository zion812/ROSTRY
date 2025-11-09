package com.rio.rostry.domain.auth.usecase

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for linking phone number to existing user account.
 * Used when user signs in with Google/Email and needs to add phone.
 * 
 * @property authRepository Repository for authentication operations
 */
class LinkPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Start phone linking process
     * 
     * @param activity Android activity for SafetyNet verification
     * @param phoneNumber Phone number to link
     * @return AuthResult with VerificationId if successful
     */
    suspend fun startLinking(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId> {
        return authRepository.linkPhoneToCurrentUser(activity, phoneNumber)
    }
    
    /**
     * Complete phone linking with OTP verification
     * 
     * @param verificationId Verification ID from phone linking
     * @param otpCode 6-digit OTP code
     * @return AuthResult indicating success or failure
     */
    suspend fun completeLinking(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit> {
        return authRepository.verifyOtpAndLink(verificationId, otpCode)
    }
}
