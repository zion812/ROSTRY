package com.rio.rostry.ui.enthusiast.lineage

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import java.text.SimpleDateFormat
import java.util.*

// Enthusiast premium colors
private val PurplePrimary = Color(0xFF673AB7)
private val PurpleDark = Color(0xFF512DA8)
private val CyanAccent = Color(0xFF00E5FF)
private val GoldAccent = Color(0xFFFFD700)
private val SurfaceDark = Color(0xFF1A1228)
private val CardDark = Color(0xFF261D35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraitRecordingScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: TraitRecordingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTraitDef by remember { mutableStateOf<TraitDefinition?>(null) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            showAddDialog = false
            selectedTraitDef = null
            viewModel.clearSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Trait Recording",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        uiState.bird?.let { bird ->
                            Text(
                                bird.name,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = SurfaceDark
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanAccent)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Category tabs
                CategoryTabRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )

                // Content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Trait input cards for current category
                    val traits = TraitRecordingViewModel.getTraitsForCategory(uiState.selectedCategory)
                    
                    items(traits) { traitDef ->
                        val latestRecord = uiState.traitRecords
                            .filter { it.traitName == traitDef.key }
                            .maxByOrNull { it.recordedAt }
                        
                        TraitCard(
                            traitDef = traitDef,
                            latestRecord = latestRecord,
                            isRecorded = traitDef.key in uiState.recordedTraitNames,
                            onClick = {
                                selectedTraitDef = traitDef
                                showAddDialog = true
                            }
                        )
                    }

                    // History section
                    val categoryRecords = uiState.traitRecords
                        .filter { it.traitCategory == uiState.selectedCategory }
                    
                    if (categoryRecords.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Recent Records",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        
                        items(categoryRecords.take(10)) { record ->
                            TraitHistoryItem(record = record, onDelete = { viewModel.deleteTrait(it) })
                        }
                    }
                }
            }
        }
    }

    // Add Trait Dialog
    if (showAddDialog && selectedTraitDef != null) {
        AddTraitDialog(
            traitDef = selectedTraitDef!!,
            category = uiState.selectedCategory,
            bird = uiState.bird,
            isSaving = uiState.isSaving,
            onDismiss = { 
                showAddDialog = false
                selectedTraitDef = null
            },
            onSave = { value, ageWeeks, notes ->
                viewModel.saveTrait(
                    traitName = selectedTraitDef!!.key,
                    traitCategory = uiState.selectedCategory,
                    traitValue = value,
                    traitUnit = selectedTraitDef!!.unit,
                    ageWeeks = ageWeeks,
                    notes = notes
                )
            }
        )
    }
}

