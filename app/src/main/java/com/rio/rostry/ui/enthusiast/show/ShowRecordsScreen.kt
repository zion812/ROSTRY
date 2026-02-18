package com.rio.rostry.ui.enthusiast.show

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.rio.rostry.data.database.entity.ShowRecordEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val ShowRecordEntity.isWin: Boolean
    get() = result.equals("WIN", ignoreCase = true)

@Composable
fun ShowRecordsScreen(
    birdId: String,
    onBack: () -> Unit,
    viewModel: ShowRecordViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(birdId) {
        viewModel.selectBird(birdId)
    }

    Scaffold(
        topBar = {
            // Assume caller handles top bar or we add one here
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Record")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && records.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (records.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records) { record ->
                        ShowRecordCard(record, onDelete = { viewModel.deleteRecord(record.recordId) })
                    }
                }
            }
            
            error?.let {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = { TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss") } }
                ) { Text(it) }
            }
        }
    }
    
    if (showAddDialog) {
        AddShowRecordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, date, result ->
                viewModel.addRecord(
                    birdId = birdId,
                    ownerId = "current_user", // TODO: Get real owner ID
                    eventName = name,
                    eventDate = date,
                    result = result
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ShowRecordCard(record: ShowRecordEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (record.isWin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, null, tint = if(record.isWin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = record.result, 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(record.eventDate)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(record.eventName, style = MaterialTheme.typography.bodyLarge)
            if (!record.notes.isNullOrBlank()) {
                Text(record.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(16.dp))
        Text("No Show Records Yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Log your bird's achievements!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShowRecordDialog(onDismiss: () -> Unit, onSave: (String, Long, String) -> Unit) {
    var eventName by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("WIN") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Show Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Result", style = MaterialTheme.typography.labelSmall)
                // Result Selection
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("WIN", "1ST", "2ND", "3RD", "PARTICIPATED").forEach { option ->
                        FilterChip(
                            selected = result == option,
                            onClick = { result = option },
                            label = { Text(option) },
                            leadingIcon = if (result == option) {
                                { Icon(Icons.Default.Check, null) }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(eventName, System.currentTimeMillis(), result) },
                enabled = eventName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
