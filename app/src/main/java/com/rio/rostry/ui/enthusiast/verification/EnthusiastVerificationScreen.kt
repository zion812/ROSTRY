package com.rio.rostry.ui.enthusiast.verification


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.domain.model.VerificationStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastVerificationScreen(
    onNavigateUp: () -> Unit,
    viewModel: EnthusiastVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var newReference by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enthusiast Verification") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Banner
            EnthusiastStatusBanner(uiState.currentStatus, uiState.rejectionReason)

            if (uiState.currentStatus == VerificationStatus.UNVERIFIED || uiState.currentStatus == VerificationStatus.REJECTED) {
                
                // Experience Section
                Text("Your Experience", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = if (uiState.experienceYears > 0) uiState.experienceYears.toString() else "",
                    onValueChange = { viewModel.onExperienceYearsChanged(it.toIntOrNull() ?: 0) },
                    label = { Text("Years of Experience") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = if (uiState.birdCount > 0) uiState.birdCount.toString() else "",
                    onValueChange = { viewModel.onBirdCountChanged(it.toIntOrNull() ?: 0) },
                    label = { Text("Current Bird Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text("Specializations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Select all that apply", style = MaterialTheme.typography.bodySmall)
                
                val availableSpecs = listOf("Breeding", "Exhibition", "Competition", "Conservation", "Trading")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSpecs.forEach { spec ->
                        FilterChip(
                            selected = uiState.specializations.contains(spec),
                            onClick = { viewModel.onSpecializationToggled(spec) },
                            label = { Text(spec) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Achievements & Notable Work", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = uiState.achievementsDescription,
                    onValueChange = { viewModel.onAchievementsChanged(it) },
                    label = { Text("Describe your achievements") },
                    placeholder = { Text("Awards, notable birds, breeding successes, etc.") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text("References (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Add phone or email of fellow enthusiasts who can vouch for you", style = MaterialTheme.typography.bodySmall)
                
                uiState.referenceContacts.forEach { ref ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, null)
                        Text(ref, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), maxLines = 1)
                        IconButton(onClick = { viewModel.onReferenceRemoved(ref) }) {
                            Icon(Icons.Default.Delete, "Remove")
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newReference,
                        onValueChange = { newReference = it },
                        label = { Text("Reference contact") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { 
                        viewModel.onReferenceAdded(newReference)
                        newReference = ""
                    }) {
                        Icon(Icons.Default.Add, "Add")
                    }
                }







                if (uiState.error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.submitVerification() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Submit for Verification")
                    }
                }
                
            } else if (uiState.currentStatus == VerificationStatus.PENDING) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Verification Under Review",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Your Enthusiast verification is being reviewed by our team. This usually takes 24-48 hours.")
                        Text("You'll receive a notification once your verification is processed.")
                    }
                }
            } else if (uiState.currentStatus == VerificationStatus.VERIFIED) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "🎉 Congratulations!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("You are now a verified Enthusiast! You have access to all premium features including pedigree tracking, showcase cards, and the champion gallery.")
                    }
                }
            }
            
            if (uiState.isSubmitted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "✓ Verification Submitted!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Thank you for submitting your Enthusiast verification. Our team will review your application and get back to you within 24-48 hours.")
                    }
                }
            }
        }
    }
}

@Composable
fun EnthusiastStatusBanner(status: VerificationStatus, reason: String?) {
    val (color, text) = when(status) {
        VerificationStatus.UNVERIFIED -> MaterialTheme.colorScheme.surfaceVariant to "Not Verified - Submit to upgrade"
        VerificationStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer to "Verification Pending"
        VerificationStatus.PENDING_UPGRADE -> MaterialTheme.colorScheme.tertiaryContainer to "Upgrade in Progress"
        VerificationStatus.VERIFIED -> MaterialTheme.colorScheme.tertiaryContainer to "Verified Enthusiast ✓"
        VerificationStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to "Verification Rejected"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (status == VerificationStatus.REJECTED && reason != null) {
                Spacer(Modifier.height(4.dp))
                Text("Reason: $reason", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
