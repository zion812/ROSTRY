package com.rio.rostry.ui.upgrade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import com.rio.rostry.domain.model.UserType

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RoleUpgradeScreen(
    targetRole: UserType,
    onNavigateBack: () -> Unit,
    onUpgradeComplete: () -> Unit,
    onNavigateToVerification: (com.rio.rostry.domain.model.UpgradeType) -> Unit,
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
                    val validationErrors = uiState.validationErrors
                    val basePrereqs = listOf(
                        "Full Name" to (uiState.user?.fullName?.isNotBlank() == true),
                        "Email" to (uiState.user?.email?.isNotBlank() == true),
                        "Phone Number" to (uiState.user?.phoneNumber?.isNotBlank() == true)
                    )
                    basePrereqs.forEach { (label, completed) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (completed) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = if (completed) "Completed" else "Incomplete",
                                tint = if (completed) Color.Green else Color.Red
                            )
                            Text(label)
                        }
                    }
                    if (targetRole == UserType.ENTHUSIAST) {
                        // Use validation errors for verification status (Comment 5)
                        // This ensures consistency with ViewModel's authoritative check
                        val verificationError = validationErrors["verification"]
                        val verified = verificationError == null
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (verified) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = if (verified) "Completed" else "Incomplete",
                                tint = if (verified) Color.Green else Color.Red
                            )
                            Text("Verification Status")
                        }
                    }
                    validationErrors.forEach { (key, error) ->
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                }
                RoleUpgradeViewModel.WizardStep.CONFIRMATION -> {
                    Text("Confirmation", style = MaterialTheme.typography.headlineSmall)
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
                                    Text("Upgrade Now")
                                }
                            }
                        }
                    }
                    uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
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