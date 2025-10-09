package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
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
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.ui.monitoring.vm.HatchingViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun HatchingProcessScreen(
    viewModel: HatchingViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val batchName = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Hatching Process", style = MaterialTheme.typography.titleLarge)
            }
            
            item {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Create and monitor incubation batches.", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = batchName.value,
                            onValueChange = { batchName.value = it },
                            label = { Text("Batch name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (batchName.value.isNotBlank()) {
                                    viewModel.startBatch(batchName.value)
                                    batchName.value = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Batch")
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }

            item {
                Text(
                    "Active Batches (${uiState.batches.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(uiState.batches) { batch ->
                HatchingBatchCard(
                    batch = batch,
                    dateFormat = dateFormat,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun HatchingBatchCard(
    batch: HatchingBatchEntity,
    dateFormat: SimpleDateFormat,
    viewModel: HatchingViewModel
) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDialog by remember { mutableStateOf(false) }
    val hatchedCount = remember { mutableStateOf("") }
    val failedCount = remember { mutableStateOf("") }
    val parentMaleId = remember { mutableStateOf("") }
    val parentFemaleId = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    
    // Update every second for live countdown
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
    
    val expectedHatchAt = batch.expectedHatchAt
    val isOverdue = expectedHatchAt != null && currentTime > expectedHatchAt
    val isDueSoon = expectedHatchAt != null && !isOverdue && (expectedHatchAt - currentTime) < TimeUnit.HOURS.toMillis(48)
    
    // Calculate countdown
    val timeRemaining = if (expectedHatchAt != null) expectedHatchAt - currentTime else 0L
    val days = TimeUnit.MILLISECONDS.toDays(timeRemaining)
    val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
    
    // Observe tasks for this batch
    val tasks by viewModel.tasksFor(batch.batchId).collectAsStateWithLifecycle(initialValue = emptyList())
    val nowTs = currentTime
    val totalCount = tasks.size
    val overdueCount = tasks.count { it.completedAt == null && it.dueAt < nowTs }
    val dueCount = tasks.count { it.completedAt == null && (it.snoozeUntil == null || it.snoozeUntil <= nowTs) && it.dueAt <= nowTs }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(batch.name, style = MaterialTheme.typography.titleMedium)
                
                when {
                    isOverdue -> {
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
                    isDueSoon -> {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                                    Text("DUE SOON", color = Color.White)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Due Soon",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
            
            Text("Started: ${dateFormat.format(batch.startedAt)}")
            
            expectedHatchAt?.let {
                Text("Expected Hatch: ${dateFormat.format(it)}")
                
                // Countdown timer
                if (!isOverdue && timeRemaining > 0) {
                    val countdownColor = when {
                        days < 2 -> MaterialTheme.colorScheme.error
                        days < 5 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                    
                    Text(
                        "Time remaining: ${days}d ${hours}h ${minutes}m ${seconds}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = countdownColor
                    )
                } else if (isOverdue) {
                    Text(
                        "OVERDUE by ${Math.abs(days)}d ${Math.abs(hours)}h ${Math.abs(minutes)}m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            batch.temperatureC?.let {
                Text("Temperature: $it°C")
            }
            batch.humidityPct?.let {
                Text("Humidity: $it%")
            }
            
            Spacer(Modifier.height(4.dp))

            // Tasks summary row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Tasks: $totalCount", style = MaterialTheme.typography.bodyMedium)
                Text("Due: $dueCount", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                Text("Overdue: $overdueCount", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { viewModel.selectBatch(batch.batchId) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Logs")
                }
                if (isDueSoon || isOverdue) {
                    Button(
                        onClick = {
                            // Prefill if available; batch doesn't hold parents, leave empty
                            showDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log Hatch Outcome")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    val count = hatchedCount.value.toIntOrNull() ?: 0
                    viewModel.logHatchOutcome(
                        batchId = batch.batchId,
                        count = count,
                        parentMaleId = parentMaleId.value.ifBlank { null },
                        parentFemaleId = parentFemaleId.value.ifBlank { null },
                        notes = notes.value.ifBlank { null }
                    )
                    showDialog = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
            title = { Text("Log Hatch Outcome") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hatchedCount.value,
                        onValueChange = { s -> hatchedCount.value = s.filter { it.isDigit() } },
                        label = { Text("Hatched Count") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = failedCount.value,
                        onValueChange = { s -> failedCount.value = s.filter { it.isDigit() } },
                        label = { Text("Failed Count (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = parentMaleId.value,
                        onValueChange = { parentMaleId.value = it },
                        label = { Text("Parent Male ID (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = parentFemaleId.value,
                        onValueChange = { parentFemaleId.value = it },
                        label = { Text("Parent Female ID (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes.value,
                        onValueChange = { notes.value = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}
