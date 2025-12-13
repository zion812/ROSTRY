package com.rio.rostry.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import com.rio.rostry.data.database.entity.GrowthRecordEntity

@Composable
fun GrowthChart(
    records: List<GrowthRecordEntity>,
    modifier: Modifier = Modifier
) {
    val sorted = records.filter { it.weightGrams != null }.sortedBy { it.week }
    if (sorted.isEmpty()) return

    val maxWeight = sorted.maxOf { it.weightGrams!! }
    val maxWeek = sorted.maxOf { it.week }
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40f
        
        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding),
            end = Offset(padding, height - padding),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 2f
        )
        
        // Plot points
        val path = Path()
        sorted.forEachIndexed { index, record ->
            val x = padding + (record.week.toFloat() / maxWeek.coerceAtLeast(1)) * (width - 2 * padding)
            val y = (height - padding) - (record.weightGrams!!.toFloat() / maxWeight.toFloat()) * (height - 2 * padding)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            drawCircle(
                color = Color.Blue,
                center = Offset(x, y),
                radius = 4f
            )
        }
        
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3f)
        )
    }
}
