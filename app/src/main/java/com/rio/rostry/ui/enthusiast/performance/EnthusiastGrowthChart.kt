package com.rio.rostry.ui.enthusiast.performance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * EnthusiastGrowthChart - Displays weight progression with breed standard comparison.
 * 
 * Features:
 * - Line chart showing actual weight over time
 * - Overlay with breed standard growth curve
 * - Analysis of growth rate (above/below expected)
 * - Weekly milestone markers
 */
@Composable
fun EnthusiastGrowthChart(
    weightRecords: List<WeightRecord>,
    breedStandard: BreedGrowthStandard?,
    modifier: Modifier = Modifier
) {
    val chartColors = ChartColors(
        actual = Color(0xFF4CAF50),
        standard = Color(0xFFFFD700),
        grid = Color.Gray.copy(alpha = 0.3f),
        background = MaterialTheme.colorScheme.surfaceVariant
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = chartColors.background
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = chartColors.actual
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Growth Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Current vs expected indicator
                if (weightRecords.isNotEmpty() && breedStandard != null) {
                    GrowthStatusBadge(weightRecords.last(), breedStandard)
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (weightRecords.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No weight records yet",
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    GrowthLineChart(
                        weightRecords = weightRecords,
                        breedStandard = breedStandard,
                        colors = chartColors
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LegendItem(color = chartColors.actual, label = "Actual Weight")
                Spacer(Modifier.width(24.dp))
                if (breedStandard != null) {
                    LegendItem(color = chartColors.standard, label = "Breed Standard", dashed = true)
                }
            }
            
            // Stats summary
            if (weightRecords.size >= 2) {
                Spacer(Modifier.height(16.dp))
                GrowthStatsSummary(weightRecords, breedStandard)
            }
        }
    }
}

@Composable
private fun GrowthLineChart(
    weightRecords: List<WeightRecord>,
    breedStandard: BreedGrowthStandard?,
    colors: ChartColors
) {
    val maxWeight = maxOf(
        weightRecords.maxOfOrNull { it.weightGrams } ?: 0,
        breedStandard?.targetWeights?.maxOrNull() ?: 0
    ).coerceAtLeast(1000)
    
    val maxWeek = maxOf(
        weightRecords.maxOfOrNull { it.weekNumber } ?: 1,
        breedStandard?.targetWeights?.size ?: 1
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val chartWidth = size.width - 60f
        val chartHeight = size.height - 40f
        val offsetX = 50f
        val offsetY = 20f
        
        // Draw grid lines
        val gridCount = 5
        for (i in 0..gridCount) {
            val y = offsetY + (chartHeight / gridCount) * i
            drawLine(
                color = colors.grid,
                start = Offset(offsetX, y),
                end = Offset(offsetX + chartWidth, y),
                strokeWidth = 1f
            )
        }
        
        // Draw breed standard curve (dashed)
        breedStandard?.targetWeights?.let { targets ->
            if (targets.isNotEmpty()) {
                val path = Path()
                targets.forEachIndexed { index, weight ->
                    val x = offsetX + (chartWidth / maxWeek) * index
                    val y = offsetY + chartHeight - (chartHeight * weight / maxWeight)
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = colors.standard,
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                )
            }
        }
        
        // Draw actual weight line
        if (weightRecords.size >= 2) {
            val path = Path()
            weightRecords.sortedBy { it.weekNumber }.forEachIndexed { index, record ->
                val x = offsetX + (chartWidth / maxWeek) * record.weekNumber
                val y = offsetY + chartHeight - (chartHeight * record.weightGrams / maxWeight)
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            
            drawPath(
                path = path,
                color = colors.actual,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }
        
        // Draw data points
        weightRecords.forEach { record ->
            val x = offsetX + (chartWidth / maxWeek) * record.weekNumber
            val y = offsetY + chartHeight - (chartHeight * record.weightGrams / maxWeight)
            
            drawCircle(
                color = colors.actual,
                radius = 8f,
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun GrowthStatusBadge(
    latestRecord: WeightRecord,
    breedStandard: BreedGrowthStandard
) {
    val weekIndex = latestRecord.weekNumber.coerceIn(0, breedStandard.targetWeights.size - 1)
    val expectedWeight = breedStandard.targetWeights.getOrNull(weekIndex) ?: 0
    val actualWeight = latestRecord.weightGrams
    
    val percentDiff = if (expectedWeight > 0) {
        ((actualWeight - expectedWeight).toFloat() / expectedWeight * 100).toInt()
    } else 0
    
    val (icon, color, text) = when {
        percentDiff >= 5 -> Triple(Icons.Default.TrendingUp, Color(0xFF4CAF50), "+$percentDiff%")
        percentDiff <= -5 -> Triple(Icons.Default.TrendingDown, Color(0xFFFF5722), "$percentDiff%")
        else -> Triple(Icons.Default.TrendingFlat, Color(0xFFFFD700), "On Track")
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, dashed: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (dashed) {
            Row {
                repeat(3) {
                    Surface(
                        modifier = Modifier.size(6.dp, 3.dp),
                        color = color,
                        shape = RoundedCornerShape(1.dp)
                    ) {}
                    Spacer(Modifier.width(2.dp))
                }
            }
        } else {
            Surface(
                modifier = Modifier.size(20.dp, 3.dp),
                color = color,
                shape = RoundedCornerShape(2.dp)
            ) {}
        }
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun GrowthStatsSummary(
    weightRecords: List<WeightRecord>,
    breedStandard: BreedGrowthStandard?
) {
    val sorted = weightRecords.sortedBy { it.weekNumber }
    val latest = sorted.last()
    val earliest = sorted.first()
    
    val totalGain = latest.weightGrams - earliest.weightGrams
    val weeksDiff = (latest.weekNumber - earliest.weekNumber).coerceAtLeast(1)
    val avgGainPerWeek = totalGain / weeksDiff
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatBox(
            label = "Current",
            value = "${latest.weightGrams}g",
            color = MaterialTheme.colorScheme.primary
        )
        StatBox(
            label = "Gained",
            value = "+${totalGain}g",
            color = Color(0xFF4CAF50)
        )
        StatBox(
            label = "Avg/Week",
            value = "${avgGainPerWeek}g",
            color = Color(0xFFFF9800)
        )
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

/**
 * Data class for weight records.
 */
data class WeightRecord(
    val weekNumber: Int,
    val weightGrams: Int,
    val recordedAt: Long
)

/**
 * Breed-specific growth standards for comparison.
 */
data class BreedGrowthStandard(
    val breedName: String,
    val targetWeights: List<Int> // Weight in grams for weeks 0, 1, 2, ... (index = week)
) {
    companion object {
        // Common breed standards (weights in grams)
        val ASEEL = BreedGrowthStandard(
            breedName = "Aseel",
            targetWeights = listOf(50, 80, 130, 200, 280, 380, 500, 650, 800, 1000, 1200, 1400, 1600)
        )
        
        val KADAKNATH = BreedGrowthStandard(
            breedName = "Kadaknath",
            targetWeights = listOf(40, 70, 110, 160, 220, 290, 380, 480, 600, 750, 900, 1050, 1200)
        )
        
        val DESI_CHICKEN = BreedGrowthStandard(
            breedName = "Desi Chicken",
            targetWeights = listOf(45, 75, 120, 180, 250, 340, 450, 580, 720, 880, 1050, 1230, 1400)
        )
        
        val BROILER = BreedGrowthStandard(
            breedName = "Broiler",
            targetWeights = listOf(50, 150, 350, 600, 900, 1300, 1800, 2300, 2800, 3200, 3500, 3800, 4000)
        )
        
        fun forBreed(breedName: String?): BreedGrowthStandard? {
            return when {
                breedName == null -> null
                breedName.contains("aseel", ignoreCase = true) -> ASEEL
                breedName.contains("kadaknath", ignoreCase = true) -> KADAKNATH
                breedName.contains("broiler", ignoreCase = true) -> BROILER
                breedName.contains("desi", ignoreCase = true) -> DESI_CHICKEN
                else -> DESI_CHICKEN // Default to Desi as baseline
            }
        }
    }
}

private data class ChartColors(
    val actual: Color,
    val standard: Color,
    val grid: Color,
    val background: Color
)
