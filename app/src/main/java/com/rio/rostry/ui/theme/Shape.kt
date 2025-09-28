package com.rio.rostry.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape system for ROSTRY.
 * - Small: chips, small buttons, text fields
 * - Medium: cards, dialogs
 * - Large: large cards, sheets
 * - ExtraLarge: hero cards, major surfaces
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
