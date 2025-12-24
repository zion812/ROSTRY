package com.rio.rostry.ui.order.evidence

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.OrderQuoteEntity
import com.rio.rostry.domain.model.OrderDeliveryType
import com.rio.rostry.domain.model.OrderPaymentType
import java.text.NumberFormat
import java.util.*

/**
 * Quote Negotiation Screen - Where buyer and seller agree on price.
 * Shows the 2-step pricing agreement flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteNegotiationScreen(
    quoteId: String,
    isBuyer: Boolean,
    onNavigateBack: () -> Unit,
    onProceedToPayment: (String) -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    val quote = uiState.currentQuote
    val quoteForm = uiState.quoteForm

    var showCounterOfferDialog by remember { mutableStateOf(false) }

    // Colors
    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6366F1),
            Color(0xFF8B5CF6)
        )
    )
    val successColor = Color(0xFF10B981)
    val warningColor = Color(0xFFF59E0B)

    LaunchedEffect(successMessage) {
        if (successMessage != null && quote?.status == "LOCKED") {
            onProceedToPayment(quote.orderId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isBuyer) "Quote Details" else "Send Quote",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
            // Status Banner
            item {
                quote?.let { q ->
                    QuoteStatusBanner(
                        status = q.status,
                        buyerAgreed = q.buyerAgreedAt != null,
                        sellerAgreed = q.sellerAgreedAt != null
                    )
                }
            }

            // Product Info Card
            item {
                quote?.let { q ->
                    ProductInfoCard(
                        productName = q.productName,
                        quantity = q.quantity,
                        unit = q.unit
                    )
                }
            }

            // Price Breakdown Card (for buyer viewing quote)
            if (isBuyer && quote?.status != "DRAFT") {
                item {
                    PriceBreakdownCard(quote = quote)
                }
            }

            // Seller Quote Form
            if (!isBuyer && (quote?.status == "DRAFT" || quote?.status == "NEGOTIATING")) {
                item {
                    SellerQuoteForm(
                        form = quoteForm,
                        quantity = quote.quantity,
                        onFormChange = { viewModel.updateQuoteForm(it) },
                        onSendQuote = { viewModel.sendQuote(quoteId) },
                        isLoading = isLoading
                    )
                }
            }

            // Delivery Info
            item {
                quote?.let { q ->
                    DeliveryInfoCard(
                        deliveryType = OrderDeliveryType.fromString(q.deliveryType),
                        address = q.deliveryAddress,
                        distance = q.deliveryDistance
                    )
                }
            }

            // Payment Preference
            item {
                quote?.let { q ->
                    PaymentPreferenceCard(
                        paymentType = OrderPaymentType.fromString(q.paymentType),
                        advanceAmount = q.advanceAmount,
                        balanceAmount = q.balanceAmount
                    )
                }
            }

            // Notes
            if (quote?.buyerNotes != null || quote?.sellerNotes != null) {
                item {
                    NotesCard(
                        buyerNotes = quote.buyerNotes,
                        sellerNotes = quote.sellerNotes
                    )
                }
            }

            // Action Buttons
            item {
                quote?.let { q ->
                    ActionButtonsSection(
                        quote = q,
                        isBuyer = isBuyer,
                        currentUserId = currentUserId,
                        isLoading = isLoading,
                        onAgree = { viewModel.agreeToQuote(quoteId, isBuyer) },
                        onCounterOffer = { showCounterOfferDialog = true }
                    )
                }
            }

            // Agreement Status
            item {
                quote?.let { q ->
                    AgreementStatusCard(
                        buyerAgreed = q.buyerAgreedAt != null,
                        sellerAgreed = q.sellerAgreedAt != null,
                        isLocked = q.status == "LOCKED",
                        lockedAt = q.lockedAt
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Error/Success Snackbar
    error?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    // Counter Offer Dialog
    if (showCounterOfferDialog) {
        CounterOfferDialog(
            currentPrice = quote?.finalTotal ?: 0.0,
            onDismiss = { showCounterOfferDialog = false },
            onSubmit = { newPrice, newDelivery, notes ->
                viewModel.counterOffer(quoteId, newPrice, newDelivery, notes)
                showCounterOfferDialog = false
            }
        )
    }
}

@Composable
private fun QuoteStatusBanner(
    status: String,
    buyerAgreed: Boolean,
    sellerAgreed: Boolean
) {
    val (backgroundColor, icon, text) = when (status) {
        "DRAFT" -> Triple(Color(0xFFFEF3C7), Icons.Default.Edit, "Draft - Waiting for seller quote")
        "SENT" -> Triple(Color(0xFFDCFCE7), Icons.Default.Send, "Quote sent - Awaiting response")
        "NEGOTIATING" -> Triple(Color(0xFFFEF3C7), Icons.Default.SwapHoriz, "Negotiating - Counter offer made")
        "BUYER_AGREED" -> Triple(Color(0xFFDCFCE7), Icons.Default.CheckCircle, "Buyer agreed - Waiting for seller")
        "SELLER_AGREED" -> Triple(Color(0xFFDCFCE7), Icons.Default.CheckCircle, "Seller agreed - Waiting for buyer")
        "LOCKED" -> Triple(Color(0xFF10B981), Icons.Default.Lock, "Agreement Locked! Price is final")
        "EXPIRED" -> Triple(Color(0xFFFEE2E2), Icons.Default.Warning, "Quote expired")
        else -> Triple(Color(0xFFF3F4F6), Icons.Default.Info, status)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (status == "LOCKED") Color.White else Color(0xFF374151)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (status == "LOCKED") Color.White else Color(0xFF374151)
            )
        }
    }
}

@Composable
private fun ProductInfoCard(
    productName: String,
    quantity: Double,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${quantity.toInt()} $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PriceBreakdownCard(quote: OrderQuoteEntity?) {
    if (quote == null) return
    
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Price Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            PriceRow("Base Price", quote.basePrice, "per unit")
            PriceRow("Quantity", quote.quantity, quote.unit)
            Divider()
            PriceRow("Product Total", quote.totalProductPrice)
            PriceRow("Delivery Charge", quote.deliveryCharge)
            if (quote.packingCharge > 0) {
                PriceRow("Packing Charge", quote.packingCharge)
            }
            if (quote.discount > 0) {
                PriceRow("Discount", -quote.discount, isDiscount = true)
            }
            Divider(thickness = 2.dp)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Final Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currencyFormat.format(quote.finalTotal),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    amount: Double,
    suffix: String = "",
    isDiscount: Boolean = false
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (suffix.isNotEmpty()) {
                "${currencyFormat.format(amount)} / $suffix"
            } else {
                currencyFormat.format(amount)
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isDiscount) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SellerQuoteForm(
    form: QuoteFormState,
    quantity: Double,
    onFormChange: (QuoteFormState) -> Unit,
    onSendQuote: () -> Unit,
    isLoading: Boolean
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val calculatedTotal = (form.basePrice * quantity) + form.deliveryCharge + form.packingCharge

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F9FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set Your Price",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0369A1)
            )

            OutlinedTextField(
                value = if (form.basePrice > 0) form.basePrice.toString() else "",
                onValueChange = {
                    onFormChange(form.copy(basePrice = it.toDoubleOrNull() ?: 0.0))
                },
                label = { Text("Base Price (per unit)") },
                leadingIcon = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = if (form.deliveryCharge > 0) form.deliveryCharge.toString() else "",
                onValueChange = {
                    onFormChange(form.copy(deliveryCharge = it.toDoubleOrNull() ?: 0.0))
                },
                label = { Text("Delivery Charge") },
                leadingIcon = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = if (form.packingCharge > 0) form.packingCharge.toString() else "",
                onValueChange = {
                    onFormChange(form.copy(packingCharge = it.toDoubleOrNull() ?: 0.0))
                },
                label = { Text("Packing Charge (optional)") },
                leadingIcon = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = form.notes ?: "",
                onValueChange = {
                    onFormChange(form.copy(notes = it.ifBlank { null }))
                },
                label = { Text("Notes for buyer (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            // Calculated Total
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0369A1))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Quote",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = currencyFormat.format(calculatedTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = onSendQuote,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = form.basePrice > 0 && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
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
                    Text("Send Quote to Buyer", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DeliveryInfoCard(
    deliveryType: OrderDeliveryType,
    address: String?,
    distance: Double?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (deliveryType) {
                        OrderDeliveryType.SELLER_DELIVERY -> Icons.Default.LocalShipping
                        OrderDeliveryType.BUYER_PICKUP -> Icons.Default.DirectionsWalk
                        OrderDeliveryType.THIRD_PARTY -> Icons.Default.Flight
                    },
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delivery",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = deliveryType.displayName,
                style = MaterialTheme.typography.bodyLarge
            )

            address?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            distance?.let {
                Text(
                    text = "Distance: ${String.format("%.1f", it)} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PaymentPreferenceCard(
    paymentType: OrderPaymentType,
    advanceAmount: Double?,
    balanceAmount: Double?
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = Color(0xFF10B981)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = paymentType.displayName,
                style = MaterialTheme.typography.bodyLarge
            )

            if (paymentType.requiresAdvance && advanceAmount != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Advance Required:", fontWeight = FontWeight.Medium)
                    Text(
                        currencyFormat.format(advanceAmount),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }
            }

            if (balanceAmount != null && balanceAmount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Balance on Delivery:", fontWeight = FontWeight.Medium)
                    Text(
                        currencyFormat.format(balanceAmount),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesCard(
    buyerNotes: String?,
    sellerNotes: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            buyerNotes?.let {
                Column {
                    Text(
                        text = "From Buyer:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            sellerNotes?.let {
                Column {
                    Text(
                        text = "From Seller:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    quote: OrderQuoteEntity,
    isBuyer: Boolean,
    currentUserId: String?,
    isLoading: Boolean,
    onAgree: () -> Unit,
    onCounterOffer: () -> Unit
) {
    val canAgree = when {
        quote.status == "LOCKED" -> false
        isBuyer && quote.buyerAgreedAt != null -> false
        !isBuyer && quote.sellerAgreedAt != null -> false
        quote.status == "DRAFT" -> false
        else -> true
    }

    val canCounterOffer = isBuyer && 
        quote.status in listOf("SENT", "NEGOTIATING", "SELLER_AGREED") &&
        quote.buyerAgreedAt == null

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (canAgree) {
            Button(
                onClick = onAgree,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I Agree to This Quote", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (canCounterOffer) {
            OutlinedButton(
                onClick = onCounterOffer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Make Counter Offer", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AgreementStatusCard(
    buyerAgreed: Boolean,
    sellerAgreed: Boolean,
    isLocked: Boolean,
    lockedAt: Long?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Agreement Locked!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "The price is now final and cannot be changed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Agreement Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AgreementStatusItem(
                        label = "Buyer",
                        agreed = buyerAgreed
                    )
                    AgreementStatusItem(
                        label = "Seller",
                        agreed = sellerAgreed
                    )
                }

                Text(
                    text = "Both parties must agree to lock the price",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AgreementStatusItem(
    label: String,
    agreed: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (agreed) Color(0xFF10B981) else Color(0xFFE5E7EB)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (agreed) Icons.Default.Check else Icons.Default.HourglassEmpty,
                contentDescription = null,
                tint = if (agreed) Color.White else Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = if (agreed) "Agreed ✓" else "Pending...",
            style = MaterialTheme.typography.labelSmall,
            color = if (agreed) Color(0xFF10B981) else Color(0xFF9CA3AF)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterOfferDialog(
    currentPrice: Double,
    onDismiss: () -> Unit,
    onSubmit: (Double?, Double?, String?) -> Unit
) {
    var newPrice by remember { mutableStateOf("") }
    var newDelivery by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Make Counter Offer", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Current total: ₹${String.format("%.0f", currentPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = newPrice,
                    onValueChange = { newPrice = it },
                    label = { Text("New price per unit (optional)") },
                    leadingIcon = { Text("₹") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newDelivery,
                    onValueChange = { newDelivery = it },
                    label = { Text("New delivery charge (optional)") },
                    leadingIcon = { Text("₹") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reason for counter offer") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        newPrice.toDoubleOrNull(),
                        newDelivery.toDoubleOrNull(),
                        notes.ifBlank { null }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Text("Send Counter Offer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
