package com.rio.rostry.ui.admin.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.admin.theme.AdminColors
import com.rio.rostry.ui.admin.theme.getStatusColor

/**
 * Reusable Admin UI Components Library.
 * 
 * Provides consistent styling and behavior for admin-specific
 * UI elements across the Admin Portal.
 */

// ========== Stat Cards ==========

/**
 * Large stat card displaying a key metric with icon and trend.
 */
@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color = AdminColors.Secondary,
    trend: StatTrend? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = AdminColors.Surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                trend?.let { t ->
                    TrendIndicator(trend = t)
                }
            }
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Compact stat card for grid layouts.
 */
@Composable
fun AdminCompactStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color = AdminColors.Secondary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AdminColors.SurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ========== Trend Indicator ==========

data class StatTrend(
    val value: Float,
    val isPositive: Boolean,
    val period: String = "vs last week"
)

@Composable
fun TrendIndicator(trend: StatTrend) {
    val color = if (trend.isPositive) AdminColors.Success else AdminColors.Error
    val icon = if (trend.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${if (trend.isPositive) "+" else ""}${String.format("%.1f", trend.value)}%",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

// ========== Status Badge ==========

@Composable
fun AdminStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val color = getStatusColor(status)
    
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status.replace("_", " ").uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ========== Action Button ==========

@Composable
fun AdminActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ActionButtonVariant = ActionButtonVariant.PRIMARY,
    enabled: Boolean = true
) {
    val backgroundColor = when (variant) {
        ActionButtonVariant.PRIMARY -> AdminColors.Primary
        ActionButtonVariant.SECONDARY -> AdminColors.Secondary
        ActionButtonVariant.DANGER -> AdminColors.Error
        ActionButtonVariant.SUCCESS -> AdminColors.Success
        ActionButtonVariant.GHOST -> Color.Transparent
    }
    
    val contentColor = when (variant) {
        ActionButtonVariant.GHOST -> AdminColors.OnSurface
        ActionButtonVariant.SECONDARY -> Color.Black
        else -> Color.White
    }
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

enum class ActionButtonVariant {
    PRIMARY, SECONDARY, DANGER, SUCCESS, GHOST
}

// ========== Search Bar ==========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AdminColors.Secondary,
            unfocusedBorderColor = AdminColors.SurfaceVariant,
            focusedContainerColor = AdminColors.Surface,
            unfocusedContainerColor = AdminColors.Surface
        )
    )
}

// ========== Filter Chip Row ==========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFilterChipRow(
    filters: List<String>,
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        FilterChip(
            selected = selectedFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("All") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AdminColors.Primary,
                selectedLabelColor = Color.White
            )
        )
        
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.replace("_", " ").replaceFirstChar { it.uppercase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AdminColors.Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

// ========== Empty State ==========

@Composable
fun AdminEmptyState(
    icon: ImageVector = Icons.Default.Inbox,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AdminColors.OnSurfaceVariant,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AdminColors.OnSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AdminColors.OnSurfaceVariant
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminColors.Primary
                )
            ) {
                Text(actionLabel)
            }
        }
    }
}

// ========== Loading State ==========

@Composable
fun AdminLoadingState(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = AdminColors.Secondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AdminColors.OnSurfaceVariant
        )
    }
}

// ========== Error State ==========

@Composable
fun AdminErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = AdminColors.Error,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AdminColors.Error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AdminColors.OnSurfaceVariant
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = onRetry,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AdminColors.Error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

// ========== Data Table ==========

@Composable
fun AdminDataTable(
    headers: List<String>,
    rows: List<List<String>>,
    onRowClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdminColors.SurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            headers.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AdminColors.OnSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Data rows
        rows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = onRowClick != null) { onRowClick?.invoke(index) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                row.forEach { cell ->
                    Text(
                        text = cell,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AdminColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            if (index < rows.size - 1) {
                HorizontalDivider(color = AdminColors.SurfaceVariant)
            }
        }
    }
}
