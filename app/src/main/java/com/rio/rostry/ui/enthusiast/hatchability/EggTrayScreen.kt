package com.rio.rostry.ui.enthusiast.hatchability

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Egg Tray Visual Grid - Shows eggs in a tray layout with fertility/hatch status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggTrayScreen(
    collectionId: String,
    onNavigateBack: () -> Unit
) {
    // Mock eggs data with status
    val eggs = remember {
        List(30) { index ->
            EggStatus(
                position = index + 1,
                status = when {
                    index < 20 -> EggState.HATCHED
                    index < 25 -> EggState.FERTILE
                    index < 28 -> EggState.INFERTILE
                    else -> EggState.PENDING
                }
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Egg Tray") },
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
                .padding(16.dp)
        ) {
            // Summary stats
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(count = 20, label = "Hatched", color = Color(0xFF4CAF50))
                    StatItem(count = 5, label = "Fertile", color = Color(0xFF2196F3))
                    StatItem(count = 3, label = "Infertile", color = Color(0xFFF44336))
                    StatItem(count = 2, label = "Pending", color = Color(0xFF9E9E9E))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "Collection: ${collectionId.take(8)}...",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Egg Grid (6 columns like a real egg tray)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(eggs) { egg ->
                    EggCell(egg)
                }
            }
            
            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF4CAF50), label = "Hatched")
                LegendItem(color = Color(0xFF2196F3), label = "Fertile")
                LegendItem(color = Color(0xFFF44336), label = "Infertile")
                LegendItem(color = Color(0xFF9E9E9E), label = "Pending")
            }
        }
    }
}

@Composable
private fun EggCell(egg: EggStatus) {
    val (bgColor, icon) = when (egg.status) {
        EggState.HATCHED -> Color(0xFF4CAF50) to Icons.Filled.Check
        EggState.FERTILE -> Color(0xFF2196F3) to null
        EggState.INFERTILE -> Color(0xFFF44336) to Icons.Filled.Close
        EggState.PENDING -> Color(0xFF9E9E9E) to Icons.Filled.QuestionMark
    }
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(bgColor.copy(alpha = 0.2f), CircleShape)
            .border(2.dp, bgColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = bgColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                "${egg.position}",
                style = MaterialTheme.typography.bodySmall,
                color = bgColor
            )
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$count",
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private data class EggStatus(
    val position: Int,
    val status: EggState
)

private enum class EggState {
    HATCHED,
    FERTILE,
    INFERTILE,
    PENDING
}
