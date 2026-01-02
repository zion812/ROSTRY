package com.rio.rostry.ui.farmer.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Groups
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmAssetListScreen(
    viewModel: FarmAssetListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAssetClick: (String) -> Unit,
    onAddAsset: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Assets") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Filter Chips could go here or in a row below
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAsset) {
                Icon(Icons.Default.Add, contentDescription = "Add Asset")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.filter == "ANIMAL",
                    onClick = { viewModel.updateFilter("ANIMAL") },
                    label = { Text("Birds") },
                    leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                FilterChip(
                    selected = state.filter == "BATCH",
                    onClick = { viewModel.updateFilter("BATCH") },
                    label = { Text("Batches") }
                )
            }

            
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isRefreshing)
            
            // Error Banner
            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.isLoading && !state.isRefreshing) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.filteredAssets.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(Modifier.height(16.dp))
                            Text("No assets found", style = MaterialTheme.typography.bodyLarge)
                            if (state.filter != null) {
                                TextButton(onClick = { viewModel.updateFilter(state.filter!!) }) {
                                    Text("Clear Filter")
                                }
                            } else {
                                Button(onClick = onAddAsset, modifier = Modifier.padding(top = 16.dp)) {
                                    Text("Add First Bird")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.filteredAssets) { asset ->
                            FarmAssetItem(asset = asset, onClick = { onAssetClick(asset.assetId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FarmAssetItem(
    asset: FarmAssetEntity,
    onClick: () -> Unit
) {
    val healthColor = when (asset.healthStatus.uppercase()) {
        "HEALTHY" -> Color(0xFF4CAF50)
        "SICK" -> MaterialTheme.colorScheme.error
        "RECOVERING" -> Color(0xFFFF9800)
        else -> Color(0xFF9E9E9E)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail Image
            if (asset.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = asset.imageUrls.first(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (asset.assetType == "BATCH") Icons.Default.Groups else Icons.Default.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Name and Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    // Status Badge (Active/Quarantine) if not active
                    if (asset.status != "ACTIVE") {
                         Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = asset.status,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    
                    // Asset Type Badge (always show but subtle or right aligned?)
                    // Let's keep existing Badge logic if status is active, or combine.
                    // Actually, the previous design had a Type Badge. Let's keep it but improve look.
                    if (asset.status == "ACTIVE") { // Only show type if status is normal to avoid crowding
                     Badge(
                        containerColor = if (asset.assetType == "BATCH") 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = asset.assetType,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                   }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Details Row: Breed • Age
                Text(
                    text = buildString {
                        if (!asset.breed.isNullOrBlank()) append(asset.breed).append(" • ")
                        append(formatAge(asset.ageWeeks))
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Footer Row: Quantity | Health | Sync
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quantity Pill
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${asset.quantity.toInt()} ${asset.unit}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Health Indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(healthColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = asset.healthStatus.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Sync Status
                    if (asset.dirty) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Pending Sync",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatAge(weeks: Int?): String {
    return when {
        weeks == null -> "Age unknown"
        weeks < 1 -> "< 1 week"
        weeks == 1 -> "1 week old"
        weeks < 52 -> "$weeks weeks old"
        else -> {
            val years = weeks / 52
            val remainingWeeks = weeks % 52
            if (remainingWeeks == 0) "$years year${if (years > 1) "s" else ""} old"
            else "$years year${if (years > 1) "s" else ""}, $remainingWeeks weeks old"
        }
    }
}
