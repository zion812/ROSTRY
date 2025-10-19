package com.rio.rostry.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.testTag

@Composable
fun OtpScreen(
    verificationId: String,
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit
) {
    val ui by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter OTP sent to your phone")
        OutlinedTextField(
            value = ui.otp,
            onValueChange = { viewModel.onOtpChanged(it) },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter 6-digit OTP" },
            label = { Text("OTP") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.verifyOtpAndSignIn() })
        )
        if (ui.error != null) {
            Text(text = ui.error!!, modifier = Modifier.padding(top = 8.dp))
        }
        Button(
            onClick = { viewModel.verifyOtpAndSignIn() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).semantics { contentDescription = "Verify OTP" },
            enabled = !ui.isLoading && ui.otp.length == 6 && ui.otp.all { it.isDigit() } && ui.verificationId != null
        ) {
            Text("Verify")
        }
        if (ui.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp).testTag("loading"))
        }
    }
}