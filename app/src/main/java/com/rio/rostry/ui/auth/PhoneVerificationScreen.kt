package com.rio.rostry.ui.auth

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun PhoneVerificationScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    activity: Activity? = null,
    onVerificationComplete: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val act = remember(activity, context) {
        activity ?: run {
            var ctx = context
            while (ctx is ContextWrapper) {
                if (ctx is Activity) return@run ctx
                ctx = ctx.baseContext
            }
            null
        }
    }

    var phoneInput by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.needsPhoneLink) {
        if (!state.needsPhoneLink && state.error == null) {
            onVerificationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verify your phone number to continue")

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        if (!state.needsPhoneLink) {
            Text("All set. Redirecting…")
            return@Column
        }

        OutlinedTextField(
            value = phoneInput,
            onValueChange = {
                phoneInput = it
                viewModel.onPhoneChanged(it)
            },
            label = { Text("Phone (+91…)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "phone_input" }
        )
        Button(
            onClick = { act?.let { viewModel.startPhoneLinking(it) } },
            enabled = !state.isLoading && state.resendCooldownSec == 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send OTP")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = otp,
            onValueChange = { val t = it.filter { ch -> ch.isDigit() }.take(6); otp = t; viewModel.onOtpChanged(t) },
            label = { Text("Enter 6-digit OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "otp_input" }
        )
        Button(
            onClick = { viewModel.verifyOtpAndLinkPhone() },
            enabled = !state.isLoading && otp.length == 6,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify & Continue")
        }
        Button(
            onClick = { act?.let { viewModel.resendOtp(it) } },
            enabled = !state.isLoading && state.resendCooldownSec == 0 && act != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            val label = if (state.resendCooldownSec > 0) "Resend in ${state.resendCooldownSec}s" else "Resend OTP"
            Text(label)
        }
        TextButton(
            onClick = { viewModel.cancelPhoneLinking() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
        if (!state.error.isNullOrBlank()) {
            Text("Error: ${state.error}")
        }
    }
}
