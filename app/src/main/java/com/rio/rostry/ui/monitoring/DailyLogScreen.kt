package com.rio.rostry.ui.monitoring

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.ui.components.SyncStatusBadge
import com.rio.rostry.ui.components.getSyncState
import com.rio.rostry.ui.monitoring.vm.DailyLogViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme

import com.rio.rostry.ui.components.ConflictNotification
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogScreen(
    onNavigateBack: () -> Unit,
    productId: String?,
    onNavigateToAddBird: () -> Unit,
    onNavigateToAddBatch: () -> Unit
) {
    val vm: DailyLogViewModel = hiltViewModel()
    
    // Local state to track selected product - initialized from nav arg
    var selectedProductId by remember { mutableStateOf(productId) }
    
    // Load for product when selectedProductId changes
    LaunchedEffect(selectedProductId) { 
        selectedProductId?.let { vm.loadForProduct(it) } 
    }
    
    val state by vm.uiState.collectAsState()
    val isSaving by vm.saving.collectAsState()
    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            vm.addCapturedPhoto(uri)
        }
    }
    // Camera capture launcher with a temp content Uri
    val captureTargetUri = remember { mutableStateOf<android.net.Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureTargetUri.value?.let { vm.addCapturedPhoto(it) }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = remember(state.products, searchQuery) {
        if (searchQuery.isBlank()) {
            state.products
        } else {
            state.products.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.birdCode?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    // Find the current product name/details based on local selectedProductId
    val currentProduct = state.products.find { it.productId == selectedProductId }
    val productName = currentProduct?.name ?: selectedProductId ?: "Unknown Bird"
    val productCode = currentProduct?.birdCode ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Daily Log")
                        if (selectedProductId != null) {
                            Text(
                                text = if (productCode.isNotEmpty()) "$productName ($productCode)" else productName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    // Sync Status Indicator
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp).padding(end = 16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (state.lastSyncedAt != null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Saved",
                            tint = Color(0xFF4CAF50), // Green
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        },
                floatingActionButton = {
            if (selectedProductId != null && state.products.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { vm.save() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Done, "Save Entry")
                }
            }
        }
    ) { padding ->
        if (selectedProductId == null) {
             LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!state.isOnline) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                            Text("Offline - Changes will sync when online", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
                state.conflictDetails?.let { c ->
                    item {
                        ConflictNotification(
                            conflict = c,
                            onDismiss = { vm.dismissConflict() },
                            onViewDetails = { vm.viewConflictDetails(c.entityId) }
                        )
                    }
                }
                item { Text("Select a bird/batch to log", style = MaterialTheme.typography.titleMedium) }
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name or ID") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                if (filteredProducts.isEmpty()) {
                    item {
                        if (state.products.isEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("No birds or batches found.")
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = onNavigateToAddBird, modifier = Modifier.weight(1f)) { Text("Add Individual Bird") }
                                OutlinedButton(onClick = onNavigateToAddBatch, modifier = Modifier.weight(1f)) { Text("Start Batch") }
                            }
                        } else {
                            Text("No matches found.", modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                } else {
                    items(filteredProducts, key = { it.productId }) { p ->
                        com.rio.rostry.ui.components.BirdSelectionItem(
                            product = p,
                            onClick = { 
                                // Update local state first, then VM loads via LaunchedEffect
                                selectedProductId = p.productId
                            }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!state.isOnline) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Text("You are offline. Changes will save locally and sync later.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                state.conflictDetails?.let { c ->
                    ConflictNotification(
                        conflict = c,
                        onDismiss = { vm.dismissConflict() },
                        onViewDetails = { vm.viewConflictDetails(c.entityId) }
                    )
                }

                // --- 1. VITALS CARD ---
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Vitals", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            val currentWeight = remember(state.currentLog?.weightGrams) { state.currentLog?.weightGrams?.toString() ?: "" }
                            val currentFeed = remember(state.currentLog?.feedKg) { state.currentLog?.feedKg?.toString() ?: "" }
                            
                            // Local state to prevent cursor jumping, synced with VM state
                            var weightText by remember { mutableStateOf(currentWeight) }
                            var feedText by remember { mutableStateOf(currentFeed) }
                            
                            // Effect to update local text if VM updates (e.g. initial load or sync)
                            LaunchedEffect(state.currentLog?.weightGrams) {
                                if (weightText.toDoubleOrNull() != state.currentLog?.weightGrams) {
                                    weightText = state.currentLog?.weightGrams?.toString() ?: ""
                                }
                            }
                            LaunchedEffect(state.currentLog?.feedKg) {
                                if (feedText.toDoubleOrNull() != state.currentLog?.feedKg) {
                                    feedText = state.currentLog?.feedKg?.toString() ?: ""
                                }
                            }

                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { 
                                    weightText = it
                                    if (it.isEmpty()) vm.updateWeight(null) 
                                    else it.toDoubleOrNull()?.let { v -> vm.updateWeight(v) }
                                },
                                label = { Text("Weight (g)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = feedText,
                                onValueChange = {
                                    feedText = it
                                    if (it.isEmpty()) vm.updateFeed(null)
                                    else it.toDoubleOrNull()?.let { v -> vm.updateFeed(v) }
                                },
                                label = { Text("Feed (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
                
                // --- 2. HEALTH & ACTIVITY CARD ---
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Health & Activity", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        // Activity Level
                        Column {
                            Text("Activity Level", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val activityLevels = listOf("LOW", "NORMAL", "HIGH")
                                activityLevels.forEach { level ->
                                    val isSelected = state.currentLog?.activityLevel == level
                                    androidx.compose.material3.FilterChip(
                                        selected = isSelected,
                                        onClick = { vm.setActivityLevel(level) },
                                        label = { Text(level) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = when(level) {
                                                "LOW" -> Color(0xFFFFCCBC) // Light Red
                                                "HIGH" -> Color(0xFFC8E6C9) // Light Green
                                                else -> MaterialTheme.colorScheme.primaryContainer
                                            }
                                        )
                                    )
                                }
                            }
                        }

                        // Symptoms
                        Column {
                            Text("Symptoms (if any)", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val symptoms = listOf("Lethargy", "Cough", "Diarrhea", "Limping", "No Appetite")
                                symptoms.forEach { s ->
                                    val isSelected = (state.currentLog?.symptomsJson ?: "").contains(s)
                                    androidx.compose.material3.FilterChip(
                                        selected = isSelected,
                                        onClick = { vm.toggleSymptom(s) },
                                        label = { Text(s) },
                                        leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) } } else null
                                    )
                                }
                            }
                        }
                        
                         // Medication
                        Column {
                            Text("Medication Given", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val meds = listOf("Vitamins", "Antibiotic", "Dewormer", "Electrolytes")
                                meds.forEach { m ->
                                    val isSelected = (state.currentLog?.medicationJson ?: "").contains(m)
                                    androidx.compose.material3.FilterChip(
                                        selected = isSelected,
                                        onClick = { vm.toggleMedication(m) },
                                        label = { Text(m) },
                                        leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) } } else null
                                    )
                                }
                            }
                        }
                    }
                }

                // --- 3. NOTES & MEDIA CARD ---
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Notes & Media", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        
                        OutlinedTextField(
                            value = state.currentLog?.notes ?: "",
                            onValueChange = { vm.setNotes(it) },
                            label = { Text("Daily Observation Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { pickImageLauncher.launch(arrayOf("image/*")) },
                                modifier = Modifier.weight(1f)
                            ) { 
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Gallery") 
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    val tmp = kotlin.runCatching { java.io.File.createTempFile("dlog_cam_", ".jpg", context.cacheDir) }.getOrNull()
                                    if (tmp != null) {
                                        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", tmp)
                                        captureTargetUri.value = uri
                                        takePictureLauncher.launch(uri)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) { 
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp)) 
                                Spacer(Modifier.width(8.dp))
                                Text("Camera") 
                            }
                        }
                    }
                }
                
                // Chart Section
                val chartLogs by vm.chartData.collectAsState()
                if (chartLogs.isNotEmpty()) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) 
                    ) {
                        Column(Modifier.padding(16.dp)) {
                             Text("Weight Trend (30 Days)", style = MaterialTheme.typography.titleSmall)
                             Spacer(Modifier.height(16.dp))
                             
                             val maxW = chartLogs.maxOfOrNull { it.weightGrams ?: 0.0 } ?: 0.0
                             val minW = chartLogs.filter { it.weightGrams != null }.minOfOrNull { it.weightGrams!! } ?: 0.0
                             val points = chartLogs.mapIndexedNotNull { idx, l ->
                                 val w = l.weightGrams ?: return@mapIndexedNotNull null
                                 idx to w
                             }
                             if (points.size >= 2 && maxW > 0) {
                                 Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                     val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                     val range = (maxW - minW).takeIf { it > 0 } ?: 1.0
                                     var prev: Offset? = null
                                     points.forEachIndexed { i, (idx, w) ->
                                         val x = idx * stepX
                                         val yRatio = ((w - minW) / range).toFloat()
                                         val y = size.height * (1f - yRatio)
                                         val ySafe = y.coerceIn(2f, size.height - 2f)
                                         
                                         val cur = Offset(x, ySafe)
                                         prev?.let { p ->
                                             drawLine(Color(0xFF1976D2), p, cur, strokeWidth = 5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                         }
                                         drawCircle(Color(0xFF1976D2), radius = 6f, center = cur)
                                         prev = cur
                                     }
                                 }
                             } else {
                                 Text("Not enough data for chart", style = MaterialTheme.typography.bodySmall)
                             }
                        }
                    }
                }

                // --- 4. HISTORY ---
                val historyLogs = remember(state.recentLogs) {
                    state.recentLogs.sortedByDescending { it.logDate }.take(5)
                }
                if (historyLogs.isNotEmpty()) {
                     Text("Recent History", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                     historyLogs.forEach { log ->
                         Card(
                             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Column(Modifier.padding(12.dp)) {
                                 val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(log.logDate))
                                 Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                     Text(dateStr, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                     if (log.weightGrams != null) {
                                         Text("${log.weightGrams}g", style = MaterialTheme.typography.bodyMedium)
                                     }
                                 }
                                 Spacer(Modifier.height(4.dp))
                                 if (!log.notes.isNullOrBlank()) {
                                     Text(log.notes, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                     Spacer(Modifier.height(4.dp))
                                 }
                                 Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                     if (!log.symptomsJson.isNullOrBlank()) {
                                         AssistChip(
                                             onClick = {}, 
                                             label = { Text("Symptoms") },
                                             colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.error)
                                         )
                                     }
                                     if (!log.medicationJson.isNullOrBlank()) {
                                         AssistChip(onClick = {}, label = { Text("Meds") })
                                     }
                                 }
                             }
                         }
                     }
                }
                Spacer(Modifier.height(16.dp))

                Spacer(Modifier.height(80.dp)) // Clearance for FAB
            }
        }
    }
}