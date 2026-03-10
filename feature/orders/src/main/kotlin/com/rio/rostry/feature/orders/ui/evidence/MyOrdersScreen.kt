package com.rio.rostry.ui.order.evidence

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.OrderPaymentEntity
import com.rio.rostry.data.database.entity.OrderQuoteEntity
import com.rio.rostry.domain.model.EvidenceOrderStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * My Orders Screen - Shows list of orders for buyer/seller with tabs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    isFarmer: Boolean, // true = seller view, false = buyer view
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    onQuoteClick: (String, Boolean) -> Unit,
    onPaymentVerify: (String) -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val buyerQuotes by viewModel.buyerQuotes.collectAsState()
    val sellerQuotes by viewModel.sellerQuotes.collectAsState()
    val paymentsToVerify by viewModel.paymentsToVerify.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    
    val tabs = if (isFarmer) {
        listOf("Incoming Orders", "To Verify", "All Orders")
    } else {
        listOf("Active Orders", "Quotes", "History")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isFarmer) "Manage Orders" else "My Orders",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(title)
                                if (isFarmer && index == 1 && paymentsToVerify.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge {
                                        Text(paymentsToVerify.size.toString())
                                    }
                                }
                            }
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    if (isFarmer) {
                        // Seller: Incoming orders (new enquiries)
                        QuotesList(
                            quotes = sellerQuotes.filter { 
                                it.status in listOf("DRAFT", "SENT", "NEGOTIATING") 
                            },
                            isBuyer = false,
                            onQuoteClick = { onQuoteClick(it, false) }
                        )
                    } else {
                        // Buyer: Active orders
                        QuotesList(
                            quotes = buyerQuotes.filter { 
                                it.status !in listOf("EXPIRED", "REJECTED") 
                            },
                            isBuyer = true,
                            onQuoteClick = { onQuoteClick(it, true) }
                        )
                    }
                }
                1 -> {
                    if (isFarmer) {
                        // Seller: Payments to verify
                        PaymentsToVerifyList(
                            payments = paymentsToVerify,
                            onVerifyClick = onPaymentVerify
                        )
                    } else {
                        // Buyer: Quotes (negotiations)
                        QuotesList(
                            quotes = buyerQuotes.filter { 
                                it.status in listOf("SENT", "NEGOTIATING", "BUYER_AGREED", "SELLER_AGREED") 
                            },
                            isBuyer = true,
                            onQuoteClick = { onQuoteClick(it, true) }
                        )
                    }
                }
                2 -> {
                    // All/History
                    QuotesList(
                        quotes = if (isFarmer) sellerQuotes else buyerQuotes,
                        isBuyer = !isFarmer,
                        onQuoteClick = { onQuoteClick(it, !isFarmer) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuotesList(
    quotes: List<OrderQuoteEntity>,
    isBuyer: Boolean,
    onQuoteClick: (String) -> Unit
) {
    if (quotes.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Inbox,
            title = "No orders yet",
            message = if (isBuyer) "Your orders will appear here" else "New enquiries will appear here"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quotes, key = { it.quoteId }) { quote ->
                OrderCard(
                    quote = quote,
                    isBuyer = isBuyer,
                    onClick = { onQuoteClick(quote.quoteId) }
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    quote: OrderQuoteEntity,
    isBuyer: Boolean,
    onClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    
    val status = EvidenceOrderStatus.fromString(quote.status)
    
    val statusColor = when (status) {
        EvidenceOrderStatus.ENQUIRY, EvidenceOrderStatus.QUOTE_SENT -> Color(0xFFF59E0B)
        EvidenceOrderStatus.AGREEMENT_LOCKED, EvidenceOrderStatus.PAYMENT_VERIFIED -> Color(0xFF10B981)
        EvidenceOrderStatus.ADVANCE_PENDING -> Color(0xFFDC2626)
        EvidenceOrderStatus.DISPATCHED, EvidenceOrderStatus.PREPARING -> Color(0xFF6366F1)
        EvidenceOrderStatus.COMPLETED -> Color(0xFF10B981)
        EvidenceOrderStatus.CANCELLED, EvidenceOrderStatus.EXPIRED -> Color(0xFF6B7280)
        else -> Color(0xFF6366F1)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = quote.productName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${quote.quantity.toInt()} ${quote.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = status.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (quote.finalTotal > 0) currencyFormat.format(quote.finalTotal) else "Pending",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = dateFormat.format(Date(quote.createdAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Show action hint
                    val actionHint = when {
                        !isBuyer && quote.status == "DRAFT" -> "Send quote →"
                        isBuyer && quote.status == "SENT" -> "Review quote →"
                        quote.status == "LOCKED" -> "Proceed →"
                        else -> "View details →"
                    }
                    Text(
                        text = actionHint,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6366F1),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentsToVerifyList(
    payments: List<OrderPaymentEntity>,
    onVerifyClick: (String) -> Unit
) {
    if (payments.isEmpty()) {
        EmptyState(
            icon = Icons.Default.CheckCircle,
            title = "All caught up!",
            message = "No pending payments to verify"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(payments, key = { it.paymentId }) { payment ->
                PaymentVerifyCard(
                    payment = payment,
                    onVerifyClick = { onVerifyClick(payment.paymentId) }
                )
            }
        }
    }
}

@Composable
private fun PaymentVerifyCard(
    payment: OrderPaymentEntity,
    onVerifyClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD97706)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${payment.paymentPhase} Payment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = dateFormat.format(Date(payment.updatedAt)),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Text(
                    text = currencyFormat.format(payment.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            payment.transactionRef?.let { ref ->
                Row {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ref: $ref",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB45309)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onVerifyClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verify", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF9CA3AF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
