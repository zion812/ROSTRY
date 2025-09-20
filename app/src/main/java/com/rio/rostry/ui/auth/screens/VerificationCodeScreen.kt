package com.rio.rostry.ui.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.presentation.auth.AuthViewModel

@Composable
fun VerificationCodeScreen(
    phoneNumber: String,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var verificationCode by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verify your phone number",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter the 6-digit code sent to $phoneNumber",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = verificationCode,
            onValueChange = { 
                if (it.text.length <= 6) {
                    verificationCode = it
                    errorMessage = null // Clear error when user types
                }
            },
            label = { Text("Verification Code") },
            placeholder = { Text("123456") },
            isError = errorMessage != null,
            supportingText = {
                errorMessage?.let { 
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (verificationCode.text.length == 6) {
                    isLoading = true
                    // In a real implementation, we would call the ViewModel method
                    // authViewModel.verifyPhoneCode(phoneNumber, verificationCode.text)
                    // For now, we'll simulate the success
                    onVerificationSuccess()
                } else {
                    errorMessage = "Please enter a 6-digit code"
                }
            },
            enabled = !isLoading && verificationCode.text.length == 6,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Verify Code")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Back to Phone Number")
        }
    }
}