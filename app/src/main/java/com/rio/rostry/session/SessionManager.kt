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
    }

    suspend fun markAuthenticated(nowMillis: Long, userType: UserType) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.lastAuthAt] = nowMillis
            prefs[Keys.role] = userType.name
        }
    }

    fun sessionRole(): Flow<UserType?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.role]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
    }

    fun lastAuthAt(): Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.lastAuthAt]
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
