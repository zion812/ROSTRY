package com.rio.rostry.ui.admin.commerce

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading && uiState.orders.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.orders.isEmpty()) {
                Text("No recent orders found.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(uiState.orders) { order ->
                        AdminOrderItem(
                            order = order,
                            onCancel = { reason -> viewModel.cancelOrder(order.orderId, reason) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderItem(
    order: OrderEntity,
    onCancel: (String) -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    ListItem(
        headlineContent = { Text("Order #${order.orderId.take(8).uppercase()}", fontWeight = FontWeight.Bold) },
        supportingContent = {
            Column {
                Text("Amount: ${order.totalAmount} ${order.paymentMethod ?: ""}")
                Text("Status: ${order.status}")
                Text("Date: ${dateFormatter.format(Date(order.orderDate))}", style = MaterialTheme.typography.bodySmall)
                if (order.status == "CANCELLED") {
                    Text(
                        "CANCELLED: ${order.cancellationReason ?: "No reason"}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        trailingContent = {
            if (order.status != "CANCELLED" && order.status != "DELIVERED" && order.status != "REFUNDED") {
                Button(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    )

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Force Cancel Order") },
            text = {
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = { cancelReason = it },
                    label = { Text("Admin Reason") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancel(cancelReason)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Force Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Abort")
                }
            }
        )
    }
}
