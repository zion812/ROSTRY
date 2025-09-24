package com.rio.rostry.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "session_prefs"

val Context.sessionDataStore by preferencesDataStore(name = DATASTORE_NAME)

class SessionManager(private val context: Context) {
    private object Keys {
        val lastAuthAt = longPreferencesKey("last_auth_at")
        val role = stringPreferencesKey("role")
        val authMode = stringPreferencesKey("auth_mode")
        val demoUserId = stringPreferencesKey("demo_user_id")
    }

    enum class AuthMode { FIREBASE, DEMO }

    suspend fun markAuthenticated(
        nowMillis: Long,
        userType: UserType,
        mode: AuthMode = AuthMode.FIREBASE,
        demoUserId: String? = null
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.lastAuthAt] = nowMillis
            prefs[Keys.role] = userType.name
            prefs[Keys.authMode] = mode.name
            if (mode == AuthMode.DEMO && demoUserId != null) {
                prefs[Keys.demoUserId] = demoUserId
            } else {
                prefs.remove(Keys.demoUserId)
            }
        }
    }

    fun sessionRole(): Flow<UserType?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.role]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
    }

    fun lastAuthAt(): Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.lastAuthAt]
    }

    fun authMode(): Flow<AuthMode> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.authMode]?.let { runCatching { AuthMode.valueOf(it) }.getOrNull() } ?: AuthMode.FIREBASE
    }

    fun currentDemoUserId(): Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.demoUserId]
    }

    fun isSessionValidFlow(nowMillisProvider: () -> Long): Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        val role = prefs[Keys.role]?.let { runCatching { UserType.valueOf(it) }.getOrNull() } ?: return@map false
        val last = prefs[Keys.lastAuthAt] ?: return@map false
        val timeoutDays = when (role) {
            UserType.GENERAL -> 30L
            UserType.FARMER, UserType.ENTHUSIAST -> 7L
        }
        val timeoutMillis = timeoutDays * 24 * 60 * 60 * 1000
        (nowMillisProvider() - last) < timeoutMillis
    }

    suspend fun signOut() {
        context.sessionDataStore.edit { it.clear() }
    }
}
