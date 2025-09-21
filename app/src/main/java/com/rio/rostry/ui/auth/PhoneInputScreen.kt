package com.rio.rostry.ui.auth

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.utils.isValidIndianPhone

@Composable
fun PhoneInputScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToOtp: (String) -> Unit
) {
    val ui by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter your phone number")
        OutlinedTextField(
            value = ui.phoneInput,
            onValueChange = { viewModel.onPhoneChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Phone (+91XXXXXXXXXX)") },
            singleLine = true
        )
        if (ui.error != null) {
            Text(text = ui.error!!, modifier = Modifier.padding(top = 8.dp))
        }
        Button(
            onClick = {
                viewModel.startVerification(activity = context as Activity)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = ui.e164?.let { isValidIndianPhone(it) } == true && !ui.isLoading
        ) {
            Text("Send OTP")
        }
        if (ui.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
