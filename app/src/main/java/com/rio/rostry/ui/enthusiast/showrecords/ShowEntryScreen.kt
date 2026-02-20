package com.rio.rostry.ui.enthusiast.showrecords

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShowEntryScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: ShowRecordsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Form state
    var eventName by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var eventDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var category by remember { mutableStateOf("") }
    var recordType by remember { mutableStateOf("SHOW") }
    var result by remember { mutableStateOf("PARTICIPATED") }
    var score by remember { mutableStateOf("") }
    var placement by remember { mutableStateOf("") }
    var totalParticipants by remember { mutableStateOf("") }
    var opponentName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var judgesNotes by remember { mutableStateOf("") }
    var saveTriggered by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    // Initialize bird context
    LaunchedEffect(productId) {
        viewModel.loadRecords(productId)
    }

    // Navigate back on successful save (detecting when isSaving goes from true to false and no error)
    LaunchedEffect(state.isSaving) {
        if (saveTriggered && !state.isSaving && state.errorMessage == null) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Show Entry") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Event Details Section ---
                Text(
                    "Event Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name *") },
                    placeholder = { Text("e.g., State Poultry Show 2024") },
                    leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = eventLocation,
                    onValueChange = { eventLocation = it },
                    label = { Text("Location") },
                    placeholder = { Text("e.g., City Convention Center") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Date picker button
                OutlinedCard(
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = eventDate }
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val selected = Calendar.getInstance().apply { set(y, m, d) }
                                eventDate = selected.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Event Date", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(dateFormatter.format(Date(eventDate)),
                                    style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Icon(Icons.Default.Edit, contentDescription = "Change date",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    placeholder = { Text("e.g., Bantam, Game Fowl, Heritage") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                HorizontalDivider()

                // --- Record Type ---
                Text(
                    "Record Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "SHOW" to Icons.Default.EmojiEvents,
                        "EXHIBITION" to Icons.Default.Visibility,
                        "COMPETITION" to Icons.Default.MilitaryTech,
                        "SPARRING" to Icons.Default.SportsMma
                    ).forEach { (type, icon) ->
                        FilterChip(
                            selected = recordType == type,
                            onClick = { recordType = type },
                            label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
                            leadingIcon = if (recordType == type) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else {
                                { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            }
                        )
                    }
                }

                // Show opponent fields for sparring type
                AnimatedVisibility(visible = recordType == "SPARRING") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = opponentName,
                            onValueChange = { opponentName = it },
                            label = { Text("Opponent Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                HorizontalDivider()

                // --- Result ---
                Text(
                    "Result",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("WIN", "1ST", "2ND", "3RD", "PARTICIPATED", "LOSS", "DRAW").forEach { option ->
                        FilterChip(
                            selected = result == option,
                            onClick = { result = option },
                            label = { Text(option) },
                            leadingIcon = if (result == option) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }

                // Scoring row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = score,
                        onValueChange = { score = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Score") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = placement,
                        onValueChange = { placement = it.filter { c -> c.isDigit() } },
                        label = { Text("Placement") },
                        placeholder = { Text("#") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = totalParticipants,
                        onValueChange = { totalParticipants = it.filter { c -> c.isDigit() } },
                        label = { Text("Total") },
                        placeholder = { Text("Entries") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                HorizontalDivider()

                // --- Notes ---
                Text(
                    "Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = judgesNotes,
                    onValueChange = { judgesNotes = it },
                    label = { Text("Judge's Feedback") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                // Error display
                state.errorMessage?.let { err ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null,
                                tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(8.dp))
                            Text(err, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }

                // Save button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        saveTriggered = true
                        viewModel.saveRecord(
                            eventName = eventName,
                            eventDate = eventDate,
                            type = recordType,
                            result = result,
                            location = eventLocation.takeIf { it.isNotBlank() },
                            category = category.takeIf { it.isNotBlank() },
                            score = score.toDoubleOrNull(),
                            placement = placement.toIntOrNull(),
                            totalParticipants = totalParticipants.toIntOrNull(),
                            opponentName = opponentName.takeIf { it.isNotBlank() },
                            judge = judgesNotes.takeIf { it.isNotBlank() },
                            notes = notes.takeIf { it.isNotBlank() }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = eventName.isNotBlank() && !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Record", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
