package com.rio.rostry.ui.enthusiast.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdComparisonScreen(
    viewModel: BirdComparisonViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bird Comparison") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.birdA != null && uiState.birdB != null) {
                        IconButton(onClick = { viewModel.swapBirds() }) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Birds")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bird Selection Headers
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BirdSelectorCard(
                            modifier = Modifier.weight(1f),
                            label = "Bird A",
                            selectedBird = uiState.birdA?.product?.name,
                            availableBirds = uiState.availableBirds,
                            excludeProductId = uiState.birdB?.product?.productId,
                            onSelect = { viewModel.selectBirdA(it) }
                        )
                        BirdSelectorCard(
                            modifier = Modifier.weight(1f),
                            label = "Bird B",
                            selectedBird = uiState.birdB?.product?.name,
                            availableBirds = uiState.availableBirds,
                            excludeProductId = uiState.birdA?.product?.productId,
                            onSelect = { viewModel.selectBirdB(it) }
                        )
                    }
                }
                
                // Comparison results
                if (uiState.comparisonHighlights.isNotEmpty()) {
                    item {
                        Text(
                            text = "Comparison Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(uiState.comparisonHighlights) { item ->
                        ComparisonRow(item = item)
                    }
                    
                    // Summary card
                    item {
                        val winsA = uiState.comparisonHighlights.count { it.winner == BirdComparisonViewModel.ComparisonWinner.A }
                        val winsB = uiState.comparisonHighlights.count { it.winner == BirdComparisonViewModel.ComparisonWinner.B }
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = uiState.birdA?.product?.name ?: "Bird A",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = "$winsA",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (winsA > winsB) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text("advantages", style = MaterialTheme.typography.labelSmall)
                                }
                                
                                Text(
                                    text = "VS",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = uiState.birdB?.product?.name ?: "Bird B",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = "$winsB",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (winsB > winsA) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text("advantages", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                } else if (uiState.birdA == null || uiState.birdB == null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CompareArrows,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Select two birds to compare",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Compare physical traits, show performance, and breeding potential",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Could show snackbar here
            viewModel.clearError()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirdSelectorCard(
    modifier: Modifier = Modifier,
    label: String,
    selectedBird: String?,
    availableBirds: List<com.rio.rostry.data.database.entity.ProductEntity>,
    excludeProductId: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.clickable { expanded = true },
        colors = CardDefaults.cardColors(
            containerColor = if (selectedBird != null) 
                MaterialTheme.colorScheme.secondaryContainer 
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = selectedBird ?: "Select...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Select"
            )
        }
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        availableBirds
            .filter { it.productId != excludeProductId }
            .forEach { bird ->
                DropdownMenuItem(
                    text = { Text(bird.name) },
                    onClick = {
                        onSelect(bird.productId)
                        expanded = false
                    }
                )
            }
        
        if (availableBirds.isEmpty()) {
            DropdownMenuItem(
                text = { Text("No birds available") },
                enabled = false,
                onClick = {}
            )
        }
    }
}

@Composable
private fun ComparisonRow(item: BirdComparisonViewModel.ComparisonItem) {
    val winnerColor = MaterialTheme.colorScheme.primary
    val normalColor = MaterialTheme.colorScheme.onSurface
    
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bird A value
            Text(
                text = item.valueA,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.winner == BirdComparisonViewModel.ComparisonWinner.A) 
                    FontWeight.Bold else FontWeight.Normal,
                color = if (item.winner == BirdComparisonViewModel.ComparisonWinner.A) 
                    winnerColor else normalColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // Label in center
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.winner == BirdComparisonViewModel.ComparisonWinner.TIE) {
                    Text(
                        text = "TIE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            
            // Bird B value
            Text(
                text = item.valueB,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.winner == BirdComparisonViewModel.ComparisonWinner.B) 
                    FontWeight.Bold else FontWeight.Normal,
                color = if (item.winner == BirdComparisonViewModel.ComparisonWinner.B) 
                    winnerColor else normalColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}
