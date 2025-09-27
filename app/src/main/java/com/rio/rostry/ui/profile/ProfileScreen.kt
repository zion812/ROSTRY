package com.rio.rostry.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun ProfileScreen(
    onVerifyFarmerLocation: () -> Unit,
    onVerifyEnthusiastKyc: () -> Unit,
    onSignIn: () -> Unit = {},
    onSignUp: () -> Unit = {},
    isDemo: Boolean = false,
    onSwitchToReal: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val sp = LocalSpacing.current

    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Profile", style = MaterialTheme.typography.titleLarge)
        Divider(modifier = Modifier.padding(vertical = sp.xs))

        if (ui.isLoading) {
            CircularProgressIndicator()
        }

        if (ui.user == null && !ui.isLoading) {
            Text("You're not signed in.", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onSignIn, modifier = Modifier.fillMaxWidth().padding(top = sp.sm)) { Text("Sign in") }
            Button(onClick = onSignUp, modifier = Modifier.fillMaxWidth().padding(top = sp.xs)) { Text("Sign up") }
        }

        ui.user?.let { user ->
            Text("Name: ${user.fullName ?: "-"}")
            Text("Phone: ${user.phoneNumber ?: "-"}")
            Text("Email: ${user.email ?: "-"}")
            Text("Role: ${user.userType}")
            Text("Verification: ${user.verificationStatus}")

            Divider(modifier = Modifier.padding(vertical = sp.sm))

            if (isDemo) {
                Button(onClick = onSwitchToReal, modifier = Modifier.fillMaxWidth()) {
                    Text("Switch to real account")
                }
                Divider(modifier = Modifier.padding(vertical = sp.xs))
            }

            // Role upgrades
            when (user.userType) {
                UserType.GENERAL -> {
                    Button(onClick = { viewModel.requestUpgrade(UserType.FARMER) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Upgrade to Farmer")
                    }
                }
                UserType.FARMER -> {
                    Button(onClick = { viewModel.requestUpgrade(UserType.ENTHUSIAST) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Upgrade to Enthusiast")
                    }
                }
                UserType.ENTHUSIAST -> {
                    Text("Max role reached")
                }
            }

            // Verification actions
            when (user.userType) {
                UserType.FARMER -> {
                    Button(onClick = onVerifyFarmerLocation, modifier = Modifier.fillMaxWidth().padding(top = sp.xs)) {
                        Text("Verify Location")
                    }
                }
                UserType.ENTHUSIAST -> {
                    Button(onClick = onVerifyEnthusiastKyc, modifier = Modifier.fillMaxWidth().padding(top = sp.xs)) {
                        Text("Complete KYC")
                    }
                }
                else -> { /* no-op */ }
            }

            // Manual verification status controls (placeholder)
            Divider(modifier = Modifier.padding(vertical = sp.sm))
            Text("Verification Actions (placeholder)")
            Button(onClick = { viewModel.updateVerification(VerificationStatus.PENDING) }) { Text("Set PENDING") }
            Button(onClick = { viewModel.updateVerification(VerificationStatus.VERIFIED) }, modifier = Modifier.padding(top = sp.xs)) { Text("Set VERIFIED") }
        }

        ui.message?.let { Text(it, modifier = Modifier.padding(top = sp.xs)) }
        ui.error?.let { Text("Error: $it", modifier = Modifier.padding(top = sp.xs)) }
    }
}
