package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import com.rio.rostry.ui.monitoring.vm.QuarantineViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

@Composable
fun QuarantineManagementScreen(
    productId: String = "",
    viewModel: QuarantineViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val reason = remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf("All") }
    val snackbarHostState = remember { SnackbarHostState() }
    // Listen for error messages from ViewModel and display
    LaunchedEffect(Unit) {
        viewModel.errors.collect { msg -> snackbarHostState.showSnackbar(message = msg) }
    }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    LaunchedEffect(productId) {
        if (productId.isNotBlank()) {
            viewModel.observe(productId)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Quarantine Management", style = MaterialTheme.typography.titleLarge)
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Start and manage quarantine protocols here.", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = reason.value,
                            onValueChange = { reason.value = it },
                            label = { Text("Reason / Protocol") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(onClick = { showFilters = !showFilters }) {
                                Text("Filters")
                            }
                            Button(
                                onClick = {
                                    if (reason.value.isNotBlank() && productId.isNotBlank()) {
                                        viewModel.start(productId, reason.value, null)
                                        reason.value = ""
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start Quarantine")
                            }
                        }
                    }
                }
            }

            if (showFilters) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = filterStatus == "All",
                            onClick = { filterStatus = "All" },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = filterStatus == "ACTIVE",
                            onClick = { filterStatus = "ACTIVE" },
                            label = { Text("Active") }
                        )
                        FilterChip(
                            selected = filterStatus == "RECOVERED",
                            onClick = { filterStatus = "RECOVERED" },
                            label = { Text("Recovered") }
                        )
                    }
                }
            }

            item {
                Text(
                    "Active Quarantines (${uiState.active.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val displayedActive = when (filterStatus) {
                "ACTIVE" -> uiState.active
                "All" -> uiState.active
                else -> emptyList()
            }

            items(items = displayedActive, key = { it.quarantineId }) { record ->
                QuarantineCard(
                    record = record,
                    dateFormat = dateFormat,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }

            item {
                Text(
                    "Quarantine History (${uiState.history.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            val displayedHistory = when (filterStatus) {
                "RECOVERED" -> uiState.history.filter { it.status == "RECOVERED" }
                "All" -> uiState.history
                else -> emptyList()
            }

            items(items = displayedHistory, key = { it.quarantineId }) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Product: ${record.productId}", style = MaterialTheme.typography.titleMedium)
                        Text("Reason: ${record.reason}")
                        Text("Started: ${dateFormat.format(record.startedAt)}")
                        record.endedAt?.let { Text("Ended: ${dateFormat.format(it)}") }
                        Text("Status: ${record.status}")
                        Text("Updates: ${record.updatesCount}")
                    }
                }
            }

            if (uiState.active.isEmpty() && uiState.history.isEmpty()) {
                item {
                    com.rio.rostry.ui.components.EmptyState(
                        title = "No quarantine records",
                        subtitle = "Start a quarantine to see it listed here",
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuarantineCard(
    record: QuarantineRecordEntity,
    dateFormat: SimpleDateFormat,
    uiState: QuarantineViewModel.UiState,
    viewModel: QuarantineViewModel
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    // Update every second for live countdown
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
    
    val nextUpdateDue = uiState.nextUpdateDue[record.quarantineId] ?: (record.lastUpdatedAt + TimeUnit.HOURS.toMillis(12))
    val isOverdue = uiState.isOverdue[record.quarantineId] ?: (currentTime > nextUpdateDue)
    val canDischarge = uiState.canDischarge[record.quarantineId] ?: false
    
    // Calculate countdown
    val timeRemaining = nextUpdateDue - currentTime
    val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Product: ${record.productId}", style = MaterialTheme.typography.titleMedium)
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Cannot List badge for active quarantine
                    Badge(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Block,
                                contentDescription = null,
                                modifier = Modifier.width(16.dp).height(16.dp)
                            )
                            Text("Cannot List")
                        }
                    }
                    
                    if (isOverdue) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("OVERDUE", color = Color.White)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Overdue",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Text("Reason: ${record.reason}")
            record.protocol?.let { Text("Protocol: $it") }
            Text("Started: ${dateFormat.format(record.startedAt)}")
            Text("Updates: ${record.updatesCount} / Required: 2+")
            
            // Countdown timer
            if (!isOverdue && timeRemaining > 0) {
                Text(
                    "Next update in: ${hours}h ${minutes}m ${seconds}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hours < 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            } else if (isOverdue) {
                Text(
                    "Update OVERDUE by ${Math.abs(hours)}h ${Math.abs(minutes)}m",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showUpdateDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
                
                Button(
                    onClick = {
                        viewModel.dischargeQuarantine(record.quarantineId)
                    },
                    enabled = canDischarge,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Discharge")
                }
            }
            
            if (!canDischarge) {
                Text(
                    "⚠ Quarantine requires updates every 12 hours and at least 2 updates before discharge.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // Info about listing restriction
            Text(
                "ℹ Products in quarantine cannot be listed on the marketplace until quarantine is completed and the bird is cleared.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    if (showUpdateDialog) {
        UpdateQuarantineDialog(
            record = record,
            isOverdue = isOverdue,
            onDismiss = { showUpdateDialog = false },
            onConfirm = { notes, medication, status, photoUri ->
                viewModel.updateQuarantine(record.quarantineId, notes, medication, status, photoUri)
                showUpdateDialog = false
            }
        )
    }
}

@Composable
private fun UpdateQuarantineDialog(
    record: QuarantineRecordEntity,
    isOverdue: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (notes: String?, medication: String?, status: String?, photoUri: String?) -> Unit
) {
    var vetNotes by remember { mutableStateOf(record.vetNotes ?: "") }
    var medication by remember { mutableStateOf(record.medicationScheduleJson ?: "") }
    var healthStatus by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var attempted by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Quarantine") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = vetNotes,
                    onValueChange = { vetNotes = it },
                    label = { Text("Vet Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (isOverdue && attempted && vetNotes.isBlank()) {
                    Text("Notes are required for overdue updates", color = MaterialTheme.colorScheme.error)
                }
                
                OutlinedTextField(
                    value = medication,
                    onValueChange = { medication = it },
                    label = { Text("Medication Schedule") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                Text("Health Status")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val options = listOf("IMPROVING", "STABLE", "DECLINING")
                    options.forEach { opt ->
                        FilterChip(
                            selected = healthStatus == opt,
                            onClick = { healthStatus = opt },
                            label = { Text(opt) }
                        )
                    }
                }

                val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    photoUri = uri?.toString()
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { imagePicker.launch("image/*") }) { Text("Add Photo") }
                    Text(photoUri?.let { "1 photo attached" } ?: "No photo")
                }
                if (isOverdue && attempted && photoUri.isNullOrBlank()) {
                    Text("Photo is required for overdue updates", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    attempted = true
                    if (!isOverdue || (vetNotes.isNotBlank() && photoUri != null)) {
                        onConfirm(
                            vetNotes.ifBlank { null },
                            medication.ifBlank { null },
                            healthStatus.ifBlank { null },
                            photoUri
                        )
                    }
                }
            ) {
                Text("Save Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
