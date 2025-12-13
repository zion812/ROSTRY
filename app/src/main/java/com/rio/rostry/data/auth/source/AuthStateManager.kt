package com.rio.rostry.data.auth.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension for DataStore
 * Note: This DataStore specifically handles phone verification state (verification ID, phone number, linking mode).
 * See AuthRepositoryImpl.kt for session-related auth preferences ("auth_prefs").
 */
private val Context.authVerificationDataStore by preferencesDataStore(name = "auth_state")

/**
 * Manages authentication state persistence using DataStore.
 * Handles storing/retrieving verification state for process death recovery.
 * This is specifically for phone verification state (verification ID, phone number, linking mode).
 * Session-related auth preferences are managed in AuthRepositoryImpl using "auth_prefs".
 */
@Singleton
class AuthStateManager @Inject constructor(
    private val context: Context
) {
    
    private companion object {
        val KEY_VERIFICATION_ID = stringPreferencesKey("verification_id")
        val KEY_PHONE_NUMBER = stringPreferencesKey("phone_number")
        val KEY_IS_LINKING_MODE = stringPreferencesKey("is_linking_mode")
    }
    
    /**
     * Save verification state to DataStore
     * 
     * @param verificationId Firebase verification ID
     * @param phoneNumber Phone number being verified
     * @param isLinkingMode Whether this is a phone linking flow
     */
    suspend fun saveVerificationState(
        verificationId: VerificationId,
        phoneNumber: PhoneNumber,
        isLinkingMode: Boolean = false
    ) {
        try {
            context.authVerificationDataStore.edit { prefs ->
                prefs[KEY_VERIFICATION_ID] = verificationId.value
                prefs[KEY_PHONE_NUMBER] = phoneNumber.value
                prefs[KEY_IS_LINKING_MODE] = isLinkingMode.toString()
            }
            Timber.d("Auth state saved: phone=${phoneNumber.masked()}, linking=$isLinkingMode")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save auth state")
        }
    }
    
    /**
     * Get saved verification ID
     *
     * @return VerificationId if exists, null otherwise
     */
    suspend fun getVerificationId(): VerificationId? {
        return try {
            context.authVerificationDataStore.data
                .map { prefs -> prefs[KEY_VERIFICATION_ID] }
                .first()
                ?.let { VerificationId(it) }
        } catch (e: Exception) {
            Timber.w(e, "Failed to get verification ID")
            null
        }
    }
    
    /**
     * Get saved phone number
     *
     * @return PhoneNumber if exists and valid, null otherwise
     */
    suspend fun getPhoneNumber(): PhoneNumber? {
        return try {
            context.authVerificationDataStore.data
                .map { prefs -> prefs[KEY_PHONE_NUMBER] }
                .first()
                ?.let {
                    runCatching { PhoneNumber(it) }.getOrNull()
                }
        } catch (e: Exception) {
            Timber.w(e, "Failed to get phone number")
            null
        }
    }
    
    /**
     * Check if current flow is phone linking mode
     *
     * @return true if linking mode, false otherwise
     */
    suspend fun isLinkingMode(): Boolean {
        return try {
            context.authVerificationDataStore.data
                .map { prefs -> prefs[KEY_IS_LINKING_MODE] }
                .first()
                ?.toBoolean() ?: false
        } catch (e: Exception) {
            Timber.w(e, "Failed to get linking mode")
            false
        }
    }
    
    /**
     * Clear all verification state
     */
    suspend fun clearVerificationState() {
        try {
            context.authVerificationDataStore.edit { prefs ->
                prefs.remove(KEY_VERIFICATION_ID)
                prefs.remove(KEY_PHONE_NUMBER)
                prefs.remove(KEY_IS_LINKING_MODE)
            }
            Timber.d("Auth state cleared")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear auth state")
        }
    }
}
