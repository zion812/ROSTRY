package com.rio.rostry.ui.enthusiast.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.domain.service.GeneticPotentialResult
import com.rio.rostry.domain.service.NotableAncestor
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodlineAnalyticsScreen(
    result: GeneticPotentialResult,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloodline Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        // Using Text for now as Icons might not be imported
                        Text("<")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Lineage Strength",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { result.lineageStrength / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                )
                Text(
                    text = "${result.lineageStrength}/100 Score",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Text(
                    text = "Genetic Potential",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                RadarChart(
                    traits = result.traitPotential,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }

            item {
                Text(
                    text = "Notable Ancestors",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(result.notableAncestors) { ancestor ->
                NotableAncestorCard(ancestor)
            }
        }
    }
}

@Composable
fun RadarChart(
    traits: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f
        val angleStep = (2 * Math.PI / traits.size).toFloat()

        // Draw web
        for (i in 1..5) {
            val r = radius * (i / 5f)
            drawCircle(
                color = Color.LightGray,
                radius = r,
                center = center,
                style = Stroke(width = 1f)
            )
        }

        // Draw axes
        traits.keys.forEachIndexed { index, label ->
            val angle = -Math.PI.toFloat() / 2 + index * angleStep
            val end = Offset(
                center.x + radius * cos(angle),
                center.y + radius * sin(angle)
            )
            drawLine(
                color = Color.LightGray,
                start = center,
                end = end,
                strokeWidth = 1f
            )
        }

        // Draw data
        val path = Path()
        traits.values.forEachIndexed { index, value ->
            val angle = -Math.PI.toFloat() / 2 + index * angleStep
            val point = Offset(
                center.x + radius * value * cos(angle),
                center.y + radius * value * sin(angle)
            )
            if (index == 0) path.moveTo(point.x, point.y)
            else path.lineTo(point.x, point.y)
        }
        path.close()

        drawPath(
            path = path,
            color = Color.Blue.copy(alpha = 0.5f),
            // style = Stroke(width = 3f)
        )
        drawPath(
            path = path,
            color = Color.Blue.copy(alpha = 0.2f),
            // style = Fill
        )
    }
}

@Composable
fun NotableAncestorCard(ancestor: NotableAncestor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ancestor.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ancestor.relation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            ancestor.achievements.forEach { achievement ->
                Text(
                    text = "• $achievement",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
