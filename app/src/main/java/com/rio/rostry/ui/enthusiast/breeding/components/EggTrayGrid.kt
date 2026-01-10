package com.rio.rostry.ui.enthusiast.breeding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Represents the status of an individual egg cell.
 */
enum class EggStatus {
    EMPTY,      // No egg
    GOOD,       // Good quality egg
    DAMAGED,    // Damaged but usable
    BROKEN      // Broken/discarded
}

/**
 * Data class for an individual egg in the tray.
 */
data class EggCell(
    val row: Int,
    val column: Int,
    val status: EggStatus = EggStatus.EMPTY
)

/**
 * Visual Egg Tray Grid component for logging eggs.
 * Displays a 6x5 grid (standard 30-egg tray) with interactive cells.
 */
@Composable
fun EggTrayGrid(
    modifier: Modifier = Modifier,
    eggs: List<EggCell>,
    onEggClick: (row: Int, column: Int) -> Unit,
    isEditable: Boolean = true,
    columns: Int = 6,
    rows: Int = 5
) {
    val eggMap = remember(eggs) {
        eggs.associateBy { "${it.row},${it.column}" }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tray Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Egg Tray",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            // Count Summary
            val goodCount = eggs.count { it.status == EggStatus.GOOD }
            val damagedCount = eggs.count { it.status == EggStatus.DAMAGED }
            val brokenCount = eggs.count { it.status == EggStatus.BROKEN }
            
            Text(
                text = "✅$goodCount  ⚠️$damagedCount  ❌$brokenCount",
                style = MaterialTheme.typography.labelMedium
            )
        }
        
        // Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(columns) { column ->
                        val key = "${row},${column}"
                        val egg = eggMap[key]
                        val status = egg?.status ?: EggStatus.EMPTY
                        
                        EggCell(
                            status = status,
                            onClick = { if (isEditable) onEggClick(row, column) }
                        )
                    }
                }
            }
        }
        
        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(status = EggStatus.EMPTY, label = "Empty")
            LegendItem(status = EggStatus.GOOD, label = "Good")
            LegendItem(status = EggStatus.DAMAGED, label = "Damaged")
            LegendItem(status = EggStatus.BROKEN, label = "Broken")
        }
    }
}

@Composable
private fun EggCell(
    status: EggStatus,
    onClick: () -> Unit
) {
    val (backgroundColor, borderColor, icon) = when (status) {
        EggStatus.EMPTY -> Triple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            null
        )
        EggStatus.GOOD -> Triple(
            Color(0xFFFFF8E1), // Cream/egg color
            Color(0xFFFFB74D), // Golden border
            Icons.Default.Check to Color(0xFF4CAF50)
        )
        EggStatus.DAMAGED -> Triple(
            Color(0xFFFFF3E0), // Light orange
            Color(0xFFFF9800),
            null
        )
        EggStatus.BROKEN -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            Icons.Default.Close to MaterialTheme.colorScheme.error
        )
    }
    
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon?.let { (iconVector, tint) ->
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = tint
            )
        }
        
        // Show egg emoji for non-empty statuses
        if (status != EggStatus.EMPTY && status != EggStatus.BROKEN) {
            Text(
                text = "🥚",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun LegendItem(
    status: EggStatus,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val color = when (status) {
            EggStatus.EMPTY -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            EggStatus.GOOD -> Color(0xFF4CAF50)
            EggStatus.DAMAGED -> Color(0xFFFF9800)
            EggStatus.BROKEN -> MaterialTheme.colorScheme.error
        }
        
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Helper function to create an EggTray state manager.
 */
@Composable
fun rememberEggTrayState(
    initialEggs: List<EggCell> = emptyList()
): EggTrayState {
    return remember { EggTrayState(initialEggs.toMutableList()) }
}

class EggTrayState(
    initialEggs: MutableList<EggCell> = mutableListOf()
) {
    var eggs by mutableStateOf(initialEggs.toList())
        private set
    
    // Current status to apply when tapping
    var currentStatus by mutableStateOf(EggStatus.GOOD)
    
    fun onEggClick(row: Int, column: Int) {
        val key = "${row},${column}"
        val existingIndex = eggs.indexOfFirst { "${it.row},${it.column}" == key }
        
        eggs = if (existingIndex >= 0) {
            val existing = eggs[existingIndex]
            // Cycle through statuses: EMPTY -> GOOD -> DAMAGED -> BROKEN -> EMPTY
            val nextStatus = when (existing.status) {
                EggStatus.EMPTY -> EggStatus.GOOD
                EggStatus.GOOD -> EggStatus.DAMAGED
                EggStatus.DAMAGED -> EggStatus.BROKEN
                EggStatus.BROKEN -> EggStatus.EMPTY
            }
            eggs.toMutableList().apply {
                if (nextStatus == EggStatus.EMPTY) {
                    removeAt(existingIndex)
                } else {
                    this[existingIndex] = existing.copy(status = nextStatus)
                }
            }
        } else {
            // Add new egg with current status
            eggs + EggCell(row, column, currentStatus)
        }
    }
    
    fun getGoodCount(): Int = eggs.count { it.status == EggStatus.GOOD }
    fun getDamagedCount(): Int = eggs.count { it.status == EggStatus.DAMAGED }
    fun getBrokenCount(): Int = eggs.count { it.status == EggStatus.BROKEN }
    fun getTotalCollected(): Int = eggs.count { it.status != EggStatus.EMPTY }
    
    fun toJson(): String {
        // Simple JSON serialization for storage
        return eggs.joinToString(",") { "${it.row}:${it.column}:${it.status.name}" }
    }
    
    companion object {
        fun fromJson(json: String): List<EggCell> {
            if (json.isBlank()) return emptyList()
            return json.split(",").mapNotNull { cell ->
                val parts = cell.split(":")
                if (parts.size == 3) {
                    EggCell(
                        row = parts[0].toIntOrNull() ?: return@mapNotNull null,
                        column = parts[1].toIntOrNull() ?: return@mapNotNull null,
                        status = EggStatus.entries.find { it.name == parts[2] } ?: EggStatus.EMPTY
                    )
                } else null
            }
        }
    }
}
