package com.rio.rostry.data.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rio.rostry.R
import com.rio.rostry.security.SecurityManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google Sign-In authentication for Free Tier mode.
 * 
 * This class handles the Google Sign-In flow and Firebase credential exchange.
 * Phone Auth is disabled on Firebase Free Tier (Spark Plan).
 */
@Singleton
class GoogleAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    
    /**
     * Get the sign-in intent to launch with an ActivityResultLauncher.
     */
    fun getSignInIntent(): Intent = googleSignInClient.signInIntent
    
    /**
     * Handle the result from Google Sign-In activity.
     * 
     * @param data The result Intent from the sign-in activity
     * @return Resource indicating success or failure with Firebase user
     */
    suspend fun handleSignInResult(data: Intent?): Resource<Unit> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            
            if (idToken == null) {
                SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to "No ID token"))
                return Resource.Error("Failed to get ID token from Google")
            }
            
            signInWithIdToken(idToken)
        } catch (e: ApiException) {
            Timber.e(e, "Google Sign-In failed with code: ${e.statusCode}")
            SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to e.message, "code" to e.statusCode))
            Resource.Error("Google Sign-In failed: ${e.localizedMessage}")
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during Google Sign-In")
            SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to e.message))
            Resource.Error("Sign-in failed: ${e.localizedMessage}")
        }
    }
    
    /**
     * Sign in to Firebase using a Google ID token.
     * 
     * @param idToken The ID token from Google Sign-In
     * @return Resource indicating success or failure
     */
    suspend fun signInWithIdToken(idToken: String): Resource<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            
            if (result.user != null) {
                // Force token refresh to ensure claims are up-to-date
                result.user?.getIdToken(true)?.await()
                Timber.d("Google Sign-In successful: ${result.user?.uid}")
                SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf(
                    "result" to "success",
                    "uid" to result.user?.uid,
                    "isNewUser" to result.additionalUserInfo?.isNewUser
                ))
                Resource.Success(Unit)
            } else {
                SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to "user is null"))
                Resource.Error("Sign-in failed: user is null")
            }
        } catch (e: Exception) {
            Timber.e(e, "Firebase credential sign-in failed")
            SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to e.message))
            Resource.Error("Sign-in failed: ${e.localizedMessage}")
        }
    }
    
    /**
     * Sign out from both Firebase and Google.
     */
    suspend fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            googleSignInClient.signOut().await()
            SecurityManager.audit("AUTH_GOOGLE_SIGNOUT", mapOf("success" to true))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Sign-out failed")
            Resource.Error("Sign-out failed: ${e.localizedMessage}")
        }
    }
    
    /**
     * Check if there's a currently signed-in Google account.
     */
    fun getLastSignedInAccount() = GoogleSignIn.getLastSignedInAccount(context)
}
