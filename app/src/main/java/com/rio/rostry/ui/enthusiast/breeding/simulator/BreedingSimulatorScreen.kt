package com.rio.rostry.ui.enthusiast.breeding.simulator

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.SimulatedOffspring
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingSimulatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: BreedingSimulatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showSirePicker by remember { mutableStateOf(false) }
    var showDamPicker by remember { mutableStateOf(false) }

    // Animation state for "Egg Hatching"
    val infiniteTransition = rememberInfiniteTransition(label = "hatch")
    val shake by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    if (showSirePicker) {
        BirdPickerSheet(
            title = "Select Sire",
            candidates = uiState.availableSires,
            onSelect = { 
                viewModel.selectSire(it)
                showSirePicker = false
            },
            onDismiss = { showSirePicker = false }
        )
    }

    if (showDamPicker) {
         BirdPickerSheet(
            title = "Select Dam",
            candidates = uiState.availableDams,
            onSelect = { 
                viewModel.selectDam(it)
                showDamPicker = false
            },
            onDismiss = { showDamPicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Simulator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 1. Parent Selection Area
            Text(
                "Select Pair",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sire Slot
                ParentSlot(
                    label = "Sire",
                    bird = uiState.sire,
                    color = Color(0xFF00BCD4),
                    onClick = { showSirePicker = true }
                )

                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )

                // Dam Slot
                ParentSlot(
                    label = "Dam",
                    bird = uiState.dam,
                    color = Color(0xFFE91E63),
                    onClick = { showDamPicker = true }
                )
            }

            Spacer(Modifier.height(32.dp))

            // 2. Control Area
            if (uiState.isAnimating) {
                // Hatching Animation
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .rotate(shake),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Egg, // Material egg icon or circle
                        contentDescription = "Hatching",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFFFFE082)
                    )
                }
                Text("Hatching...", style = MaterialTheme.typography.titleMedium)
            } else {
                Button(
                    onClick = { viewModel.breed() },
                    enabled = uiState.sire != null && uiState.dam != null,
                    modifier = Modifier
                        .height(56.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Science, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("SIMULATE CLUTCH")
                }
            }
            
            uiState.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(32.dp))

            // 3. Results Area
            if (uiState.offspring.isNotEmpty() && !uiState.isAnimating) {
                Text(
                    "Simulation Results",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.offspring) { chick ->
                        OffspringCard(chick)
                    }
                }
            }
            
            // 4. Save Plan Action
            if (uiState.offspring.isNotEmpty() && !uiState.isAnimating) {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.savePlan() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("SAVE AS PLANNED MATING")
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ParentSlot(
    label: String,
    bird: ProductEntity?,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .clickable(onClick = onClick),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = bird?.let { color.copy(alpha = 0.1f) } ?: MaterialTheme.colorScheme.surfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, color)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (bird != null) {
                    Text(
                        bird.name.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(bird?.name ?: "Select $label", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        bird?.breed?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
    }
}

@Composable
fun OffspringCard(chick: SimulatedOffspring) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chick Icon/Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (chick.gender == "Male") Color(0xFFE0F7FA) else Color(0xFFFCE4EC)
                    ),
                contentAlignment = Alignment.Center
            ) {
                 Icon(
                    Icons.Filled.Pets,
                    contentDescription = null,
                    tint = if (chick.gender == "Male") Color(0xFF00BCD4) else Color(0xFFE91E63),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            Text(chick.gender, fontWeight = FontWeight.Bold, color = if (chick.gender == "Male") Color(0xFF00BCD4) else Color(0xFFE91E63))
            Text(chick.color, style = MaterialTheme.typography.bodyMedium)
            Text(chick.quality, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            
            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))
            
            // Mini Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("BVI", String.format("%.0f", chick.bvi * 100))
                StatItem("Traits", "${chick.traits.size}")
            }
            
            Spacer(Modifier.weight(1f))
            
            // Top Trait
            val bestTrait = chick.traits.maxByOrNull { it.value }
            bestTrait?.let {
                AssistChip(
                    onClick = {},
                    label = { Text("${it.key}: ${String.format("%.1f", it.value)}") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFFFF8E1)
                    )
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdPickerSheet(
    title: String,
    candidates: List<ProductEntity>,
    onSelect: (ProductEntity) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            if (candidates.isEmpty()) {
                Text("No matching birds found in your flock.", style = MaterialTheme.typography.bodyMedium)
            }
            
            androidx.compose.foundation.lazy.LazyColumn {
                items(candidates) { bird ->
                    ListItem(
                        headlineContent = { Text(bird.name) },
                        supportingContent = { Text("${bird.breed} • ${bird.ageWeeks} weeks") },
                        leadingContent = {
                            Icon(Icons.Filled.Pets, null)
                        },
                        modifier = Modifier.clickable { onSelect(bird) }
                    )
                    Divider()
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
