package com.rio.rostry.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.Icons
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
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToStorageQuota: () -> Unit = {},
    onNavigateToAdminDashboard: () -> Unit = {},
    onUpgradeClick: (UserType) -> Unit = {}, // Navigate to upgrade wizard (Comment 6)
    isAdmin: Boolean = false,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
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
                    when (user.role) {
                        UserType.FARMER -> Button(onClick = onVerifyFarmerLocation, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Start Location Verification") }
                        UserType.ENTHUSIAST -> Button(onClick = onVerifyEnthusiastKyc, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Start KYC Verification") }
                        else -> {}
                    }
                }
                VerificationStatus.PENDING -> {
                    Text("Verification Pending", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                    Text("We're reviewing your documents. This usually takes 24-48 hours.")
                }
                VerificationStatus.PENDING_UPGRADE -> {
                    Text("Upgrade Pending", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                    Text("Your role upgrade request is pending admin approval.")
                }
                VerificationStatus.VERIFIED -> {
                    Text("Verified", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    if (user.role == UserType.FARMER) {
                        Text("Farm Location Verified: ${user.farmLocationLat}, ${user.farmLocationLng}")
                    }
                    if (user.role == UserType.ENTHUSIAST) {
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
                                kycRejectionReason = null,
                                updatedAt = java.util.Date()
                            )
                            viewModel.updateUser(reset)
                            // Navigate to appropriate flow
                            if (user.role == UserType.FARMER) onVerifyFarmerLocation() else if (user.role == UserType.ENTHUSIAST) onVerifyEnthusiastKyc()
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Resubmit Verification") }
                }
            }

            // Role upgrades - Navigate to wizard instead of direct upgrade (Comment 6)
            if (user.verificationStatus != VerificationStatus.PENDING_UPGRADE) {
                when (user.role) {
                    UserType.GENERAL -> {
                        Button(onClick = { onUpgradeClick(UserType.FARMER) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Upgrade to Farmer")
                        }
                    }
                    UserType.FARMER -> {
                        Button(onClick = { onUpgradeClick(UserType.ENTHUSIAST) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Upgrade to Enthusiast")
                        }
                    }
                    UserType.ENTHUSIAST -> {
                        Text("Max role reached")
                    }
                    UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> {
                        Text("Admin Account")
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { /* TODO: View request details */ }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Text("Upgrade in Progress...")
                }
            }

            // Verification actions
            when (user.role) {
                UserType.FARMER -> {
                    Button(onClick = onVerifyFarmerLocation, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Verify Location")
                    }
                    Button(
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Farm Analytics")
                    }
                }
                UserType.ENTHUSIAST -> {
                    Button(onClick = onVerifyEnthusiastKyc, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Complete KYC")
                    }
                }
                else -> { /* no-op */ }
            }

            Button(
                onClick = onNavigateToStorageQuota,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Cloud, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Storage & Quota")
            }

            // Manual verification status controls (Admin Only)
            if (isAdmin) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text("Admin Controls", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Button(
                    onClick = onNavigateToAdminDashboard,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Verification Dashboard")
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.updateVerification(VerificationStatus.PENDING) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Set PENDING") }
                    OutlinedButton(
                        onClick = { viewModel.updateVerification(VerificationStatus.VERIFIED) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Set VERIFIED") }
                }
            }
        }

        ui.message?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        ui.error?.let { Text("Error: $it", modifier = Modifier.padding(top = 8.dp)) }
    }
}
