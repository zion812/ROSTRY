package com.rio.rostry.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.components.RostryTextField

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String?, () -> Unit, (String) -> Unit) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        RostryTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )

        RostryTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        RostryTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone (Optional)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isLoading = true
            onRegisterClick(email, password, phone.ifBlank { null }, {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
            }, {
                isLoading = false
                Toast.makeText(context, "Registration Failed: $it", Toast.LENGTH_SHORT).show()
            })
        }) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = "Register")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text(text = "Already have an account? Login")
        }
    }
}
