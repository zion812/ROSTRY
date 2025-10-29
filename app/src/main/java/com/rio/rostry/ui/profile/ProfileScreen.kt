package com.rio.rostry.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus

@Composable
fun ProfileScreen(
    onVerifyFarmerLocation: () -> Unit,
    onVerifyEnthusiastKyc: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Profile")
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        if (ui.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (!ui.isLoading && ui.user == null) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No profile data",
                subtitle = "Please sign in or try again later",
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        }

        ui.user?.let { user ->
            Text("Name: ${user.fullName ?: "-"}")
            Text("Phone: ${user.phoneNumber ?: "-"}")
            Text("Email: ${user.email ?: "-"}")
            Text("Role: ${user.userType}")
            Text("Verification: ${user.verificationStatus}")

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Verification status section
            when (user.verificationStatus) {
                VerificationStatus.UNVERIFIED, null -> {
                    Text("Not Verified", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                    when (user.userType) {
                        UserType.FARMER -> Button(onClick = onVerifyFarmerLocation, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Start Location Verification") }
                        UserType.ENTHUSIAST -> Button(onClick = onVerifyEnthusiastKyc, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Start KYC Verification") }
                        else -> {}
                    }
                }
                VerificationStatus.PENDING -> {
                    Text("Verification Pending", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                    Text("We're reviewing your documents. This usually takes 24-48 hours.")
                }
                VerificationStatus.VERIFIED -> {
                    Text("Verified", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    if (user.userType == UserType.FARMER) {
                        Text("Farm Location Verified: ${user.farmLocationLat}, ${user.farmLocationLng}")
                    }
                    if (user.userType == UserType.ENTHUSIAST) {
                        Text("KYC Level: ${user.kycLevel ?: "-"}")
                    }
                }
                VerificationStatus.REJECTED -> {
                    Text("Verification Rejected", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                    if (!user.kycRejectionReason.isNullOrEmpty()) {
                        Text("Reason: ${user.kycRejectionReason}")
                    }
                    Button(
                        onClick = {
                            // Reset verification fields locally and request update
                            val reset = user.copy(
                                verificationStatus = VerificationStatus.UNVERIFIED,
                                kycDocumentUrls = null,
                                kycImageUrls = null,
                                kycDocumentTypes = null,
                                kycUploadStatus = null,
                                kycUploadedAt = null,
                                kycRejectionReason = null,
                                updatedAt = System.currentTimeMillis()
                            )
                            viewModel.updateUser(reset)
                            // Navigate to appropriate flow
                            if (user.userType == UserType.FARMER) onVerifyFarmerLocation() else if (user.userType == UserType.ENTHUSIAST) onVerifyEnthusiastKyc()
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Resubmit Verification") }
                }
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
                    Button(onClick = onVerifyFarmerLocation, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Verify Location")
                    }
                }
                UserType.ENTHUSIAST -> {
                    Button(onClick = onVerifyEnthusiastKyc, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Complete KYC")
                    }
                }
                else -> { /* no-op */ }
            }

            // Manual verification status controls (placeholder)
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Verification Actions (placeholder)")
            Button(onClick = { viewModel.updateVerification(VerificationStatus.PENDING) }) { Text("Set PENDING") }
            Button(onClick = { viewModel.updateVerification(VerificationStatus.VERIFIED) }, modifier = Modifier.padding(top = 8.dp)) { Text("Set VERIFIED") }
        }

        ui.message?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        ui.error?.let { Text("Error: $it", modifier = Modifier.padding(top = 8.dp)) }
    }
}
