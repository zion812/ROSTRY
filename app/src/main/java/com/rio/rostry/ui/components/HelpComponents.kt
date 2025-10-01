package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

enum class HelpType { INFO, TIP, WARNING }

@Composable
fun HelpButton(
    title: String,
    content: String,
    helpType: HelpType = HelpType.INFO,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showDialog = true },
        modifier = modifier.size(24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Help,
            contentDescription = "Help",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showDialog) {
        HelpDialog(
            title = title,
            content = content,
            helpType = helpType,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun HelpDialog(
    title: String,
    content: String,
    helpType: HelpType,
    onDismiss: () -> Unit,
    learnMoreLink: String? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (helpType) {
                            HelpType.INFO -> Icons.Filled.Info
                            HelpType.TIP -> Icons.Filled.Help
                            HelpType.WARNING -> Icons.Filled.Warning
                        },
                        contentDescription = null,
                        tint = when (helpType) {
                            HelpType.INFO -> MaterialTheme.colorScheme.primary
                            HelpType.TIP -> MaterialTheme.colorScheme.tertiary
                            HelpType.WARNING -> MaterialTheme.colorScheme.error
                        }
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (learnMoreLink != null) {
                    TextButton(onClick = { /* Open link */ }) {
                        Text("Learn more")
                    }
                }
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

@Composable
fun InlineHelp(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun FieldWithHelp(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    helpTitle: String,
    helpContent: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            isError = isError,
            supportingText = supportingText
        )
        HelpButton(
            title = helpTitle,
            content = helpContent
        )
    }
}

object HelpContent {
    const val PRODUCT_TITLE = "Choose a clear, descriptive title that highlights the key features of your product."
    const val TRACEABILITY = "Traceable products have documented lineage and can be tracked through their lifecycle, increasing buyer confidence."
    const val HEALTH_RECORDS = "Upload vaccination and health check documents to verify the health status of your fowl."
    const val LOCATION = "We use your location to show your products to nearby buyers and calculate delivery costs accurately."
    const val TRANSFER_TYPE = "Choose the type of transfer based on your transaction: Sale for monetary exchange, Gift for free transfer, Breeding Loan for temporary arrangement, or Ownership Transfer for permanent change."
    const val PAYMENT_TERMS = "Define how payment will be handled: Full upfront, Installments, or Cash on Delivery (COD)."
}
