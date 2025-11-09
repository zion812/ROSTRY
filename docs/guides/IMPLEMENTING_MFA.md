# Implementing Multi-Factor Authentication (MFA) in ROSTRY

**Reference:** [Firebase Android MFA Documentation](https://firebase.google.com/docs/auth/android/multi-factor)

---

## Overview

This guide provides step-by-step instructions for implementing Time-based One-Time Password (TOTP) multi-factor authentication in the ROSTRY app.

---

## 1. Prerequisites

### Firebase Console Configuration

1. Open [Firebase Console](https://console.firebase.google.com)
2. Navigate to: **Authentication → Sign-in method → Advanced**
3. Enable **Multi-factor authentication**
4. Select **TOTP** as allowed second factor

### Update Dependencies

No additional dependencies needed - TOTP is included in `firebase-auth-ktx:23.2.1`

---

## 2. Update Domain Layer

### Update AuthRepository Interface

```kotlin
// domain/auth/AuthRepository.kt
interface AuthRepository {
    // ... existing methods
    
    // MFA Enrollment
    suspend fun generateTOTPSecret(): Resource<TOTPSecret>
    suspend fun enrollTOTP(
        secret: TOTPSecret,
        verificationCode: String,
        displayName: String
    ): Resource<Unit>
    
    // MFA Management
    suspend fun getEnrolledFactors(): Resource<List<MultiFactorInfo>>
    suspend fun unenrollFactor(factorUid: String): Resource<Unit>
    
    // MFA Sign-in
    suspend fun handleMFARequired(
        exception: FirebaseAuthMultiFactorException
    ): Resource<MultiFactorResolver>
    
    suspend fun verifyTOTPSecondFactor(
        resolver: MultiFactorResolver,
        verificationCode: String,
        selectedFactorIndex: Int = 0
    ): Resource<Unit>
}

// Add data classes
data class TOTPSecret(
    val secretKey: String,
    val qrCodeUrl: String,
    val accountName: String,
    val issuer: String = "ROSTRY"
)
```

### Add MFA Events

```kotlin
// domain/auth/AuthEvent.kt
sealed class AuthEvent {
    // ... existing events
    
    data class MFARequired(val resolver: MultiFactorResolver) : AuthEvent()
    data class MFAEnrollmentStarted(val secret: TOTPSecret) : AuthEvent()
    data class MFAEnrollmentCompleted(val factorName: String) : AuthEvent()
    data class MFAVerificationSuccess(val userId: String) : AuthEvent()
}
```

---

## 3. Implement Repository Layer

### AuthRepositoryImpl.kt

```kotlin
// data/auth/AuthRepositoryImpl.kt

import com.google.firebase.auth.TotpMultiFactorGenerator
import com.google.firebase.auth.TotpSecret
import com.google.firebase.auth.MultiFactorInfo
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.FirebaseAuthMultiFactorException

override suspend fun generateTOTPSecret(): Resource<TOTPSecret> {
    return try {
        val user = firebaseAuth.currentUser 
            ?: return Resource.Error("Not authenticated")
        
        val session = user.multiFactor.session.await()
        val totpSecret: TotpSecret = TotpMultiFactorGenerator.generateSecret(session)
        
        val qrCodeUrl = totpSecret.generateQrCodeUrl(
            accountName = user.email ?: user.phoneNumber ?: user.uid,
            issuer = "ROSTRY"
        )
        
        SecurityManager.audit("MFA_SECRET_GENERATED", mapOf("uid" to user.uid))
        
        Resource.Success(
            TOTPSecret(
                secretKey = totpSecret.secretKey,
                qrCodeUrl = qrCodeUrl,
                accountName = user.email ?: user.phoneNumber ?: "User",
                issuer = "ROSTRY"
            )
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to generate TOTP secret")
        Resource.Error("Failed to generate authenticator code: ${e.message}")
    }
}

override suspend fun enrollTOTP(
    secret: TOTPSecret,
    verificationCode: String,
    displayName: String
): Resource<Unit> {
    return try {
        val user = firebaseAuth.currentUser 
            ?: return Resource.Error("Not authenticated")
        
        // Create TotpSecret from stored secret key
        val session = user.multiFactor.session.await()
        val totpSecret = TotpSecret(
            secretKey = secret.secretKey,
            /* other fields populated by Firebase */
        )
        
        // Generate assertion with verification code
        val multiFactorAssertion = TotpMultiFactorGenerator
            .getAssertionForEnrollment(totpSecret, verificationCode)
        
        // Enroll the factor
        user.multiFactor.enroll(multiFactorAssertion, displayName).await()
        
        SecurityManager.audit("MFA_ENROLLED", mapOf(
            "uid" to user.uid,
            "displayName" to displayName,
            "factorCount" to user.multiFactor.enrolledFactors.size
        ))
        
        _events.tryEmit(AuthEvent.MFAEnrollmentCompleted(displayName))
        
        Resource.Success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Failed to enroll TOTP")
        Resource.Error("Failed to verify code: ${e.message}")
    }
}

override suspend fun getEnrolledFactors(): Resource<List<MultiFactorInfo>> {
    return try {
        val user = firebaseAuth.currentUser 
            ?: return Resource.Error("Not authenticated")
        
        val factors = user.multiFactor.enrolledFactors
        Resource.Success(factors)
    } catch (e: Exception) {
        Timber.e(e, "Failed to get enrolled factors")
        Resource.Error("Failed to retrieve factors: ${e.message}")
    }
}

override suspend fun unenrollFactor(factorUid: String): Resource<Unit> {
    return try {
        val user = firebaseAuth.currentUser 
            ?: return Resource.Error("Not authenticated")
        
        val factor = user.multiFactor.enrolledFactors
            .firstOrNull { it.uid == factorUid }
            ?: return Resource.Error("Factor not found")
        
        user.multiFactor.unenroll(factor).await()
        
        SecurityManager.audit("MFA_UNENROLLED", mapOf(
            "uid" to user.uid,
            "factorUid" to factorUid
        ))
        
        Resource.Success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Failed to unenroll factor")
        Resource.Error("Failed to remove authenticator: ${e.message}")
    }
}

override suspend fun handleMFARequired(
    exception: FirebaseAuthMultiFactorException
): Resource<MultiFactorResolver> {
    return try {
        val resolver = exception.resolver
        _events.tryEmit(AuthEvent.MFARequired(resolver))
        Resource.Success(resolver)
    } catch (e: Exception) {
        Timber.e(e, "Failed to handle MFA exception")
        Resource.Error("Failed to process MFA: ${e.message}")
    }
}

override suspend fun verifyTOTPSecondFactor(
    resolver: MultiFactorResolver,
    verificationCode: String,
    selectedFactorIndex: Int
): Resource<Unit> {
    return try {
        val selectedHint = resolver.hints[selectedFactorIndex]
        
        val multiFactorAssertion = TotpMultiFactorGenerator
            .getAssertionForSignIn(selectedHint.uid, verificationCode)
        
        val result = resolver.resolveSignIn(multiFactorAssertion).await()
        
        if (result.user != null) {
            _isAuthenticated.value = true
            _events.tryEmit(AuthEvent.MFAVerificationSuccess(result.user!!.uid))
            
            SecurityManager.audit("MFA_VERIFICATION_SUCCESS", mapOf(
                "uid" to result.user!!.uid,
                "factorUid" to selectedHint.uid
            ))
            
            Resource.Success(Unit)
        } else {
            Resource.Error("MFA verification failed: User is null")
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to verify TOTP second factor")
        SecurityManager.audit("MFA_VERIFICATION_FAILED", mapOf("error" to e.message))
        Resource.Error("Invalid verification code")
    }
}
```

---

## 4. Update ViewModel Layer

### AuthViewModel.kt

```kotlin
// ui/auth/AuthViewModel.kt

// Update UiState
data class UiState(
    // ... existing fields
    val mfaResolver: MultiFactorResolver? = null,
    val totpSecret: TOTPSecret? = null,
    val enrolledFactors: List<MultiFactorInfo> = emptyList()
)

// Add navigation actions
sealed class NavAction {
    // ... existing actions
    data class ToMFAVerification(val resolver: MultiFactorResolver): NavAction()
    data class ToMFAEnrollment(val secret: TOTPSecret): NavAction()
    object ToMFAManagement: NavAction()
}

// Listen for MFA events in init
init {
    viewModelScope.launch {
        authRepository.events.collectLatest { event ->
            when (event) {
                // ... existing events
                is AuthEvent.MFARequired -> {
                    _uiState.value = _uiState.value.copy(
                        mfaResolver = event.resolver,
                        isLoading = false
                    )
                    _navigation.tryEmit(NavAction.ToMFAVerification(event.resolver))
                }
                is AuthEvent.MFAEnrollmentStarted -> {
                    _uiState.value = _uiState.value.copy(totpSecret = event.secret)
                    _navigation.tryEmit(NavAction.ToMFAEnrollment(event.secret))
                }
            }
        }
    }
}

// MFA Enrollment functions
fun startMFAEnrollment() {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        when (val res = authRepository.generateTOTPSecret()) {
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totpSecret = res.data
                )
                _navigation.tryEmit(NavAction.ToMFAEnrollment(res.data!!))
            }
            is Resource.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = res.message
                )
            }
            else -> Unit
        }
    }
}

fun completeMFAEnrollment(verificationCode: String, displayName: String) {
    val secret = _uiState.value.totpSecret
    if (secret == null) {
        _uiState.value = _uiState.value.copy(error = "No enrollment in progress")
        return
    }
    
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        when (val res = authRepository.enrollTOTP(secret, verificationCode, displayName)) {
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totpSecret = null
                )
                // Navigate back to settings
                _navigation.tryEmit(NavAction.ToMFAManagement)
            }
            is Resource.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = res.message
                )
            }
            else -> Unit
        }
    }
}

// MFA Verification during sign-in
fun verifyMFACode(code: String) {
    val resolver = _uiState.value.mfaResolver
    if (resolver == null) {
        _uiState.value = _uiState.value.copy(error = "No MFA challenge found")
        return
    }
    
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        when (val res = authRepository.verifyTOTPSecondFactor(resolver, code)) {
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    mfaResolver = null
                )
                postAuthBootstrapAndNavigate()
            }
            is Resource.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = res.message
                )
            }
            else -> Unit
        }
    }
}

// Handle MFA exception during sign-in
fun verifyOtpAndSignIn() {
    // ... existing code
    viewModelScope.launch {
        when (val res = authRepository.verifyOtp(verificationId, code)) {
            is Resource.Success -> {
                postAuthBootstrapAndNavigate()
            }
            is Resource.Error -> {
                // Check if MFA is required
                if (res.exception is FirebaseAuthMultiFactorException) {
                    val mfaException = res.exception as FirebaseAuthMultiFactorException
                    authRepository.handleMFARequired(mfaException)
                    // NavAction.ToMFAVerification emitted by event listener
                } else {
                    _uiState.value = _uiState.value.copy(error = res.message)
                }
            }
            else -> Unit
        }
    }
}
```

---

## 5. Create UI Screens

### MFAEnrollmentScreen.kt

```kotlin
// ui/auth/MFAEnrollmentScreen.kt

@Composable
fun MFAEnrollmentScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val secret = state.totpSecret
    
    if (secret == null) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }
    
    var verificationCode by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("Authenticator App") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Set Up Two-Factor Authentication",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 1: Install authenticator app
        Text(
            text = "Step 1: Install an authenticator app",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Download Google Authenticator, Microsoft Authenticator, or similar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 2: Scan QR code
        Text(
            text = "Step 2: Scan this QR code",
            style = MaterialTheme.typography.titleMedium
        )
        
        // QR Code image (use library like zxing to generate)
        AsyncImage(
            model = secret.qrCodeUrl,
            contentDescription = "QR Code",
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )
        
        // Manual entry option
        Text(
            text = "Or enter this code manually:",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = secret.secretKey,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Step 3: Enter verification code
        Text(
            text = "Step 3: Enter the 6-digit code",
            style = MaterialTheme.typography.titleMedium
        )
        
        OutlinedTextField(
            value = verificationCode,
            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) verificationCode = it },
            label = { Text("Verification Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Device Name (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                viewModel.completeMFAEnrollment(verificationCode, displayName)
            },
            enabled = verificationCode.length == 6 && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Verify and Enable")
            }
        }
        
        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```

### MFAVerificationScreen.kt

```kotlin
// ui/auth/MFAVerificationScreen.kt

@Composable
fun MFAVerificationScreen(
    viewModel: AuthViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var verificationCode by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Two-Factor Authentication",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "Enter the code from your authenticator app",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = verificationCode,
            onValueChange = { 
                if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                    verificationCode = it
                }
            },
            label = { Text("6-Digit Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                fontSize = 24.sp,
                letterSpacing = 8.sp,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.verifyMFACode(verificationCode) },
            enabled = verificationCode.length == 6 && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Verify")
            }
        }
        
        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```

---

## 6. Integration Checklist

- [ ] Enable MFA in Firebase Console
- [ ] Update `AuthRepository` interface
- [ ] Implement MFA methods in `AuthRepositoryImpl`
- [ ] Add MFA state to `AuthViewModel`
- [ ] Create `MFAEnrollmentScreen`
- [ ] Create `MFAVerificationScreen`
- [ ] Add MFA routes to navigation
- [ ] Add "Enable 2FA" button in settings
- [ ] Test enrollment flow
- [ ] Test sign-in with MFA
- [ ] Add unit tests for MFA functions
- [ ] Document MFA setup for users

---

## 7. Testing

### Test Cases

1. **Enrollment Flow**
   - Generate TOTP secret
   - Display QR code
   - Verify code from authenticator app
   - Successfully enroll factor

2. **Sign-in with MFA**
   - Sign in with phone/email
   - Trigger MFA verification
   - Enter TOTP code
   - Successfully authenticate

3. **Factor Management**
   - View enrolled factors
   - Unenroll factor (with re-authentication)

### Manual Testing Steps

```text
1. Sign in to ROSTRY app
2. Go to Settings → Security
3. Tap "Enable Two-Factor Authentication"
4. Scan QR code with Google Authenticator
5. Enter 6-digit code from app
6. Verify enrollment success
7. Sign out
8. Sign in again
9. Enter TOTP code when prompted
10. Verify successful sign-in
```

---

## Resources

- [Firebase MFA Docs](https://firebase.google.com/docs/auth/android/multi-factor)
- [TOTP RFC 6238](https://datatracker.ietf.org/doc/html/rfc6238)
- [Google Authenticator](https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2)
