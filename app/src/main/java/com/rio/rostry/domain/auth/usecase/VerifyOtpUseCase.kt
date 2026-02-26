package com.rio.rostry.domain.auth.usecase

import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.UserFriendlyError
import com.rio.rostry.domain.auth.model.VerificationId
import javax.inject.Inject

/**
 * Use case for verifying an OTP code.
 *
 * Phone authentication has been deprecated in ROSTRY (Google Sign-In is the primary auth method).
 * This stub exists to prevent compilation errors from ViewModels that still reference it.
 * These ViewModels should be removed as part of the deprecated code cleanup (Task 26).
 */
class VerifyOtpUseCase @Inject constructor() {
    suspend operator fun invoke(verificationId: VerificationId, otpCode: OtpCode): AuthResult<Unit> {
        return AuthResult.Error(UserFriendlyError.PhoneAuthDisabled)
    }
}
