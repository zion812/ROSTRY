package com.rio.rostry.ui.upgrade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import com.rio.rostry.domain.model.UserType

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RoleUpgradeScreen(
    targetRole: UserType,
    onNavigateBack: () -> Unit,
    onUpgradeComplete: () -> Unit,
    onNavigateToVerification: (com.rio.rostry.domain.model.UpgradeType) -> Unit,
    onNavigateToProfileEdit: (String) -> Unit,
    viewModel: RoleUpgradeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(targetRole) {
        viewModel.setTargetRole(targetRole)
    }
    val lastRole = remember { mutableStateOf(uiState.currentRole) }
    
    // Handle UI events (Navigation)
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is RoleUpgradeViewModel.UiEvent.NavigateToVerification -> {
                    onNavigateToVerification(event.upgradeType)
                }
                is RoleUpgradeViewModel.UiEvent.NavigateToProfileEdit -> {
                    onNavigateToProfileEdit(event.field)
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(uiState.currentRole, uiState.isUpgrading) {
        val prev = lastRole.value
        val now = uiState.currentRole
        if (prev != now && !uiState.isUpgrading && now == targetRole) {
            onUpgradeComplete()
        }
        lastRole.value = now
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upgrade to ${targetRole.displayName}") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val totalSteps = 4
            val currentStepIndex = when (uiState.currentStep) {
                RoleUpgradeViewModel.WizardStep.CURRENT_ROLE -> 1
                RoleUpgradeViewModel.WizardStep.BENEFITS -> 2
                RoleUpgradeViewModel.WizardStep.PREREQUISITES -> 3
                RoleUpgradeViewModel.WizardStep.CONFIRMATION -> 4
            }
            LinearProgressIndicator(
                progress = currentStepIndex / totalSteps.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            when (uiState.currentStep) {
                RoleUpgradeViewModel.WizardStep.CURRENT_ROLE -> {
                    Text("Current Role Summary", style = MaterialTheme.typography.headlineSmall)
                    uiState.currentRole?.let { currentRole ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Your current role: ${currentRole.displayName}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Features:")
                                currentRole.primaryFeatures.forEach { feature ->
                                    Text("• $feature")
                                }
                            }
                        }
                    }
                }
                RoleUpgradeViewModel.WizardStep.BENEFITS -> {
                    Text("Benefits of Upgrading to ${targetRole.displayName}", style = MaterialTheme.typography.headlineSmall)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("New Features:")
                            targetRole.primaryFeatures.forEach { feature ->
                                Text("• $feature")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Comparison:")
                            uiState.currentRole?.let { currentRole ->
                                Text("Current: ${currentRole.primaryFeatures.joinToString(", ")}")
                                Text("After Upgrade: ${targetRole.primaryFeatures.joinToString(", ")}")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Testimonials: Upgrading has helped many users access advanced features!")
                        }
                    }
                }
                RoleUpgradeViewModel.WizardStep.PREREQUISITES -> {
                    Text("Prerequisites Check", style = MaterialTheme.typography.headlineSmall)
                    val basePrereqs = listOf(
                        Triple("Full Name", uiState.user?.fullName?.isNotBlank() == true, "fullName"),
                        Triple("Email", uiState.user?.email?.isNotBlank() == true, "email"),
                        Triple("Phone Number", uiState.user?.phoneNumber?.isNotBlank() == true, "phoneNumber")
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        basePrereqs.forEach { (label, completed, fieldKey) ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (completed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (completed) Icons.Filled.Check else Icons.Filled.Close,
                                            contentDescription = if (completed) "Completed" else "Incomplete",
                                            tint = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    if (!completed) {
                                        TextButton(onClick = { viewModel.fixPrerequisite(fieldKey) }) {
                                            Text("Fix")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (targetRole == UserType.ENTHUSIAST) {
                        val verificationError = uiState.validationErrors["verification"]
                        val verified = verificationError == null
                        Card(
                             colors = CardDefaults.cardColors(
                                containerColor = if (verified) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (verified) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = if (verified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Verification Status", style = MaterialTheme.typography.bodyLarge)
                                    if (!verified) {
                                        Text(
                                            text = "Complete your farmer verification first", 
                                            style = MaterialTheme.typography.bodySmall, 
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Show any other generic errors not covered above
                    uiState.validationErrors.filterKeys { it !in listOf("fullName", "email", "phoneNumber", "verification") }.forEach { (_, error) ->
                         Text(error, color = MaterialTheme.colorScheme.error)
                    }
                }
                RoleUpgradeViewModel.WizardStep.CONFIRMATION -> {
                    Text("Confirmation", style = MaterialTheme.typography.headlineSmall)
                    
                    val verificationStatus = uiState.user?.verificationStatus
                    
                    if (verificationStatus == com.rio.rostry.domain.model.VerificationStatus.PENDING) {
                         Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                     Icon(imageVector = Icons.Filled.Schedule, contentDescription = null)
                                     Spacer(Modifier.width(8.dp))
                                     Text("Verification Pending", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Your verification request is under review. You will be notified once it is approved.")
                            }
                        }
                    } else {
                        if (verificationStatus == com.rio.rostry.domain.model.VerificationStatus.REJECTED) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                         Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                         Spacer(Modifier.width(8.dp))
                                         Text("Verification Rejected", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    uiState.user?.kycRejectionReason?.let { reason ->
                                        Text("Reason: $reason", color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.height(8.dp))
                                    }
                                    Text("You can submit a new verification request.", color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Summary of Changes:")
                                Text("Upgrading from ${uiState.currentRole?.displayName} to ${targetRole.displayName}")
                                Text("New features will be unlocked upon upgrade.")
                                Spacer(modifier = Modifier.height(8.dp))
                                if (uiState.isUpgrading) {
                                    Text("Upgrading...")
                                } else {
                                    Button(onClick = { viewModel.performUpgrade() }) {
                                        Text(if (verificationStatus == com.rio.rostry.domain.model.VerificationStatus.REJECTED) "Try Again" else "Upgrade Now")
                                    }
                                }
                            }
                        }
                        uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                }
            }

            if (uiState.isUpgrading) {
                MigrationProgressDialog(
                    status = uiState.migrationStatus,
                    onDismiss = { /* Handle if they want to cancel or if it failed */ },
                    onComplete = { onUpgradeComplete() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    if (uiState.currentStep == RoleUpgradeViewModel.WizardStep.CURRENT_ROLE) {
                        onNavigateBack()
                    } else {
                        viewModel.previousStep()
                    }
                }) {
                    Text("Back")
                }
                if (uiState.currentStep != RoleUpgradeViewModel.WizardStep.CONFIRMATION) {
                    Button(
                        onClick = { viewModel.nextStep() },
                        enabled = uiState.canProceed
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}