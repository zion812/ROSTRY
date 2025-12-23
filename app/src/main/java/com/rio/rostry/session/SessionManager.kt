package com.rio.rostry.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    // Flag to prevent auto-sync during an active role upgrade
    // When true, SessionViewModel should NOT react to role changes from userRepository
    private val _upgradeInProgress = MutableStateFlow(false)
    val upgradeInProgress: StateFlow<Boolean> = _upgradeInProgress.asStateFlow()

    fun setUpgradeInProgress(inProgress: Boolean) {
        _upgradeInProgress.value = inProgress
    }

    suspend fun markAuthenticated(
        nowMillis: Long,
        userType: UserType,
        mode: AuthMode = AuthMode.FIREBASE
    ) {
        val currentData = context.sessionDataStore.data.firstOrNull()
        val currentRole = currentData?.get(Keys.role)
        val currentMode = currentData?.get(Keys.authMode)
        val lastAuth = currentData?.get(Keys.lastAuthAt)
        
        // Only edit if something changed (avoiding millisecond precision jitter)
        if (currentRole == userType.name && currentMode == mode.name && lastAuth != null && (nowMillis - lastAuth) < 1000) {
            return
        }

        context.sessionDataStore.edit { prefs ->
            prefs[Keys.lastAuthAt] = nowMillis
            prefs[Keys.role] = userType.name
            prefs[Keys.authMode] = mode.name
        }
    }

    suspend fun updateRole(userType: UserType) {
        val current = context.sessionDataStore.data.firstOrNull()?.get(Keys.role)
        if (current == userType.name) return
        
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
    }.distinctUntilChanged()

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

    /**
     * Refreshes the Firebase auth token.
     * 
     * ZERO-COST ARCHITECTURE: We no longer rely on Cloud Functions to set custom claims.
     * Roles are now read directly from the user document in Firestore/Room.
     * This eliminates the Cloud Functions cost (₹775+/month saved).
     * 
     * The role should be updated by calling updateRole() after fetching the user document,
     * not from token claims.
     */
    suspend fun refreshAuthToken() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                // Refresh the ID token to maintain auth session
                // We no longer extract role from claims - role comes from Firestore/Room
                user.getIdToken(true).await()
                
                // Note: Role updates should come from UserRepository.getCurrentUser()
                // which reads from Firestore and syncs to Room
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}