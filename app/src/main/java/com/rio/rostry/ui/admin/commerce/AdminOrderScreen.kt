package com.rio.rostry.ui.admin.commerce

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.OrderEntity
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(
    viewModel: AdminOrderViewModel = hiltViewModel(),
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
                title = { Text("Order Intervention") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshOrders() }) {
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
                placeholder = { Text("Search by order ID, buyer, seller...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Status Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AdminOrderViewModel.OrderFilter.values().toList()) { filter ->
                    val count = when (filter) {
                        AdminOrderViewModel.OrderFilter.ALL -> uiState.orders.size
                        AdminOrderViewModel.OrderFilter.PENDING -> uiState.orders.count { it.status == "PENDING" }
                        AdminOrderViewModel.OrderFilter.PROCESSING -> uiState.orders.count { it.status == "PROCESSING" }
                        AdminOrderViewModel.OrderFilter.SHIPPED -> uiState.orders.count { it.status == "SHIPPED" }
                        AdminOrderViewModel.OrderFilter.DELIVERED -> uiState.orders.count { it.status == "DELIVERED" }
                        AdminOrderViewModel.OrderFilter.CANCELLED -> uiState.orders.count { it.status == "CANCELLED" }
                        AdminOrderViewModel.OrderFilter.REFUNDED -> uiState.orders.count { it.status == "REFUNDED" }
                    }
                    FilterChip(
                        selected = uiState.currentFilter == filter,
                        onClick = { viewModel.onFilterChanged(filter) },
                        label = { 
                            Text(
                                when (filter) {
                                    AdminOrderViewModel.OrderFilter.ALL -> "All ($count)"
                                    else -> "${filter.name.lowercase().replaceFirstChar { it.uppercase() }} ($count)"
                                }
                            ) 
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Orders List
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.filteredOrders.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.filteredOrders.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No orders match filters", color = Color.Gray)
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                        items(uiState.filteredOrders, key = { it.orderId }) { order ->
                            AdminOrderItem(
                                order = order,
                                isProcessing = uiState.processingId == order.orderId,
                                onCancel = { reason -> viewModel.cancelOrder(order.orderId, reason) },
                                onRefund = { reason -> viewModel.refundOrder(order.orderId, reason) },
                                onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.orderId, newStatus) },
                                onForceComplete = { viewModel.forceComplete(order.orderId) }
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
fun AdminOrderItem(
    order: OrderEntity,
    isProcessing: Boolean,
    onCancel: (String) -> Unit,
    onRefund: (String) -> Unit,
    onUpdateStatus: (String) -> Unit,
    onForceComplete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showRefundDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    var refundReason by remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    val isFinal = order.status in listOf("CANCELLED", "REFUNDED", "DELIVERED")

    ListItem(
        headlineContent = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Order #${order.orderId.take(8).uppercase()}", fontWeight = FontWeight.Bold)
                if (isProcessing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }
        },
        supportingContent = {
            Column {
                Text("₹${order.totalAmount}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("Buyer: ${order.buyerId?.take(8) ?: "N/A"}... | Seller: ${order.sellerId.take(8)}...", style = MaterialTheme.typography.bodySmall)
                Text("Date: ${dateFormatter.format(Date(order.orderDate))}", style = MaterialTheme.typography.labelSmall)
                
                Spacer(modifier = Modifier.height(4.dp))
                OrderStatusBadge(status = order.status)
                
                if (order.status == "CANCELLED" && !order.cancellationReason.isNullOrBlank()) {
                    Text(
                        "Reason: ${order.cancellationReason}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        leadingContent = {
            Icon(
                imageVector = when (order.status) {
                    "DELIVERED" -> Icons.Default.Check
                    "CANCELLED", "REFUNDED" -> Icons.Default.Close
                    "SHIPPED" -> Icons.Default.LocalShipping
                    else -> Icons.Default.Payment
                },
                contentDescription = null,
                tint = when (order.status) {
                    "DELIVERED" -> Color(0xFF4CAF50)
                    "CANCELLED" -> MaterialTheme.colorScheme.error
                    "REFUNDED" -> Color(0xFFFF9800)
                    "SHIPPED" -> Color(0xFF2196F3)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        },
        trailingContent = {
            if (!isFinal || order.status == "DELIVERED") {
                Box {
                    IconButton(onClick = { showMenu = true }, enabled = !isProcessing) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        if (!isFinal) {
                            // Change Status
                            DropdownMenuItem(
                                text = { Text("Change Status") },
                                onClick = {
                                    showMenu = false
                                    showStatusDialog = true
                                }
                            )
                            
                            // Force Complete
                            DropdownMenuItem(
                                text = { Text("Force Complete") },
                                onClick = {
                                    showMenu = false
                                    onForceComplete()
                                }
                            )
                            
                            HorizontalDivider()
                            
                            // Cancel
                            DropdownMenuItem(
                                text = { Text("Cancel Order", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    showCancelDialog = true
                                }
                            )
                        }
                        
                        // Refund (available for delivered orders too)
                        if (order.status != "REFUNDED" && order.status != "CANCELLED") {
                            DropdownMenuItem(
                                text = { Text("Refund", color = Color(0xFFFF9800)) },
                                onClick = {
                                    showMenu = false
                                    showRefundDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order") },
            text = {
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = { cancelReason = it },
                    label = { Text("Reason for cancellation") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancel(cancelReason)
                        showCancelDialog = false
                        cancelReason = ""
                    },
                    enabled = cancelReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancel Order")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Refund Dialog
    if (showRefundDialog) {
        AlertDialog(
            onDismissRequest = { showRefundDialog = false },
            title = { Text("Refund Order") },
            text = {
                Column {
                    Text("Issue a full refund to the buyer.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = refundReason,
                        onValueChange = { refundReason = it },
                        label = { Text("Reason for refund") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRefund(refundReason)
                        showRefundDialog = false
                        refundReason = ""
                    },
                    enabled = refundReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Refund")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRefundDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Status Change Dialog
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Change Order Status") },
            text = {
                Column {
                    AdminOrderViewModel.ORDER_STATUSES.filter { it != order.status }.forEach { status ->
                        TextButton(
                            onClick = {
                                onUpdateStatus(status)
                                showStatusDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(status)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (color, text) = when (status) {
        "PENDING" -> Color(0xFFFFC107) to "Pending"
        "PROCESSING" -> Color(0xFF2196F3) to "Processing"
        "SHIPPED" -> Color(0xFF9C27B0) to "Shipped"
        "DELIVERED" -> Color(0xFF4CAF50) to "Delivered"
        "CANCELLED" -> MaterialTheme.colorScheme.error to "Cancelled"
        "REFUNDED" -> Color(0xFFFF9800) to "Refunded"
        else -> MaterialTheme.colorScheme.primary to status
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