@Composable
private fun CategoryTabRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        BirdTraitRecordEntity.CATEGORY_PHYSICAL to "Physical",
        BirdTraitRecordEntity.CATEGORY_BEHAVIORAL to "Behavioral",
        BirdTraitRecordEntity.CATEGORY_PRODUCTION to "Production",
        BirdTraitRecordEntity.CATEGORY_QUALITY to "Quality"
    )
    
    val icons = listOf(
        Icons.Filled.Straighten,
        Icons.Filled.Psychology,
        Icons.Filled.Egg,
        Icons.Filled.Stars
    )

    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it.first == selectedCategory }.coerceAtLeast(0),
        containerColor = CardDark,
        contentColor = Color.White,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            val index = categories.indexOfFirst { it.first == selectedCategory }.coerceAtLeast(0)
            if (index < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = CyanAccent
                )
            }
        }
    ) {
        categories.forEachIndexed { index, (key, label) ->
            Tab(
                selected = selectedCategory == key,
                onClick = { onCategorySelected(key) },
                text = { Text(label, fontSize = 13.sp) },
                icon = { Icon(icons[index], contentDescription = label, modifier = Modifier.size(18.dp)) },
                selectedContentColor = CyanAccent,
                unselectedContentColor = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun TraitCard(
    traitDef: TraitDefinition,
    latestRecord: BirdTraitRecordEntity?,
    isRecorded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecorded) Brush.linearGradient(
                            listOf(PurplePrimary, CyanAccent)
                        ) else Brush.linearGradient(
                            listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.5f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isRecorded) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    traitDef.label,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                
                if (latestRecord != null) {
                    val displayValue = buildString {
                        append(latestRecord.traitValue)
                        latestRecord.traitUnit?.let { unit ->
                            if (unit != "score_1_10") append(" $unit")
                            else append("/10")
                        }
                        latestRecord.ageWeeks?.let { append(" @ ${it}w") }
                    }
                    Text(
                        displayValue,
                        color = CyanAccent,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        "Tap to record",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                }
            }
            
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun TraitHistoryItem(
    record: BirdTraitRecordEntity,
    onDelete: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark.copy(alpha = 0.6f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                record.traitName.replace("_", " ").replaceFirstChar { it.uppercase() },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    record.traitValue,
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                record.traitUnit?.let { unit ->
                    Text(
                        if (unit == "score_1_10") "/10" else " $unit",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                record.ageWeeks?.let { age ->
                    Text(
                        " • ${age}w",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Text(
            dateFormat.format(Date(record.recordedAt)),
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 11.sp
        )
        
        IconButton(
            onClick = { onDelete(record.recordId) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTraitDialog(
    traitDef: TraitDefinition,
    category: String,
    bird: ProductEntity?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (value: String, ageWeeks: Int?, notes: String?) -> Unit
) {
    var traitValue by remember { mutableStateOf("") }
    var sliderValue by remember { mutableFloatStateOf(5f) }
    var selectedOption by remember { mutableStateOf(traitDef.options.firstOrNull() ?: "") }
    var ageWeeks by remember { mutableStateOf(bird?.ageWeeks?.toString() ?: "") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text("Record ${traitDef.label}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Value input based on type
                when (traitDef.inputType) {
                    TraitInputType.NUMERIC -> {
                        OutlinedTextField(
                            value = traitValue,
                            onValueChange = { traitValue = it },
                            label = { Text("Value${traitDef.unit?.let { u -> " ($u)" } ?: ""}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = CyanAccent,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    TraitInputType.SLIDER -> {
                        Column {
                            Text(
                                "Score: ${sliderValue.toInt()}/10",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Slider(
                                value = sliderValue,
                                onValueChange = { sliderValue = it },
                                valueRange = 1f..10f,
                                steps = 8,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyanAccent,
                                    activeTrackColor = PurplePrimary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                    TraitInputType.DROPDOWN -> {
                        Column {
                            Text("Select:", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                traitDef.options.forEach { option ->
                                    FilterChip(
                                        selected = selectedOption == option,
                                        onClick = { selectedOption = option },
                                        label = { Text(option, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PurplePrimary,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.White.copy(alpha = 0.1f),
                                            labelColor = Color.White.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                    TraitInputType.TEXT -> {
                        OutlinedTextField(
                            value = traitValue,
                            onValueChange = { traitValue = it },
                            label = { Text(traitDef.label) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = CyanAccent,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Age at measurement
                OutlinedTextField(
                    value = ageWeeks,
                    onValueChange = { ageWeeks = it },
                    label = { Text("Age (weeks)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanAccent,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanAccent,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalValue = when (traitDef.inputType) {
                        TraitInputType.SLIDER -> sliderValue.toInt().toString()
                        TraitInputType.DROPDOWN -> selectedOption
                        else -> traitValue
                    }
                    if (finalValue.isNotBlank()) {
                        onSave(finalValue, ageWeeks.toIntOrNull(), notes.takeIf { it.isNotBlank() })
                    }
                },
                enabled = !isSaving && when (traitDef.inputType) {
                    TraitInputType.SLIDER -> true
                    TraitInputType.DROPDOWN -> selectedOption.isNotBlank()
                    else -> traitValue.isNotBlank()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary,
                    contentColor = Color.White
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        }
    )
}

// Using ProductEntity inline to avoid import confusion
private typealias ProductEntity = com.rio.rostry.data.database.entity.ProductEntity
