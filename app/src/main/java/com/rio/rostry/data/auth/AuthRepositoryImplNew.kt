package com.rio.rostry.data.auth

import com.rio.rostry.data.auth.source.FirebaseAuthDataSource
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImplNew @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {
    
    override val isAuthenticated: Flow<Boolean> =
        firebaseAuthDataSource.isAuthenticated
    
    override suspend fun signInWithGoogle(idToken: String): AuthResult<Unit> {
        Log.d("AuthRepositoryNew", "Signing in with Google")
        return firebaseAuthDataSource.signInWithGoogle(idToken)
    }
    
    override suspend fun signOut(): AuthResult<Unit> {
        Log.d("AuthRepositoryNew", "Signing out")
        return firebaseAuthDataSource.signOut()
    }
}
