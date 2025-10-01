package com.rio.rostry.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.flow.MutableStateFlow

object TooltipManager {
    private val shownTooltips = MutableStateFlow<Set<String>>(emptySet())

    fun shouldShow(tooltipId: String): Boolean {
        return tooltipId !in shownTooltips.value
    }

    fun markAsShown(tooltipId: String) {
        shownTooltips.value = shownTooltips.value + tooltipId
    }

    fun resetAll() {
        shownTooltips.value = emptySet()
    }
}

enum class TooltipPlacement { TOP, BOTTOM, LEFT, RIGHT }

@Composable
fun FeatureTooltip(
    visible: Boolean,
    title: String,
    description: String,
    onDismiss: () -> Unit,
    placement: TooltipPlacement = TooltipPlacement.BOTTOM,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Popup(
            alignment = when (placement) {
                TooltipPlacement.TOP -> Alignment.TopCenter
                TooltipPlacement.BOTTOM -> Alignment.BottomCenter
                TooltipPlacement.LEFT -> Alignment.CenterStart
                TooltipPlacement.RIGHT -> Alignment.CenterEnd
            },
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = modifier
                    .width(280.dp)
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Got it")
                    }
                }
            }
        }
    }
}

@Composable
fun TooltipAnchor(
    tooltipId: String,
    title: String,
    description: String,
    placement: TooltipPlacement = TooltipPlacement.BOTTOM,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    LaunchedEffect(tooltipId) {
        showTooltip = TooltipManager.shouldShow(tooltipId)
    }

    Box {
        content()
        
        FeatureTooltip(
            visible = showTooltip,
            title = title,
            description = description,
            onDismiss = {
                TooltipManager.markAsShown(tooltipId)
                showTooltip = false
            },
            placement = placement
        )
    }
}
