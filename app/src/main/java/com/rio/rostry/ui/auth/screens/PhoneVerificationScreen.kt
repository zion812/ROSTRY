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
import com.rio.rostry.util.PhoneValidator

@Composable
fun PhoneVerificationScreen(
    onVerificationCodeSent: (phoneNumber: String) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
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
            text = "Enter your phone number",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { 
                phoneNumber = it
                errorMessage = null // Clear error when user types
            },
            label = { Text("Phone Number") },
            placeholder = { Text("e.g., 9876543210") },
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
                val cleanNumber = PhoneValidator.cleanPhoneNumber(phoneNumber.text)
                if (PhoneValidator.isValidIndianPhoneNumber(cleanNumber)) {
                    isLoading = true
                    // In a real implementation, we would call the ViewModel method
                    // authViewModel.sendVerificationCode("+91$cleanNumber")
                    // For now, we'll simulate the success
                    onVerificationCodeSent("+91$cleanNumber")
                } else {
                    errorMessage = "Please enter a valid Indian phone number"
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Send Verification Code")
        }
    }
}