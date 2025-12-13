package com.rio.rostry.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class StatusOption(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusPickerSheet(
    currentStatus: String?,
    onStatusSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusOptions = remember {
        listOf(
            StatusOption(
                id = "available",
                label = "Available",
                icon = Icons.Filled.CheckCircle,
                color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
            ),
            StatusOption(
                id = "reserved",
                label = "Reserved",
                icon = Icons.Filled.Schedule,
                color = androidx.compose.ui.graphics.Color(0xFFFFC107)
            ),
            StatusOption(
                id = "ready_to_ship",
                label = "Ready to Ship",
                icon = Icons.Filled.LocalShipping,
                color = androidx.compose.ui.graphics.Color(0xFF2196F3)
            ),
            StatusOption(
                id = "pending_pickup",
                label = "Pending Pickup",
                icon = Icons.Filled.LocationOn,
                color = androidx.compose.ui.graphics.Color(0xFFFF9800)
            ),
            StatusOption(
                id = "sold",
                label = "Sold",
                icon = Icons.Filled.Sell,
                color = androidx.compose.ui.graphics.Color(0xFF9C27B0)
            ),
            StatusOption(
                id = "not_available",
                label = "Not Available",
                icon = Icons.Filled.Block,
                color = androidx.compose.ui.graphics.Color(0xFFF44336)
            ),
            StatusOption(
                id = "under_review",
                label = "Under Review",
                icon = Icons.Filled.Article,
                color = androidx.compose.ui.graphics.Color(0xFF607D8B)
            )
        )
    }

    var customStatusText by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { option ->
                    StatusOptionCard(
                        option = option,
                        isSelected = currentStatus == option.id,
                        onClick = {
                            onStatusSelected(option.id)
                            onDismiss()
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (showCustomInput) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = customStatusText,
                                    onValueChange = { customStatusText = it },
                                    label = { Text("Custom Status") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = {
                                        showCustomInput = false
                                        customStatusText = ""
                                    }) {
                                        Text("Cancel")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (customStatusText.isNotBlank()) {
                                                onStatusSelected(customStatusText)
                                                onDismiss()
                                            }
                                        },
                                        enabled = customStatusText.isNotBlank()
                                    ) {
                                        Text("Apply")
                                    }
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showCustomInput = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Custom Status")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusOptionCard(
    option: StatusOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                option.color.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, option.color)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = option.color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
