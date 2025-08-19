package com.rio.rostry.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rio.rostry.viewmodel.DebugAuthViewModel
import com.rio.rostry.viewmodel.AuthError
import com.rio.rostry.repository.AuthErrorType

@Composable
fun DebugLoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: DebugAuthViewModel = viewModel()
) {
    Log.d("DebugLoginScreen", "Composing DebugLoginScreen")
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by authViewModel.authError.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        Log.d("DebugLoginScreen", "isLoggedIn changed to: $isLoggedIn")
        if (isLoggedIn) {
            Log.d("DebugLoginScreen", "User is logged in, calling onLoginSuccess")
            onLoginSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Debug Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                Log.d("DebugLoginScreen", "Email changed to: $email")
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isError = authError?.type == AuthErrorType.INVALID_CREDENTIALS || 
                     authError?.type == AuthErrorType.USER_NOT_FOUND
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                Log.d("DebugLoginScreen", "Password changed")
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isError = authError?.type == AuthErrorType.INVALID_CREDENTIALS
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Log.d("DebugLoginScreen", "Login button clicked with email: $email")
                authViewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text("Login")
            }
        }

        // Display error message if there is one
        authError?.let { error ->
            Log.d("DebugLoginScreen", "Displaying error: ${error.message}")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = getFriendlyErrorMessage(error),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                Log.d("DebugLoginScreen", "SignUp button clicked")
                onSignUpClick()
            },
            enabled = !isLoading
        ) {
            Text("Don't have an account? Sign Up")
        }
        
        // Clear error button
        if (authError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { 
                    Log.d("DebugLoginScreen", "Clear error button clicked")
                    authViewModel.clearAuthError()
                },
                enabled = !isLoading
            ) {
                Text("Clear")
            }
        }
    }
}

private fun getFriendlyErrorMessage(error: AuthError): String {
    Log.d("DebugLoginScreen", "Getting friendly error message for: ${error.type}")
    return com.rio.rostry.utils.ErrorUtils.getFriendlyAuthErrorMessage(error)
}