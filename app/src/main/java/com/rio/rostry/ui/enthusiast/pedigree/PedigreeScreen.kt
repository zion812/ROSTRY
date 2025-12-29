package com.rio.rostry.ui.enthusiast.pedigree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeBird
import com.rio.rostry.domain.pedigree.PedigreeCompleteness
import com.rio.rostry.domain.pedigree.PedigreeTree

/**
 * Pedigree Screen - Shows genealogical family tree for Enthusiast birds.
 * 
 * Features:
 * - Recursive 3-generation tree visualization (Sire/Dam lines)
 * - Pedigree completeness badge
 * - Parent linking functionality
 * - Gender-colored cards (Blue=Male, Pink=Female)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedigreeScreen(
    onNavigateBack: () -> Unit,
    onBirdClick: (String) -> Unit,
    viewModel: PedigreeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val parentSelectionState by viewModel.parentSelectionState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedigree Tree", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is PedigreeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Building family tree...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            is PedigreeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is PedigreeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header with stats
                    PedigreeHeader(
                        birdName = state.rootBird.name,
                        completeness = state.completeness,
                        ancestorCount = state.ancestorCount,
                        offspringCount = state.offspringCount
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Genealogical Tree (scrollable horizontally for large trees)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        PedigreeTreeView(
                            tree = state.pedigreeTree,
                            onBirdClick = onBirdClick,
                            onAddParent = { gender -> viewModel.openParentSelection(gender) }
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Legend
                    PedigreeLegend()
                }
            }
        }
        
        // Parent Selection Dialog
        when (val selection = parentSelectionState) {
            is ParentSelectionState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ParentSelectionState.Visible -> {
                ParentSelectionDialog(
                    title = "Select ${selection.targetLabel}",
                    candidates = selection.candidates,
                    onSelect = { viewModel.linkParent(it.productId) },
                    onDismiss = { viewModel.closeParentSelection() }
                )
            }
            is ParentSelectionState.Error -> {
                // Show error snackbar or dialog
            }
            is ParentSelectionState.Hidden -> { /* No dialog */ }
        }
    }
}

@Composable
private fun PedigreeHeader(
    birdName: String,
    completeness: PedigreeCompleteness,
    ancestorCount: Int,
    offspringCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌳 $birdName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Completeness Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(completeness.badgeColor).copy(alpha = 0.2f)
            ) {
                Text(
                    text = completeness.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color(completeness.badgeColor),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatItem(icon = Icons.Default.AccountTree, value = "$ancestorCount", label = "Ancestors")
                StatItem(icon = Icons.Default.ChildCare, value = "$offspringCount", label = "Offspring")
            }
        }
    }
}

@Composable
private fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * Recursive genealogical tree view.
 * Renders in a pyramid structure with the root bird at the left, parents to the right.
 */
@Composable
private fun PedigreeTreeView(
    tree: PedigreeTree,
    onBirdClick: (String) -> Unit,
    onAddParent: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // Current bird
        PedigreeBirdCard(
            bird = tree.bird,
            level = tree.generation,
            onClick = { onBirdClick(tree.bird.id) }
        )
        
        // Connector line and parents column
        if (tree.sire != null || tree.dam != null || tree.generation == 0) {
            Spacer(Modifier.width(8.dp))
            
            // Horizontal connector
            Canvas(modifier = Modifier.size(24.dp, 2.dp)) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2f
                )
            }
            
            Spacer(Modifier.width(8.dp))
            
            // Parents column (Sire on top, Dam on bottom)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sire (Father) branch
                if (tree.sire != null) {
                    Box {
                        PedigreeTreeView(
                            tree = tree.sire,
                            onBirdClick = onBirdClick,
                            onAddParent = onAddParent
                        )
                    }
                } else if (tree.generation < 2) {
                    // Placeholder for adding sire
                    AddParentCard(
                        label = "Add Sire",
                        gender = "male",
                        onClick = { onAddParent("male") }
                    )
                }
                
                // Dam (Mother) branch
                if (tree.dam != null) {
                    Box {
                        PedigreeTreeView(
                            tree = tree.dam,
                            onBirdClick = onBirdClick,
                            onAddParent = onAddParent
                        )
                    }
                } else if (tree.generation < 2) {
                    // Placeholder for adding dam
                    AddParentCard(
                        label = "Add Dam",
                        gender = "female",
                        onClick = { onAddParent("female") }
                    )
                }
            }
        }
    }
}

@Composable
private fun PedigreeBirdCard(
    bird: PedigreeBird,
    level: Int,
    onClick: () -> Unit
) {
    val cardWidth = when (level) {
        0 -> 130.dp
        1 -> 110.dp
        else -> 90.dp
    }
    
    val accentColor = if (bird.isGuestParent) {
        Color(0xFF9E9E9E) // Gray for guest parents
    } else {
        when (bird.name.firstOrNull()?.uppercaseChar()) {
            // Fallback to detect gender from common naming patterns
            in 'A'..'M' -> Color(0xFF2196F3) // Assume male (blue)
            else -> Color(0xFFE91E63) // Assume female (pink)
        }
    }

    Card(
        onClick = onClick,
        modifier = Modifier.width(cardWidth),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (level == 0) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                accentColor.copy(alpha = 0.1f)
        ),
        border = if (level == 0) null else CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(accentColor.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            if (bird.imageUrl != null) {
                AsyncImage(
                    model = bird.imageUrl,
                    contentDescription = bird.name,
                    modifier = Modifier
                        .size(if (level == 0) 48.dp else 36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(if (level == 0) 48.dp else 36.dp),
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (bird.isGuestParent) Icons.Default.PersonOutline else Icons.Default.Pets,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(if (level == 0) 24.dp else 18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Name
            Text(
                text = bird.name,
                style = if (level == 0) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Breed
            bird.breed?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Guest parent indicator
            if (bird.isGuestParent) {
                Text(
                    text = "Guest",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun AddParentCard(
    label: String,
    gender: String,
    onClick: () -> Unit
) {
    val color = if (gender == "male") Color(0xFF2196F3) else Color(0xFFE91E63)
    
    Surface(
        onClick = onClick,
        modifier = Modifier.width(90.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = color.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun PedigreeLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color(0xFF2196F3), label = "Sire (♂)")
            LegendItem(color = Color(0xFFE91E63), label = "Dam (♀)")
            LegendItem(color = Color(0xFF9E9E9E), label = "Guest")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.dp, color)
        ) {}
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParentSelectionDialog(
    title: String,
    candidates: List<ProductEntity>,
    onSelect: (ProductEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            if (candidates.isEmpty()) {
                Text(
                    "No eligible birds found. Add birds to your flock first.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    candidates.forEach { bird ->
                        Surface(
                            onClick = { onSelect(bird) },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar
                                if (bird.imageUrls.isNotEmpty()) {
                                    AsyncImage(
                                        model = bird.imageUrls.first(),
                                        contentDescription = bird.name,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Pets, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                
                                Spacer(Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bird.name, fontWeight = FontWeight.SemiBold)
                                    bird.breed?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
