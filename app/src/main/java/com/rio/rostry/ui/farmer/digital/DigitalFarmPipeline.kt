package com.rio.rostry.ui.farmer.digital

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.LifecycleStage

@Composable
fun DigitalFarmPipeline(
    viewModel: PipelineViewModel = hiltViewModel(),
    onStageClick: (LifecycleStage) -> Unit,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            onNavigate(route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Farm Pipeline",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ) {
                Text(
                    text = "${uiState.totalBirds} Heads",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // HATCHING (Virtual Stage)
            item {
                PipelineStageCard(
                    title = "Hatching",
                    subtitle = "Incubation",
                    count = uiState.hatchingCount,
                    icon = Icons.Default.Egg,
                    colorStart = Color(0xFFFEF3C7),
                    colorEnd = Color(0xFFFDE68A),
                    textColor = Color(0xFF92400E),
                    onClick = { viewModel.navigateToModule(com.rio.rostry.ui.navigation.Routes.Builders.monitoringHatching()) }
                )
            }

            // CHICK (Nursery)
            item {
                PipelineStageCard(
                    title = "Nursery",
                    subtitle = "0-8 Weeks",
                    count = uiState.stageCounts[LifecycleStage.CHICK] ?: 0,
                    alertCount = uiState.readyToGrowCount,
                    icon = Icons.Default.ChildCare, // Or similar
                    colorStart = Color(0xFFDBEAFE),
                    colorEnd = Color(0xFFBFDBFE),
                    textColor = Color(0xFF1E40AF),
                    onClick = { onStageClick(LifecycleStage.CHICK) }
                )
            }

            // GROWER
            item {
                PipelineStageCard(
                    title = "Growers",
                    subtitle = "8-18 Weeks",
                    count = uiState.stageCounts[LifecycleStage.GROWER] ?: 0,
                    alertCount = uiState.readyToLayCount,
                    icon = Icons.Default.TrendingUp,
                    colorStart = Color(0xFFDCFCE7),
                    colorEnd = Color(0xFF86EFAC),
                    textColor = Color(0xFF166534),
                    onClick = { onStageClick(LifecycleStage.GROWER) }
                )
            }

            // LAYER / BREEDER
            item {
                val layerCount = uiState.stageCounts[LifecycleStage.LAYER] ?: 0
                val breederCount = uiState.stageCounts[LifecycleStage.BREEDER] ?: 0
                PipelineStageCard(
                    title = "Production",
                    subtitle = "Layers & Breeders",
                    count = layerCount + breederCount,
                    icon = Icons.Default.EggAlt,
                    colorStart = Color(0xFFFCE7F3),
                    colorEnd = Color(0xFFFBCFE8),
                    textColor = Color(0xFF9D174D),
                    onClick = { onStageClick(LifecycleStage.LAYER) } // Simplified nav
                )
            }
        }
    }
}

@Composable
fun PipelineStageCard(
    title: String,
    subtitle: String? = null,
    count: Int,
    alertCount: Int = 0,
    icon: ImageVector,
    colorStart: Color,
    colorEnd: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorStart, colorEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Icon and Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (alertCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626), // Red alert
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (alertCount > 9) "9+" else alertCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom: Stats
                Column {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
