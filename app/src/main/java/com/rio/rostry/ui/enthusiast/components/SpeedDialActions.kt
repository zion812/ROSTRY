package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.components.pulse
import com.rio.rostry.ui.theme.Coral
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.Emerald
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.RostryBlue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Action types for the speed dial.
 */
enum class SpeedDialAction(
    val icon: ImageVector,
    val label: String,
    val color: Color
) {
    VACCINATION(Icons.Default.Healing, "Vaccination", Coral),
    EGGS(Icons.Default.Egg, "Eggs", EnthusiastGold),
    ANALYTICS(Icons.Default.Analytics, "Analytics", ElectricBlue),
    BREEDING(Icons.Default.EmojiEvents, "Breeding", Emerald)
}

/**
 * Quick action data for long-press menu.
 */
data class QuickActionItem(
    val label: String,
    val onClick: () -> Unit
)

/**
 * Apple Shortcuts-inspired floating action dock with radial layout.
 * Features:
 * - Central FAB with expand/collapse animation
 * - Radial action buttons with 90° spacing
 * - Pulsing badges for pending tasks
 * - Long-press quick actions menu
 */
@Composable
fun SpeedDialActions(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit = {},
    pendingTasks: Map<SpeedDialAction, Int> = emptyMap(),
    onActionClick: (SpeedDialAction) -> Unit = {},
    quickActions: Map<SpeedDialAction, List<QuickActionItem>> = emptyMap()
) {
    var expanded by remember { mutableStateOf(isExpanded) }
    var showQuickActionsFor by remember { mutableStateOf<SpeedDialAction?>(null) }
    val haptic = LocalHapticFeedback.current
    
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(200),
        label = "fabRotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.1f else 1f,
        animationSpec = tween(200),
        label = "fabScale"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Action buttons (positioned radially)
        SpeedDialAction.values().forEachIndexed { index, action ->
            val angle = -90.0 - (index * 45.0) // Start from top, go clockwise
            val distance by animateDpAsState(
                targetValue = if (expanded) 80.dp else 0.dp,
                animationSpec = tween(200 + index * 50),
                label = "actionDistance$index"
            )
            
            val offsetX = (distance.value * cos(Math.toRadians(angle))).dp
            val offsetY = (distance.value * sin(Math.toRadians(angle))).dp
            
            AnimatedVisibility(
                visible = expanded,
                enter = scaleIn(animationSpec = tween(200, delayMillis = index * 30)) + 
                        fadeIn(animationSpec = tween(150, delayMillis = index * 30)),
                exit = scaleOut(animationSpec = tween(150)) + fadeOut(animationSpec = tween(100))
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToPx(), offsetY.roundToPx()) }
                ) {
                    val pendingCount = pendingTasks[action] ?: 0
                    
                    Box(
                        modifier = Modifier
                            .pointerInput(action) {
                                detectTapGestures(
                                    onTap = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onActionClick(action)
                                    },
                                    onLongPress = {
                                        if (quickActions.containsKey(action)) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            showQuickActionsFor = action
                                        }
                                    }
                                )
                            }
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingCount > 0) {
                                    Badge(
                                        containerColor = Coral,
                                        modifier = Modifier.pulse()
                                    ) {
                                        Text(pendingCount.toString())
                                    }
                                }
                            }
                        ) {
                            SmallFloatingActionButton(
                                onClick = { /* handled by pointerInput */ },
                                containerColor = action.color.copy(alpha = 0.9f),
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(4.dp)
                            ) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.label,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        // Quick actions dropdown
                        DropdownMenu(
                            expanded = showQuickActionsFor == action,
                            onDismissRequest = { showQuickActionsFor = null }
                        ) {
                            quickActions[action]?.forEach { quickAction ->
                                DropdownMenuItem(
                                    text = { Text(quickAction.label) },
                                    onClick = {
                                        quickAction.onClick()
                                        showQuickActionsFor = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Central FAB
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                expanded = !expanded
                onExpandToggle()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .scale(scale)
                .graphicsLayer { rotationZ = rotation }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (expanded) "Close" else "Open actions"
            )
        }
    }
}

/**
 * Simplified speed dial for compact layouts.
 */
@Composable
fun CompactSpeedDial(
    actions: List<Pair<ImageVector, () -> Unit>>,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary
) {
    var expanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEach { (icon, onClick) ->
                    SmallFloatingActionButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        },
                        containerColor = primaryColor.copy(alpha = 0.85f)
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                expanded = !expanded
            },
            containerColor = primaryColor
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.rotate(if (expanded) 45f else 0f)
            )
        }
    }
}
