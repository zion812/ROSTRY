package com.rio.rostry.ui.enthusiast.showrecords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ShowRecordEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Show Records Screen - View and add competition/exhibition records for Enthusiast birds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowRecordsScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: ShowRecordsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(productId) {
        viewModel.loadRecords(productId)
    }
    
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Show Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Summary Card
            if (uiState.records.isNotEmpty()) {
                ShowRecordsSummaryCard(
                    totalRecords = uiState.records.size,
                    wins = uiState.records.count { it.isWin },
                    podiums = uiState.records.count { it.isPodium }
                )
            }
            
            // Records List
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No show records yet", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Add competition and exhibition results",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add First Record")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.records) { record ->
                        ShowRecordCard(
                            record = record,
                            onDelete = { recordId -> viewModel.deleteRecord(recordId) }
                        )
                    }
                }
            }
        }
    }
    
    // Add Record Dialog
    if (showAddDialog) {
        AddShowRecordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { eventName, recordType, result, eventDate, placement, notes ->
                viewModel.addRecord(
                    productId = productId,
                    eventName = eventName,
                    recordType = recordType,
                    result = result,
                    eventDate = eventDate,
                    placement = placement,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ShowRecordsSummaryCard(
    totalRecords: Int,
    wins: Int,
    podiums: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatColumn(value = "$totalRecords", label = "Total", icon = "📊")
            StatColumn(value = "$wins", label = "Wins", icon = "🏆")
            StatColumn(value = "$podiums", label = "Podiums", icon = "🥇")
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, style = MaterialTheme.typography.headlineMedium)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun ShowRecordCard(
    record: ShowRecordEntity,
    onDelete: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val resultColor = when (record.result) {
        ShowRecordEntity.RESULT_WIN, ShowRecordEntity.RESULT_1ST -> Color(0xFFFFD700) // Gold
        ShowRecordEntity.RESULT_2ND -> Color(0xFFC0C0C0) // Silver
        ShowRecordEntity.RESULT_3RD -> Color(0xFFCD7F32) // Bronze
        ShowRecordEntity.RESULT_LOSS -> Color(0xFFD32F2F)
        else -> MaterialTheme.colorScheme.primary
    }
    
    val resultEmoji = when (record.result) {
        ShowRecordEntity.RESULT_WIN, ShowRecordEntity.RESULT_1ST -> "🏆"
        ShowRecordEntity.RESULT_2ND -> "🥈"
        ShowRecordEntity.RESULT_3RD -> "🥉"
        ShowRecordEntity.RESULT_LOSS -> "❌"
        ShowRecordEntity.RESULT_DRAW -> "🤝"
        else -> "📋"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Result Badge
                Surface(
                    shape = CircleShape,
                    color = resultColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(resultEmoji, style = MaterialTheme.typography.titleLarge)
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        record.eventName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${record.recordType} • ${dateFormat.format(Date(record.eventDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (record.placement != null) {
                        Text(
                            "Placed #${record.placement}" + (record.totalParticipants?.let { " of $it" } ?: ""),
                            style = MaterialTheme.typography.labelMedium,
                            color = resultColor
                        )
                    }
                }
                
                // Result label
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = resultColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        record.result,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = resultColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Delete Button
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Record",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Record") },
            text = { Text("Are you sure you want to delete this show record? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(record.recordId)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddShowRecordDialog(
    onDismiss: () -> Unit,
    onSave: (eventName: String, recordType: String, result: String, eventDate: Long, placement: Int?, notes: String?) -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ShowRecordEntity.TYPE_SHOW) }
    var selectedResult by remember { mutableStateOf(ShowRecordEntity.RESULT_PARTICIPATED) }
    var placement by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    
    val recordTypes = listOf(
        ShowRecordEntity.TYPE_SHOW to "Show",
        ShowRecordEntity.TYPE_EXHIBITION to "Exhibition",
        ShowRecordEntity.TYPE_COMPETITION to "Competition",
        ShowRecordEntity.TYPE_SPARRING to "Sparring"
    )
    
    val resultOptions = listOf(
        ShowRecordEntity.RESULT_1ST to "🥇 1st Place",
        ShowRecordEntity.RESULT_2ND to "🥈 2nd Place",
        ShowRecordEntity.RESULT_3RD to "🥉 3rd Place",
        ShowRecordEntity.RESULT_WIN to "🏆 Win",
        ShowRecordEntity.RESULT_LOSS to "❌ Loss",
        ShowRecordEntity.RESULT_DRAW to "🤝 Draw",
        ShowRecordEntity.RESULT_PARTICIPATED to "📋 Participated"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Show Record") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Event Name
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Record Type
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recordTypes.forEach { (type, label) ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                
                // Result
                Text("Result", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    resultOptions.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { (result, label) ->
                                FilterChip(
                                    selected = selectedResult == result,
                                    onClick = { selectedResult = result },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                // Date
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, null)
                    Spacer(Modifier.width(8.dp))
                    Text(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate)))
                }
                
                // Placement (optional)
                OutlinedTextField(
                    value = placement,
                    onValueChange = { placement = it.filter { c -> c.isDigit() } },
                    label = { Text("Placement (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                
                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        eventName,
                        selectedType,
                        selectedResult,
                        selectedDate,
                        placement.toIntOrNull(),
                        notes.ifBlank { null }
                    )
                },
                enabled = eventName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
