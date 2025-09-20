package com.rio.rostry.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

/**
 * Utility class for handling biometric authentication
 */
object BiometricAuthUtil {

    /**
     * Check if biometric authentication is available
     * @param context The application context
     * @return true if biometric auth is available, false otherwise
     */
    fun isBiometricAuthAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Timber.d("Biometric auth not available: ${biometricManager.canAuthenticate()}")
                false
            }
            else -> false
        }
    }

    /**
     * Show biometric authentication prompt
     * @param activity The fragment activity
     * @param title The title for the biometric prompt
     * @param subtitle The subtitle for the biometric prompt
     * @param onAuthenticationSuccess Callback when authentication succeeds
     * @param onAuthenticationError Callback when authentication fails
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onAuthenticationSuccess: () -> Unit,
        onAuthenticationError: (Int, String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.d("Biometric authentication succeeded")
                    onAuthenticationSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Timber.e("Biometric authentication error: $errorCode - $errString")
                    onAuthenticationError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Timber.e("Biometric authentication failed")
                    onAuthenticationError(
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        "Authentication failed"
                    )
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}