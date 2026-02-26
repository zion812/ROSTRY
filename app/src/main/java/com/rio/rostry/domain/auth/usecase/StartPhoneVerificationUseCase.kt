package com.rio.rostry.domain.auth.usecase

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.UserFriendlyError
import com.rio.rostry.domain.auth.model.VerificationId
import javax.inject.Inject

/**
 * Use case for starting phone verification.
 *
 * Phone authentication has been deprecated in ROSTRY (Google Sign-In is the primary auth method).
 * This stub exists to prevent compilation errors from ViewModels that still reference it.
 * These ViewModels should be removed as part of the deprecated code cleanup (Task 26).
 */
class StartPhoneVerificationUseCase @Inject constructor() {
    suspend operator fun invoke(activity: Activity, phoneNumber: PhoneNumber): AuthResult<VerificationId> {
        return AuthResult.Error(UserFriendlyError.PhoneAuthDisabled)
    }
}
