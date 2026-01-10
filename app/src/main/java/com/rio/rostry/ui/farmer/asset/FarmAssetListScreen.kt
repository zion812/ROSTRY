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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Check
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
    
    // Local UI state for search, sort, and view mode
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf(SortOption.NAME) }
    var isGridView by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    // Filtered and sorted assets
    val displayAssets = remember(state.filteredAssets, searchQuery, sortBy) {
        state.filteredAssets
            .filter { asset ->
                searchQuery.isBlank() || 
                asset.name.contains(searchQuery, ignoreCase = true) ||
                (asset.breed?.contains(searchQuery, ignoreCase = true) == true)
            }
            .sortedWith(
                when (sortBy) {
                    SortOption.NAME -> compareBy { it.name.lowercase() }
                    SortOption.AGE -> compareByDescending { it.ageWeeks ?: 0 }
                    SortOption.WEIGHT -> compareByDescending { it.weightGrams ?: 0 }
                    SortOption.RECENT -> compareByDescending { it.updatedAt }
                    SortOption.HEALTH -> compareBy { 
                        when (it.healthStatus.uppercase()) {
                            "SICK" -> 0; "INJURED" -> 1; "RECOVERING" -> 2; "HEALTHY" -> 3; else -> 4
                        }
                    }
                }
            )
    }

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
                    // Grid/List Toggle
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            if (isGridView) Icons.Default.ViewList else androidx.compose.material.icons.Icons.Default.GridView,
                            contentDescription = if (isGridView) "List View" else "Grid View"
                        )
                    }
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search birds by name or breed...") },
                leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(androidx.compose.material.icons.Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            
            // Quick Filter Chips Row with counts
            val filterCounts by viewModel.filterCounts.collectAsState()
            val currentQuickFilter by viewModel.currentQuickFilter.collectAsState()
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickFilter.entries.take(4).forEach { filter ->
                    val count = filterCounts[filter] ?: 0
                    val isSelected = currentQuickFilter == filter
                    
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.applyQuickFilter(filter) },
                        label = { 
                            Text(
                                text = if (filter == QuickFilter.ALL) "All" else "${filter.icon} ${filter.displayName}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        trailingIcon = if (count > 0 && filter != QuickFilter.ALL) {
                            {
                                Surface(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = count.toString(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else null
                    )
                }
            }
            
            // Filter & Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
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
                
                Spacer(Modifier.weight(1f))
                
                // Sort Dropdown
                Box {
                    FilterChip(
                        selected = false,
                        onClick = { showSortMenu = true },
                        label = { Text(sortBy.label) },
                        leadingIcon = { Icon(Icons.Default.FilterList, null, Modifier.size(16.dp)) }
                    )
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = { 
                                    sortBy = option
                                    showSortMenu = false
                                },
                                leadingIcon = {
                                    if (sortBy == option) {
                                        Icon(androidx.compose.material.icons.Icons.Default.Check, null, Modifier.size(18.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Results count
            Text(
                "${displayAssets.size} asset${if (displayAssets.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
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
                        items(displayAssets) { asset ->
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
        "INJURED" -> Color(0xFFE91E63)
        else -> Color(0xFF9E9E9E)
    }
    
    val genderIcon = when (asset.gender?.uppercase()) {
        "MALE" -> "♂"
        "FEMALE" -> "♀"
        else -> null
    }
    
    val genderColor = when (asset.gender?.uppercase()) {
        "MALE" -> Color(0xFF2196F3)
        "FEMALE" -> Color(0xFFE91E63)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Check for pending actions
    val hasVaccinationDue = asset.nextVaccinationDate?.let { it < System.currentTimeMillis() } == true
    val isListed = asset.listingId != null
    val daysSinceUpdate = ((System.currentTimeMillis() - asset.updatedAt) / (24 * 60 * 60 * 1000)).toInt()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Enhanced Image with Gradient Overlay
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    if (asset.imageUrls.isNotEmpty()) {
                        AsyncImage(
                            model = asset.imageUrls.first(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Gradient overlay for premium look
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f)
                                        ),
                                        startY = 0f,
                                        endY = Float.POSITIVE_INFINITY
                                    )
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (asset.assetType == "BATCH") Icons.Default.Groups else Icons.Default.Pets,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    
                    // Gender badge on image
                    if (genderIcon != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.9f),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = genderIcon,
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = genderColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    // Name and Type Badge Row
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
                        
                        // Type Badge with premium styling
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (asset.assetType == "BATCH") 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = asset.assetType,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = if (asset.assetType == "BATCH")
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Details Row: Breed • Gender • Age
                    Text(
                        text = buildString {
                            if (!asset.breed.isNullOrBlank()) append(asset.breed)
                            if (!asset.gender.isNullOrBlank() && asset.gender != "Unknown") {
                                if (isNotEmpty()) append(" • ")
                                append(asset.gender)
                            }
                            if (isNotEmpty()) append(" • ")
                            append(formatAge(asset.ageWeeks))
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Metrics Row: Quantity | Health | Weight
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quantity Pill
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "${asset.quantity.toInt()} ${asset.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
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
                                color = healthColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Weight if available
                        if (asset.weightGrams != null && asset.weightGrams > 0) {
                            Text(
                                text = "%.1fkg".format(asset.weightGrams / 1000),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Action Badges Row (Vaccination Due, Listed, Recent Activity)
            if (hasVaccinationDue || isListed || asset.dirty || daysSinceUpdate <= 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vaccination Alert
                    if (hasVaccinationDue) {
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Vaccination Due",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    
                    // Listed for Sale Badge
                    if (isListed) {
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "📢 Listed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    
                    // Recent Activity
                    if (daysSinceUpdate <= 1) {
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (daysSinceUpdate == 0) "Updated today" else "Updated yesterday",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                    
                    Spacer(Modifier.weight(1f))
                    
                    // Sync Status
                    if (asset.dirty) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Pending Sync",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
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

/**
 * Sort options for farm asset list
 */
private enum class SortOption(val label: String) {
    NAME("Name"),
    AGE("Age"),
    WEIGHT("Weight"),
    RECENT("Recent"),
    HEALTH("Health")
}
