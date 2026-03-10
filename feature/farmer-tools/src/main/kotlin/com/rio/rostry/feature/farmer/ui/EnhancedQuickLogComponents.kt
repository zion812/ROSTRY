package com.rio.rostry.ui.farmer

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Quick action data class for bulk and one-tap operations
 */
data class QuickAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val badge: String? = null,
    val enabled: Boolean = true
)

/**
 * Enhanced Quick Log Card with smart suggestions and one-tap actions.
 * Shows contextual suggestions based on time of day, recent activity, and pending tasks.
 */
@Composable
fun EnhancedQuickLogCard(
    suggestions: List<QuickAction>,
    onActionClick: (String) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Quick Log",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onViewAll) {
                    Text("View All")
                    Icon(Icons.Default.ChevronRight, null, Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(suggestions) { action ->
                    QuickActionChip(
                        action = action,
                        onClick = { onActionClick(action.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    action: QuickAction,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        enabled = action.enabled,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = action.color.copy(alpha = 0.1f),
            contentColor = action.color
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(action.icon, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(action.label, fontWeight = FontWeight.Medium)
            }
            
            if (action.badge != null) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp),
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(action.badge)
                }
            }
        }
    }
}

/**
 * Bulk action sheet for batch operations on multiple birds/batches
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkActionSheet(
    selectedCount: Int,
    onVaccinate: () -> Unit,
    onWeigh: () -> Unit,
    onFeed: () -> Unit,
    onCreateListing: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Bulk Actions for $selectedCount birds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(16.dp))
            
            BulkActionRow(
                icon = Icons.Default.Vaccines,
                label = "Record Vaccination",
                description = "Apply same vaccination to all selected",
                color = Color(0xFF7C3AED),
                onClick = onVaccinate
            )
            
            BulkActionRow(
                icon = Icons.Default.Scale,
                label = "Record Weight",
                description = "Batch weigh all selected birds",
                color = Color(0xFF2563EB),
                onClick = onWeigh
            )
            
            BulkActionRow(
                icon = Icons.Default.Restaurant,
                label = "Record Feed",
                description = "Log feed distribution to selected",
                color = Color(0xFF16A34A),
                onClick = onFeed
            )
            
            Divider(Modifier.padding(vertical = 12.dp))
            
            BulkActionRow(
                icon = Icons.Default.Storefront,
                label = "Create Group Listing",
                description = "List all selected for sale together",
                color = Color(0xFFEA580C),
                onClick = onCreateListing
            )
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BulkActionRow(
    icon: ImageVector,
    label: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Smart suggestion generator based on context
 */
object SmartSuggestionProvider {
    
    fun getSuggestions(
        pendingVaccinations: Int,
        lastFeedTime: Long?,
        lastWeighTime: Long?,
        currentHour: Int = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    ): List<QuickAction> {
        val suggestions = mutableListOf<QuickAction>()
        
        // Morning: Feed suggestion
        if (currentHour in 5..9) {
            suggestions.add(
                QuickAction(
                    id = "morning_feed",
                    label = "Morning Feed",
                    icon = Icons.Default.WbSunny,
                    color = Color(0xFFFF9800),
                    enabled = true
                )
            )
        }
        
        // Pending vaccinations
        if (pendingVaccinations > 0) {
            suggestions.add(
                QuickAction(
                    id = "vaccination",
                    label = "Vaccinations",
                    icon = Icons.Default.Vaccines,
                    color = Color(0xFF7C3AED),
                    badge = pendingVaccinations.toString()
                )
            )
        }
        
        // Weekly weigh reminder (if last weigh > 7 days)
        val weekMs = 7 * 24 * 60 * 60 * 1000L
        if (lastWeighTime == null || System.currentTimeMillis() - lastWeighTime > weekMs) {
            suggestions.add(
                QuickAction(
                    id = "weekly_weigh",
                    label = "Weekly Weigh",
                    icon = Icons.Default.Scale,
                    color = Color(0xFF2563EB)
                )
            )
        }
        
        // Evening: Evening feed
        if (currentHour in 16..19) {
            suggestions.add(
                QuickAction(
                    id = "evening_feed",
                    label = "Evening Feed",
                    icon = Icons.Default.NightsStay,
                    color = Color(0xFF5C6BC0)
                )
            )
        }
        
        // Default actions
        if (suggestions.size < 3) {
            suggestions.add(
                QuickAction(
                    id = "expense",
                    label = "Log Expense",
                    icon = Icons.Default.Receipt,
                    color = Color(0xFFEA580C)
                )
            )
        }
        
        return suggestions.take(4)
    }
}
