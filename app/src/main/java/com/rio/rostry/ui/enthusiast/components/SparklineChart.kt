package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Coral
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.Emerald
import kotlin.math.roundToInt

/**
 * Inline sparkline chart for trend visualization.
 * Features:
 * - Smooth bezier curve drawing
 * - Animated line reveal
 * - Gradient fill under line
 * - Tap for tooltip with value
 */
@Composable
fun SparklineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color? = null, // Auto-determines based on trend if null
    showFill: Boolean = true,
    animationDurationMs: Int = 800
) {
    if (dataPoints.isEmpty()) return
    
    // Determine trend color
    val trend = if (dataPoints.size >= 2) {
        dataPoints.last() - dataPoints.first()
    } else 0f
    
    val chartColor = lineColor ?: when {
        trend > 0 -> Emerald
        trend < 0 -> Coral
        else -> ElectricBlue
    }
    
    // Animation progress
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(animationDurationMs),
        label = "sparklineProgress"
    )
    
    LaunchedEffect(dataPoints) {
        animationProgress = 1f
    }
    
    // Tooltip state
    var tooltipData by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .pointerInput(dataPoints) {
                    detectTapGestures { offset ->
                        val stepWidth = size.width.toFloat() / (dataPoints.size - 1)
                        val index = (offset.x / stepWidth).roundToInt().coerceIn(0, dataPoints.size - 1)
                        tooltipData = Pair(offset.x, dataPoints[index])
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val maxValue = dataPoints.maxOrNull() ?: 1f
            val minValue = dataPoints.minOrNull() ?: 0f
            val range = (maxValue - minValue).coerceAtLeast(1f)
            
            val stepWidth = width / (dataPoints.size - 1)
            
            // Calculate points
            val points = dataPoints.mapIndexed { index, value ->
                Offset(
                    x = index * stepWidth,
                    y = height - ((value - minValue) / range * height * 0.8f) - height * 0.1f
                )
            }
            
            if (points.size >= 2) {
                // Create smooth path
                val linePath = Path()
                val fillPath = Path()
                
                linePath.moveTo(points.first().x, points.first().y)
                fillPath.moveTo(points.first().x, height)
                fillPath.lineTo(points.first().x, points.first().y)
                
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    
                    // Control points for smooth curve
                    val controlX1 = prev.x + (curr.x - prev.x) / 2
                    val controlX2 = prev.x + (curr.x - prev.x) / 2
                    
                    // Animate drawing
                    val animatedX = prev.x + (curr.x - prev.x) * progress
                    val animatedY = prev.y + (curr.y - prev.y) * progress
                    
                    if (i <= (points.size - 1) * progress) {
                        linePath.cubicTo(controlX1, prev.y, controlX2, curr.y, curr.x, curr.y)
                        fillPath.cubicTo(controlX1, prev.y, controlX2, curr.y, curr.x, curr.y)
                    }
                }
                
                // Close fill path
                val lastDrawnIndex = ((points.size - 1) * progress).toInt().coerceIn(0, points.size - 1)
                fillPath.lineTo(points[lastDrawnIndex].x, height)
                fillPath.close()
                
                // Draw gradient fill
                if (showFill) {
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                chartColor.copy(alpha = 0.3f),
                                chartColor.copy(alpha = 0.05f)
                            )
                        )
                    )
                }
                
                // Draw line
                drawPath(
                    path = linePath,
                    color = chartColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                
                // Draw end dot
                val endPoint = points[lastDrawnIndex]
                drawCircle(
                    color = chartColor,
                    radius = 4.dp.toPx(),
                    center = endPoint
                )
            }
        }
        
        // Tooltip
        tooltipData?.let { (x, value) ->
            SparklineTooltip(
                value = value,
                modifier = Modifier.offset { IntOffset(x.roundToInt() - 30, -40) }
            )
        }
    }
}

@Composable
private fun SparklineTooltip(
    value: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = "%.1f".format(value),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Sparkline with label for dashboard metrics.
 */
@Composable
fun LabeledSparkline(
    label: String,
    value: String,
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    trendEmoji: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                trendEmoji?.let {
                    Spacer(Modifier.width(4.dp))
                    Text(text = it)
                }
            }
        }
        
        SparklineChart(
            dataPoints = dataPoints,
            modifier = Modifier
                .width(80.dp)
                .height(32.dp)
        )
    }
}

/**
 * Mini sparkline for compact KPI display.
 */
@Composable
fun MiniSparkline(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = ElectricBlue
) {
    SparklineChart(
        dataPoints = dataPoints,
        lineColor = color,
        showFill = false,
        animationDurationMs = 500,
        modifier = modifier
            .width(48.dp)
            .height(20.dp)
    )
}
