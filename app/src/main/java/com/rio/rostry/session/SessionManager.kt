package com.rio.rostry.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val DATASTORE_NAME = "session_prefs"

val Context.sessionDataStore by preferencesDataStore(name = DATASTORE_NAME)

class SessionManager(private val context: Context) {
    private object Keys {
        val lastAuthAt = longPreferencesKey("last_auth_at")
        val role = stringPreferencesKey("role")
        val authMode = stringPreferencesKey("auth_mode")
        val guestRolePreference = stringPreferencesKey("guest_role_preference")
        val guestSessionStartedAt = longPreferencesKey("guest_session_started_at")
    }

    enum class AuthMode { FIREBASE, GUEST }

    suspend fun markAuthenticated(
        nowMillis: Long,
        userType: UserType,
        mode: AuthMode = AuthMode.FIREBASE
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.lastAuthAt] = nowMillis
            prefs[Keys.role] = userType.name
            prefs[Keys.authMode] = mode.name
        }
    }

    suspend fun updateRole(userType: UserType) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.role] = userType.name
        }
    }

    fun sessionRole(): Flow<UserType?> = context.sessionDataStore.data.map { prefs ->
        val mode = prefs[Keys.authMode]?.let { runCatching { AuthMode.valueOf(it) }.getOrNull() } ?: AuthMode.FIREBASE
        when (mode) {
            AuthMode.GUEST -> prefs[Keys.guestRolePreference]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
            else -> prefs[Keys.role]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
        }
    }

    fun lastAuthAt(): Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.lastAuthAt]
    }

    fun authMode(): Flow<AuthMode> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.authMode]?.let { runCatching { AuthMode.valueOf(it) }.getOrNull() } ?: AuthMode.FIREBASE
    }

    fun isSessionValidFlow(nowMillisProvider: () -> Long): Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        val mode = prefs[Keys.authMode]?.let { runCatching { AuthMode.valueOf(it) }.getOrNull() } ?: AuthMode.FIREBASE
        val role = when (mode) {
            AuthMode.GUEST -> prefs[Keys.guestRolePreference]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
            else -> prefs[Keys.role]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
        } ?: return@map false
        val last = when (mode) {
            AuthMode.GUEST -> prefs[Keys.guestSessionStartedAt]
            else -> prefs[Keys.lastAuthAt]
        } ?: return@map false
        val timeoutDays = when {
            mode == AuthMode.GUEST -> 7L
            role == UserType.GENERAL -> 30L
            else -> 7L
        }
        val timeoutMillis = timeoutDays * 24 * 60 * 60 * 1000
        (nowMillisProvider() - last) < timeoutMillis
    }

    suspend fun signOut() {
        context.sessionDataStore.edit { it.clear() }
    }

    suspend fun markGuestSession(role: UserType, nowMillis: Long) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.authMode] = AuthMode.GUEST.name
            prefs[Keys.guestRolePreference] = role.name
            prefs[Keys.guestSessionStartedAt] = nowMillis
        }
    }

    fun isGuestSession(): Flow<Boolean> = authMode().map { it == AuthMode.GUEST }

    fun getGuestRole(): Flow<UserType?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.guestRolePreference]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
    }

    suspend fun getGuestSessionStartedAt(): Long? {
        return context.sessionDataStore.data.firstOrNull()?.get(Keys.guestSessionStartedAt)
    }

    suspend fun upgradeGuestToAuthenticated(userType: UserType, nowMillis: Long) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.authMode] = AuthMode.FIREBASE.name
            prefs[Keys.role] = userType.name
            prefs[Keys.lastAuthAt] = nowMillis
            prefs.remove(Keys.guestRolePreference)
            prefs.remove(Keys.guestSessionStartedAt)
        }
    }

    suspend fun refreshAuthToken() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                // Force refresh of the ID token to get new custom claims
                val result = user.getIdToken(true).await()
                val claims = result.claims
                val roleString = claims["role"] as? String

                if (roleString != null) {
                    val userType = runCatching { UserType.valueOf(roleString) }.getOrNull()
                    if (userType != null) {
                        updateRole(userType)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}