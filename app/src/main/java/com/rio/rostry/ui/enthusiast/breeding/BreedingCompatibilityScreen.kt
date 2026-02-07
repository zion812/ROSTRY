package com.rio.rostry.ui.enthusiast.breeding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.CompatibilityResult
import com.rio.rostry.domain.breeding.RiskLevel
import com.rio.rostry.domain.breeding.TraitPrediction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingCompatibilityScreen(
    viewModel: BreedingCompatibilityViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showSireSheet by remember { mutableStateOf(false) }
    var showDamSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Compatibility") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.selectedSire != null || state.selectedDam != null) {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Selection Area
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SelectionCard(
                        modifier = Modifier.weight(1f),
                        title = "Select Sire",
                        bird = state.selectedSire,
                        placeholderIcon = Icons.Default.Male,
                        onClick = { showSireSheet = true }
                    )
                    SelectionCard(
                        modifier = Modifier.weight(1f),
                        title = "Select Dam",
                        bird = state.selectedDam,
                        placeholderIcon = Icons.Default.Female,
                        onClick = { showDamSheet = true }
                    )
                }
            }

            // Results Area
            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.compatibility != null && state.prediction != null) {
                item {
                    CompatibilityResultCard(result = state.compatibility!!)
                }
                item {
                    TraitPredictionCard(prediction = state.prediction!!)
                }
            } else if (state.error != null) {
                item {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Select both parents to analyze compatibility",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showSireSheet) {
        BirdSelectionSheet(
            title = "Select Sire",
            birds = state.availableSires,
            onDismiss = { showSireSheet = false },
            onSelect = { 
                viewModel.selectSire(it)
                showSireSheet = false 
            }
        )
    }

    if (showDamSheet) {
        BirdSelectionSheet(
            title = "Select Dam",
            birds = state.availableDams,
            onDismiss = { showDamSheet = false },
            onSelect = { 
                viewModel.selectDam(it)
                showDamSheet = false 
            }
        )
    }
}

@Composable
fun SelectionCard(
    modifier: Modifier,
    title: String,
    bird: ProductEntity?,
    placeholderIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        colors = if (bird != null) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                 else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (bird != null) {
                Icon(placeholderIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(bird.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(bird.breed ?: "Unknown", style = MaterialTheme.typography.labelSmall)
            } else {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CompatibilityResultCard(result: CompatibilityResult) {
    val color = when(result.riskLevel) {
        RiskLevel.LOW -> Color(0xFF4CAF50)
        RiskLevel.MODERATE -> Color(0xFFFF9800)
        RiskLevel.HIGH -> Color(0xFFFF5722)
        RiskLevel.CRITICAL -> Color(0xFFD32F2F)
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Compatibility Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Surface(color = color, shape = MaterialTheme.shapes.small) {
                    Text(
                        "${result.score}% matches", 
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text("Analysis", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            
            if (result.warnings.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("No genetic risks detected", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                result.warnings.forEach { warning ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(warning, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            
            result.recommendations.forEach { rec ->
                 Text("• $rec", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun TraitPredictionCard(prediction: TraitPrediction) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Offspring Prediction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            PredictionRow("Estimated Quality", prediction.estimatedQuality)
            
            Spacer(Modifier.height(8.dp))
            
            Text("Possible Colors", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            prediction.possibleColors.forEach { (color, prob) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(color, style = MaterialTheme.typography.bodySmall)
                    Text("${(prob * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text("Possible Breeds", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            prediction.possibleBreeds.forEach { (breed, prob) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(breed, style = MaterialTheme.typography.bodySmall)
                    Text("${(prob * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PredictionRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdSelectionSheet(title: String, birds: List<ProductEntity>, onDismiss: () -> Unit, onSelect: (ProductEntity) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            if (birds.isEmpty()) {
                Text("No available birds found for this gender.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(birds.size) { index ->
                        val bird = birds[index]
                        Card(
                            onClick = { onSelect(bird) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(bird.name, fontWeight = FontWeight.Bold)
                                    Text("${bird.breed} • ${bird.color}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
