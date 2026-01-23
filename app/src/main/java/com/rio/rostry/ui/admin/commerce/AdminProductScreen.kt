package com.rio.rostry.ui.admin.commerce

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(
    viewModel: AdminProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Side Effect for Toasts
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
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.products.isEmpty()) {
                Text(
                    "No products found.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn {
                    items(uiState.products) { product ->
                        AdminProductItem(
                            product = product,
                            onFlag = { reason -> viewModel.flagProduct(product.productId, reason) },
                            onHide = { viewModel.hideProduct(product.productId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductItem(
    product: ProductEntity,
    onFlag: (String) -> Unit,
    onHide: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showFlagDialog by remember { mutableStateOf(false) }
    var flagReason by remember { mutableStateOf("") }

    ListItem(
        headlineContent = { Text(product.name, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Column {
                Text("Seller ID: ${product.sellerId.take(8)}...")
                if (product.adminFlagged) {
                    Text(
                        "FLAGGED: ${product.moderationNote ?: "No reason"}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        leadingContent = {
            if (product.adminFlagged) {
                 Icon(Icons.Default.Warning, contentDescription = "Flagged", tint = MaterialTheme.colorScheme.error)
            }
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Flag Product") },
                        onClick = {
                            showMenu = false
                            showFlagDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hide Product (Make Private)") },
                        onClick = {
                            showMenu = false
                            onHide()
                        }
                    )
                }
            }
        }
    )

    if (showFlagDialog) {
        AlertDialog(
            onDismissRequest = { showFlagDialog = false },
            title = { Text("Flag Product") },
            text = {
                OutlinedTextField(
                    value = flagReason,
                    onValueChange = { flagReason = it },
                    label = { Text("Reason") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onFlag(flagReason)
                    showFlagDialog = false
                }) {
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
}
