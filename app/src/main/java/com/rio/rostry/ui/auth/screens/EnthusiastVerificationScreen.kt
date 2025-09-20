package com.rio.rostry.ui.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.User
import com.rio.rostry.presentation.auth.AuthViewModel

@Composable
fun EnthusiastVerificationScreen(
    user: User,
    onVerificationSubmitted: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var idNumber by remember { mutableStateOf(TextFieldValue("")) }
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enthusiast Verification",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please provide your KYC details for verification",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = idNumber,
            onValueChange = { idNumber = it },
            label = { Text("ID Number") },
            placeholder = { Text("Aadhaar, PAN, or Passport number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (fullName.text.isNotBlank() && idNumber.text.isNotBlank() && address.text.isNotBlank()) {
                    isLoading = true
                    // In a real implementation, we would call the ViewModel method
                    // authViewModel.verifyEnthusiast(user.id, mapOf(
                    //     "fullName" to fullName.text,
                    //     "idNumber" to idNumber.text,
                    //     "address" to address.text
                    // ))
                    // For now, we'll simulate the success
                    onVerificationSubmitted()
                } else {
                    errorMessage = "Please fill all fields"
                }
            },
            enabled = !isLoading && fullName.text.isNotBlank() && idNumber.text.isNotBlank() && address.text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Submit for Verification")
        }

        errorMessage?.let { 
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}