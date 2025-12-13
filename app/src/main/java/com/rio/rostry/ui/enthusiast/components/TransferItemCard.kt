package com.rio.rostry.ui.enthusiast.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.ui.theme.Dimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransferItemCard(
    transfer: TransferEntity,
    isSelected: Boolean,
    trustScore: Int?,
    onToggleSelection: () -> Unit,
    onOpenDetails: () -> Unit,
    onVerify: () -> Unit,
    onOpenChain: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.space_large),
            verticalArrangement = Arrangement.spacedBy(Dimens.space_medium)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() },
                    modifier = Modifier.semantics { contentDescription = "Select transfer" }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.space_small)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send icon",
                        modifier = Modifier.size(Dimens.icon_medium)
                    )
                    Text(
                        text = transfer.transferId,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                val statusColor = when (transfer.status.uppercase()) {
                    "PENDING" -> MaterialTheme.colorScheme.tertiary
                    "VERIFIED" -> MaterialTheme.colorScheme.primary
                    "COMPLETED" -> Color(0xFF4CAF50)
                    "DISPUTED" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                AssistChip(
                    onClick = {},
                    label = { Text(transfer.status) },
                    colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                        containerColor = statusColor
                    )
                )
            }

            // Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
            ) {
                val typeIcon = if (transfer.type.uppercase() == "OUTGOING") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
                val typeText = if (transfer.type.uppercase() == "OUTGOING") "Outgoing" else "Incoming"
                Icon(
                    imageVector = typeIcon,
                    contentDescription = "$typeText transfer",
                    modifier = Modifier.size(Dimens.icon_medium)
                )
                Text(text = typeText, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatRelativeTime(transfer.initiatedAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                transfer.productId?.let { pid ->
                    val truncatedPid = if (pid.length > 12) "${pid.take(12)}..." else pid
                    Text(
                        text = truncatedPid,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Trust Score Section
            trustScore?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "Trust score icon",
                        modifier = Modifier.size(Dimens.icon_medium)
                    )
                    Text(text = "Trust Score", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = it / 100f,
                        modifier = Modifier.weight(1f),
                        color = when {
                            it < 30 -> Color(0xFFE53935)
                            it in 30..70 -> Color(0xFFFDD835)
                            else -> Color(0xFF43A047)
                        }
                    )
                    Text(text = "$it/100", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Verification Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_small)
            ) {
                val steps = listOf("Pending", "Verified", "Completed")
                val currentStep = when (transfer.status.uppercase()) {
                    "PENDING" -> 0
                    "VERIFIED" -> 1
                    "COMPLETED" -> 2
                    else -> 0
                }
                steps.forEachIndexed { index, step ->
                    val isCompleted = index <= currentStep
                    Icon(
                        imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = step,
                        tint = if (index == currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.icon_medium)
                    )
                    if (index < steps.lastIndex) {
                        Divider(
                            modifier = Modifier.weight(1f).padding(horizontal = Dimens.space_small),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action Buttons
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_small),
                verticalArrangement = Arrangement.spacedBy(Dimens.space_small)
            ) {
                OutlinedButton(onClick = onOpenDetails) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Details",
                        modifier = Modifier.size(Dimens.icon_small)
                    )
                    Text("Details")
                }
                if (transfer.status.uppercase() == "PENDING") {
                    Button(onClick = onVerify) {
                        Text("Verify")
                    }
                }
                if (transfer.productId != null) {
                    TextButton(onClick = onOpenChain) {
                        Icon(
                            imageVector = Icons.Filled.AccountTree,
                            contentDescription = "Chain",
                            modifier = Modifier.size(Dimens.icon_small)
                        )
                        Text("Chain")
                    }
                }
                IconButton(onClick = onExport) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Export",
                        modifier = Modifier.size(Dimens.icon_medium)
                    )
                }
            }
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        else -> "$days days ago"
    }
}