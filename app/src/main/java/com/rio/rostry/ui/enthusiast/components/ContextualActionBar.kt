package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.theme.EnthusiastGold

/**
 * Context-aware action that can be shown in the ContextualActionBar.
 */
data class QuickAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color = Color.Unspecified,
    val badgeCount: Int = 0
)

/**
 * Instagram-inspired floating action bar showing max 3 context-aware actions.
 * Actions are determined based on the current state (e.g., pending vaccinations, eggs to log).
 * 
 * Features:
 * - Glassmorphic background
 * - Smooth scale animations on press
 * - Badge indicators for urgent items
 * - Haptic feedback
 */
@Composable
fun ContextualActionBar(
    actions: List<QuickAction>,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxActions: Int = 3
) {
    val haptic = LocalHapticFeedback.current
    val displayActions = actions.take(maxActions)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            displayActions.forEach { action ->
                ContextActionItem(
                    action = action,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onActionClick(action.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun ContextActionItem(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (action.color != Color.Unspecified) {
        action.color
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    Surface(
        modifier = modifier
            .semantics {
                contentDescription = "${action.label}${if (action.badgeCount > 0) ", ${action.badgeCount} pending" else ""}"
                role = Role.Button
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = iconColor.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            androidx.compose.foundation.layout.Box {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
                
                // Badge for pending count
                if (action.badgeCount > 0) {
                    androidx.compose.material3.Badge(
                        modifier = Modifier.align(Alignment.TopEnd),
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = if (action.badgeCount > 9) "9+" else action.badgeCount.toString(),
                            fontSize = 9.sp
                        )
                    }
                }
            }
            
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = iconColor
            )
        }
    }
}

/**
 * Predefined quick actions for common Enthusiast workflows.
 */
object EnthusiastQuickActions {
    val LOG_EGGS = QuickAction(
        id = "log_eggs",
        label = "Eggs",
        icon = Icons.Filled.EggAlt,
        color = Color(0xFFFF9800)
    )
    
    val VACCINATION = QuickAction(
        id = "vaccination",
        label = "Vaccinate",
        icon = Icons.Filled.Vaccines,
        color = Color(0xFF4CAF50)
    )
    
    val BREEDING = QuickAction(
        id = "breeding",
        label = "Breeding",
        icon = Icons.Filled.Favorite,
        color = Color(0xFFE91E63)
    )
    
    val ANALYTICS = QuickAction(
        id = "analytics",
        label = "Analytics",
        icon = Icons.Filled.Analytics,
        color = Color(0xFF2196F3)
    )
    
    val ADD_BIRD = QuickAction(
        id = "add_bird",
        label = "Add",
        icon = Icons.Filled.Add,
        color = Color(0xFF673AB7)
    )
}
