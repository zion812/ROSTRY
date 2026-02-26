package com.rio.rostry.data.auth.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rio.rostry.data.auth.mapper.ErrorMapper
import com.rio.rostry.domain.auth.model.AuthResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    
    val isAuthenticated: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
    
    suspend fun signInWithGoogle(idToken: String): AuthResult<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            if (result.user != null) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(ErrorMapper.mapFirebaseError(Exception("User is null after sign-in")))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthDataSource", "Google sign-in failed", e)
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    suspend fun signOut(): AuthResult<Unit> {
        return try {
            firebaseAuth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthDataSource", "Sign-out failed", e)
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
}
