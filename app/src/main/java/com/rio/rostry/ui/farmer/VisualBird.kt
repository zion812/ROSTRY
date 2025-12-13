package com.rio.rostry.ui.farmer

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class VisualBird(
    val id: String,
    val color: Color,
    val sizeRadius: Float, // Calculated from heightCm/Weight
    val x: Float, // Normalized 0f..1f relative to screen width
    val y: Float, // Normalized 0f..1f relative to screen height
    val label: String, // e.g., "R-102"
    val stage: BirdStage,
    val details: String, // Pre-formatted details for the toast/dialog
    val location: String = "Free Range", // Default to Free Range if unknown
    val isSick: Boolean = false,
    val status: BirdHealthStatus = BirdHealthStatus.NORMAL,
    val isNew: Boolean = false
)

enum class BirdStage {
    CHICK,
    JUVENILE,
    ADULT,
    BREEDER
}

enum class BirdHealthStatus {
    NORMAL,
    VACCINE_DUE,
    READY_TO_SELL
}
