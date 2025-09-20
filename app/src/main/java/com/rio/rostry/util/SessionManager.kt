package com.rio.rostry.util

import android.content.Context
import android.content.SharedPreferences
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("rostry_session", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    companion object {
        private const val PREF_USER_ID = "user_id"
        private const val PREF_USER_PHONE = "user_phone"
        private const val PREF_USER_EMAIL = "user_email"
        private const val PREF_USER_TYPE = "user_type"
        private const val PREF_LOGIN_TIME = "login_time"
        private const val PREF_SESSION_TIMEOUT_GENERAL = 30 * 24 * 60 * 60 * 1000L // 30 days
        private const val PREF_SESSION_TIMEOUT_OTHER = 7 * 24 * 60 * 60 * 1000L // 7 days
    }

    init {
        loadUserFromPreferences()
    }

    /**
     * Save user session
     */
    fun saveUserSession(user: User) {
        _currentUser.value = user
        with(sharedPreferences.edit()) {
            putString(PREF_USER_ID, user.id)
            putString(PREF_USER_PHONE, user.phone)
            putString(PREF_USER_EMAIL, user.email)
            putString(PREF_USER_TYPE, user.userType.name)
            putLong(PREF_LOGIN_TIME, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Load user from preferences
     */
    private fun loadUserFromPreferences() {
        val userId = sharedPreferences.getString(PREF_USER_ID, null) ?: return
        val phone = sharedPreferences.getString(PREF_USER_PHONE, "") ?: ""
        val email = sharedPreferences.getString(PREF_USER_EMAIL, null)
        val userTypeStr = sharedPreferences.getString(PREF_USER_TYPE, UserType.GENERAL.name)
        val loginTime = sharedPreferences.getLong(PREF_LOGIN_TIME, 0L)

        // Check if session is still valid
        if (isSessionValid(userTypeStr?.let { UserType.valueOf(it) }, loginTime)) {
            val user = User(
                id = userId,
                phone = phone,
                email = email,
                userType = userTypeStr?.let { UserType.valueOf(it) } ?: UserType.GENERAL,
                createdAt = Date(),
                updatedAt = Date()
            )
            _currentUser.value = user
        } else {
            // Session expired, clear preferences
            clearUserSession()
        }
    }

    /**
     * Check if session is valid based on user type and login time
     */
    private fun isSessionValid(userType: UserType?, loginTime: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeout = if (userType == UserType.GENERAL) {
            PREF_SESSION_TIMEOUT_GENERAL
        } else {
            PREF_SESSION_TIMEOUT_OTHER
        }
        return (currentTime - loginTime) < timeout
    }

    /**
     * Clear user session
     */
    fun clearUserSession() {
        _currentUser.value = null
        with(sharedPreferences.edit()) {
            remove(PREF_USER_ID)
            remove(PREF_USER_PHONE)
            remove(PREF_USER_EMAIL)
            remove(PREF_USER_TYPE)
            remove(PREF_LOGIN_TIME)
            apply()
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return _currentUser.value != null
    }

    /**
     * Get current user
     */
    fun getUser(): User? {
        return _currentUser.value
    }
}