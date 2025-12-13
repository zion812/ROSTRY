package com.rio.rostry.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.OrderStatus
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.rio.rostry.domain.model.PaymentStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    onOrderCancelled: () -> Unit = {},
    onRateOrder: (String) -> Unit,
    onContactSupport: () -> Unit,
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }
    var review by remember { mutableStateOf("") }
    var showSubmitBillDialog by remember { mutableStateOf(false) }
    var showUploadSlipDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OrderTrackingEvent.OrderCancelled -> {
                    snackbarHostState.showSnackbar("Order cancelled successfully")
                    onOrderCancelled()
                }
                is OrderTrackingEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is OrderTrackingEvent.InvoiceDownloaded -> {
                    // Handle invoice download (e.g. open intent)
                    snackbarHostState.showSnackbar("Invoice downloaded")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Order Tracking") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.order != null) {
            val order = uiState.order!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Order Timeline
                item {
                    Text("Order Timeline", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OrderTimeline(order.status, order.timelineEvents)
                }

                // Order Details
                item {
                    Text("Order Details", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            order.items.forEach { item ->
                                OrderItemRow(item)
                                Divider()
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("₹${"%.2f".format(order.total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Delivery Info
                item {
                    Text("Delivery Information", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Delivery Address", fontWeight = FontWeight.Bold)
                            Text(order.deliveryAddress)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Estimated Delivery", fontWeight = FontWeight.Bold)
                            Text(order.estimatedDeliveryDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "TBD")
                        }
                    }
                }

                // Payment Info
                item {
                    Text("Payment Information", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Payment Method", fontWeight = FontWeight.Bold)
                            Text(order.paymentMethod)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Payment Status", fontWeight = FontWeight.Bold)
                            val paymentStatusEnum = PaymentStatus.fromString(order.paymentStatus)
                            val isPaid = paymentStatusEnum.isPaid
                            Text(order.paymentStatus, color = if (isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Seller Info
                item {
                    Text("Seller Information", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Seller", fontWeight = FontWeight.Bold)
                            Text(order.sellerName)
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${order.sellerPhone}")
                                }
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Phone, contentDescription = "Call Seller")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Contact Seller")
                            }
                        }
                    }
                }

                // Actions
                item {
                    Text("Actions", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Seller Actions
                        if (order.isSeller) {
                            if (order.negotiationStatus == "REQUESTED") {
                                Button(onClick = { viewModel.acceptOrder(orderId) }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Accept Order Request")
                                }
                            }
                            if (order.status == OrderTrackingViewModel.UiOrderStatus.PLACED || order.paymentStatus == "pending") {
                                Button(onClick = { showSubmitBillDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Submit Bill")
                                }
                            }
                            if (order.paymentStatus == "submitted") {
                                Button(onClick = { viewModel.confirmPayment(orderId) }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Confirm Payment")
                                }
                            }
                        }

                        // Buyer Actions
                        if (order.isBuyer) {
                            if (order.paymentStatus == "pending" && order.total > 0) {
                                Button(onClick = { showUploadSlipDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Upload Payment Slip")
                                }
                            }
                        }

                        if (order.canCancel) {
                            OutlinedButton(onClick = { showCancelDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Cancel Order")
                            }
                        }
                        if (order.status == OrderTrackingViewModel.UiOrderStatus.DELIVERED) {
                            Button(onClick = { showRateDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Rate & Review")
                            }
                        }
                        OutlinedButton(onClick = { viewModel.downloadInvoice() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Download Invoice")
                        }
                        OutlinedButton(onClick = onContactSupport, modifier = Modifier.fillMaxWidth()) {
                            Text("Contact Support")
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Order not found")
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelOrder("User requested")
                    showCancelDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            title = { Text("Rate & Review") },
            text = {
                Column {
                    Text("Rating:")
                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    if (i <= rating) Icons.Filled.Star else Icons.Default.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= rating) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = review,
                        onValueChange = { review = it },
                        label = { Text("Review (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.submitRating(orderId, rating, review)
                    onRateOrder(orderId)
                    showRateDialog = false
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSubmitBillDialog) {
        SubmitBillDialog(
            initialAmount = uiState.order?.total ?: 0.0,
            onDismiss = { showSubmitBillDialog = false },
            onSubmit = { amount, uri ->
                viewModel.submitBill(orderId, amount, uri)
                showSubmitBillDialog = false
            }
        )
    }

    if (showUploadSlipDialog) {
        UploadSlipDialog(
            onDismiss = { showUploadSlipDialog = false },
            onSubmit = { uri ->
                viewModel.uploadPaymentSlip(orderId, uri)
                showUploadSlipDialog = false
            }
        )
    }
}

@Composable
fun OrderTimeline(currentStatus: OrderTrackingViewModel.UiOrderStatus, events: List<OrderTrackingViewModel.TimelineEvent>) {
    val statuses = listOf(
        OrderTrackingViewModel.UiOrderStatus.PLACED to "Order Placed",
        OrderTrackingViewModel.UiOrderStatus.CONFIRMED to "Order Confirmed",
        OrderTrackingViewModel.UiOrderStatus.PROCESSING to "Processing",
        OrderTrackingViewModel.UiOrderStatus.OUT_FOR_DELIVERY to "Out for Delivery",
        OrderTrackingViewModel.UiOrderStatus.DELIVERED to "Delivered"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        statuses.forEachIndexed { index, (status, label) ->
            val event = events.find { it.status == status }
            val isCompleted = currentStatus.ordinal >= status.ordinal
            val color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                status == currentStatus -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val icon = when (status) {
                OrderTrackingViewModel.UiOrderStatus.PLACED -> Icons.Default.ShoppingCart
                OrderTrackingViewModel.UiOrderStatus.CONFIRMED -> Icons.Default.Check
                OrderTrackingViewModel.UiOrderStatus.PROCESSING -> Icons.Default.Build
                OrderTrackingViewModel.UiOrderStatus.OUT_FOR_DELIVERY -> Icons.Default.LocalShipping
                OrderTrackingViewModel.UiOrderStatus.DELIVERED -> Icons.Default.CheckCircle
                OrderTrackingViewModel.UiOrderStatus.CANCELLED -> Icons.Default.Cancel
                OrderTrackingViewModel.UiOrderStatus.REFUNDED -> Icons.Default.Receipt
            }
            TimelineItem(
                title = label,
                subtitle = event?.timestamp?.let { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(it) } ?: "",
                icon = icon,
                color = color,
                isCompleted = isCompleted
            )
        }
    }
}

@Composable
fun TimelineItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isCompleted: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderTrackingViewModel.OrderItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = "Product image",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Qty: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
        }
        Text("₹${"%.2f".format(item.price)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SubmitBillDialog(
    initialAmount: Double,
    onDismiss: () -> Unit,
    onSubmit: (Double, String?) -> Unit
) {
    var amount by remember { mutableStateOf(initialAmount.toString()) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit Bill") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Total Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcher.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (imageUri != null) "Bill Image Selected" else "Select Bill Image")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                onSubmit(amt, imageUri)
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UploadSlipDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var imageUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Payment Slip") },
        text = {
            Column {
                Text("Please upload a screenshot of your payment.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcher.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (imageUri != null) "Slip Image Selected" else "Select Image")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (imageUri != null) onSubmit(imageUri!!)
                },
                enabled = imageUri != null
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
