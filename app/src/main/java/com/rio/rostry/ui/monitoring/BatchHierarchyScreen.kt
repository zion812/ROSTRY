package com.rio.rostry.ui.monitoring

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Batch Hierarchy Screen - Tree view of all batches and their birds
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchHierarchyScreen(
    onNavigateBack: () -> Unit,
    onBatchClick: (String) -> Unit,
    onBirdClick: (String) -> Unit,
    viewModel: BatchHierarchyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Batch Hierarchy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "Error")
                    }
                }
            }
            state.batches.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Egg,
                            null,
                            Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No batches yet", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Start a hatching batch to see it here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.batches) { node ->
                        BatchTreeNode(
                            node = node,
                            expandedIds = state.expandedBatchIds,
                            dateFormatter = dateFormatter,
                            onToggleExpand = { viewModel.toggleExpanded(it) },
                            onBatchClick = onBatchClick,
                            onBirdClick = onBirdClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchTreeNode(
    node: BatchNode,
    expandedIds: Set<String>,
    dateFormatter: SimpleDateFormat,
    onToggleExpand: (String) -> Unit,
    onBatchClick: (String) -> Unit,
    onBirdClick: (String) -> Unit
) {
    val isExpanded = node.batch.batchId in expandedIds
    val hasChildren = node.childBirds.isNotEmpty()

    Column(
        modifier = Modifier.padding(start = (node.level * 16).dp)
    ) {
        // Batch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onBatchClick(node.batch.batchId) }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand/Collapse Button
                if (hasChildren) {
                    IconButton(
                        onClick = { onToggleExpand(node.batch.batchId) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                } else {
                    Spacer(Modifier.width(32.dp))
                }

                // Batch Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getBatchStatusColor(node.batch.status).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Egg,
                        null,
                        tint = getBatchStatusColor(node.batch.status),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Batch ${node.batch.batchId.take(6)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${node.batch.eggsCount} eggs • ${node.batch.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stats
                if (node.childBirds.isNotEmpty()) {
                    Text(
                        "${node.childBirds.size} birds",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Children (if expanded)
        AnimatedVisibility(
            visible = isExpanded && hasChildren,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Child birds
                node.childBirds.forEach { bird ->
                    BirdLeafNode(
                        bird = bird,
                        level = node.level + 1,
                        onClick = { onBirdClick(bird.assetId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BirdLeafNode(
    bird: com.rio.rostry.data.database.entity.FarmAssetEntity,
    level: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = ((level + 1) * 16).dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Pets,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    bird.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    bird.breed ?: "Unknown breed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun getBatchStatusColor(status: String): Color = when (status.uppercase()) {
    "ACTIVE", "INCUBATING" -> Color(0xFF16A34A)
    "COMPLETED" -> Color(0xFF2563EB)
    "CANCELLED" -> Color(0xFFDC2626)
    else -> Color(0xFF6B7280)
}
