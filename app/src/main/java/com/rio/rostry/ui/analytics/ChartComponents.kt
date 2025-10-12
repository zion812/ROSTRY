package com.rio.rostry.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Lightweight, dependency-free interactive charts using Compose Canvas.
// Each component accepts a callback for drill-down on user tap.

data class BarDatum(
    val label: String,
    val value: Float,
    val metaId: String? = null,
)

@Composable
fun BarChartView(
    data: List<BarDatum>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    onBarClick: (BarDatum) -> Unit = {}
) {
    if (data.isEmpty()) {
        Text("No data")
        return
    }
    // If all values are zero or negative, don't render zero-height bars; show placeholder
    val maxRaw = (data.maxOfOrNull { it.value } ?: 0f)
    if (maxRaw <= 0f) {
        Text("No data")
        return
    }
    val max = maxRaw.coerceAtLeast(1f)
    // Compute bar hit areas
    val bars = remember(data) { data }
    Box(modifier = modifier.background(Color.Transparent)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(bars) {
                    detectTapGestures { offset ->
                        // Simple hit detection by x-region
                        val width = size.width
                        val barWidth = width / bars.size
                        val index = (offset.x / barWidth).toInt().coerceIn(0, bars.lastIndex)
                        onBarClick(bars[index])
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val barWidth = width / bars.size
            bars.forEachIndexed { i, d ->
                val h = (d.value / max) * (height * 0.8f)
                val left = i * barWidth + barWidth * 0.15f
                val right = (i + 1) * barWidth - barWidth * 0.15f
                drawRect(
                    color = barColor,
                    topLeft = Offset(left, height - h),
                    size = androidx.compose.ui.geometry.Size(right - left, h)
                )
            }
        }
    }
}

data class LinePoint(
    val xLabel: String,
    val y: Float,
    val metaId: String? = null,
)

@Composable
fun LineChartView(
    points: List<LinePoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.secondary,
    onPointClick: (LinePoint) -> Unit = {}
) {
    if (points.isEmpty()) {
        Text("No data")
        return
    }
    val max = (points.maxOfOrNull { it.y } ?: 0f).coerceAtLeast(1f)
    val pts = remember(points) { points }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .pointerInput(pts) {
                detectTapGestures { offset ->
                    val width = size.width
                    val step = width / pts.size
                    val idx = (offset.x / step).toInt().coerceIn(0, pts.lastIndex)
                    onPointClick(pts[idx])
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val step = width / pts.size
        var prev: Offset? = null
        pts.forEachIndexed { i, p ->
            val x = i * step + step / 2f
            val y = height - (p.y / max) * (height * 0.8f)
            val pt = Offset(x, y)
            prev?.let { pr ->
                drawLine(color = lineColor, start = pr, end = pt, strokeWidth = 6f)
            }
            prev = pt
            drawIntoCanvas {
                // point
                drawCircle(color = lineColor, radius = 6f, center = pt)
            }
        }
    }
}

data class PieSlice(
    val label: String,
    val value: Float,
    val color: Color,
    val metaId: String? = null,
)

@Composable
fun PieChartView(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    onSliceClick: (PieSlice) -> Unit = {}
) {
    if (slices.isEmpty()) {
        Text("No data")
        return
    }
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    val s = remember(slices) { slices }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .pointerInput(s) {
                detectTapGestures { offset ->
                    // Very rough hit detection by angle sector
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val v = offset - center
                    val angle = (Math.toDegrees(kotlin.math.atan2(v.y.toDouble(), v.x.toDouble())) + 360.0) % 360.0
                    var start = -90f
                    var found: PieSlice? = null
                    s.forEach { slice ->
                        val sweep = (slice.value / total) * 360f
                        val end = start + sweep
                        if (angle >= start && angle < end) {
                            found = slice
                            return@forEach
                        }
                        start = end
                    }
                    found?.let(onSliceClick)
                }
            }
    ) {
        val radius = size.minDimension / 2f * 0.8f
        val topLeft = Offset(x = size.width / 2f - radius, y = size.height / 2f - radius)
        val sizeSq = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        var start = -90f
        s.forEach { slice ->
            val sweep = (slice.value / total) * 360f
            drawArc(color = slice.color, startAngle = start, sweepAngle = sweep, useCenter = true, topLeft = topLeft, size = sizeSq)
            start += sweep
        }
    }
}
