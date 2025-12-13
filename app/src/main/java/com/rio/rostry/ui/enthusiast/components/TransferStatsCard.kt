package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferStatsCard(
    totalCount: Int,
    pendingCount: Int,
    disputedCount: Int,
    completedCount: Int,
    onFilterByStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(Dimens.space_large)) {
            Text(
                "Transfer Overview",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Last 30 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(Dimens.space_medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    icon = Icons.Filled.Send,
                    value = totalCount.toString(),
                    label = "Total",
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = { onFilterByStatus("ALL") },
                    contentDescription = "Total transfers: $totalCount, tap to view all"
                )
                VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp))
                MetricItem(
                    icon = Icons.Filled.Timer,
                    value = pendingCount.toString(),
                    label = "Pending",
                    tint = MaterialTheme.colorScheme.tertiary,
                    showBadge = pendingCount > 0,
                    onClick = { onFilterByStatus("PENDING") },
                    contentDescription = "Pending transfers: $pendingCount, tap to filter"
                )
                VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp))
                MetricItem(
                    icon = Icons.Filled.Warning,
                    value = disputedCount.toString(),
                    label = "Disputed",
                    tint = MaterialTheme.colorScheme.error,
                    showBadge = disputedCount > 0,
                    onClick = { onFilterByStatus("DISPUTED") },
                    contentDescription = "Disputed transfers: $disputedCount, tap to filter"
                )
                VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp))
                MetricItem(
                    icon = Icons.Filled.CheckCircle,
                    value = completedCount.toString(),
                    label = "Completed",
                    tint = Color(0xFF4CAF50),
                    onClick = { onFilterByStatus("COMPLETED") },
                    contentDescription = "Completed transfers: $completedCount, tap to filter"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color,
    showBadge: Boolean = false,
    onClick: () -> Unit,
    contentDescription: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(scale)
            .semantics { this.contentDescription = contentDescription }
            .padding(vertical = Dimens.space_small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.space_small)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            if (showBadge) {
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(tint, CircleShape)
                )
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
