package com.rio.rostry.ui.admin.commerce

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(
    viewModel: AdminProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Moderation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh would reload products */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search products, sellers...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminProductViewModel.ProductFilter.values().forEach { filter ->
                    FilterChip(
                        selected = uiState.currentFilter == filter,
                        onClick = { viewModel.onFilterChanged(filter) },
                        label = { 
                            Text(
                                when (filter) {
                                    AdminProductViewModel.ProductFilter.ALL -> "All"
                                    AdminProductViewModel.ProductFilter.FLAGGED -> "Flagged"
                                    AdminProductViewModel.ProductFilter.ACTIVE -> "Active"
                                    AdminProductViewModel.ProductFilter.HIDDEN -> "Hidden"
                                }
                            ) 
                        },
                        leadingIcon = if (filter == AdminProductViewModel.ProductFilter.FLAGGED && uiState.products.count { it.adminFlagged } > 0) {
                            {
                                Badge { Text(uiState.products.count { it.adminFlagged }.toString()) }
                            }
                        } else null
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Products List
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.filteredProducts.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.filteredProducts.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No products match filters", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.filteredProducts, key = { it.productId }) { product ->
                            AdminProductItem(
                                product = product,
                                isProcessing = uiState.processingId == product.productId,
                                onFlag = { reason -> viewModel.flagProduct(product.productId, reason) },
                                onUnflag = { viewModel.unflagProduct(product.productId) },
                                onHide = { viewModel.hideProduct(product.productId) },
                                onRestore = { viewModel.restoreProduct(product.productId) },
                                onDelete = { viewModel.deleteProduct(product.productId) },
                                onSuspendSeller = { reason -> viewModel.suspendSeller(product.sellerId, reason) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductItem(
    product: ProductEntity,
    isProcessing: Boolean,
    onFlag: (String) -> Unit,
    onUnflag: () -> Unit,
    onHide: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    onSuspendSeller: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showFlagDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuspendSellerDialog by remember { mutableStateOf(false) }
    var flagReason by remember { mutableStateOf("") }
    var suspendReason by remember { mutableStateOf("") }

    val isHidden = product.status == "private" || product.status == "hidden"

    ListItem(
        headlineContent = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.name, fontWeight = FontWeight.Bold)
                if (isProcessing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }
        },
        supportingContent = {
            Column {
                Text("Seller: ${product.sellerId.take(8)}...", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Status badge
                    StatusChip(
                        text = product.status.uppercase(),
                        color = when (product.status) {
                            "active" -> Color(0xFF4CAF50)
                            "private", "hidden" -> Color.Gray
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    
                    // Flagged badge
                    if (product.adminFlagged) {
                        StatusChip(text = "FLAGGED", color = MaterialTheme.colorScheme.error)
                    }
                }
                if (product.adminFlagged && !product.moderationNote.isNullOrBlank()) {
                    Text(
                        "Reason: ${product.moderationNote}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        leadingContent = {
            if (product.adminFlagged) {
                Icon(Icons.Default.Warning, contentDescription = "Flagged", tint = MaterialTheme.colorScheme.error)
            } else if (isHidden) {
                Icon(Icons.Default.VisibilityOff, contentDescription = "Hidden", tint = Color.Gray)
            } else {
                Icon(Icons.Default.Visibility, contentDescription = "Active", tint = Color(0xFF4CAF50))
            }
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showMenu = true }, enabled = !isProcessing) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    // Flag/Unflag
                    if (product.adminFlagged) {
                        DropdownMenuItem(
                            text = { Text("Remove Flag") },
                            onClick = {
                                showMenu = false
                                onUnflag()
                            },
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50)) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Flag Product") },
                            onClick = {
                                showMenu = false
                                showFlagDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                    
                    // Hide/Restore
                    if (isHidden) {
                        DropdownMenuItem(
                            text = { Text("Restore to Active") },
                            onClick = {
                                showMenu = false
                                onRestore()
                            },
                            leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Hide Product") },
                            onClick = {
                                showMenu = false
                                onHide()
                            },
                            leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) }
                        )
                    }
                    
                    HorizontalDivider()
                    
                    // Suspend Seller
                    DropdownMenuItem(
                        text = { Text("Suspend Seller", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            showSuspendSellerDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                    
                    // Delete
                    DropdownMenuItem(
                        text = { Text("Delete Product", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    )

    // Flag Dialog
    if (showFlagDialog) {
        AlertDialog(
            onDismissRequest = { showFlagDialog = false },
            title = { Text("Flag Product") },
            text = {
                Column {
                    Text("Select reason or enter custom:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Inappropriate content", "Misleading information", "Suspected fraud", "Policy violation").forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = flagReason == reason,
                                onClick = { flagReason = reason }
                            )
                            Text(reason)
                        }
                    }
                    OutlinedTextField(
                        value = if (flagReason in listOf("Inappropriate content", "Misleading information", "Suspected fraud", "Policy violation")) "" else flagReason,
                        onValueChange = { flagReason = it },
                        label = { Text("Custom reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onFlag(flagReason)
                        showFlagDialog = false
                        flagReason = ""
                    },
                    enabled = flagReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Flag")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFlagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Product?") },
            text = { Text("This will permanently remove \"${product.name}\" from the marketplace. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Suspend Seller Dialog
    if (showSuspendSellerDialog) {
        AlertDialog(
            onDismissRequest = { showSuspendSellerDialog = false },
            title = { Text("Suspend Seller?") },
            text = {
                Column {
                    Text("This will prevent the seller from listing any products.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = suspendReason,
                        onValueChange = { suspendReason = it },
                        label = { Text("Reason for suspension") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSuspendSeller(suspendReason)
                        showSuspendSellerDialog = false
                        suspendReason = ""
                    },
                    enabled = suspendReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Suspend")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuspendSellerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
