package com.rio.rostry.ui.farmer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * SmartActionCard - Aggregates all urgent actionable items for the farmer.
 * Shows a unified count of actions needed with category breakdown.
 * 
 * Part of the Farmer Integration Improvement (Priority 1: Smart Task Aggregator).
 */
@Composable
fun SmartActionCard(
    vaccinationOverdue: Int,
    vaccinationDue: Int,
    tasksOverdue: Int,
    tasksDue: Int,
    hatchingDueThisWeek: Int,
    ordersToVerify: Int,
    enquiriesPending: Int,
    onVaccinationClick: () -> Unit,
    onTasksClick: () -> Unit,
    onHatchingClick: () -> Unit,
    onOrdersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalUrgent = vaccinationOverdue + tasksOverdue
    val totalDue = vaccinationDue + tasksDue + hatchingDueThisWeek + ordersToVerify + enquiriesPending
    val totalActions = totalUrgent + totalDue
    
    if (totalActions == 0) return // Don't show if nothing to do
    
    val hasUrgent = totalUrgent > 0
    val gradientColors = if (hasUrgent) {
        listOf(Color(0xFFE53935), Color(0xFFFF7043)) // Red-orange gradient for urgent
    } else {
        listOf(Color(0xFF43A047), Color(0xFF66BB6A)) // Green gradient for normal
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column {
                // Header with total count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (hasUrgent) "⚠️ Actions Required" else "✓ Today's Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        AnimatedContent(
                            targetState = totalActions,
                            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                            label = "count_animation"
                        ) { count ->
                            Text(
                                text = "$count items need attention",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    
                    // Animated count badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "$totalActions",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action chips row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Vaccination chip (show if any due or overdue)
                    if (vaccinationDue + vaccinationOverdue > 0) {
                        item {
                            ActionChip(
                                icon = Icons.Default.Vaccines,
                                label = "Vaccination",
                                count = vaccinationDue + vaccinationOverdue,
                                isUrgent = vaccinationOverdue > 0,
                                onClick = onVaccinationClick
                            )
                        }
                    }
                    
                    // Tasks chip
                    if (tasksDue + tasksOverdue > 0) {
                        item {
                            ActionChip(
                                icon = Icons.Default.Task,
                                label = "Tasks",
                                count = tasksDue + tasksOverdue,
                                isUrgent = tasksOverdue > 0,
                                onClick = onTasksClick
                            )
                        }
                    }
                    
                    // Hatching chip
                    if (hatchingDueThisWeek > 0) {
                        item {
                            ActionChip(
                                icon = Icons.Default.EggAlt,
                                label = "Hatching",
                                count = hatchingDueThisWeek,
                                isUrgent = false,
                                onClick = onHatchingClick
                            )
                        }
                    }
                    
                    // Orders chip (combine verifications and enquiries)
                    if (ordersToVerify + enquiriesPending > 0) {
                        item {
                            ActionChip(
                                icon = Icons.Default.ShoppingCart,
                                label = "Orders",
                                count = ordersToVerify + enquiriesPending,
                                isUrgent = ordersToVerify > 0,
                                onClick = onOrdersClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: ImageVector,
    label: String,
    count: Int,
    isUrgent: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = if (isUrgent) Color.White else Color.White.copy(alpha = 0.85f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = if (isUrgent) Color(0xFFE53935) else Color(0xFF43A047)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF212121)
            )
            // Count badge
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isUrgent) Color(0xFFE53935) else Color(0xFF43A047)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
