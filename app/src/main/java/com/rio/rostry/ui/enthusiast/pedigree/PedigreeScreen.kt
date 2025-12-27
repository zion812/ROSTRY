package com.rio.rostry.ui.enthusiast.pedigree

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity

/**
 * Pedigree Screen - Shows family tree for a bird.
 * Displays lineage with connecting lines using Canvas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedigreeScreen(
    onNavigateBack: () -> Unit,
    onBirdClick: (String) -> Unit,
    viewModel: PedigreeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedigree", fontWeight = FontWeight.Bold) },
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
                        Text("Building family tree...")
                    }
                }
            }
            is PedigreeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(state.message)
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🌳 Family Tree",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(24.dp))

                    // Render tree recursively
                    PedigreeTreeView(
                        node = state.pedigreeTree,
                        level = 0,
                        onBirdClick = onBirdClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PedigreeTreeView(
    node: PedigreeNode,
    level: Int,
    onBirdClick: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current bird card
        BirdPedigreeCard(
            bird = node.bird,
            level = level,
            onClick = { onBirdClick(node.bird.productId) }
        )

        // Connecting lines and parents
        if (node.parents.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))

            // Connecting line
            Canvas(modifier = Modifier.size(2.dp, 20.dp)) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 2f
                )
            }

            Spacer(Modifier.height(8.dp))

            // Parents row
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                node.parents.forEach { parent ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Connecting line from horizontal bar
                        Canvas(modifier = Modifier.size(2.dp, 16.dp)) {
                            drawLine(
                                color = Color.Gray,
                                start = Offset(size.width / 2, 0f),
                                end = Offset(size.width / 2, size.height),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        PedigreeTreeView(
                            node = parent,
                            level = level + 1,
                            onBirdClick = onBirdClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirdPedigreeCard(
    bird: ProductEntity,
    level: Int,
    onClick: () -> Unit
) {
    val cardSize = when (level) {
        0 -> 120.dp
        1 -> 100.dp
        else -> 80.dp
    }

    val accentColor = when (bird.gender?.lowercase()) {
        "male" -> Color(0xFF2196F3)
        "female" -> Color(0xFFE91E63)
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
        modifier = Modifier.width(cardSize),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(accentColor.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(if (level == 0) 48.dp else 36.dp),
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (bird.gender?.lowercase() == "male") "♂" else "♀",
                        color = accentColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Name
            Text(
                text = bird.name.take(12),
                style = if (level == 0) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            // Breed
            bird.breed?.let {
                Text(
                    text = it.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
