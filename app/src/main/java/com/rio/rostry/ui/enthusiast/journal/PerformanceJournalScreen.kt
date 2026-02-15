package com.rio.rostry.ui.enthusiast.journal

import androidx.compose.ui.res.stringResource
import com.rio.rostry.R
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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.DailyBirdLogEntity
import com.rio.rostry.ui.enthusiast.breeding.BirdSelectionDialog
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceJournalScreen(
    onNavigateBack: () -> Unit
) {
    val vm: PerformanceJournalViewModel = hiltViewModel()
    val logs by vm.logs.collectAsState()
    val selectedBird by vm.selectedBird.collectAsState()
    val allBirdsState by vm.myBirds.collectAsState()
    
    var showBirdSelector by remember { mutableStateOf(false) }
    var showAddEntryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedBird?.name?.let { "$it's Journal" } ?: "Performance Journal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBirdSelector = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Select Bird") // Placeholder icon
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedBird != null) {
                FloatingActionButton(onClick = { showAddEntryDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Entry")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (selectedBird == null) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select a bird to view performance logs")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { showBirdSelector = true }) {
                        Text("Select Bird")
                    }
                }
            } else {
                if (logs.isEmpty()) {
                    // Empty state for logs
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📓", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No entries yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Start tracking ${selectedBird?.name ?: "this bird"}'s daily performance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { showAddEntryDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add First Entry")
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(logs.size) { index ->
                            val log = logs[index]
                            LogEntryCard(log, onDelete = { vm.deleteLog(log.id) })
                        }
                    }
                }
            }
        }
    }

    if (showBirdSelector) {
        val birds = allBirdsState.data ?: emptyList()
        BirdSelectionDialog(
            title = "Select Bird for Journal",
            birds = birds,
            onSelect = { 
                vm.selectBird(it)
                showBirdSelector = false 
            },
            onDismiss = { showBirdSelector = false }
        )
    }

    if (showAddEntryDialog && selectedBird != null) {
        AddLogEntryDialog(
            birdId = selectedBird!!.productId,
            onDismiss = { showAddEntryDialog = false },
            onSave = { entry ->
                vm.addLog(entry)
                showAddEntryDialog = false
            }
        )
    }
}

@Composable
fun LogEntryCard(log: DailyBirdLogEntity, onDelete: () -> Unit) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    SimpleDateFormat(stringResource(R.string.date_format_pattern), Locale.getDefault()).format(Date(log.date)),
                    style = MaterialTheme.typography.labelMedium
                )
                Icon(
                    Icons.Filled.Delete, 
                    contentDescription = stringResource(R.string.delete), 
                    modifier = Modifier.clickable { onDelete() },
                    tint = MaterialTheme.colorScheme.error 
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(log.activityType, style = MaterialTheme.typography.titleMedium)
            if (!log.notes.isNullOrBlank()) {
                Text(log.notes, style = MaterialTheme.typography.bodyMedium)
            }
            
            // Render attached images if any
            if (!log.mediaUrlsJson.isNullOrBlank()) {
                val urls = try {
                    val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
                    com.google.gson.Gson().fromJson<List<String>>(log.mediaUrlsJson, type) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
                
                val url = urls.firstOrNull()
                if (!url.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    coil.compose.AsyncImage(
                        model = url,
                        contentDescription = "Log attachment",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
            
            if (log.performanceRating != null) {
                Spacer(Modifier.height(4.dp))
                Text("Rating: ${log.performanceRating}/10", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AddLogEntryDialog(
    birdId: String,
    onDismiss: () -> Unit,
    onSave: (DailyBirdLogEntity) -> Unit
) {
    var activityType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Log Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = activityType, 
                    onValueChange = { activityType = it }, 
                    label = { Text("Activity (e.g., Training)") }
                )
                OutlinedTextField(
                    value = notes, 
                    onValueChange = { notes = it }, 
                    label = { Text("Notes") },
                    minLines = 3
                )
                // Placeholder for real image picker
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL (Optional)") },
                    placeholder = { Text("https://example.com/photo.jpg") }
                )
                Text("Performance Rating: ${rating.toInt()}/10")
                Slider(
                    value = rating, 
                    onValueChange = { rating = it }, 
                    valueRange = 1f..10f, 
                    steps = 8
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        DailyBirdLogEntity(
                            birdId = birdId,
                            date = System.currentTimeMillis(),
                            activityType = activityType,
                            notes = notes,
                            performanceRating = rating.toInt(),
                            mediaUrlsJson = if (imageUrl.isNotBlank()) "[\"$imageUrl\"]" else null
                        )
                    )
                },
                enabled = activityType.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
