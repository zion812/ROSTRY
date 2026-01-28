package com.rio.rostry.ui.enthusiast.breeding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.BreedingCompatibilityCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingCalculatorScreen(
    onNavigateBack: () -> Unit
) {
    val vm: BreedingCalculatorViewModel = hiltViewModel()
    val selectionState by vm.selectionState.collectAsState()
    val prediction by vm.prediction.collectAsState()
    val compatibility by vm.compatibility.collectAsState()
    
    val sire = selectionState.first
    val dam = selectionState.second
    val allBirds = selectionState.third

    var showSireSelector by remember { mutableStateOf(false) }
    var showDamSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Calculator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Predict offspring stats by selecting parents.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // Parent Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ParentSelectorCard(
                    label = "SIRE (Male)",
                    selectedBird = sire,
                    onClick = { showSireSelector = true },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(Modifier.width(16.dp))
                
                ParentSelectorCard(
                    label = "DAM (Female)",
                    selectedBird = dam,
                    onClick = { showDamSelector = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            // Prediction Results
            if (prediction != null && compatibility != null) {
                val p = prediction!!
                val c = compatibility!!
                Text("Prediction Results", style = MaterialTheme.typography.titleLarge)
                
                // Compatibility Score Card
                CompatibilityScoreCard(result = c)
                
                Spacer(Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResultRow("Est. Weight", "${"%.2f".format(p.weightRange.min / 1000)} - ${"%.2f".format(p.weightRange.max / 1000)} kg")
                        ResultRow("Est. Height", "${"%.1f".format(p.heightRange.min)} - ${"%.1f".format(p.heightRange.max)} cm")
                        
                        Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        
                        Text("Predicted Characteristics:", fontWeight = FontWeight.Bold)
                        // Traits from Genetic Engine
                        if (p.likelyTraits.isNotEmpty()) {
                             p.likelyTraits.forEach { 
                                 Text("• $it", style = MaterialTheme.typography.bodyMedium)
                             }
                             Spacer(Modifier.height(8.dp))
                        }
                        
                        Text("Color Probability (Punnett):", fontWeight = FontWeight.Bold)
                        p.colorProbabilities.forEach { prob ->
                             Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(prob.item)
                                Text("${prob.percentage}%")
                             }
                             // Simple bar visualization
                             Box(
                                 Modifier
                                     .fillMaxWidth()
                                     .height(4.dp)
                                     .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                             ) {
                                 Box(
                                    Modifier
                                        .fillMaxWidth(prob.percentage / 100f)
                                        .height(4.dp)
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                                 )
                             }
                        }
                    }
                }
            } else {
                Text(
                    "Select both parents to see prediction",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }

    if (showSireSelector) {
        BirdSelectionDialog(
            title = "Select Sire",
            birds = allBirds.filter { it.gender?.equals("male", ignoreCase = true) == true || it.gender == null },
            onSelect = { vm.selectSire(it); showSireSelector = false },
            onDismiss = { showSireSelector = false }
        )
    }

    if (showDamSelector) {
        BirdSelectionDialog(
            title = "Select Dam",
            birds = allBirds.filter { it.gender?.equals("female", ignoreCase = true) == true || it.gender == null },
            onSelect = { vm.selectDam(it); showDamSelector = false },
            onDismiss = { showDamSelector = false }
        )
    }
}

@Composable
fun ParentSelectorCard(
    label: String,
    selectedBird: ProductEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedBird != null) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            if (selectedBird != null) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(selectedBird.name.ifBlank { "Unnamed" }, fontWeight = FontWeight.Medium)
                Text(selectedBird.breed ?: "-", fontSize = 10.sp)
            } else {
                 Text("Tap to Select", color = Color.Gray)
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdSelectionDialog(
    title: String,
    birds: List<ProductEntity>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
                )
            }
        ) { padding ->
            LazyColumn(Modifier.padding(padding)) {
                items(birds.size) { index ->
                    val bird = birds[index]
                    androidx.compose.material3.ListItem(
                        headlineContent = { Text(bird.name.ifBlank { "Bird ${bird.productId.take(4)}" }) },
                        supportingContent = { Text("${bird.breed ?: "Unknown"} • ${bird.ageWeeks ?: "?"} wks") },
                        modifier = Modifier.clickable { onSelect(bird.productId) }
                    )
                    Divider()
                }
            }
        }
    }
}

/**
 * Compatibility Score Card - Displays real calculated score from domain logic.
 */
@Composable
private fun CompatibilityScoreCard(
    result: BreedingCompatibilityCalculator.CompatibilityResult
) {
    val totalScore = result.score
    
    val scoreColor = when {
        totalScore >= 80 -> Color(0xFF4CAF50) // Green 
        totalScore >= 60 -> Color(0xFFFFB300) // Amber 
        totalScore >= 40 -> Color(0xFFFF9800) // Orange 
        else -> Color(0xFFD32F2F) // Red 
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Compatibility Score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (result.coiPercent > 10.0) {
                   Spacer(Modifier.width(8.dp))
                   // Warning Badge
                    Box(Modifier.background(Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                        Text("INBREEDING RISK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Score Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                CircularProgressIndicator(
                    progress = { totalScore / 100f },
                    modifier = Modifier.height(80.dp).width(80.dp),
                    color = scoreColor,
                    strokeWidth = 8.dp,
                    trackColor = scoreColor.copy(alpha = 0.2f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$totalScore%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                }
            }
            
            Text(
                result.verdict,
                style = MaterialTheme.typography.titleSmall,
                color = scoreColor,
                fontWeight = FontWeight.SemiBold
            )
            if (result.coiPercent > 0) {
                 Text(
                    "COI: ${"%.2f".format(result.coiPercent)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if(result.coiPercent > 6.25) Color.Red else Color.Gray
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Reasons
            result.reasons.forEach { reason ->
                 Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                     val isBad = reason.contains("WARNING") || reason.contains("CRITICAL")
                     Text(
                         text = if (isBad) "⚠️ $reason" else "✓ $reason",
                         style = MaterialTheme.typography.bodySmall,
                         color = if (isBad) Color.Red else Color.DarkGray // Ideally OnSurface
                     )
                 }
            }
        }
    }
}
