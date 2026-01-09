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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingCalculatorScreen(
    onNavigateBack: () -> Unit
) {
    val vm: BreedingCalculatorViewModel = hiltViewModel()
    val selectionState by vm.selectionState.collectAsState()
    val prediction by vm.prediction.collectAsState()
    
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
            if (prediction != null) {
                val p = prediction!!
                Text("Prediction Results", style = MaterialTheme.typography.titleLarge)
                
                // Compatibility Score Card
                CompatibilityScoreCard(
                    sire = sire,
                    dam = dam
                )
                
                Spacer(Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResultRow("Weight", "${"%.2f".format(p.weightRange.min / 1000)} - ${"%.2f".format(p.weightRange.max / 1000)} kg")
                        ResultRow("Height", "${"%.1f".format(p.heightRange.min)} - ${"%.1f".format(p.heightRange.max)} cm")
                        
                        Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        
                        Text("Color Probability:", fontWeight = FontWeight.Bold)
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
 * Compatibility Score Card - Calculates and displays breed compatibility.
 */
@Composable
private fun CompatibilityScoreCard(
    sire: ProductEntity?,
    dam: ProductEntity?
) {
    if (sire == null || dam == null) return
    
    // Calculate compatibility score based on various factors
    val breedMatch = if (sire.breed?.equals(dam.breed, ignoreCase = true) == true) 30 else 15
    val ageCompatibility = calculateAgeCompatibility(sire.ageWeeks, dam.ageWeeks)
    val healthBonus = if (sire.healthStatus == "HEALTHY" && dam.healthStatus == "HEALTHY") 20 else 10
    
    val totalScore = minOf(100, breedMatch + ageCompatibility + healthBonus + 30) // Base 30 for having both parents
    
    val scoreColor = when {
        totalScore >= 80 -> Color(0xFF4CAF50) // Green - Excellent
        totalScore >= 60 -> Color(0xFFFFB300) // Amber - Good
        totalScore >= 40 -> Color(0xFFFF9800) // Orange - Fair
        else -> Color(0xFFD32F2F) // Red - Poor
    }
    
    val recommendation = when {
        totalScore >= 80 -> "Excellent Match! 🌟"
        totalScore >= 60 -> "Good Pairing 👍"
        totalScore >= 40 -> "Fair Compatibility"
        else -> "Consider Other Options"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Compatibility Score",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
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
                recommendation,
                style = MaterialTheme.typography.titleSmall,
                color = scoreColor,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Score breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreFactor("Breed", if (breedMatch >= 30) "✓ Same" else "Mixed")
                ScoreFactor("Age", if (ageCompatibility >= 20) "✓ Ideal" else "Okay")
                ScoreFactor("Health", if (healthBonus >= 20) "✓ Both" else "Check")
            }
        }
    }
}

@Composable
private fun ScoreFactor(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

private fun calculateAgeCompatibility(sireAge: Int?, damAge: Int?): Int {
    if (sireAge == null || damAge == null) return 15
    val ageDiff = kotlin.math.abs(sireAge - damAge)
    return when {
        ageDiff <= 8 -> 25  // Within 2 months difference
        ageDiff <= 16 -> 20 // Within 4 months
        ageDiff <= 26 -> 15 // Within 6 months
        else -> 10
    }
}
