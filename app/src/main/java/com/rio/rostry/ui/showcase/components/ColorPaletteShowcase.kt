package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import com.rio.rostry.ui.theme.*

@Composable
fun ColorPaletteShowcase() {
    Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Dimens.space_large)) {
        Text("Color Palettes", style = MaterialTheme.typography.titleLarge)
        PaletteRow("Primary", listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))
        PaletteRow("Secondary", listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer))
        PaletteRow("Tertiary", listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer))
        PaletteRow("Surface", listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant))
        PaletteRow("Background", listOf(MaterialTheme.colorScheme.background))
        PaletteRow("Error", listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer))
    }
}

@Composable
private fun PaletteRow(title: String, colors: List<Color>) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
            colors.forEach { c ->
                Box(
                    modifier = Modifier.size(56.dp).background(c),
                    contentAlignment = Alignment.Center
                ) {}
            }
        }
    }
}
