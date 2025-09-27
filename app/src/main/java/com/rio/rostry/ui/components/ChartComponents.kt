package com.rio.rostry.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.dao.CategoryCount
import com.rio.rostry.data.database.dao.DayCount
import com.rio.rostry.data.repository.monitoring.GrowthPoint
import kotlin.math.PI

@Composable
fun LineChartView(
    data: List<DayCount>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return

    val maxY = (data.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    Canvas(modifier = modifier) {
        val padding = 16f
        val w = size.width - padding * 2
        val h = size.height - padding * 2
        if (w <= 0f || h <= 0f) return@Canvas

        // Points
        val stepX = if (data.size > 1) w / (data.size - 1).coerceAtLeast(1) else 0f
        val pts = data.mapIndexed { idx, d ->
            val x = padding + idx * stepX
            val y = padding + (h - (d.count / maxY.toFloat()) * h)
            Offset(x, y)
        }

        // Axes (minimal)
        drawLine(Color.LightGray, start = Offset(padding, padding + h), end = Offset(padding + w, padding + h))
        drawLine(Color.LightGray, start = Offset(padding, padding), end = Offset(padding, padding + h))

        // Path
        val path = Path()
        path.moveTo(pts.first().x, pts.first().y)
        for (i in 1 until pts.size) path.lineTo(pts[i].x, pts[i].y)
        drawPath(path, color = lineColor, style = Stroke(width = 4f))

        // Points
        pts.forEach { p ->
            drawCircle(color = lineColor, radius = 5f, center = p)
        }
    }
}

@Composable
fun TwoLineChartView(
    points: List<GrowthPoint>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(180.dp),
    expectedColor: Color = MaterialTheme.colorScheme.primary,
    actualColor: Color = MaterialTheme.colorScheme.tertiary
) {
    if (points.isEmpty()) return
    val maxY = listOf(
        points.maxOfOrNull { it.expectedWeightGrams } ?: 0.0,
        points.maxOfOrNull { it.actualWeightGrams ?: 0.0 } ?: 0.0
    ).max().coerceAtLeast(1.0)

    Canvas(modifier = modifier) {
        val padding = 16f
        val w = size.width - padding * 2
        val h = size.height - padding * 2
        if (w <= 0f || h <= 0f) return@Canvas

        val xs = points.map { it.week }.distinct().sorted()
        val stepX = if (xs.size > 1) w / (xs.size - 1).coerceAtLeast(1) else 0f

        fun toPoint(week: Int, value: Double): Offset {
            val idx = xs.indexOf(week).coerceAtLeast(0)
            val x = padding + idx * stepX
            val y = padding + (h - (value.toFloat() / maxY.toFloat()) * h)
            return Offset(x, y)
        }

        // Axes
        drawLine(Color.LightGray, start = Offset(padding, padding + h), end = Offset(padding + w, padding + h))
        drawLine(Color.LightGray, start = Offset(padding, padding), end = Offset(padding, padding + h))

        // Expected path
        if (points.isNotEmpty()) {
            val pathExp = Path()
            val first = toPoint(points.first().week, points.first().expectedWeightGrams)
            pathExp.moveTo(first.x, first.y)
            for (i in 1 until points.size) {
                val p = points[i]
                val off = toPoint(p.week, p.expectedWeightGrams)
                pathExp.lineTo(off.x, off.y)
            }
            drawPath(pathExp, color = expectedColor, style = Stroke(width = 4f))
        }

        // Actual path (skip nulls, break segments)
        var prev: Offset? = null
        points.forEach { gp ->
            val v = gp.actualWeightGrams
            if (v != null) {
                val off = toPoint(gp.week, v)
                prev?.let { pr -> drawLine(actualColor, start = pr, end = off, strokeWidth = 4f) }
                prev = off
                drawCircle(color = actualColor, radius = 5f, center = off)
            } else {
                prev = null
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun PieChartView(
    data: List<CategoryCount>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
    palette: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
    )
) {
    if (data.isEmpty()) return
    val total = data.sumOf { it.count }.coerceAtLeast(1)
    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center
    )

    Canvas(modifier = modifier) {
        val padding = 8f
        val diameter = (size.minDimension - padding * 2)
        val radius = diameter / 2
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val rect = Rect(topLeft, androidx.compose.ui.geometry.Size(diameter, diameter))

        var startAngle = -90f
        data.forEachIndexed { index, item ->
            val sweep = (item.count / total.toFloat()) * 360f
            val color = palette[index % palette.size]
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = rect.topLeft,
                size = rect.size
            )

            // Label
            val midAngleRad = (startAngle + sweep / 2) * (PI / 180).toFloat()
            val labelRadius = radius * 0.6f
            val lx = rect.center.x + labelRadius * kotlin.math.cos(midAngleRad)
            val ly = rect.center.y + labelRadius * kotlin.math.sin(midAngleRad)
            val pct = (item.count * 100f / total)
            val text = "${item.category.take(6)} ${"%.0f".format(pct)}%"
            drawText(
                textMeasurer = textMeasurer,
                text = text,
                topLeft = Offset(lx - 20f, ly - 8f),
                style = labelStyle
            )

            startAngle += sweep
        }
    }
}
