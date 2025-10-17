package com.rio.rostry.ui.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransferCreateScreen(
    viewModel: TransferCreateViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToQuarantine: () -> Unit = {}, // New: For actionable button navigation
    onNavigateToFarmData: () -> Unit = {} // New: For actionable button navigation
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.successTransferId) {
        if (state.successTransferId != null) {
            onBack()
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Enhanced error banner with icons
        state.error?.let { error ->
            EnhancedErrorBanner(
                message = error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        when {
            !state.confirmationStep -> TransferFormStep(
                state = state,
                onSelectProduct = viewModel::openProductPicker,
                onSelectRecipient = viewModel::openRecipientPicker,
                onTypeSelect = viewModel::setTransferType,
                onAmountChange = { viewModel.update("amount", it) },
                onNotesChange = { viewModel.update("notes", it) },
                onProceed = viewModel::proceedToConfirmation,
                onBack = onBack,
                onNavigateToQuarantine = onNavigateToQuarantine,
                onNavigateToFarmData = onNavigateToFarmData
            )
            else -> ConfirmationStep(
                state = state,
                onConfirm = viewModel::confirmAndSubmit,
                onBack = viewModel::exitConfirmation
            )
        }

        // Product Picker Sheet
        if (state.showProductPicker) {
            ProductPickerSheet(
                products = state.availableProducts,
                onSelect = viewModel::selectProduct,
                onDismiss = viewModel::dismissProductPicker
            )
        }

        // Recipient Picker Sheet
        if (state.showRecipientPicker) {
            RecipientPickerSheet(
                onSearch = viewModel::searchRecipients,
                results = state.searchResults,
                onSelect = viewModel::selectRecipient,
                onDismiss = viewModel::dismissRecipientPicker
            )
        }
    }
}

// New: Enhanced error banner with icon support
@Composable
private fun EnhancedErrorBanner(message: String, modifier: Modifier = Modifier) {
    val icon = when {
        message.contains("quarantine", ignoreCase = true) || message.contains("farm data", ignoreCase = true) -> Icons.Filled.Warning
        else -> Icons.Filled.Error
    }
    val containerColor = when {
        message.contains("quarantine", ignoreCase = true) || message.contains("farm data", ignoreCase = true) -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when {
        message.contains("quarantine", ignoreCase = true) || message.contains("farm data", ignoreCase = true) -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Card(colors = CardDefaults.cardColors(containerColor = containerColor), modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Spacer(Modifier.width(8.dp))
            Text(text = message, color = contentColor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TransferFormStep(
    state: TransferCreateViewModel.UiState,
    onSelectProduct: () -> Unit,
    onSelectRecipient: () -> Unit,
    onTypeSelect: (TransferCreateViewModel.TransferType) -> Unit,
    onAmountChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onProceed: () -> Unit,
    onBack: () -> Unit,
    onNavigateToQuarantine: () -> Unit,
    onNavigateToFarmData: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Create Transfer", style = MaterialTheme.typography.titleLarge)

        // Transfer Type Selector
        Text("Transfer Type", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TransferCreateViewModel.TransferType.values().forEach { type ->
                FilterChip(
                    selected = state.transferType == type,
                    onClick = { onTypeSelect(type) },
                    label = { Text(type.name.replace('_', ' ')) }
                )
            }
        }

        // Product Selection with inline badge
        Card(
            onClick = onSelectProduct,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Product", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            state.selectedProduct?.name ?: "Select a product",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        state.selectedProduct?.let { product ->
                            val (badgeColor, badgeText) = when (state.productStatus) { // Assumes new field in UiState
                                "quarantine" -> MaterialTheme.colorScheme.tertiary to "Quarantine - Cannot Transfer"
                                "deceased" -> MaterialTheme.colorScheme.error to "Deceased - Cannot Transfer"
                                "transferred" -> MaterialTheme.colorScheme.outline to "Already Transferred"
                                else -> MaterialTheme.colorScheme.primary to "Available for Transfer"
                            }
                            Spacer(Modifier.width(8.dp))
                            Card(colors = CardDefaults.cardColors(containerColor = badgeColor), modifier = Modifier.height(20.dp)) {
                                Text(badgeText, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
                Icon(Icons.Filled.ChevronRight, null)
            }
        }
        state.validationErrors["product"]?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Product Status Warning Card (conditional)
        state.selectedProduct?.let {
            when (state.productStatus) {
                "quarantine" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("⚠️ This product is in quarantine. Complete quarantine protocol before transfer.", style = MaterialTheme.typography.bodyMedium)
                            OutlinedButton(onClick = onNavigateToQuarantine) { Text("View Quarantine Status") }
                        }
                    }
                }
                "deceased" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("❌ This bird is marked as deceased. Cannot transfer.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                "transferred" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("❌ This product has already been transferred.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                "outdated_farm_data" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("⚠️ Farm data is outdated. Update vaccination and health logs before transfer.", style = MaterialTheme.typography.bodyMedium)
                            OutlinedButton(onClick = onNavigateToFarmData) { Text("Update Farm Data") }
                        }
                    }
                }
                else -> Unit
            }
        }

        // Recipient Selection
        Card(
            onClick = onSelectRecipient,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Recipient", style = MaterialTheme.typography.labelMedium)
                    Text(
                        state.selectedRecipient?.fullName ?: "Select a recipient",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Icon(Icons.Filled.ChevronRight, null)
            }
        }
        state.validationErrors["recipient"]?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Amount (if applicable)
        if (state.transferType == TransferCreateViewModel.TransferType.SALE) {
            OutlinedTextField(
                value = state.amount,
                onValueChange = {
                    val cleaned = it.replace(Regex("[^0-9.]"), "")
                    val parts = cleaned.split('.')
                    val normalized = when {
                        parts.size <= 1 -> cleaned
                        else -> parts[0] + "." + parts.drop(1).joinToString("").take(2)
                    }
                    onAmountChange(normalized)
                },
                label = { Text("Amount (₹)") },
                isError = state.validationErrors.containsKey("amount"),
                supportingText = state.validationErrors["amount"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // Notes
        OutlinedTextField(
            value = state.notes,
            onValueChange = onNotesChange,
            label = { Text("Notes (optional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
            Text("Cancel")
        }
        Button(
            onClick = onProceed,
            enabled = !state.validating, // Assumes new field in UiState
            modifier = Modifier.weight(1f)
        ) {
            if (state.validating) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Validating transfer eligibility...")
            } else {
                Text("Review")
            }
        }
    }
}

@Composable
private fun ConfirmationStep(
    state: TransferCreateViewModel.UiState,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val checklistItems = listOf(
        "Product ownership verified" to (state.ownershipVerified ?: true), // Assumes new field in UiState
        "Product not in quarantine" to (state.productStatus != "quarantine"),
        "Product not deceased or transferred" to (state.productStatus !in listOf("deceased", "transferred")),
        "Farm data up-to-date (for traceable products)" to (state.productStatus != "outdated_farm_data" || state.selectedProduct?.familyTreeId == null)
    )
    val allValid = checklistItems.all { it.second }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Confirm Transfer", style = MaterialTheme.typography.titleLarge)

        // Validation Checklist Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Validation Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                checklistItems.forEach { (label, passed) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (passed) Icons.Filled.Check else Icons.Filled.Close,
                            contentDescription = null,
                            tint = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Transfer Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                DetailRow("Type", state.transferType.name.replace('_', ' '))
                Divider()
                DetailRow("Product", state.selectedProduct?.name ?: "-")
                Divider()
                DetailRow("Recipient", state.selectedRecipient?.fullName ?: "-")

                if (state.transferType == TransferCreateViewModel.TransferType.SALE) {
                    Divider()
                    DetailRow("Amount", "₹${state.amount}")
                }

                if (state.notes.isNotBlank()) {
                    Divider()
                    Column {
                        Text("Notes", style = MaterialTheme.typography.labelMedium)
                        Text(state.notes, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text("⚠️ Important", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("This action cannot be undone. Please verify all details before confirming.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
            Text("Back")
        }
        Button(
            onClick = onConfirm,
            enabled = !state.loading && allValid,
            modifier = Modifier.weight(1f)
        ) {
            if (state.loading) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("Confirm Transfer")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductPickerSheet(
    products: List<com.rio.rostry.data.database.entity.ProductEntity>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text("Select Product", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            if (products.isEmpty()) {
                Text("No products available", style = MaterialTheme.typography.bodyMedium)
            } else {
                products.forEach { product ->
                    Card(
                        onClick = { onSelect(product.productId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.bodyLarge)
                                Text("₹${product.price}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Icon(Icons.Filled.ChevronRight, null)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipientPickerSheet(
    onSearch: (String) -> Unit,
    results: List<com.rio.rostry.data.database.entity.UserEntity>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text("Select Recipient", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearch(it)
                },
                label = { Text("Search by name or ID") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(16.dp))
            
            if (results.isEmpty() && searchQuery.length >= 2) {
                Text("No users found", style = MaterialTheme.typography.bodyMedium)
            } else {
                results.forEach { user ->
                    Card(
                        onClick = { onSelect(user.userId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(user.fullName ?: user.userId, style = MaterialTheme.typography.bodyLarge)
                                Text(user.userId, style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(Icons.Filled.ChevronRight, null)
                        }
                    }
                }
            }
        }
    }
}
