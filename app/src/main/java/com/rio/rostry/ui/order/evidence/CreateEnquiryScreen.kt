package com.rio.rostry.ui.order.evidence

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.OrderDeliveryType
import com.rio.rostry.domain.model.OrderPaymentType

/**
 * Create Enquiry Screen - Buyer initiates an order by sending enquiry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEnquiryScreen(
    productId: String,
    productName: String,
    sellerId: String,
    sellerName: String,
    basePrice: Double,
    onNavigateBack: () -> Unit,
    onEnquirySent: (String) -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val form = uiState.enquiryForm

    var showDeliveryTypeSheet by remember { mutableStateOf(false) }
    var showPaymentTypeSheet by remember { mutableStateOf(false) }

    LaunchedEffect(successMessage) {
        if (successMessage?.contains("Enquiry sent") == true) {
            uiState.currentQuote?.let { quote ->
                kotlinx.coroutines.delay(1000)
                onEnquirySent(quote.quoteId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Send Enquiry", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product Info Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = productName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "by $sellerName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "₹${String.format("%.0f", basePrice)} / unit",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Quantity Section
            item {
                Text(
                    text = "How many do you want?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = if (form.quantity > 0) form.quantity.toString() else "",
                                onValueChange = {
                                    viewModel.updateEnquiryForm(
                                        form.copy(quantity = it.toDoubleOrNull() ?: 0.0)
                                    )
                                },
                                label = { Text("Quantity") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Unit selector
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = form.unit,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Unit") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier.menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    listOf("BIRDS", "KG", "TRAYS", "PCS").forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                viewModel.updateEnquiryForm(form.copy(unit = unit))
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Estimated total
                        val estimatedTotal = form.quantity * basePrice
                        if (estimatedTotal > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFDCFCE7)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Estimated Total:",
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        text = "₹${String.format("%.0f", estimatedTotal)}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Delivery Type Section
            item {
                Text(
                    text = "How do you want it delivered?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                DeliveryTypeSelector(
                    selected = form.deliveryType,
                    onSelect = { viewModel.updateEnquiryForm(form.copy(deliveryType = it)) }
                )
            }

            // Address (if delivery selected)
            if (form.deliveryType == OrderDeliveryType.SELLER_DELIVERY) {
                item {
                    OutlinedTextField(
                        value = form.deliveryAddress ?: "",
                        onValueChange = {
                            viewModel.updateEnquiryForm(
                                form.copy(deliveryAddress = it.ifBlank { null })
                            )
                        },
                        label = { Text("Delivery Address") },
                        placeholder = { Text("Enter your full delivery address") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }
            }

            // Payment Preference Section
            item {
                Text(
                    text = "How do you want to pay?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                PaymentTypeSelector(
                    selected = form.paymentPreference,
                    onSelect = { viewModel.updateEnquiryForm(form.copy(paymentPreference = it)) }
                )
            }

            // Notes
            item {
                OutlinedTextField(
                    value = form.notes ?: "",
                    onValueChange = {
                        viewModel.updateEnquiryForm(form.copy(notes = it.ifBlank { null }))
                    },
                    label = { Text("Additional Notes (optional)") },
                    placeholder = { Text("Any special requirements or questions?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        viewModel.createEnquiry(
                            sellerId = sellerId,
                            productId = productId,
                            productName = productName
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = form.quantity > 0 && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Enquiry", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Error/Success Messages
            error?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, null, tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(it, color = Color(0xFFDC2626))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun DeliveryTypeSelector(
    selected: OrderDeliveryType,
    onSelect: (OrderDeliveryType) -> Unit
) {
    val options = listOf(
        Triple(OrderDeliveryType.SELLER_DELIVERY, Icons.Default.LocalShipping, "Seller Delivers"),
        Triple(OrderDeliveryType.BUYER_PICKUP, Icons.Default.DirectionsWalk, "I'll Pick Up"),
        Triple(OrderDeliveryType.THIRD_PARTY, Icons.Default.Flight, "Third Party")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(options) { (type, icon, label) ->
            val isSelected = selected == type

            Card(
                modifier = Modifier
                    .width(120.dp)
                    .clickable { onSelect(type) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF6366F1) else Color(0xFFF3F4F6)
                ),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                    1.dp, Color(0xFFE5E7EB)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Color(0xFF6B7280),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White else Color(0xFF374151),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentTypeSelector(
    selected: OrderPaymentType,
    onSelect: (OrderPaymentType) -> Unit
) {
    val options = listOf(
        PaymentOption(OrderPaymentType.COD, Icons.Default.Payments, "Cash on Delivery", "Pay when you receive"),
        PaymentOption(OrderPaymentType.FULL_ADVANCE, Icons.Default.AccountBalance, "Full Advance", "100% upfront payment"),
        PaymentOption(OrderPaymentType.SPLIT_50_50, Icons.Default.Percent, "50% + 50%", "Half now, half on delivery"),
        PaymentOption(OrderPaymentType.BUYER_PICKUP, Icons.Default.Store, "Pay at Pickup", "Pay when you collect")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected == option.type

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option.type) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFE0E7FF) else Color(0xFFF9FAFB)
                ),
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6366F1))
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                }
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
                        tint = if (isSelected) Color(0xFF6366F1) else Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color(0xFF3730A3) else Color(0xFF374151)
                        )
                        Text(
                            text = option.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF6366F1)
                        )
                    }
                }
            }
        }
    }
}

private data class PaymentOption(
    val type: OrderPaymentType,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val name: String,
    val description: String
)
