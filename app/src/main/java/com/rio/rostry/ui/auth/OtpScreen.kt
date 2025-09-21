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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
            modifier = Modifier.fillMaxWidth(),
            label = { Text("OTP") },
            singleLine = true,
            // keyboardOptions removed for compatibility
        )
        if (ui.error != null) {
            Text(text = ui.error!!, modifier = Modifier.padding(top = 8.dp))
        }
        Button(
            onClick = { viewModel.verifyOtpAndSignIn() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !ui.isLoading && ui.otp.length >= 4
        ) {
            Text("Verify")
        }
        if (ui.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
