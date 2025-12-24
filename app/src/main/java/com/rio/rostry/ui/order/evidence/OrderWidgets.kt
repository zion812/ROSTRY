package com.rio.rostry.ui.order.evidence

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.rio.rostry.data.database.entity.OrderPaymentEntity
import com.rio.rostry.data.database.entity.OrderQuoteEntity
import com.rio.rostry.domain.model.EvidenceOrderStatus
import java.text.NumberFormat
import java.util.*

/**
 * Dashboard widgets for Evidence-Based Order System.
 * Can be embedded in FarmerHomeScreen or SellerDashboard.
 */

/**
 * Widget showing pending payment verifications for seller.
 */
@Composable
fun PendingVerificationsWidget(
    payments: List<OrderPaymentEntity>,
    onViewAll: () -> Unit,
    onVerifyPayment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (payments.isEmpty()) return

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                    ),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pending,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Payments to Verify",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${payments.size} pending verification${if (payments.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    TextButton(
                        onClick = onViewAll,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("View All")
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(payments.take(3)) { payment ->
                        PendingPaymentChip(
                            amount = currencyFormat.format(payment.amount),
                            phase = payment.paymentPhase,
                            onClick = { onVerifyPayment(payment.paymentId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingPaymentChip(
    amount: String,
    phase: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = phase,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verify",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Widget showing incoming enquiries for seller.
 */
@Composable
fun IncomingEnquiriesWidget(
    quotes: List<OrderQuoteEntity>,
    onViewAll: () -> Unit,
    onQuoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingQuotes = quotes.filter { it.status == "DRAFT" }
    
    if (pendingQuotes.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0E7FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6366F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "New Enquiries",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3730A3)
                        )
                        Text(
                            text = "${pendingQuotes.size} waiting for your quote",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6366F1)
                        )
                    }
                }

                TextButton(
                    onClick = onViewAll,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF6366F1)
                    )
                ) {
                    Text("View All")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            pendingQuotes.take(3).forEach { quote ->
                EnquiryItem(
                    productName = quote.productName,
                    quantity = "${quote.quantity.toInt()} ${quote.unit}",
                    onClick = { onQuoteClick(quote.quoteId) }
                )
            }
        }
    }
}

@Composable
private fun EnquiryItem(
    productName: String,
    quantity: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = quantity,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Send Quote", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/**
 * Widget showing active orders summary for buyer.
 */
@Composable
fun ActiveOrdersWidget(
    quotes: List<OrderQuoteEntity>,
    onViewAll: () -> Unit,
    onOrderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeOrders = quotes.filter { 
        EvidenceOrderStatus.fromString(it.status) !in listOf(
            EvidenceOrderStatus.COMPLETED,
            EvidenceOrderStatus.CANCELLED,
            EvidenceOrderStatus.EXPIRED
        )
    }

    if (activeOrders.isEmpty()) return

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Orders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAll) {
                    Text("View All")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            activeOrders.take(3).forEach { quote ->
                val status = EvidenceOrderStatus.fromString(quote.status)
                val statusColor = when (status) {
                    EvidenceOrderStatus.ADVANCE_PENDING -> Color(0xFFDC2626)
                    EvidenceOrderStatus.DISPATCHED, EvidenceOrderStatus.PREPARING -> Color(0xFF6366F1)
                    else -> Color(0xFFF59E0B)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOrderClick(quote.quoteId) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = quote.productName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = status.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor
                                )
                            }
                        }
                        Text(
                            text = currencyFormat.format(quote.finalTotal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Quick action button for "Buy from this Seller" on product details.
 */
@Composable
fun BuyFromSellerButton(
    productId: String,
    sellerId: String,
    productName: String,
    basePrice: Double,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Button(
        onClick = onBuyClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6366F1)
        )
    ) {
        Icon(Icons.Default.ShoppingCart, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Send Enquiry • ${currencyFormat.format(basePrice)}/unit",
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Order status badge component.
 */
@Composable
fun OrderStatusBadge(
    status: EvidenceOrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        EvidenceOrderStatus.ENQUIRY, EvidenceOrderStatus.QUOTE_SENT -> 
            Color(0xFFFEF3C7) to Color(0xFF92400E)
        EvidenceOrderStatus.AGREEMENT_LOCKED, EvidenceOrderStatus.PAYMENT_VERIFIED -> 
            Color(0xFFDCFCE7) to Color(0xFF065F46)
        EvidenceOrderStatus.ADVANCE_PENDING -> 
            Color(0xFFFEE2E2) to Color(0xFFB91C1C)
        EvidenceOrderStatus.DISPATCHED, EvidenceOrderStatus.PREPARING, EvidenceOrderStatus.READY_FOR_PICKUP -> 
            Color(0xFFE0E7FF) to Color(0xFF3730A3)
        EvidenceOrderStatus.COMPLETED -> 
            Color(0xFF10B981) to Color.White
        EvidenceOrderStatus.CANCELLED, EvidenceOrderStatus.EXPIRED -> 
            Color(0xFFF3F4F6) to Color(0xFF6B7280)
        EvidenceOrderStatus.DISPUTE, EvidenceOrderStatus.ESCALATED -> 
            Color(0xFFFEE2E2) to Color(0xFFB91C1C)
        else -> Color(0xFFF3F4F6) to Color(0xFF374151)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
