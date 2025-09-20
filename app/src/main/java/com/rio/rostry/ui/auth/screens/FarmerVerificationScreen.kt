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
fun FarmerVerificationScreen(
    user: User,
    onVerificationSubmitted: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var location by remember { mutableStateOf(TextFieldValue(user.location ?: "")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Farmer Verification",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please provide your location for verification",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            placeholder = { Text("Enter your farm location") },
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (location.text.isNotBlank()) {
                    isLoading = true
                    // In a real implementation, we would call the ViewModel method
                    // authViewModel.verifyFarmer(user.id, location.text)
                    // For now, we'll simulate the success
                    onVerificationSubmitted()
                } else {
                    errorMessage = "Please enter your location"
                }
            },
            enabled = !isLoading && location.text.isNotBlank(),
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
    }
}