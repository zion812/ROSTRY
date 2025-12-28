package com.rio.rostry.data.auth

import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rio.rostry.data.database.dao.RateLimitDao
import com.rio.rostry.data.database.entity.RateLimitEntity
import com.rio.rostry.domain.auth.AuthEvent
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.security.SecurityManager
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

// ============================================================
// FREE TIER MODE: Phone Auth is disabled. Use Google Sign-In.
// To re-enable Phone Auth, upgrade to Firebase Blaze Plan and
// remove the PHONE_AUTH_DISABLED flag below.
// ============================================================
private const val PHONE_AUTH_DISABLED = true
private const val PHONE_AUTH_DISABLED_MESSAGE = "Phone authentication is disabled in Free Tier mode. Please use 'Sign in with Google' instead."

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val rateLimitDao: RateLimitDao,
    private val sessionManager: SessionManager,
    private val googleAuthManager: GoogleAuthManager,
    private val context: Context
) : AuthRepository {

    private val _isAuthenticated = MutableStateFlow(isFirebaseUserValid())
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated

    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 8)
    override val events: Flow<AuthEvent> = _events

    private val _currentVerificationId = MutableStateFlow<String?>(null)
    override val currentVerificationId: StateFlow<String?> = _currentVerificationId

    private val _currentPhoneE164 = MutableStateFlow<String?>(null)
    override val currentPhoneE164: StateFlow<String?> = _currentPhoneE164

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var isLinkingMode: Boolean = false

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Restore persisted verification state on initialization
        scope.launch {
            runCatching {
                val prefs = context.authDataStore.data.first()
                val vid = prefs[stringPreferencesKey("auth_verification_id")]
                val phone = prefs[stringPreferencesKey("auth_phone_e164")]
                if (!vid.isNullOrBlank()) _currentVerificationId.value = vid
                if (!phone.isNullOrBlank()) _currentPhoneE164.value = phone
            }.onFailure { Timber.w(it, "Failed to restore verification state") }
        }
    }

    // ============================================================
    // Google Sign-In (Free Tier Primary Auth)
    // ============================================================
    
    override suspend fun signInWithGoogle(idToken: String): Resource<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            
            if (result.user != null) {
                // Force token refresh to ensure claims are up-to-date
                result.user?.getIdToken(true)?.await()
                _isAuthenticated.value = isFirebaseUserValid()
                _events.tryEmit(AuthEvent.GoogleSignInSuccess)
                
                Timber.d("Google Sign-In successful: ${result.user?.uid}")
                SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf(
                    "result" to "success",
                    "uid" to result.user?.uid,
                    "isNewUser" to result.additionalUserInfo?.isNewUser
                ))
                Resource.Success(Unit)
            } else {
                _events.tryEmit(AuthEvent.GoogleSignInFailed("Sign-in failed: user is null"))
                SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to "user is null"))
                Resource.Error("Sign-in failed: user is null")
            }
        } catch (e: Exception) {
            Timber.e(e, "Google Sign-In failed")
            _events.tryEmit(AuthEvent.GoogleSignInFailed(e.message ?: "Unknown error"))
            SecurityManager.audit("AUTH_GOOGLE_SIGNIN", mapOf("result" to "failure", "error" to e.message))
            Resource.Error("Sign-in failed: ${e.localizedMessage}")
        }
    }

    // ============================================================
    // Phone Auth (DISABLED on Free Tier - Requires Blaze Plan)
    // ============================================================
    
    @Suppress("DEPRECATION")
    override suspend fun startPhoneVerification(activity: Activity, phoneE164: String): Resource<Unit> {
        // FREE TIER: Phone Auth disabled
        if (PHONE_AUTH_DISABLED) {
            Timber.w("Phone Auth is disabled in Free Tier mode")
            SecurityManager.audit("AUTH_PHONE_DISABLED", mapOf("action" to "startPhoneVerification"))
            return Resource.Error(PHONE_AUTH_DISABLED_MESSAGE)
        }
        
        val userId = phoneE164 // Use phone as userId for rate limiting
        val now = System.currentTimeMillis()
        val limit = rateLimitDao.get(userId, "AUTH_SEND_OTP")
        if (limit != null && (now - limit.lastAt) < 60000) {
            val remaining = 60 - ((now - limit.lastAt) / 1000)
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf("action" to "AUTH_SEND_OTP", "phone" to maskPhone(phoneE164), "remaining" to remaining))
            return Resource.Error("Rate limit exceeded. Try again in $remaining seconds.")
        }
        return try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    scope.launch {
                        firebaseAuth.signInWithCredential(credential).await()
                        if (firebaseAuth.currentUser != null) {
                            _isAuthenticated.value = isFirebaseUserValid()
                            _events.tryEmit(AuthEvent.AutoVerified)
                            clearVerificationState()
                            SecurityManager.audit("AUTH_VERIFICATION_COMPLETED", mapOf("success" to true))
                        } else {
                            Timber.e("Auto sign-in failed")
                            SecurityManager.audit("AUTH_VERIFICATION_COMPLETED", mapOf("success" to false, "error" to "user is null"))
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Timber.e(e, "Phone verification failed")
                    _events.tryEmit(AuthEvent.VerificationFailed(mapError(e)))
                    SecurityManager.audit("AUTH_VERIFICATION_FAILED", mapOf("error" to e.message))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _currentVerificationId.value = verificationId
                    _currentPhoneE164.value = phoneE164
                    resendToken = token
                    Timber.d("OTP code sent; verificationId stored")
                    _events.tryEmit(AuthEvent.CodeSent(verificationId))
                    // Persist to DataStore
                    scope.launch { persistVerificationState(verificationId, phoneE164) }
                    SecurityManager.audit("AUTH_CODE_SENT", mapOf("phone" to maskPhone(phoneE164), "verificationId" to verificationId))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneE164)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            // Update rate limit after successful request
            rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_SEND_OTP", userId = userId, action = "AUTH_SEND_OTP", lastAt = now))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "startPhoneVerification exception")
            Resource.Error("Failed to start verification: ${e.message}")
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun verifyOtp(verificationId: String, otpCode: String): Resource<Unit> {
        // FREE TIER: Phone Auth disabled
        if (PHONE_AUTH_DISABLED) {
            Timber.w("Phone Auth is disabled in Free Tier mode")
            SecurityManager.audit("AUTH_PHONE_DISABLED", mapOf("action" to "verifyOtp"))
            return Resource.Error(PHONE_AUTH_DISABLED_MESSAGE)
        }
        
        // Repository-level rate limit per phone/user for OTP verification attempts
        val phone = _currentPhoneE164.value ?: ""
        val userId = if (phone.isNotBlank()) phone else verificationId
        val now = System.currentTimeMillis()
        val limit = rateLimitDao.get(userId, "AUTH_VERIFY_OTP")
        if (limit != null && (now - limit.lastAt) < 60000) {
            val remaining = 60 - ((now - limit.lastAt) / 1000)
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf("action" to "AUTH_VERIFY_OTP", "remaining" to remaining))
            return Resource.Error("Rate limit exceeded. Try again in $remaining seconds.")
        }
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            val result = firebaseAuth.signInWithCredential(credential).await()
            if (result.user != null) {
                _isAuthenticated.value = isFirebaseUserValid()
                clearVerificationState()
                SecurityManager.audit("AUTH_OTP_VERIFIED", mapOf("result" to "success"))
                // Update rate limit record on attempt/success
                rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_VERIFY_OTP", userId = userId, action = "AUTH_VERIFY_OTP", lastAt = now))
                Resource.Success(Unit)
            } else {
                SecurityManager.audit("AUTH_OTP_VERIFIED", mapOf("result" to "failure", "reason" to "user is null"))
                rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_VERIFY_OTP", userId = userId, action = "AUTH_VERIFY_OTP", lastAt = now))
                Resource.Error("Verification failed: user is null")
            }
        } catch (e: Exception) {
            Timber.e(e, "verifyOtp exception")
            SecurityManager.audit("AUTH_OTP_VERIFIED", mapOf("result" to "failure", "error" to e.message))
            // Record attempt even on failure
            runCatching { rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_VERIFY_OTP", userId = userId, action = "AUTH_VERIFY_OTP", lastAt = now)) }
            Resource.Error("Failed to verify OTP: ${e.message}")
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun resendVerificationCode(activity: Activity): Resource<Unit> {
        // FREE TIER: Phone Auth disabled
        if (PHONE_AUTH_DISABLED) {
            Timber.w("Phone Auth is disabled in Free Tier mode")
            SecurityManager.audit("AUTH_PHONE_DISABLED", mapOf("action" to "resendVerificationCode"))
            return Resource.Error(PHONE_AUTH_DISABLED_MESSAGE)
        }
        
        val phone = _currentPhoneE164.value
        val token = resendToken
        if (phone == null || token == null) {
            return Resource.Error("No previous verification session found.")
        }
        val userId = phone // Use phone as userId for rate limiting
        val now = System.currentTimeMillis()
        val limit = rateLimitDao.get(userId, "AUTH_SEND_OTP")
        if (limit != null && (now - limit.lastAt) < 60000) {
            val remaining = 60 - ((now - limit.lastAt) / 1000)
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf("action" to "AUTH_SEND_OTP", "phone" to maskPhone(phone), "remaining" to remaining))
            return Resource.Error("Rate limit exceeded. Try again in $remaining seconds.")
        }
        return try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    scope.launch {
                        firebaseAuth.signInWithCredential(credential).await()
                        if (firebaseAuth.currentUser != null) {
                            _isAuthenticated.value = isFirebaseUserValid()
                            _events.tryEmit(AuthEvent.AutoVerified)
                            clearVerificationState()
                            SecurityManager.audit("AUTH_VERIFICATION_COMPLETED", mapOf("success" to true))
                        } else {
                            Timber.e("Auto sign-in failed (resend)")
                            SecurityManager.audit("AUTH_VERIFICATION_COMPLETED", mapOf("success" to false, "error" to "user is null"))
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Timber.e(e, "Phone verification failed (resend)")
                    _events.tryEmit(AuthEvent.VerificationFailed(mapError(e)))
                    SecurityManager.audit("AUTH_VERIFICATION_FAILED", mapOf("error" to e.message))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _currentVerificationId.value = verificationId
                    resendToken = token
                    Timber.d("OTP code re-sent; verificationId updated")
                    _events.tryEmit(AuthEvent.CodeSent(verificationId))
                    // Persist to DataStore
                    scope.launch { persistVerificationState(verificationId, phone) }
                    SecurityManager.audit("AUTH_CODE_SENT", mapOf("phone" to maskPhone(phone), "verificationId" to verificationId))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(token)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            // Update rate limit after successful request
            rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_SEND_OTP", userId = userId, action = "AUTH_SEND_OTP", lastAt = now))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "resendVerificationCode exception")
            Resource.Error("Failed to resend code: ${e.message}")
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun linkPhoneToCurrentUser(activity: Activity, phoneE164: String): Resource<Unit> {
        // FREE TIER: Phone Auth disabled
        if (PHONE_AUTH_DISABLED) {
            Timber.w("Phone Auth is disabled in Free Tier mode")
            SecurityManager.audit("AUTH_PHONE_DISABLED", mapOf("action" to "linkPhoneToCurrentUser"))
            return Resource.Error(PHONE_AUTH_DISABLED_MESSAGE)
        }
        
        val user = firebaseAuth.currentUser ?: return Resource.Error("Not authenticated")
        val userId = phoneE164
        val now = System.currentTimeMillis()
        val limit = rateLimitDao.get(userId, "AUTH_SEND_OTP")
        if (limit != null && (now - limit.lastAt) < 60000) {
            val remaining = 60 - ((now - limit.lastAt) / 1000)
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf("action" to "AUTH_SEND_OTP", "phone" to maskPhone(phoneE164), "remaining" to remaining))
            return Resource.Error("Rate limit exceeded. Try again in $remaining seconds.")
        }
        return try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    scope.launch {
                        runCatching { user.linkWithCredential(credential).await() }
                            .onSuccess {
                                val phone = firebaseAuth.currentUser?.phoneNumber ?: phoneE164
                                _events.tryEmit(AuthEvent.PhoneLinkSuccess(phone))
                                clearVerificationState()
                                SecurityManager.audit("AUTH_PHONE_LINK_COMPLETED", mapOf("success" to true, "phone" to maskPhone(phone)))
                            }
                            .onFailure { e ->
                                val msg = mapError(e as Exception)
                                _events.tryEmit(AuthEvent.PhoneLinkFailed(msg))
                                SecurityManager.audit("AUTH_PHONE_LINK_COMPLETED", mapOf("success" to false, "error" to e.message))
                            }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Timber.e(e, "Phone link verification failed")
                    _events.tryEmit(AuthEvent.PhoneLinkFailed(mapError(e)))
                    SecurityManager.audit("AUTH_PHONE_LINK_FAILED", mapOf("error" to e.message))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _currentVerificationId.value = verificationId
                    _currentPhoneE164.value = phoneE164
                    resendToken = token
                    isLinkingMode = true
                    Timber.d("Link OTP code sent; verificationId stored")
                    _events.tryEmit(AuthEvent.CodeSent(verificationId))
                    scope.launch { persistVerificationState(verificationId, phoneE164) }
                    SecurityManager.audit("AUTH_CODE_SENT", mapOf("phone" to maskPhone(phoneE164), "verificationId" to verificationId, "mode" to "link"))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneE164)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_SEND_OTP", userId = userId, action = "AUTH_SEND_OTP", lastAt = now))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "linkPhoneToCurrentUser exception")
            Resource.Error("Failed to start phone linking: ${e.message}")
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun verifyOtpAndLink(verificationId: String, otpCode: String): Resource<Unit> {
        // FREE TIER: Phone Auth disabled
        if (PHONE_AUTH_DISABLED) {
            Timber.w("Phone Auth is disabled in Free Tier mode")
            SecurityManager.audit("AUTH_PHONE_DISABLED", mapOf("action" to "verifyOtpAndLink"))
            return Resource.Error(PHONE_AUTH_DISABLED_MESSAGE)
        }
        
        val phone = _currentPhoneE164.value ?: ""
        val user = firebaseAuth.currentUser ?: return Resource.Error("Not authenticated")
        val userId = if (phone.isNotBlank()) phone else verificationId
        val now = System.currentTimeMillis()
        val limit = rateLimitDao.get(userId, "AUTH_VERIFY_OTP")
        if (limit != null && (now - limit.lastAt) < 60000) {
            val remaining = 60 - ((now - limit.lastAt) / 1000)
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf("action" to "AUTH_VERIFY_OTP", "remaining" to remaining))
            return Resource.Error("Rate limit exceeded. Try again in $remaining seconds.")
        }
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            runCatching { user.linkWithCredential(credential).await() }
                .onSuccess {
                    val linkedPhone = firebaseAuth.currentUser?.phoneNumber ?: phone
                    _events.tryEmit(AuthEvent.PhoneLinkSuccess(linkedPhone))
                    clearVerificationState()
                    rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_VERIFY_OTP", userId = userId, action = "AUTH_VERIFY_OTP", lastAt = now))
                    SecurityManager.audit("AUTH_OTP_LINK_VERIFIED", mapOf("result" to "success", "phone" to maskPhone(linkedPhone)))
                    return Resource.Success(Unit)
                }
                .onFailure { e ->
                    val msg = if (e is FirebaseAuthUserCollisionException) "Phone already linked to another account" else (e.message ?: "Linking failed")
                    _events.tryEmit(AuthEvent.PhoneLinkFailed(msg))
                    rateLimitDao.upsert(RateLimitEntity(id = "$userId:AUTH_VERIFY_OTP", userId = userId, action = "AUTH_VERIFY_OTP", lastAt = now))
                    SecurityManager.audit("AUTH_OTP_LINK_VERIFIED", mapOf("result" to "failure", "error" to e.message))
                    return Resource.Error("Failed to link phone: $msg")
                }
            Resource.Error("Unexpected error during linking")
        } catch (e: Exception) {
            Timber.e(e, "verifyOtpAndLink exception")
            SecurityManager.audit("AUTH_OTP_LINK_VERIFIED", mapOf("result" to "failure", "error" to e.message))
            Resource.Error("Failed to verify OTP for link: ${e.message}")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            _isAuthenticated.value = isFirebaseUserValid()
            clearVerificationState()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "signOut exception")
            Resource.Error("Failed to sign out: ${e.message}")
        }
    }

    private fun isFirebaseUserValid(): Boolean = firebaseAuth.currentUser != null

    private fun mapError(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid phone number or code. Please check and try again."
            is FirebaseAuthException -> when (e.errorCode) {
                "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please wait a while before retrying."
                else -> e.message ?: "Authentication error"
            }
            else -> e.message ?: "Network or server error. Please try again."
        }
    }

    private fun maskPhone(phone: String): String = if (phone.length > 4) "***${phone.takeLast(4)}" else phone

    private suspend fun persistVerificationState(verificationId: String, phoneE164: String) {
        context.authDataStore.edit { prefs ->
            prefs[stringPreferencesKey("auth_verification_id")] = verificationId
            prefs[stringPreferencesKey("auth_phone_e164")] = phoneE164
        }
    }

    private suspend fun clearVerificationState() {
        _currentVerificationId.value = null
        _currentPhoneE164.value = null
        isLinkingMode = false
        context.authDataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("auth_verification_id"))
            prefs.remove(stringPreferencesKey("auth_phone_e164"))
        }
    }
}