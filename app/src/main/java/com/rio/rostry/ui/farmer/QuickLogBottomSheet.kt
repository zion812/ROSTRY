package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.ProductEntity

/**
 * QuickLogBottomSheet - One-tap logging for batch and bird operations.
 * Allows farmers to quickly log mortality, feed, expenses, or weight for a batch or individual bird.
 * 
 * Part of the Farmer-First Reliability Overhaul (Phase 1).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogBottomSheet(
    products: List<ProductEntity>,  // Renamed from 'batches' - includes both birds and batches
    onDismiss: () -> Unit,
    onLogSubmit: (productId: String?, logType: QuickLogType, value: Double, notes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var selectedLogType by remember { mutableStateOf<QuickLogType?>(null) }
    var inputValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var productDropdownExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Log",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step 1: Select Bird or Batch
            Text(
                text = "1. Select Bird / Batch",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = productDropdownExpanded,
                onExpandedChange = { productDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedProduct?.name ?: "Select a bird or batch...",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = productDropdownExpanded,
                    onDismissRequest = { productDropdownExpanded = false }
                ) {
                    // Option for "All"
                    DropdownMenuItem(
                        text = { Text("All Birds / Batches") },
                        onClick = {
                            selectedProduct = null
                            productDropdownExpanded = false
                        }
                    )
                    products.forEach { product ->
                        DropdownMenuItem(
                            text = { 
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(product.name ?: "Unnamed")
                                        if (product.isBatch == true) {
                                            Text(
                                                text = "Batch",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            Text(
                                                text = "Bird",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }
                                    product.quantity?.let { qty ->
                                        if (product.isBatch == true) {
                                            Text(
                                                text = "${qty.toInt()} birds",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                selectedProduct = product
                                productDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 2: Select Action
            Text(
                text = "2. Select Action",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickLogType.entries.forEach { logType ->
                    FilterChip(
                        selected = selectedLogType == logType,
                        onClick = { selectedLogType = logType },
                        label = { Text(logType.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 3: Enter Value
            selectedLogType?.let { logType ->
                Text(
                    text = "3. Enter ${logType.label}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it.filter { c -> c.isDigit() || c == '.' } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(logType.inputLabel) },
                    suffix = { Text(logType.unit) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Optional notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notes (optional)") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = {
                    val value = inputValue.toDoubleOrNull() ?: 0.0
                    selectedLogType?.let { logType ->
                        onLogSubmit(
                            selectedProduct?.productId,
                            logType,
                            value,
                            notes.ifBlank { null }
                        )
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedLogType != null && inputValue.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Log ${selectedLogType?.label ?: "Entry"}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

enum class QuickLogType(val label: String, val inputLabel: String, val unit: String) {
    MORTALITY("Deaths", "Number of birds", "birds"),
    FEED("Feed", "Amount of feed", "kg"),
    EXPENSE("Expense", "Amount spent", "₹"),
    WEIGHT("Weight", "Average weight", "g"),
    VACCINATION("Vaccination", "Dose/Units given", "doses"),
    DEWORMING("Deworming", "Dose/Units given", "doses"),
    MEDICATION("Medication", "Amount given", "ml/mg"),
    SANITATION("Sanitation", "Hours spent", "hrs"),
    MAINTENANCE("Maintenance", "Cost spent", "₹")
}
