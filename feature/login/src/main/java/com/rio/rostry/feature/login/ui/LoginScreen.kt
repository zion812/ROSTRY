package com.rio.rostry.feature.login.ui
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()
    
    // FirebaseUI Launcher
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = com.firebase.ui.auth.FirebaseAuthUIActivityResultContract()
    ) { res ->
        authViewModel.handleFirebaseUIResult(res.idpResponse, res.resultCode)
    }

    LaunchedEffect(uiState.currentStep) {
        when (uiState.currentStep) {
            LoginStep.SUCCESS -> {
                onLoginSuccess()
            }
            else -> Unit
        }
    }
    
    // Observe AuthViewModel navigation for Google Sign-In success or User Setup
    LaunchedEffect(Unit) {
        authViewModel.navigation.collect { event ->
            when (event) {
                is AuthViewModel.NavAction.ToHome -> onLoginSuccess()
                is AuthViewModel.NavAction.ToUserSetup -> {
                    // Navigate to modularized onboarding
                    onNavigateToOnboarding()
                }
            }
        }
    }
    
    when (uiState.currentStep) {
        LoginStep.PHONE_INPUT -> PhoneInputScreen(
            onPhoneSubmitted = loginViewModel::initiatePhoneAuth,
            onGoogleSignIn = {
                val providers = listOf(
                    com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder().build()
                )
                val intent = com.firebase.ui.auth.AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false, true)
                    .build()
                launcher.launch(intent)
            },
            isLoading = uiState.isLoading
        )
        LoginStep.OTP_VERIFICATION -> OtpVerificationScreen(
            onOtpVerified = loginViewModel::verifyOtp,
            onBack = loginViewModel::reset,
            isLoading = uiState.isLoading
        )
        LoginStep.SUCCESS -> LoadingScreen()
    }
}

@Composable
private fun PhoneInputScreen(
    onPhoneSubmitted: (String) -> Unit,
    onGoogleSignIn: () -> Unit,
    isLoading: Boolean
) {
    var phoneNumber by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign in to ROSTRY",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter your phone number to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            placeholder = { Text("+1 123 456 7890") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onPhoneSubmitted(phoneNumber) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && phoneNumber.isNotBlank(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Send Verification Code")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = " OR ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "G",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Continue with Google")
        }
    }
}

@Composable
private fun OtpVerificationScreen(
    onOtpVerified: (String) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    var otp by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verify your number",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter the 6-digit code sent to you",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("Verification Code") },
            placeholder = { Text("000000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                letterSpacing = 8.dp.value.sp
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onOtpVerified(otp) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && otp.length == 6,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Verify & Continue")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onBack) {
            Text("Changed your number? Go back")
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
