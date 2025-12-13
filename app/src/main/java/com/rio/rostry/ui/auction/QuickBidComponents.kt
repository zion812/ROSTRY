package com.rio.rostry.ui.auction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.components.hapticPressAnimation
import com.rio.rostry.ui.components.FadeScaleVisibility

data class QuickBidOption(
    val percentage: Int,
    val label: String
)

@Composable
fun QuickBidChips(
    currentBid: Double,
    onBidSelected: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickBidOptions = remember {
        listOf(
            QuickBidOption(5, "+5%"),
            QuickBidOption(10, "+10%"),
            QuickBidOption(15, "+15%"),
            QuickBidOption(20, "+20%")
        )
    }

    var selectedOption by remember { mutableStateOf<QuickBidOption?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Quick Bid",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickBidOptions) { option ->
                QuickBidChip(
                    option = option,
                    currentBid = currentBid,
                    isSelected = selectedOption == option,
                    onClick = {
                        selectedOption = option
                        val newBid = currentBid * (1 + option.percentage / 100.0)
                        onBidSelected(newBid)
                    }
                )
            }

            item {
                InputChip(
                    selected = false,
                    onClick = { /* Show custom bid input */ },
                    label = { Text("Custom") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun QuickBidChip(
    option: QuickBidOption,
    currentBid: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val newBid = currentBid * (1 + option.percentage / 100.0)

    InputChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${String.format("%.0f", newBid)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        colors = InputChipDefaults.inputChipColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.hapticPressAnimation(onClick = onClick)
    )
}

/**
 * Outbid Snackbar
 */
@Composable
fun OutbidSnackbar(
    productName: String,
    newHighestBid: Double,
    onDismiss: () -> Unit,
    onRebid: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        action = {
            TextButton(
                onClick = {
                    onRebid()
                    onDismiss()
                }
            ) {
                Text("Re-Bid")
            }
        },
        dismissAction = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = "Dismiss")
            }
        },
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "You've been outbid!",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$productName - New bid: ₹${String.format("%.0f", newHighestBid)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Bid Confirmation Card
 */
@Composable
fun BidConfirmationCard(
    visible: Boolean,
    productName: String,
    bidAmount: Double,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    FadeScaleVisibility(visible = visible) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Confirm Your Bid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₹${String.format("%.0f", bidAmount)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm Bid")
                    }
                }
            }
        }
    }
}
