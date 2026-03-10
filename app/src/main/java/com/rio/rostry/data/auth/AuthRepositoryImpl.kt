package com.rio.rostry.data.auth

import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rio.rostry.domain.auth.AuthEvent
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val context: Context
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override val isAuthenticated: Flow<Boolean> = auth.authStateFlow().map { it != null }

    private val _events = MutableSharedFlow<AuthEvent>()
    override val events = _events.asSharedFlow()

    private val _currentVerificationId = MutableStateFlow<String?>(null)
    override val currentVerificationId = _currentVerificationId.asStateFlow()

    private val _currentPhoneE164 = MutableStateFlow<String?>(null)
    override val currentPhoneE164 = _currentPhoneE164.asStateFlow()

    override suspend fun signInWithGoogle(idToken: String): Resource<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            
            if (user != null) {
                android.util.Log.i("AuthRepository", "Google Sign-In successful for user: ${user.uid}")
                Resource.Success(Unit)
            } else {
                Resource.Error("Sign-in failed: User is null")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Google Sign-In failed", e)
            Resource.Error(e.message ?: "Google Sign-In failed")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            auth.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Sign-out failed", e)
            Resource.Error(e.message ?: "Sign-out failed")
        }
    }
}

/**
 * Extension to observe auth state changes
 */
fun FirebaseAuth.authStateFlow(): Flow<com.google.firebase.auth.FirebaseUser?> = kotlinx.coroutines.flow.callbackFlow {
    val listener = FirebaseAuth.AuthStateListener {
        trySend(it.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}
