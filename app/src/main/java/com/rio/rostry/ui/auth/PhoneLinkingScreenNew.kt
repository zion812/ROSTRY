package com.rio.rostry.ui.auth

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.presentation.auth.linking.PhoneLinkingStep
import com.rio.rostry.presentation.auth.linking.PhoneLinkingViewModel
import com.rio.rostry.utils.isValidE164

/**
 * Screen for linking phone number to existing account (Google/Email sign-in).
 * Two-step process: phone entry → OTP verification.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneLinkingScreenNew(
    viewModel: PhoneLinkingViewModel = hiltViewModel(),
    onLinked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Navigate when linked
    LaunchedEffect(state.isLinked) {
        if (state.isLinked) {
            onLinked()
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Link Phone Number") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Link, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state.step) {
                PhoneLinkingStep.PHONE_ENTRY -> {
                    PhoneEntryStep(
                        state = state,
                        viewModel = viewModel,
                        activity = activity
                    )
                }
                PhoneLinkingStep.OTP_ENTRY -> {
                    OtpEntryStep(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneEntryStep(
    state: com.rio.rostry.presentation.auth.linking.PhoneLinkingUiState,
    viewModel: PhoneLinkingViewModel,
    activity: Activity?
) {
    // Header
    Icon(
        imageVector = Icons.Default.Phone,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(
        text = "Add your phone number",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
    
    Text(
        text = "Link your phone for enhanced security",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Phone input
    OutlinedTextField(
        value = state.phoneInput,
        onValueChange = viewModel::onPhoneChanged,
        label = { Text("Phone Number") },
        placeholder = { Text("+91 98765 43210") },
        leadingIcon = {
            Text(
                text = "🇮🇳",
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                activity?.let { viewModel.startLinking(it) }
            }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isLoading,
        isError = state.error != null
    )
    
    // Error message
    if (state.error != null) {
        Text(
            text = state.error!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Send OTP button
    Button(
        onClick = {
            activity?.let { viewModel.startLinking(it) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !state.isLoading && 
                 state.phoneE164?.let { isValidE164(it) } == true &&
                 activity != null
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Send Verification Code")
        }
    }
}

@Composable
private fun OtpEntryStep(
    state: com.rio.rostry.presentation.auth.linking.PhoneLinkingUiState,
    viewModel: PhoneLinkingViewModel
) {
    // Header
    Icon(
        imageVector = Icons.Default.Link,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(
        text = "Verify your phone",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
    
    Text(
        text = "Enter the 6-digit code we sent",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // OTP input
    OutlinedTextField(
        value = state.otp,
        onValueChange = viewModel::onOtpChanged,
        label = { Text("Verification Code") },
        placeholder = { Text("000000") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isLoading,
        isError = state.error != null,
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            textAlign = TextAlign.Center,
            letterSpacing = 8.sp
        )
    )
    
    // Error message
    if (state.error != null) {
        Text(
            text = state.error!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Verify button
    Button(
        onClick = viewModel::verifyAndLink,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !state.isLoading && state.otp.length == 6
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Verify & Link")
        }
    }
    
    // Resend countdown
    if (state.resendCooldownSeconds > 0) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Resend code in ${state.resendCooldownSeconds}s",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LinearProgressIndicator(
            progress = state.resendCooldownSeconds / 60f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}
