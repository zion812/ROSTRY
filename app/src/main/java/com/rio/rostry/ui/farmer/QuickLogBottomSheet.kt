package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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
    onLogSubmit: (productIds: Set<String>, logType: QuickLogType, value: Double, notes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Multi-select: Track selected product IDs
    var selectedProductIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectAll by remember { mutableStateOf(false) }
    var selectedLogType by remember { mutableStateOf<QuickLogType?>(null) }
    var inputValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showBirdSelector by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter products based on search
    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.name?.contains(searchQuery, ignoreCase = true) == true ||
            it.birdCode?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    // Update selectAll state when all products are selected
    LaunchedEffect(selectedProductIds, products) {
        selectAll = selectedProductIds.size == products.size && products.isNotEmpty()
    }

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

            // Step 1: Select Birds/Batches (Multi-select)
            Text(
                text = "1. Select Birds / Batches",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Selection summary card
            Card(
                onClick = { showBirdSelector = !showBirdSelector },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when {
                                selectedProductIds.isEmpty() -> "Tap to select birds/batches"
                                selectedProductIds.size == products.size -> "All selected (${products.size})"
                                else -> "${selectedProductIds.size} selected"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (selectedProductIds.isNotEmpty()) {
                            // Show first few names
                            val selectedNames = products
                                .filter { it.productId in selectedProductIds }
                                .take(3)
                                .joinToString { it.name ?: "Unnamed" }
                            val moreCount = selectedProductIds.size - 3
                            Text(
                                text = if (moreCount > 0) "$selectedNames +$moreCount more" else selectedNames,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = if (showBirdSelector) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (showBirdSelector) "Collapse" else "Expand"
                    )
                }
            }
            
            // Expandable bird selector
            if (showBirdSelector) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            placeholder = { Text("Search birds or batches...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        // Select All (Filtered) option
                        // Check if all *currently visible/filtered* items are selected
                        val allVisibleSelected = filteredProducts.isNotEmpty() && 
                                               filteredProducts.all { it.productId in selectedProductIds }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = allVisibleSelected,
                                onCheckedChange = { checked ->
                                    selectedProductIds = if (checked) {
                                        // Add all visible items to selection
                                        selectedProductIds + filteredProducts.map { it.productId }
                                    } else {
                                        // Remove all visible items from selection
                                        selectedProductIds - filteredProducts.map { it.productId }.toSet()
                                    }
                                }
                            )
                            Text(
                                text = if (searchQuery.isBlank()) "Select All (${products.size})" 
                                       else "Select All Matches (${filteredProducts.size})",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        HorizontalDivider()
                        
                        if (filteredProducts.isEmpty()) {
                            Text(
                                text = "No birds found matching \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                            )
                        } else {
                            // Individual filtered product checkboxes
                            filteredProducts.forEach { product ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = product.productId in selectedProductIds,
                                        onCheckedChange = { checked ->
                                            selectedProductIds = if (checked) {
                                                selectedProductIds + product.productId
                                            } else {
                                                selectedProductIds - product.productId
                                            }
                                        }
                                    )
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = product.name ?: "Unnamed",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = if (product.isBatch == true) "Batch" else "Bird",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (product.isBatch == true) 
                                                    MaterialTheme.colorScheme.primary 
                                                    else MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                        product.birdCode?.let { code ->
                                            Text(
                                                text = code,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
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
            
            // Action type chips - split into two rows for readability
            val topRowTypes = listOf(
                QuickLogType.MORTALITY, QuickLogType.FEED, QuickLogType.EXPENSE, 
                QuickLogType.WEIGHT, QuickLogType.VACCINATION
            )
            val bottomRowTypes = listOf(
                QuickLogType.DEWORMING, QuickLogType.MEDICATION, 
                QuickLogType.SANITATION, QuickLogType.MAINTENANCE
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    topRowTypes.forEach { logType ->
                        FilterChip(
                            selected = selectedLogType == logType,
                            onClick = { selectedLogType = logType },
                            label = { Text(logType.label, maxLines = 1) }
                        )
                    }
                }
                // Bottom row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bottomRowTypes.forEach { logType ->
                        FilterChip(
                            selected = selectedLogType == logType,
                            onClick = { selectedLogType = logType },
                            label = { Text(logType.label, maxLines = 1) }
                        )
                    }
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
            val selectionCountText = when {
                selectedProductIds.isEmpty() -> ""
                selectedProductIds.size == 1 -> " for 1 bird"
                else -> " for ${selectedProductIds.size} birds"
            }
            Button(
                onClick = {
                    val value = inputValue.toDoubleOrNull() ?: 0.0
                    selectedLogType?.let { logType ->
                        android.util.Log.d("QuickLog", "Submitting ${logType.label} for ${selectedProductIds.size} birds: $selectedProductIds")
                        onLogSubmit(
                            selectedProductIds,
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
                enabled = selectedLogType != null && inputValue.isNotBlank() && selectedProductIds.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Log ${selectedLogType?.label ?: "Entry"}$selectionCountText",
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
