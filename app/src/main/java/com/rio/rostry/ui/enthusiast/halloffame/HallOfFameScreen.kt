package com.rio.rostry.ui.enthusiast.halloffame

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Hall of Fame Screen - Showcases top-performing birds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HallOfFameScreen(
    onNavigateBack: () -> Unit,
    onBirdClick: (String) -> Unit,
    viewModel: HallOfFameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆", fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Hall of Fame", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFD700).copy(alpha = 0.1f)
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is HallOfFameUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFFFD700))
                        Spacer(Modifier.height(16.dp))
                        Text("Loading legends...")
                    }
                }
            }
            is HallOfFameUiState.Error -> {
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
            is HallOfFameUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top Sales Section
                    item {
                        SectionHeader(
                            icon = "💰",
                            title = "Highest Sales",
                            subtitle = "Top birds by sale price"
                        )
                    }

                    if (state.topByPrice.isEmpty()) {
                        item {
                            EmptySection("No sales recorded yet")
                        }
                    } else {
                        items(state.topByPrice) { bird ->
                            HallOfFameCard(
                                bird = bird,
                                rank = state.topByPrice.indexOf(bird) + 1,
                                onClick = { onBirdClick(bird.product.productId) }
                            )
                        }
                    }

                    // Top Transfers Section
                    item {
                        Spacer(Modifier.height(16.dp))
                        SectionHeader(
                            icon = "🔄",
                            title = "Most Transferred",
                            subtitle = "Birds with rich ownership history"
                        )
                    }

                    if (state.topByTransfers.isEmpty()) {
                        item {
                            EmptySection("No transfers recorded yet")
                        }
                    } else {
                        items(state.topByTransfers) { bird ->
                            HallOfFameCard(
                                bird = bird,
                                rank = state.topByTransfers.indexOf(bird) + 1,
                                onClick = { onBirdClick(bird.product.productId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 28.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HallOfFameCard(
    bird: HallOfFameBird,
    rank: Int,
    onClick: () -> Unit
) {
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = medalColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(medalColor, medalColor.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else Color.Black
                )
            }

            Spacer(Modifier.width(16.dp))

            // Bird Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    bird.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                bird.product.breed?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (bird.achievementType) {
                        AchievementType.HIGHEST_SALE -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        AchievementType.MOST_TRANSFERS -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        AchievementType.TOP_RATED -> Color(0xFFFFD700).copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = bird.achievement,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when (bird.achievementType) {
                            AchievementType.HIGHEST_SALE -> Color(0xFF4CAF50)
                            AchievementType.MOST_TRANSFERS -> Color(0xFF2196F3)
                            AchievementType.TOP_RATED -> Color(0xFFFFD700)
                        }
                    )
                }
            }

            // Trophy for top 3
            if (rank <= 3) {
                Text(
                    text = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> ""
                    },
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
private fun EmptySection(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
